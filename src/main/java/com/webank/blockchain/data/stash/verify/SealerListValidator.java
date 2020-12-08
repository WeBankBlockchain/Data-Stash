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
package com.webank.blockchain.data.stash.verify;

import java.math.BigInteger;
import java.util.List;

import com.webank.blockchain.data.stash.db.dao.SysConsensusInfoMapper;
import com.webank.blockchain.data.stash.db.model.SysConsensusInfo;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.blockchain.data.stash.db.dao.SysConsensusInfoMapper;
import com.webank.blockchain.data.stash.db.model.SysConsensusInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * SealerListValidator
 *
 * @Description: SealerListValidator
 * @author maojiayu
 * @data Sep 11, 2019 4:57:23 PM
 *
 */
@Service
@Slf4j
public class SealerListValidator {
    @Autowired
    private SysConsensusInfoMapper sysConsensusInfoMapper;

    public boolean validSealerList(List<byte[]> sealerList, BigInteger blockNumber) {
        for (byte[] sealer : sealerList) {
            String s = Hex.toHexString(sealer);
            SysConsensusInfo info = sysConsensusInfoMapper.selectByNodeIdOrderByNumDescLimit(s);
            if (info == null) {
                log.error("Validate sealer list error, sealer: {} ", s);
                return false;
            }
            if (info.getStatus() != 0) {
                log.error("Validate sealer list error, not a valid seal node: {}", s);
            }
            BigInteger i = new BigInteger(info.getEnableNum());
            if (i.compareTo(blockNumber) > 0 && blockNumber.compareTo(BigInteger.ZERO) > 0) {
                log.error("Enable number {} is bigger than block Number {}", i, blockNumber);
                return false;
            }
        }
        return true;
    }

}
