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

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.*;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.Enumeration;

/**
 * 
 * @author 小帅丶
 * @类名称 SM2VerifySign
 * @remark
 * @date 2017-8-16
 * @author maojiayu
 */
@Slf4j
public class SM2VerifySign {
    /**
     * 默认USERID
     */
    public static String USER_ID = "1234567812345678";

    /**
     * 私钥签名 使用SM3进行对明文数据计算一个摘要值
     * 
     * @param privatekey 私钥
     * @param sourceData 明文数据
     * @return 签名后的值
     * @throws Exception
     */
    public static SM2SignVO Sign2SM2(byte[] privatekey, byte[] sourceData) throws Exception {
        SM2SignVO sm2SignVO = new SM2SignVO();
        sm2SignVO.setSm2Type("sign");
        SM2Factory factory = SM2Factory.getInstance();
        BigInteger userD = new BigInteger(1, privatekey);
        log.debug("userD: {}", userD.toString(16));
        sm2SignVO.setUserD(userD.toString(16));

        ECPoint userKey = factory.ecc_point_g.multiply(userD);
        log.debug("椭圆曲线点X: {}", userKey.getXCoord().toBigInteger().toString(16));
        log.debug("椭圆曲线点Y: {}", userKey.getYCoord().toBigInteger().toString(16));
        sm2SignVO.setXCoord(userKey.getXCoord().toBigInteger().toString(16))
                .setYCoord(userKey.getYCoord().toBigInteger().toString(16));

        SM3Digest sm3Digest = new SM3Digest();
        byte[] z = factory.sm2GetZ(USER_ID.getBytes(), userKey);
        log.debug("SM3摘要Z: {}", Hex.toHexString(z));
        log.debug("被加密数据的16进制: {}", Hex.toHexString(sourceData));
        sm2SignVO.setSm3Z(Hex.toHexString(z)).setHexData(Hex.toHexString(sourceData));

        sm3Digest.update(z, 0, z.length);
        sm3Digest.update(sourceData, 0, sourceData.length);
        byte[] md = new byte[32];
        sm3Digest.doFinal(md, 0);
        log.debug("SM3摘要值: {}", Hex.toHexString(md));
        sm2SignVO.setSm3Hash(Hex.toHexString(md));

        SM2Result sm2Result = new SM2Result();
        factory.sm2Sign(md, userD, userKey, sm2Result);
        log.debug("r: {}", sm2Result.getR().toString(16));
        log.debug("s: {}", sm2Result.getS().toString(16));
        sm2SignVO.setSignR(sm2Result.getR().toString(16)).setSignS(sm2Result.getS().toString(16));

        ASN1Integer d_r = new ASN1Integer(sm2Result.getR());
        ASN1Integer d_s = new ASN1Integer(sm2Result.getS());
        ASN1EncodableVector v2 = new ASN1EncodableVector();
        v2.add(d_r);
        v2.add(d_s);
        DERSequence sign = new DERSequence(v2);
        String result = Hex.toHexString(sign.getEncoded());
        sm2SignVO.setSm2Sign(result);
        return sm2SignVO;
    }

    /**
     * 验证签名
     * 
     * @param publicKey 公钥信息
     * @param sourceData 密文信息
     * @param signData 签名信息
     * @return 验签的对象 包含了相关参数和验签结果
     */
    public static SM2SignVO VerifySignSM2(byte[] publicKey, byte[] sourceData, byte[] signData) {
        try {
            byte[] formatedPubKey;
            SM2SignVO verifyVo = new SM2SignVO();
            verifyVo.setSm2Type("verify");
            if (publicKey.length == 64) {
                // 添加一字节标识，用于ECPoint解析
                formatedPubKey = new byte[65];
                formatedPubKey[0] = 0x04;
                System.arraycopy(publicKey, 0, formatedPubKey, 1, publicKey.length);
            } else {
                formatedPubKey = publicKey;
            }
            SM2Factory factory = SM2Factory.getInstance();
            ECPoint userKey = factory.ecc_curve.decodePoint(formatedPubKey);
            SM3Digest sm3Digest = new SM3Digest();
            byte[] z = factory.sm2GetZ(USER_ID.getBytes(), userKey);
            log.debug("SM3摘要Z: {}", Hex.toHexString(z));
            verifyVo.setSm3Z(Hex.toHexString(z));
            sm3Digest.update(z, 0, z.length);
            sm3Digest.update(sourceData, 0, sourceData.length);
            byte[] md = new byte[32];
            sm3Digest.doFinal(md, 0);
            log.debug("SM3摘要值: {}", Hex.toHexString(md));
            verifyVo.setSm3Hash(Hex.toHexString(md));
            try (ByteArrayInputStream bis = new ByteArrayInputStream(signData);
                    ASN1InputStream dis = new ASN1InputStream(bis);) {
                ASN1Primitive derObj = dis.readObject();
                @SuppressWarnings("unchecked")
                Enumeration<ASN1Integer> e = ((ASN1Sequence) derObj).getObjects();
                BigInteger r = ((ASN1Integer) e.nextElement()).getValue();
                BigInteger s = ((ASN1Integer) e.nextElement()).getValue();
                SM2Result sm2Result = new SM2Result().setR(r).setS(s);
                log.debug("vr: {}", sm2Result.getR().toString(16));
                log.debug("vs: {}", sm2Result.getS().toString(16));
                verifyVo.setVerifyR(sm2Result.getR().toString(16)).setVerifyS(sm2Result.getS().toString(16));
                factory.sm2Verify(md, userKey, sm2Result.getR(), sm2Result.getS(), sm2Result);
                boolean verifyFlag = sm2Result.getR().equals(sm2Result.getR2());
                verifyVo.setVerifyBoolean(verifyFlag);
            }
            return verifyVo;
        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
            return null;
        }
    }
}
