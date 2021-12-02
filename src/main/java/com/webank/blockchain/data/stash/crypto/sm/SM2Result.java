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
package com.webank.blockchain.data.stash.crypto.sm;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

/**
 * SM2Result
 *
 * @Description: SM2Result
 * @author maojiayu
 * @data Dec 27, 2019 3:11:24 PM
 *
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class SM2Result {
    // 签名r
    private BigInteger r;
    private BigInteger s;
    // 验签R
    private BigInteger R2;
    // 密钥交换
    private byte[] sa;
    private byte[] sb;
    private byte[] s1;
    private byte[] s2;
    private ECPoint ECPointRa;
    private ECPoint ECPointRb;
}
