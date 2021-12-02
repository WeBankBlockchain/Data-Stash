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
import org.fisco.bcos.sdk.abi.datatypes.Address;

import java.math.BigInteger;

/**
 * TransactionDetail
 *
 * @Description: TransactionDetail
 * @author maojiayu
 * @data Sep 10, 2019 8:02:42 PM
 *
 */
@Data
@Accessors
@EqualsAndHashCode
public class TransactionDetail {
    private BigInteger nonce;
    private BigInteger gasPrice;
    private BigInteger gas;
    private BigInteger blockLimit;
    private int type;
    private Address receiveAddress;
    private BigInteger value;
    private byte[] data;
    private BigInteger chainId;
    private BigInteger groupId;
    private byte[] v;
    private BigInteger r;
    private BigInteger s;

    public TransactionDetail(RLPList rlpHeader) {
        this.nonce = ByteUtil.bytesToBigInteger(rlpHeader.get(0).getRLPData());
        this.gasPrice = ByteUtil.bytesToBigInteger(rlpHeader.get(1).getRLPData());
        this.gas = ByteUtil.bytesToBigInteger(rlpHeader.get(2).getRLPData());
        this.blockLimit = ByteUtil.bytesToBigInteger(rlpHeader.get(3).getRLPData());
        this.type = ByteUtil.byteArrayToInt(rlpHeader.get(4).getRLPData());
        this.receiveAddress = new Address(ByteUtil.bytesToBigInteger(rlpHeader.get(5).getRLPData()));
        this.value = ByteUtil.bytesToBigInteger(rlpHeader.get(6).getRLPData());
        this.data = rlpHeader.get(7).getRLPData();
        this.chainId = ByteUtil.bytesToBigInteger(rlpHeader.get(8).getRLPData());
        this.groupId = ByteUtil.bytesToBigInteger(rlpHeader.get(9).getRLPData());
        this.v = rlpHeader.get(10).getRLPData();
        this.r = ByteUtil.bytesToBigInteger(rlpHeader.get(11).getRLPData());
        this.s = ByteUtil.bytesToBigInteger(rlpHeader.get(12).getRLPData());
    }

}
