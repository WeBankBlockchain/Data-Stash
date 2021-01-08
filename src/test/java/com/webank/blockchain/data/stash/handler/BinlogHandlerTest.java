/*
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
package com.webank.blockchain.data.stash.handler;

import com.webank.blockchain.data.stash.BaseTest;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.parser.BlockBytesParser;
import com.webank.blockchain.data.stash.rlp.ByteUtil;
import org.junit.Test;
import com.webank.blockchain.data.stash.BaseTest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;

/**
 * BlockParserTest
 *
 * @Description: BlockParserTest
 * @author graysonzhang
 * @data 2019-08-05 17:30:10
 *
 */
public class BinlogHandlerTest {
    
    @Test
    public void testBinlogHandler() throws Exception{
        /*
        File f = new File("0.binlog");
        FileInputStream fis = new FileInputStream(f);
        BufferedInputStream bis = new BufferedInputStream(fis);
        //Skip Version
        bis.read(new byte[4]);
        //Binlog Length
        while(true){

            byte[] blockHeader = new byte[4];
            if(bis.read(blockHeader) < 4) {
                System.out.println("读取结束");
                break;
            }
            int size = ByteUtil.byteArrayToInt(blockHeader);
            byte[] content = new byte[size];
            if(bis.read(content) < content.length){
                System.out.println("读取结束");
                break;
            }
            BlockBytesParser parser = new BlockBytesParser();
            BinlogBlockInfo parsed = parser.getBinlogBlockInfo(content);
            System.out.println("区块高度"+parsed.getBlockNum());
        }

         */
    }

}
