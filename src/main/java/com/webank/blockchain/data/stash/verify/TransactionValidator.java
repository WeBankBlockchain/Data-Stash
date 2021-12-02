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

import com.webank.blockchain.data.stash.block.TransactionDetail;
import com.webank.blockchain.data.stash.db.dao.SysConfigInfoMapper;
import com.webank.blockchain.data.stash.db.model.SysConfigInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * TransactionValidator
 *
 * @Description: TransactionValidator
 * @author maojiayu
 * @data Sep 14, 2019 8:06:53 PM
 *
 */
@Slf4j
@Service
public class TransactionValidator {
    @Autowired
    private SysConfigInfoMapper sysConfigInfoMapper;

    public static final String TX_COUNT_LIMIT = "tx_count_limit";

    public boolean validBlockLimit(List<TransactionDetail> list, BigInteger blockNumber) {
        for (TransactionDetail td : list) {
            if (td.getBlockLimit().compareTo(blockNumber) < 0) {
                log.error("Transaction block limit {} is less than {}", td.getBlockLimit(), blockNumber);
                return false;
            }
        }
        return true;
    }

    public boolean validTxCountLimit(List<TransactionDetail> list) {
        SysConfigInfo config = sysConfigInfoMapper.selectByKey(TX_COUNT_LIMIT);
        log.debug("Tx count limit is {}", config.getValue());
        long size = list.size();
        if (size > Long.parseLong(config.getValue())) {
            log.error("Valid tx count limit error, required is {}, but actuall is {}", config.getValue(), size);
        }
        return true;
    }

}
