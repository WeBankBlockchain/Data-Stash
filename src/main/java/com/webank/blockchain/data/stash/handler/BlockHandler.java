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
import java.util.concurrent.atomic.AtomicInteger;

import com.webank.blockchain.data.stash.db.face.DataStorage;
import com.webank.blockchain.data.stash.db.mapper.BlockTaskPoolMapper;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.enums.BlockTaskPoolSyncStatusEnum;
import com.webank.blockchain.data.stash.parser.BlockBytesParser;
import com.webank.blockchain.data.stash.store.LedgerDBStorage;
import com.webank.blockchain.data.stash.store.StateDBStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.enums.DataStashExceptionCodeEnums;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.verify.ComparisonValidation;

import lombok.extern.slf4j.Slf4j;

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
    private SystemPropertyConfig config;
    @Autowired
    private BlockTaskPoolMapper blockTaskPoolMapper;
    @Autowired
    private TaskCounterHandler taskCounterHandler;

    @Autowired
    private ThreadPoolExecutor ledgerPool;

    @Autowired
    private ThreadPoolExecutor statePool;

    @Autowired
    private LedgerDBStorage ledgerDBStorage;
    @Autowired
    private StateDBStorage stateDBStorage;


    public BinlogBlockInfo parseBinlogThenVerify(long block, List<byte[]> blockBytesList) {
        try{
            // 1. Parse binlog
            byte[] firstBlockBytes = blockBytesList.get(0);
            BinlogBlockInfo blockInfo = parser.getBinlogBlockInfo(firstBlockBytes);
            log.debug("===============end binlog parse===================");

            // 2. Verify
            if(blockInfo == null){
                throw new DataStashException(DataStashExceptionCodeEnums.DATA_STASH_BINLOG_NULL);
            }
            if(block != blockInfo.getBlockNum())
                throw new DataStashException(DataStashExceptionCodeEnums.DATA_STASH_BINLOG_BLOCKNUM_NOT_MATCH);

            if (config.getBinlogVerify() == 1 && blockBytesList.size() > 1) {
                if (!validator.compareValidate(blockInfo, blockBytesList)){
                    throw new DataStashException(DataStashExceptionCodeEnums.DATA_STASH_BINLOG_VERIFY_ERROR);
                }
                log.debug("===============end binlog verify===================");
            }

            return blockInfo;
        }
        catch (Exception ex){
            onException(block, ex);
            return null;
        }
    }

    public void submitStorageTask(BinlogBlockInfo blockInfo) {
        taskCounterHandler.increase();
        MultiPartsTask storeTask = new MultiPartsTask(blockInfo, 2);

        this.statePool.execute(()->{
            doStoreForPart(storeTask, stateDBStorage);
        });

        this.ledgerPool.execute(()->{
            doStoreForPart(storeTask, ledgerDBStorage);
        });
    }

    public void awaitAllTasksFinished(){
        this.taskCounterHandler.await();
    }

    private void doStoreForPart(MultiPartsTask storeTask, DataStorage storage){
        BinlogBlockInfo blockInfo = storeTask.getBody();
        try{
            storage.storeBlock(blockInfo);
            onPartDone(storeTask);
        }
        catch (Exception ex){
            onException(blockInfo.getBlockNum(), ex);
        }
    }

    private void onPartDone(MultiPartsTask storeTask){
        boolean allPartsFinished = storeTask.finishPart();
        if(allPartsFinished){
            onTaskFinished(storeTask);
        }
    }

    private void onTaskFinished(MultiPartsTask storeTask){
        blockTaskPoolMapper.updateSyncStatusByBlockHeight(BlockTaskPoolSyncStatusEnum.Done.getSyncStatus(),
                storeTask.getBody().getBlockNum());
        taskCounterHandler.decrease();
        log.debug("===============end block data store===================");
    }

    private void onException(long blockNumber, Exception ex){
        blockTaskPoolMapper.updateSyncStatusByBlockHeight(BlockTaskPoolSyncStatusEnum.ERROR.getSyncStatus(),
                blockNumber);
        log.error("Exception encounted: ", ex);
        System.exit(-1);
    }



    public static class MultiPartsTask {
        private BinlogBlockInfo binlogBlockInfo;
        private AtomicInteger counter;

        public MultiPartsTask(BinlogBlockInfo binlogBlockInfo, int taskParts){
            this.binlogBlockInfo = binlogBlockInfo;
            this.counter = new AtomicInteger(taskParts);
        }

        public boolean finishPart(){
            int decreaseVal = this.counter.decrementAndGet();
            return decreaseVal == 0;
        }

        public BinlogBlockInfo getBody(){
            return binlogBlockInfo;
        }
    }
}
