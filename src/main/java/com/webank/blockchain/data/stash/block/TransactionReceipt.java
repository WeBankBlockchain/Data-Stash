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
package com.webank.blockchain.data.stash.block;

import com.webank.blockchain.data.stash.rlp.ByteUtil;
import com.webank.blockchain.data.stash.rlp.RLPList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;

/**
 * TransactionRecipt
 *
 * @Description: TransactionReceipt
 * @author maojiayu
 * @data Sep 25, 2019 10:36:21 AM
 *
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class TransactionReceipt {
    private byte[] stateRoot;
    private BigInteger gasUsed;
    private String contractAddress;
    private byte[] logsBloom;
    private long status;
    private byte[] outputBytes;
    private byte[] logs;

    public TransactionReceipt(RLPList rlpTransactionReceipt) {
        this.stateRoot = rlpTransactionReceipt.get(0).getRLPData();
        this.gasUsed = ByteUtil.bytesToBigInteger(rlpTransactionReceipt.get(1).getRLPData());
        this.contractAddress = Hex.encodeHexString(rlpTransactionReceipt.get(2).getRLPData());
        this.logsBloom = rlpTransactionReceipt.get(3).getRLPData();
        this.status = ByteUtil.bytesToBigInteger(rlpTransactionReceipt.get(4).getRLPData()).longValue();
        this.outputBytes = rlpTransactionReceipt.get(5).getRLPData();
        this.logs = rlpTransactionReceipt.get(6).getRLPData();
    }

}
