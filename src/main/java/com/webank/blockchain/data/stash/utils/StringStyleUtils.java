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

import java.util.PrimitiveIterator.OfInt;
import java.util.stream.IntStream;

/**
 * 
 * StringStyleUtils
 *
 * @Description: StringStyleUtils
 * @author graysonzhang
 * @data 2019-08-09 18:03:49
 *
 */
public class StringStyleUtils {

    public static String underline2upper(String str) {
        StringBuilder sb = new StringBuilder();
        boolean mode = false;
        int wordCount = 0;
        IntStream intStream = str.chars();
        OfInt iterator = intStream.iterator();
        while (iterator.hasNext()) {
            int c = iterator.nextInt();
            char cc = (char) c;
            if (mode) {
                if(wordCount == 1 && cc >= 'a' && cc <= 'z'){
                    sb.append((char) cc);
                    mode = false;
                    continue;
                }else if(wordCount != 1 && cc >= 'a' && cc <= 'z') {
                    sb.append(((char) (cc - 32)));
                    mode = false;
                    continue;
                }
                if (cc == '_') {
                    sb.append('_');
                    continue;
                }
                sb.append((char) cc);
                mode = false;
            } else {
                if (cc == '_') {
                    wordCount++;
                    mode = true;
                } else {
                    sb.append((char) cc);
                }
            }
        }
        /*if (mode) {
            sb.append('_');
        }*/
        return sb.toString();
    }

    /*
    public static void main(String[] args) {
        String teString = "_sys_block_2_nonces_detail_";
        System.out.println(underline2upper(teString));
    }

     */
}
