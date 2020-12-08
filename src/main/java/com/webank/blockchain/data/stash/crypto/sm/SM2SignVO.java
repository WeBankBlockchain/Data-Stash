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

/**
 * SM2SignVO
 *
 * @Description: SM2SignVO
 * @author maojiayu
 * @data Dec 27, 2019 3:14:45 PM
 *
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class SM2SignVO {
    // 16进制的私钥
    private String userD;
    // 椭圆曲线点X
    private String xCoord;
    // 椭圆曲线点Y
    private String yCoord;
    // SM3摘要Z
    private String sm3Z;
    // 明文数据16进制
    private String hexData;
    // SM3摘要值
    private String sm3Hash;
    // R
    private String signR;
    // S
    private String signS;
    // R
    private String verifyR;
    // S
    private String verifyS;
    // 签名值
    private String sm2Sign;
    // sign 签名 verfiy验签
    private String sm2Type;
    // 是否验签成功 true false
    private boolean verifyBoolean;
}
