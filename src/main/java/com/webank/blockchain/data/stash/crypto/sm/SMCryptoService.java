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

import com.webank.blockchain.data.stash.crypto.CyptoInterface;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.fisco.bcos.sdk.crypto.hash.Hash;
import org.fisco.bcos.sdk.crypto.hash.SM3Hash;
import org.fisco.bcos.sdk.crypto.signature.SM2Signature;
import org.fisco.bcos.sdk.crypto.signature.Signature;

import java.io.IOException;

/**
 * CryptoService
 *
 * @Description: CryptoService
 * @author maojiayu
 * @data Dec 23, 2019 4:59:45 PM
 *
 */
@Slf4j
public class SMCryptoService implements CyptoInterface {

    private Hash sm3Hash = new SM3Hash();

    private Signature sm2Signature = new SM2Signature();

    @Override
    public byte[] hash(byte[] src) {
        return sm3Hash.hash(src);
    }

    @Override
    public String hash(String src) {
        return sm3Hash.hash(src);
    }

    @Override
    public boolean verify(byte[] data, String signature, String nodeKey) throws IOException {
        return sm2Signature.verify(nodeKey, Hex.toHexString(data), signature);
    }
}
