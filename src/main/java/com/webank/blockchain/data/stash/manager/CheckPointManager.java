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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.*;

import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.db.model.CheckPointInfo;
import com.webank.blockchain.data.stash.db.model.SysTablesInfo;
import com.webank.blockchain.data.stash.db.service.CheckPointInfoService;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.handler.TreeMapComparator;
import org.apache.commons.codec.binary.Hex;

import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.crypto.hash.Keccak256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.webank.blockchain.data.stash.aspect.UseTime;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.db.model.CheckPointInfo;
import com.webank.blockchain.data.stash.db.model.SysTablesInfo;
import com.webank.blockchain.data.stash.db.service.CheckPointInfoService;
import com.webank.blockchain.data.stash.db.service.SysTablesInfoService;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.entity.ColumnInfo;
import com.webank.blockchain.data.stash.entity.EntryInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.handler.TreeMapComparator;
import com.webank.blockchain.data.stash.utils.CommonUtil;
import com.webank.blockchain.data.stash.utils.JsonUtils;

import javax.annotation.PostConstruct;

/**
 * CheckPointOperator
 *
 * @Description: CheckPointOperator
 * @author maojiayu
 * @data Oct 24, 2019 9:16:45 PM
 *
 */
@Service
public class CheckPointManager {

    private Hash keccak = new Keccak256();
    @Autowired
    private CheckPointInfoService checkPointService;
    @Autowired
    private SysTablesInfoService sysTablesInfoService;
    private ExecutorService executor;

    private SystemPropertyConfig config;

    @PostConstruct
    private void init(){
        this.executor = new ThreadPoolExecutor(1,1,0, TimeUnit.DAYS,new LinkedBlockingQueue<>(config.getQueuedSize()));
    }

    public Future<Boolean> futureCreateCheckPoint(BinlogBlockInfo blockInfo) {
        return (Future<Boolean>) executor.submit(() -> {
            return createCheckPoint(blockInfo);
        });
    }

    @UseTime
    public boolean createCheckPoint(BinlogBlockInfo blockInfo) {
        StringBuffer buffer = new StringBuffer();
        long blockNum = blockInfo.getBlockNum();
        // 1. get pre block data hash
        if (blockNum > 0) {
            CheckPointInfo preCheckPoint = checkPointService.getByBlockNum(blockNum - 1);
            buffer.append(preCheckPoint.getBlockDataHash());
        }
        // 2. create check point
        Map<String, TableDataInfo> map = blockInfo.getTables();
        Map<String, List<TreeMap<String, String>>> result = new TreeMap<>();
        for (Entry<String, TableDataInfo> e : map.entrySet()) {
            String tableName = e.getKey();
            if (tableName.equalsIgnoreCase(DBStaticTableConstants.SYS_TABLES_TABLE)) {
                continue;
            }
            List<TreeMap<String, String>> columns = new ArrayList<>();
            TableDataInfo t = e.getValue();
            TreeSet<EntryInfo> all = new TreeSet<>();
            all.addAll(t.getDirtyEntrys());
            all.addAll(t.getNewEntrys());
            for (EntryInfo ei : all) {
                TreeMap<String, String> columnsMap = new TreeMap<>();
                List<ColumnInfo> list = ei.getColumns();
                if (ei.getHash() == null) {
                    columnsMap.put("_hash_", "0x00");
                } else {
                    columnsMap.put("_hash_", ei.getHash());
                }
                columnsMap.put("_num_", ei.getNum() + "");
                columnsMap.put("_status_", ei.getStatus() + "");
                columnsMap.put("_id_", ei.getId() + "");
                list.forEach(l -> {
                    columnsMap.put(l.getColumnName(), l.getColumnValue());
                });
                columns.add(columnsMap);
            }
            Collections.sort(columns, new TreeMapComparator());
            result.put(tableName, columns);
        }
        buffer.append(JsonUtils.toJson(result));
        String checkpoint = Hex.encodeHexString(keccak.hash(buffer.toString().getBytes()));
        CheckPointInfo checkPoint = new CheckPointInfo();
        checkPoint.setBlockNum(blockInfo.getBlockNum());
        checkPoint.setBlockDataHash(checkpoint);
        // 3. save check point
        checkPointService.save(checkPoint);
        return true;
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
