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
package com.webank.blockchain.data.stash.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.webank.blockchain.data.stash.block.TransactionDetail;
import com.webank.blockchain.data.stash.rlp.ByteUtil;
import com.webank.blockchain.data.stash.rlp.RLP;
import com.webank.blockchain.data.stash.rlp.RLPList;
import com.webank.blockchain.data.stash.utils.BytesUtil;

/**
 * BlockRlpParser
 *
 * @Description: BlockRlpParser
 * @author maojiayu
 * @data Sep 4, 2019 10:04:08 AM
 *
 */
public class BlockRlpParser {
    
    public static List<TransactionDetail> parseTransactionDetail(byte[] transactions){
        List<TransactionDetail> list = new ArrayList<>();
        if(ArrayUtils.isEmpty(transactions)) {
            return list;
        }
        long count = (int) ByteUtil.byte4UnsignToLong(transactions, 0);
        int offset = (int) (count*4+8);
        int startPos, endPos = 0;
        for(long i=1;i<=count;i++) {
            startPos = (int) ByteUtil.byte4UnsignToLong(transactions, (int) (4*i));
            endPos = (int) ByteUtil.byte4UnsignToLong(transactions, (int) (4*i+4));
            byte[] sub = BytesUtil.subBytes(transactions, offset+startPos, endPos-startPos);
            TransactionDetail t = new TransactionDetail((RLPList) RLP.decode2(sub).get(0));
            list.add(t);
        }
        return list;
    }
}
