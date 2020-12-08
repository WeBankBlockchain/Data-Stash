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
package com.webank.blockchain.data.stash.crypto;

import java.security.KeyPair;
import java.security.PublicKey;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;

/**
 * KeyToolTest
 *
 * @Description: KeyToolTest
 * @author maojiayu
 * @data Dec 25, 2019 2:34:30 PM
 *
 */
public class KeyToolTest {
    public static String pk =
            "04e8c670380cb220095268f40221fc748fa6ac39d6e930e63c30da68bad97f885da6e8c9ad722c3683ab859393220d1431eb1818ed44a942efb07b261a0fc769e7";

    public static void main(String[] args) {
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        PublicKey publicKey = pair.getPublic();
        byte[] data = KeyUtil.encodeECPublicKey(publicKey);
        System.out.println(Hex.toHexString(data));
        String encodeHex = HexUtil.encodeHexStr(data);
        String encodeB64 = Base64.encode(data);
        System.out.println(publicKey.getFormat());
        System.out.println(encodeHex);
        System.out.println(encodeB64);
        PublicKey Hexdecode = KeyUtil.decodeECPoint(encodeHex, KeyUtil.SM2_DEFAULT_CURVE);
        PublicKey B64decode = KeyUtil.decodeECPoint(encodeB64, KeyUtil.SM2_DEFAULT_CURVE);
        Assert.assertEquals(HexUtil.encodeHexStr(publicKey.getEncoded()), HexUtil.encodeHexStr(Hexdecode.getEncoded()));
        Assert.assertEquals(HexUtil.encodeHexStr(publicKey.getEncoded()), HexUtil.encodeHexStr(B64decode.getEncoded()));
        
        //BCUtil.decodeECPoint(Base64.encode(pk), "sm2p256v1");
        
        PublicKey p1 = KeyUtil.decodeECPoint(pk, KeyUtil.SM2_DEFAULT_CURVE);
        System.out.println(Hex.toHexString(p1.getEncoded()));
        System.out.println(p1.getFormat());

    }

}
