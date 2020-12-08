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
import java.util.concurrent.Future;

import com.webank.blockchain.data.stash.db.face.DataStorage;
import com.webank.blockchain.data.stash.db.mapper.BlockTaskPoolMapper;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.enums.BlockTaskPoolSyncStatusEnum;
import com.webank.blockchain.data.stash.manager.CheckPointManager;
import com.webank.blockchain.data.stash.parser.BlockBytesParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.enums.DataStashExceptionCodeEnums;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.verify.BlockValidation;

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
    private BlockValidation validator;
    @Autowired
    private DataStorage dataStorage;
    @Autowired
    private CheckPointManager checkPointManager;
    @Autowired
    private SystemPropertyConfig config;
    @Autowired
    private BlockTaskPoolMapper blockTaskPoolMapper;

    public boolean handler(List<byte[]> blockBytesList) throws Exception {

        if (blockBytesList == null || blockBytesList.size() == 0) {
            throw new DataStashException(DataStashExceptionCodeEnums.DATA_STASH_BLOCK_BYTES_LIST_IS_NULL);
        }

        // 1. parse block data
        byte[] firstBlockBytes = blockBytesList.get(0);
        BinlogBlockInfo blockInfo = parser.getBinlogBlockInfo(firstBlockBytes);
        log.debug("===============end binlog parse===================");

        // 2. verify block data
        if (blockBytesList.size() != 1 && config.getBinlogVerify() == 1) {
            if (!validator.vaildBlocks(blockInfo, blockBytesList))
                return false;
        }
        log.debug("===============end block validation===================");

        // set task pool status
        blockTaskPoolMapper.updateSyncStatusByBlockHeight(BlockTaskPoolSyncStatusEnum.DOING.getSyncStatus(),
                blockInfo.getBlockNum());
        Future<Boolean> f = checkPointManager.futureCreateCheckPoint(blockInfo);
        dataStorage.storageBlock(blockInfo);
        log.debug("===============end block data store===================");

        f.get();
        log.debug("===============end create check point===================");

        return true;
    }
}
