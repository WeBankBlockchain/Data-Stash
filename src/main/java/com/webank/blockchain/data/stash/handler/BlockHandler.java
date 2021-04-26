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
package com.webank.blockchain.data.stash.handler;

import java.util.List;
import java.util.concurrent.*;

import com.webank.blockchain.data.stash.db.face.DataStorage;
import com.webank.blockchain.data.stash.db.mapper.BlockTaskPoolMapper;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.enums.BlockTaskPoolSyncStatusEnum;
import com.webank.blockchain.data.stash.parser.BlockBytesParser;
import com.webank.blockchain.data.stash.thread.DataStashThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.enums.DataStashExceptionCodeEnums;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.verify.ComparisonValidation;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * BlockHandler
 *
 * @Description: BlockHandler
 * @author graysonzhang
 * @data 2019-09-01 22:41:58
 *
 */
@Service
@Slf4j
public class BlockHandler {

    @Autowired
    private BlockBytesParser parser;
    @Autowired
    private ComparisonValidation validator;
    @Autowired
    private DataStorage dataStorage;
    @Autowired
    private SystemPropertyConfig config;
    @Autowired
    private BlockTaskPoolMapper blockTaskPoolMapper;

    ExecutorService parserPool;
    ExecutorService sqlPool;

    @PostConstruct
    private void init(){
        //Dont use unbound arrays, otherwise OOM will happen!
        parserPool = new ThreadPoolExecutor(this.config.getParseThreads(), this.config.getParseThreads(),
                0, TimeUnit.DAYS, new LinkedBlockingQueue<>(config.getParseQueueSize()), new DataStashThreadFactory("parserPool"),
                (r, executor) -> r.run());
        sqlPool = new ThreadPoolExecutor(this.config.getSqlThreads(), this.config.getSqlThreads(),
                0, TimeUnit.DAYS, new LinkedBlockingQueue<>(config.getSqlQueueSize()), new DataStashThreadFactory("sqlPool"), (r, executor) -> r.run());
    }

    public CompletableFuture<Void> handleAsync(List<byte[]> blockBytesList) {
        return CompletableFuture
                //Parse
                .supplyAsync(() -> parseBinlogThenVerify(blockBytesList), parserPool)
                //Store
                .thenAcceptAsync(blockInfo -> storeBlockData(blockInfo), sqlPool);
    }


    private BinlogBlockInfo parseBinlogThenVerify(List<byte[]> blockBytesList) {
        // 1. Parse binlog
        byte[] firstBlockBytes = blockBytesList.get(0);
        BinlogBlockInfo blockInfo = parser.getBinlogBlockInfo(firstBlockBytes);
        log.debug("===============end binlog parse===================");

        // 2. Verify
        if (config.getBinlogVerify() == 1 && blockBytesList.size() > 1) {
            if (!validator.compareValidate(blockInfo, blockBytesList)){
                throw new DataStashException(DataStashExceptionCodeEnums.DATA_STASH_BINLOG_VERIFY_ERROR);
            }
            log.debug("===============end binlog verify===================");
        }
        return blockInfo;
    }

    private BinlogBlockInfo storeBlockData(BinlogBlockInfo blockInfo) {
        blockTaskPoolMapper.updateSyncStatusByBlockHeight(BlockTaskPoolSyncStatusEnum.DOING.getSyncStatus(),
                blockInfo.getBlockNum());
        dataStorage.storeBlock(blockInfo);
        log.debug("===============end block data store===================");
        return blockInfo;
    }
}
