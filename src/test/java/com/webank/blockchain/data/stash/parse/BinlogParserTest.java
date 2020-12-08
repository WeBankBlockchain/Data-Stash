/**
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.stash.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.webank.blockchain.data.stash.constants.BinlogConstants;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.parser.BlockBytesParser;
import com.webank.blockchain.data.stash.utils.BytesUtil;
import com.webank.blockchain.data.stash.utils.CRC32Util;

import cn.hutool.core.convert.Convert;
import org.junit.Assert;
import org.junit.Test;

/**
 * BinlogParserTest
 *
 * @Description: BinlogParserTest
 * @author maojiayu
 * @data Dec 13, 2019 2:35:45 PM
 *
 */
public class BinlogParserTest {

    @Test
    public void testBinLogParse() throws Exception{

        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("0.binlog")){
            //Version
            byte[] versionBytes = new byte[4];
            safeRead(is, versionBytes, 0, 4);
            int version = Convert.bytesToInt(versionBytes);
            Assert.assertEquals(1, version);
            //BLock 0
            byte[] blockLenthBytes = new byte[BinlogConstants.BLOCK_LENGTH];
            safeRead(is, blockLenthBytes, 0, 4);
            int blockLength = Convert.bytesToInt(blockLenthBytes);
            Assert.assertEquals(2395, blockLength);

            byte[] blockData = new byte[blockLength - BinlogConstants.CRC32_LENGTH];
            safeRead(is, blockData, 0, blockData.length);
            testBlockData(blockData);
        }
        finally {

        }
    }

    private void testBlockData(byte[] blockData) throws Exception{
        BlockBytesParser parser = new BlockBytesParser();
        BinlogBlockInfo blockInfo = parser.getBinlogBlockInfo(blockData);

    }

    private void safeRead(InputStream is, byte[] buffer, int offset, int length) throws IOException{
        int n = 0;
        while (n < length){
            n += is.read(buffer, offset, length - n);
        }
    }

    public static boolean verifyCRC32(byte[] content, byte[] datas) {
        byte[] checksumBytes = Arrays.copyOfRange(content, content.length - BinlogConstants.CRC32_LENGTH, content.length);
        long c = BytesUtil.byte4UnsignToLong(checksumBytes);
        if (!CRC32Util.equals(datas,c)) {
            System.out.println("CRC32 check fail, block ");
            return false;
        }
        return true;
    }

    public static BinlogBlockInfo getBinlogBlockInfo(byte[] blockBytes) throws DataStashException {

        BinlogBlockInfo binlogBlockInfo = new BinlogBlockInfo();

        // 1. get block number
        long blockNum = BytesUtil.byte8FromSrcToLong(blockBytes, 0);
        binlogBlockInfo.setBlockNum(blockNum);
        System.out.println("block bytes parser num : " + blockNum);
        // 2. get data count
        int dataCount = BytesUtil.byte4FromSrcToInt(blockBytes, 8);
        binlogBlockInfo.setDataCount(dataCount);

        int dataIndex = 12;
        System.out.println("data count : " + dataCount);
        for (int i = 0; i < dataCount; i++) {

            int nameLen = blockBytes[dataIndex] & 0xFF;

            byte[] tableNameBytes = BytesUtil.subBytes(blockBytes, dataIndex + 1, nameLen);
            String tableName = new String(tableNameBytes);
            System.out.println("tableName: {}" + tableName);
        }

        return binlogBlockInfo;
    }
}
