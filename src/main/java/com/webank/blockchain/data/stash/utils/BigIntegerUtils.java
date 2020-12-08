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
package com.webank.blockchain.data.stash.utils;

import org.fisco.bcos.sdk.utils.Numeric;

import java.math.BigInteger;


/**
 * @Description BigIntegerUtils
 * @author yuzhichu
 * @date 2019-12-11 
 */
public abstract class BigIntegerUtils {

    public static BigInteger fromBytesUnsigned(byte[] bytes) {
        return new BigInteger(1, bytes);
    }
    
    public static BigInteger fromHexUnsigned(String hex) {
        return new BigInteger(hex, 16);
    }
    
    /**
     * Use this instead of using toByteArray() which may contains a sign byte
     */
    public static byte[] toByteUnsigned(BigInteger d) {
        String hex = toHexUnsigned(d);
        return Numeric.hexStringToByteArray(hex);
    }
    
    public static String toHexUnsigned(BigInteger d) {
        return d.toString(16);
    }
    
    
}












