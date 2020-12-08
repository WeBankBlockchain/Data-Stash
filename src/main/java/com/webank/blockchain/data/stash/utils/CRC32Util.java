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


/**
 * CRCUtil
 *
 * @Description: CRCUtil
 * @author graysonzhang
 * @data 2019-07-31 15:44:01
 *
 */
public class CRC32Util {
    
    private static long[] crc32Table = new long[256];

    static {
        long crcValue;
        for (int i = 0; i < 256; i++) {
            crcValue = i;
            for (int j = 0; j < 8; j++) {
                if ((crcValue & 1) == 1) {
                    crcValue = crcValue >> 1;
                    crcValue = 0x00000000edb88320L ^ crcValue;
                } else {
                    crcValue = crcValue >> 1;

                }
            }
            crc32Table[i] = crcValue;
        }
    }
    
    public static long getCrc32(byte[] bytes) {
        long resultCrcValue = 0x00000000ffffffffL;
        for (int i = 0; i < bytes.length; i++) {
            int index = (int) ((resultCrcValue ^ bytes[i]) & 0xff);
            resultCrcValue = crc32Table[index] ^ (resultCrcValue >> 8);
        }
        resultCrcValue = resultCrcValue ^ 0x00000000ffffffffL;
        return resultCrcValue;
    }
    
    
    public static boolean equals(byte[] b, long value){
        long compareValue = getCrc32(b);
        if(compareValue == value){
            return true;
        }else{
            return false;
        }
    }
}
