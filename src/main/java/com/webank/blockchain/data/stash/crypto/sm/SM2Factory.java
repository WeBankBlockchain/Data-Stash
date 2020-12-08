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

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECFieldElement.Fp;
import org.bouncycastle.math.ec.ECPoint;

import com.webank.blockchain.data.stash.rlp.ByteUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * SM2Factory
 * 
 * @author 小帅丶
 * @类名称 SM2Factory
 * @date 2017-8-16
 * @author maojiayu
 */
@Slf4j
public class SM2Factory {
    // A 第一系数
    private static final BigInteger a =
            new BigInteger("fffffffeffffffffffffffffffffffffffffffff00000000fffffffffffffffc", 16);
    // B 第二系数
    private static final BigInteger b =
            new BigInteger("28e9fa9e9d9f5e344d5a9e4bcf6509a7f39789f515ab8f92ddbcbd414d940e93", 16);
    // 曲线X系数
    private static final BigInteger gx =
            new BigInteger("32c4ae2c1f1981195f9904466a39c9948fe30bbff2660be1715a4589334c74c7", 16);
    // 曲线Y系数
    private static final BigInteger gy =
            new BigInteger("bc3736a2f4f6779c59bdcee36b692153d0a9877cc62a474002df32e52139f0a0", 16);
    // 生产者顺序系数
    private static final BigInteger n =
            new BigInteger("fffffffeffffffffffffffffffffffff7203df6b21c6052b53bbf40939d54123", 16);
    // 素数
    private static final BigInteger p =
            new BigInteger("fffffffeffffffffffffffffffffffffffffffff00000000ffffffffffffffff", 16);
    // 因子系数 1
    // private static final int h = 1;
    public final ECFieldElement ecc_gx_fieldelement;
    public final ECFieldElement ecc_gy_fieldelement;
    public final ECCurve ecc_curve;
    public final ECPoint ecc_point_g;
    public final ECDomainParameters ecc_bc_spec;
    public final ECKeyPairGenerator ecc_key_pair_generator;

    /**
     * 初始化方法
     * 
     * @return
     */
    public static SM2Factory getInstance() {
        return new SM2Factory();
    }

    @SuppressWarnings("deprecation")
    public SM2Factory() {

        this.ecc_gx_fieldelement = new Fp(p, gx);
        this.ecc_gy_fieldelement = new Fp(p, gy);

        this.ecc_curve = new ECCurve.Fp(p, a, b);

        this.ecc_point_g = new ECPoint.Fp(this.ecc_curve, this.ecc_gx_fieldelement, this.ecc_gy_fieldelement, false);
        this.ecc_bc_spec = new ECDomainParameters(ecc_curve, ecc_point_g, n);

        ECKeyGenerationParameters ecc_ecgenparam;
        ecc_ecgenparam = new ECKeyGenerationParameters(this.ecc_bc_spec, new SecureRandom());

        this.ecc_key_pair_generator = new ECKeyPairGenerator();
        this.ecc_key_pair_generator.init(ecc_ecgenparam);
    }

    /**
     * 根据私钥、曲线参数计算Z
     * 
     * @param userId
     * @param userKey
     * @return
     */
    public byte[] sm2GetZ(byte[] userId, ECPoint userKey) {
        SM3Digest sm3 = new SM3Digest();

        int len = userId.length * 8;
        sm3.update((byte) (len >> 8 & 0xFF));
        sm3.update((byte) (len & 0xFF));
        sm3.update(userId, 0, userId.length);

        byte[] p = ByteUtil.byteConvert32Bytes(a);
        sm3.update(p, 0, p.length);

        p = ByteUtil.byteConvert32Bytes(b);
        sm3.update(p, 0, p.length);

        p = ByteUtil.byteConvert32Bytes(gx);
        sm3.update(p, 0, p.length);

        p = ByteUtil.byteConvert32Bytes(gy);
        sm3.update(p, 0, p.length);

        p = ByteUtil.byteConvert32Bytes(userKey.normalize().getXCoord().toBigInteger());
        sm3.update(p, 0, p.length);

        p = ByteUtil.byteConvert32Bytes(userKey.normalize().getYCoord().toBigInteger());
        sm3.update(p, 0, p.length);

        byte[] md = new byte[sm3.getDigestSize()];
        sm3.doFinal(md, 0);
        return md;
    }

    /**
     * 签名相关值计算
     * 
     * @param md
     * @param userD
     * @param userKey
     * @param sm2Result
     */
    public void sm2Sign(byte[] md, BigInteger userD, ECPoint userKey, SM2Result sm2Result) {
        BigInteger e = new BigInteger(1, md);
        BigInteger k = null;
        ECPoint kp = null;
        BigInteger r = null;
        BigInteger s = null;
        do {
            do {
                // 正式环境
                AsymmetricCipherKeyPair keypair = ecc_key_pair_generator.generateKeyPair();
                ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) keypair.getPrivate();
                ECPublicKeyParameters ecpub = (ECPublicKeyParameters) keypair.getPublic();
                k = ecpriv.getD();
                kp = ecpub.getQ();
                log.debug("BigInteger: {} \\nECPoint: {}", k, kp);
                log.debug("计算曲线点X1: {}", kp.getXCoord().toBigInteger().toString(16));
                log.debug("计算曲线点Y1: {}", kp.getYCoord().toBigInteger().toString(16));
                r = e.add(kp.getXCoord().toBigInteger());
                r = r.mod(n);
            } while (r.equals(BigInteger.ZERO) || r.add(k).equals(n));

            // (1 + dA)~-1
            BigInteger da_1 = userD.add(BigInteger.ONE);
            da_1 = da_1.modInverse(n);
            // s
            s = r.multiply(userD);
            s = k.subtract(s).mod(n);
            s = da_1.multiply(s).mod(n);
        } while (s.equals(BigInteger.ZERO));

        sm2Result.setR(r).setS(s);
    }

    /**
     * 验签
     * 
     * @param md sm3摘要
     * @param userKey 根据公钥decode一个ecpoint对象
     * @param r 没有特殊含义
     * @param s 没有特殊含义
     * @param sm2Result 接收参数的对象
     */
    public void sm2Verify(byte md[], ECPoint userKey, BigInteger r, BigInteger s, SM2Result sm2Result) {
        sm2Result.setR2(null);
        BigInteger e = new BigInteger(1, md);
        //System.out.println("e: " + e);
        BigInteger t = r.add(s).mod(n);
        if (t.equals(BigInteger.ZERO)) {
        } else {
            ECPoint x1y1 = ecc_point_g.multiply(sm2Result.getS());
            log.debug("计算曲线点X0: {}", x1y1.normalize().getXCoord().toBigInteger().toString(16));
            log.debug("计算曲线点Y0: {}", x1y1.normalize().getYCoord().toBigInteger().toString(16));
            x1y1 = x1y1.add(userKey.multiply(t));
            log.debug("计算曲线点X1: {}", x1y1.normalize().getXCoord().toBigInteger().toString(16));
            log.debug("计算曲线点Y1: {}", x1y1.normalize().getYCoord().toBigInteger().toString(16));
            sm2Result.setR2(e.add(x1y1.normalize().getXCoord().toBigInteger()).mod(n));
            //System.out.println("R: " + sm2Result.getR2());
            log.debug("R: {}", sm2Result.getR2().toString(16));
        }
    }

}
