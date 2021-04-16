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
import java.util.Date;
import java.util.List;

import com.webank.blockchain.data.stash.aspect.UseTime;
import com.webank.blockchain.data.stash.block.BlockV2RC2;
import com.webank.blockchain.data.stash.block.TransactionDetail;
import com.webank.blockchain.data.stash.db.dao.SysHash2BlockInfoMapper;
import com.webank.blockchain.data.stash.db.mapper.BlockTaskPoolMapper;
import com.webank.blockchain.data.stash.db.model.BlockTaskPool;
import com.webank.blockchain.data.stash.db.model.SysHash2BlockInfo;
import com.webank.blockchain.data.stash.parser.BlockRlpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * ValidatorController
 *
 * @Description: ValidatorController
 * @author maojiayu
 * @data Sep 20, 2019 6:27:26 PM
 *
 */
@Service
@Slf4j
public class ValidatorController {
    @Autowired
    private SealerListValidator sealerListValidator;
    @Autowired
    private TransactionValidator transactionValidator;
    @Autowired
    private BlockTaskPoolMapper blockTaskPoolMapper;
    @Autowired
    private SysHash2BlockInfoMapper sysHash2BlockInfoMapper;

    @Autowired
    private BlockHeaderValidator blockHeaderValidator;
    @Autowired
    private BlockValidator blockValidator;
    @Autowired
    private TransationReceiptValidator transationReceiptValidator;

    @UseTime
    public boolean validateBlockRlp(String blockRlp) throws Exception {
        /*
        BlockV2RC2 block = new BlockV2RC2(blockRlp);
        BigInteger blockNumber = BigInteger.valueOf(blockTaskPoolMapper.getLatestOne().getBlockHeight());
        BigInteger parentBlockNumber = blockNumber.subtract(BigInteger.ONE);

        if (!blockValidator.validateSigList(block)) {
            log.error("blockRlp sigList check Error: {}", blockRlp);
            return false;
        }

        if (!sealerListValidator.validSealerList(block.getBlockHeader().getSealerList(), blockNumber)) {
            log.error("blockRlp sealerList check Error: {}", blockRlp);
            return false;
        }
        if (blockNumber.compareTo(BigInteger.ZERO) > 0) {
            List<TransactionDetail> txs = BlockRlpParser.parseTransactionDetail(block.getTransactions());
            if (!transactionValidator.validBlockLimit(txs, blockNumber)) {
                log.error("blockRlp transactions check Error: {}", blockRlp);
                return false;
            }

            if (!transactionValidator.validTxCountLimit(txs)) {
                log.error("blockRlp transactions count check Error: {}", blockRlp);
                return false;
            }

            BlockTaskPool b = blockTaskPoolMapper.getByBlockHeight(parentBlockNumber.longValue());
            if (!BlockHeaderValidator.validTimestamp(block.getBlockHeader().getTimestamp().longValue(),
                    b.getBlockTime().getTime())) {
                log.error("blockRlp timestamp check Error: {}", b.getBlockTime());
                return false;
            }

            log.info("Parent block number is {}, current number is {}", parentBlockNumber,
                    block.getBlockHeader().getNumber());
            SysHash2BlockInfo h = sysHash2BlockInfoMapper.selectByBlockNumber(parentBlockNumber.longValue());
            if (!BlockHeaderValidator.validParentHash(block, h.getHash())) {
                log.error("blockRlp parentHash check Error: {}", h.getHash());
                return false;
            }

            if (!TransationReceiptValidator.validTransactionReceiptStateRoot(block)) {
                log.error("TransactionReceipt stateRoot check Error");
                return false;
            }
        }

        blockTaskPoolMapper.updateBlockTimestampByBlockHeight(
                new Date(block.getBlockHeader().getTimestamp().longValue()),
                block.getBlockHeader().getNumber().longValue());

        if (!blockHeaderValidator.validBlockHeader(block)) {
            log.error("blockRlp block header check Error: {}", blockRlp);
            return false;
        }

        if (!BlockHeaderValidator.validBlockNumber(block.getBlockHeader().getNumber(), parentBlockNumber)) {
            log.error("blockRlp sigList check Error: {}", blockRlp);
            return false;
        }

         */

        return true;

    }

}
