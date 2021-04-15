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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.constants.BinlogConstants;
import com.webank.blockchain.data.stash.db.mapper.BinlogOffsetMapper;
import com.webank.blockchain.data.stash.db.mapper.BlockTaskPoolMapper;
import com.webank.blockchain.data.stash.db.model.BinlogOffset;
import com.webank.blockchain.data.stash.db.model.BlockTaskPool;
import com.webank.blockchain.data.stash.db.rollback.RollBackService;
import com.webank.blockchain.data.stash.entity.RemoteServerInfo;
import com.webank.blockchain.data.stash.enums.BlockTaskPoolSyncStatusEnum;
import com.webank.blockchain.data.stash.fetch.BinlogLocationBO;
import com.webank.blockchain.data.stash.handler.BlockHandler;
import com.webank.blockchain.data.stash.read.MultiSourceBlockReader;
import com.webank.blockchain.data.stash.utils.BinlogFileUtils;
import com.webank.blockchain.data.stash.utils.BytesUtil;
import com.webank.blockchain.data.stash.utils.CRC32Util;
import com.webank.blockchain.data.stash.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import lombok.extern.slf4j.Slf4j;

/**
 * BlockReadManager
 *
 * @Description: BlockReadManager
 * @author maojiayu
 * @data Aug 28, 2019 4:17:14 PM
 *
 */
@Service
@Slf4j
public class BlockReadManager {
    @Autowired
    private BlockTaskPoolMapper blockTaskPoolMapper;
    @Autowired
    private SystemPropertyConfig config;
    @Autowired
    private BlockHandler blockHandler;
    @Autowired
    private List<RemoteServerInfo> sources;
    @Autowired
    private RollBackService rollBackService;

    public void read() throws IORuntimeException, InterruptedException, Exception {
        //Determine the block to start
        BlockTaskPool blockTaskPool = blockTaskPoolMapper.getLatestOne();
        long todoNumber = prepare(blockTaskPool);

        try(MultiSourceBlockReader blockReader = new MultiSourceBlockReader(sources, todoNumber,config.getBinlogSuffix())){
            List<byte[]> blocks;
            while ((blocks = blockReader.read()) != null){
                //Extract body and verify crc
                List<byte[]> blockDatas = toBlockBodyDatas(todoNumber, blocks);
                //Handle block body
                handleBlockBody(todoNumber, blockDatas);
                //Update block progress
                blockTaskPoolMapper.updateSyncStatusByBlockHeight(BlockTaskPoolSyncStatusEnum.Done.getSyncStatus(),
                        todoNumber);
                todoNumber++;
                insertTaskPool(todoNumber);
            }
        }
    }

    private List<byte[]> toBlockBodyDatas(long blockNumber, List<byte[]> blockPackage){
        //1. Verify block CRC
        List<byte[]> blockDatas = new ArrayList<>();
        for(byte[] block: blockPackage){
            byte[] data = Arrays.copyOfRange(block, 0, block.length - BinlogConstants.CRC32_LENGTH);
            blockDatas.add(data);
            //verify crc32
            if (!verifyCRC32(block, data)) {
                log.error("Block {}, verify error", blockNumber);
                blockTaskPoolMapper.updateSyncStatusByBlockHeight(
                        BlockTaskPoolSyncStatusEnum.ERROR.getSyncStatus(), blockNumber);
                System.exit(-1);
            }
        }
        return blockDatas;
    }

    private void handleBlockBody(long todoNumber, List<byte[]> blockBodys){
        try {
            if (blockHandler.handler(blockBodys)) {
                blockTaskPoolMapper.updateSyncStatusByBlockHeight(BlockTaskPoolSyncStatusEnum.Done.getSyncStatus(),
                        todoNumber);
            } else {
                blockTaskPoolMapper.updateSyncStatusByBlockHeight(BlockTaskPoolSyncStatusEnum.ERROR.getSyncStatus(),
                        todoNumber);
                log.error("Block handle error. System exit. ");
                System.exit(2);
            }
        } catch (Exception e) {
            blockTaskPoolMapper.updateSyncStatusByBlockHeight(BlockTaskPoolSyncStatusEnum.ERROR.getSyncStatus(),
                    todoNumber);
            log.error("Exception encounted: ", e);
            System.exit(1);
        }
    }

    public long prepare(BlockTaskPool blockTaskPool) {
        if (blockTaskPool == null) {
            insertTaskPool(0);
            return 0;
        }
        if (blockTaskPool.getSyncStatus() == 2) {
            insertTaskPool(blockTaskPool.getBlockHeight() + 1);
            return blockTaskPool.getBlockHeight() + 1;
        } else {
            long todoNumber = blockTaskPool.getBlockHeight();
            rollBackService.rollBack(todoNumber);
            blockTaskPoolMapper.updateSyncStatusByBlockHeight(BlockTaskPoolSyncStatusEnum.INIT.getSyncStatus(),
                    todoNumber);
            return todoNumber;
        }
    }
    public BlockTaskPool insertTaskPool(long todoNumber) {
        BlockTaskPool bp = new BlockTaskPool();
        bp.setBlockHeight(todoNumber);
        log.info("begin to insert block {}", todoNumber);
        blockTaskPoolMapper.insert(bp);
        return bp;
    }


    public boolean verifyCRC32(byte[] content, byte[] datas) {
        byte[] checksumBytes =
                Arrays.copyOfRange(content, content.length - BinlogConstants.CRC32_LENGTH, content.length);
        long checksum = BytesUtil.byte4UnsignToLong(checksumBytes);

        if (!CRC32Util.equals(datas, checksum)) {
            return false;
        }
        return true;
    }
}
