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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

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
import com.webank.blockchain.data.stash.utils.BinlogFileUtils;
import com.webank.blockchain.data.stash.utils.BytesUtil;
import com.webank.blockchain.data.stash.utils.CRC32Util;
import com.webank.blockchain.data.stash.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
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
import com.webank.blockchain.data.stash.utils.BinlogFileUtils;
import com.webank.blockchain.data.stash.utils.BytesUtil;
import com.webank.blockchain.data.stash.utils.CRC32Util;
import com.webank.blockchain.data.stash.utils.JsonUtils;

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
    private BinlogOffsetMapper binlogOffsetMapper;
    @Autowired
    private SystemPropertyConfig config;
    @Autowired
    private BlockHandler blockHandler;
    @Autowired
    private List<RemoteServerInfo> sources;
    @Autowired
    private RollBackService rollBackService;

    public void read() throws IORuntimeException, InterruptedException, IOException {
        BlockTaskPool blockTaskPool = blockTaskPoolMapper.getLatestOne();
        long todoNumber = cursor(blockTaskPool);
        while (true) {
            ArrayList<byte[]> list = Lists.newArrayList();
            for (RemoteServerInfo serverInfo : sources) {
                long offset = getLatestOffset(blockTaskPool, serverInfo.getItem());
                log.info("Latest block number {}, and the last offset is {}, item {}", todoNumber, offset,
                        serverInfo.getItem());
                BinlogLocationBO bo = locate(todoNumber, serverInfo, offset);
                log.info("Block number {}, offset {}, in file path of {}, item {}", todoNumber, bo.getOffset(),
                        bo.getFilePath(), serverInfo.getItem());
                File f = new File(bo.getFilePath());
                if (toContinue(f, bo.getOffset())) {
                    try (InputStream stream = FileUtil.getInputStream(f)) {
                        byte[] blockLenthBytes = new byte[BinlogConstants.BLOCK_LENGTH];
                        long skipped = stream.skip(bo.getOffset());
                        log.debug("{} skipped, and offset is {}", skipped, bo.getOffset());
                        stream.read(blockLenthBytes, 0, BinlogConstants.BLOCK_LENGTH);
                        int blockLength = Convert.bytesToInt(blockLenthBytes);
                        log.info("Block {} binlog length is {}", todoNumber, blockLength);
                        byte[] content = new byte[blockLength];
                        stream.read(content, 0, blockLength);
                        byte[] datas = Arrays.copyOfRange(content, 0, blockLength - BinlogConstants.CRC32_LENGTH);
                        if (!verifyCRC32(content, datas)) {
                            log.error("Block {}, verify error", todoNumber);
                            blockTaskPoolMapper.updateSyncStatusByBlockHeight(
                                    BlockTaskPoolSyncStatusEnum.ERROR.getSyncStatus(), todoNumber);
                            Thread.sleep(5 * 1000L);
                            return;
                        }
                        list.add(datas); 
                        BinlogOffset binlogOffset = new BinlogOffset().setItem(serverInfo.getItem())
                                .setBlockHeight(todoNumber).setBinlogName(f.getAbsolutePath()).setLength(blockLength)
                                .setOffset(bo.getOffset());
                        binlogOffsetMapper.deleteByBlockHeightAndItem(todoNumber, serverInfo.getItem());
                        binlogOffsetMapper.insert(binlogOffset);
                    }
                } else {
                    log.info("No this file part, item: {}, block {}", serverInfo.getItem(), todoNumber);
                    Thread.sleep(5 * 1000L);
                    return;
                }
            }
            try {
                if (blockHandler.handler(list)) {
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
            blockTaskPoolMapper.updateSyncStatusByBlockHeight(BlockTaskPoolSyncStatusEnum.Done.getSyncStatus(),
                    todoNumber);
            todoNumber++;
            blockTaskPool = insertTaskPool(todoNumber);
        }
    }

    public long cursor(BlockTaskPool blockTaskPool) throws IORuntimeException, InterruptedException, IOException {
        if (blockTaskPool == null) {
            return prepare(blockTaskPool);
        } else if (blockTaskPool.getSyncStatus() == BlockTaskPoolSyncStatusEnum.Done.getSyncStatus()) {
            return prepare(blockTaskPool);
        } else {
            //Init or Error
            long todoNumber = blockTaskPool.getBlockHeight();
            rollBackService.rollBack(todoNumber);
            blockTaskPoolMapper.updateSyncStatusByBlockHeight(BlockTaskPoolSyncStatusEnum.INIT.getSyncStatus(),
                    todoNumber);
            relocate(todoNumber);
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

    public boolean toContinue(File file, long offset) throws InterruptedException {
        long fileSize = FileUtil.size(file);
        if (offset >= fileSize) {
            log.info("no new binlog content.");
            return false;
        }
        return true;
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

    public long getLatestOffset(BlockTaskPool blockTaskPool, int item) {
        if (blockTaskPool == null) {
            return 4;
        } else {
            BinlogOffset binlogOffset =
                    binlogOffsetMapper.getByBlockHeightAndItem(blockTaskPool.getBlockHeight(), item);
            if (binlogOffset == null) {
                binlogOffset = binlogOffsetMapper.getByBlockHeightAndItem(blockTaskPool.getBlockHeight() - 1, item);
                if (binlogOffset == null) {
                    return 4;
                }
                return binlogOffset.getOffset() + BinlogConstants.BLOCK_LENGTH + binlogOffset.getLength();
            } else {
                return binlogOffset.getOffset();
            }
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
            return blockTaskPool.getBlockHeight();
        }
    }

    public void nextTask(long blockHeight) {
        BlockTaskPool blockTaskPool = new BlockTaskPool();
        blockTaskPool.setBlockHeight(blockHeight);
        blockTaskPoolMapper.insert(blockTaskPool);
        log.info("insert block {} finished", blockHeight);
    }

    public BinlogLocationBO locate(long blockNumber, RemoteServerInfo serverInfo, long offset) {
        TreeSet<Long> filesSet = BinlogFileUtils.getFileIds(serverInfo.getLocalFilePath(), config.getBinlogSuffix());
        long floor = filesSet.floor(blockNumber);
        BinlogLocationBO bo = new BinlogLocationBO();
        if (filesSet.contains(blockNumber)) {
            bo.setOffset(4);
        } else {
            bo.setOffset(offset);
        }
        log.debug(JsonUtils.toJson(filesSet));
        bo.setFilePath(serverInfo.getLocalFilePath() + floor + "." + config.getBinlogSuffix());
        return bo;
    }

    public void relocate(long blockNumber) throws IORuntimeException, InterruptedException, IOException {
        for (RemoteServerInfo serverInfo : sources) {
            log.info("start to relocate, block number {}, item {}", blockNumber, serverInfo.getItem());
            long floor = BinlogFileUtils.floor(serverInfo.getLocalFilePath(), config.getBinlogSuffix(), blockNumber);
            long offset = 4;
            File f = new File(serverInfo.getLocalFilePath() + floor + "." + config.getBinlogSuffix());
            BinlogOffset binlogOffset = new BinlogOffset().setItem(serverInfo.getItem())
                    .setBinlogName(f.getAbsolutePath()).setBlockHeight(blockNumber);
            long i = floor;
            try (InputStream stream = FileUtil.getInputStream(f)) {
                while (i <= blockNumber) {
                    if (toContinue(f, offset)) {
                        byte[] blockLenthBytes = new byte[BinlogConstants.BLOCK_LENGTH];
                        long skipped = stream.skip(offset);
                        log.debug("{} skipped, and offset is {}", skipped, offset);
                        stream.read(blockLenthBytes, 0, BinlogConstants.BLOCK_LENGTH);
                        int blockLength = Convert.bytesToInt(blockLenthBytes);
                        log.info("Block {} binlog length is {}", i, blockLength);
                        offset = offset + blockLength + BinlogConstants.BLOCK_LENGTH;
                        if (i == blockNumber) {
                            binlogOffset.setLength(blockLength).setOffset(offset);
                            continue;
                        }
                        i++;
                    } else {
                        return;
                    }
                }
            }
            binlogOffsetMapper.insert(binlogOffset);
        }
    }

}
