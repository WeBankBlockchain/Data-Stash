/**
 * Copyright 2020 Webank.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.stash.manager;

import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.db.dao.SysHash2BlockInfoMapper;
import com.webank.blockchain.data.stash.db.mapper.BlockTaskPoolMapper;
import com.webank.blockchain.data.stash.db.model.BlockTaskPool;
import com.webank.blockchain.data.stash.db.model.CheckPointInfo;
import com.webank.blockchain.data.stash.db.model.SysHash2BlockInfo;
import com.webank.blockchain.data.stash.db.model.SysTablesInfo;
import com.webank.blockchain.data.stash.db.service.CheckPointInfoService;
import com.webank.blockchain.data.stash.db.service.SysTablesInfoService;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.handler.TreeMapComparator;
import com.webank.blockchain.data.stash.utils.CommonUtil;
import com.webank.blockchain.data.stash.utils.JsonUtils;
import com.webank.blockchain.data.stash.verify.ValidatorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.crypto.hash.Keccak256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * CheckPointOperator
 *
 * @Description: CheckPointOperator
 * @author maojiayu
 * @data Oct 24, 2019 9:16:45 PM
 *
 */
@Service
@ConditionalOnProperty(prefix = "system",name = "checkPointVerify", havingValue = "1")
@Slf4j
public class CheckPointManager {

    private Hash keccak = new Keccak256();
    @Autowired
    private CheckPointInfoService checkPointService;
    @Autowired
    private SysTablesInfoService sysTablesInfoService;
    @Autowired
    private ValidatorService validatorService;
    @Autowired
    private SystemPropertyConfig config;
    @Autowired
    private BlockTaskPoolMapper blockTaskPoolMapper;
    @Autowired
    private SysHash2BlockInfoMapper hash2BlockInfoMapper;

    private ScheduledExecutorService executor;
    private long nextCheckpoint;

    @PostConstruct
    private void init(){
        this.nextCheckpoint = checkPointService.nextCheckpoint();
        this.executor = Executors.newScheduledThreadPool(1);
        this.executor.scheduleAtFixedRate(this::verifyThenCheckpoint, 0, 30, TimeUnit.SECONDS);
    }

    private void verifyThenCheckpoint() {
        BlockTaskPool lastFinishedBlock = blockTaskPoolMapper.getLastFinishedBlock();
        if (!shouldDoCheckpoint(lastFinishedBlock)) {
            return;
        }
        long end = lastFinishedBlock.getBlockHeight();
        for(;nextCheckpoint <= end; nextCheckpoint++){
            if(!doVerify(nextCheckpoint)){
                throw new DataStashException("verify block failed for block "+nextCheckpoint);
            }
            doCheckpoint(nextCheckpoint);
            this.nextCheckpoint++;
        }

    }

    private boolean shouldDoCheckpoint(BlockTaskPool lastFinishedBlock) {
        return lastFinishedBlock == null || lastFinishedBlock.getBlockHeight() < this.nextCheckpoint;
    }

    private boolean doVerify(long block){
        SysHash2BlockInfo blockInfo = this.hash2BlockInfoMapper.selectByBlockNumber(block);
        try{
            return validatorService.validateBlockRlp(BigInteger.valueOf(block), blockInfo.getValue());
        }
        catch (Exception ex){
            log.error("block verify failed",ex);
            return false;
        }
    }


    private void doCheckpoint(long block){
        String blockHash = exportCheckPoint(block);
        CheckPointInfo checkPoint = new CheckPointInfo();
        checkPoint.setBlockNum(block);
        checkPoint.setBlockDataHash(blockHash);
        checkPointService.save(checkPoint);
    }

    public String exportCheckPoint(Long blockNum) {
        StringBuffer buffer = new StringBuffer();
        // 1. get pre block data hash
        if (blockNum > 0) {
            CheckPointInfo preCheckPoint = checkPointService.getByBlockNum(blockNum - 1);
            buffer.append(preCheckPoint.getBlockDataHash());
        }
        // 2. get all tables
        List<SysTablesInfo> tables = sysTablesInfoService.selectAllTables();
        // 3. get block data string
        Map<String, List<TreeMap<String, String>>> result = new TreeMap<>();
        for (SysTablesInfo table : tables) {
            if (!table.getTableName().equals(DBStaticTableConstants.SYS_TABLES_TABLE)) {
                List<TreeMap<String, String>> detailEntrys = checkPointService
                        .selectBlockDataByBlockNum(CommonUtil.getDetailTableName(table.getTableName()), blockNum);
                if (CollectionUtils.isEmpty(detailEntrys)) {
                    continue;
                }
                for (TreeMap<String, String> tm : detailEntrys) {
                    tm.remove("pk_id");
                    tm.put("_id_", String.valueOf(tm.get("_id_")));
                    tm.put("_num_", String.valueOf(tm.get("_num_")));
                    tm.put("_status_", String.valueOf(tm.get("_status_")));
                    tm.put("_hash_", "0x00");
                }
                Collections.sort(detailEntrys, new TreeMapComparator());
                result.put(table.getTableName(), detailEntrys);
            }
        }
        buffer.append(JsonUtils.toJson(result));
        // 4. create check point
        return Hex.encodeHexString(keccak.hash(buffer.toString().getBytes()));
    }

}
