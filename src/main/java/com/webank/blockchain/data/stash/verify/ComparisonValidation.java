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

import com.webank.blockchain.data.stash.aspect.UseTime;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.entity.EntryInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.parser.BlockBytesParser;
import com.webank.blockchain.data.stash.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * BlockValidation
 *
 * @Description: BlockValidation
 * @author graysonzhang
 * @author maojiayu
 * @data 2019-07-31 10:26:44
 *
 */
@Service
@Slf4j
public class ComparisonValidation {

    @Autowired
    private BlockBytesParser parser;
    @UseTime
    public boolean compareValidate(BinlogBlockInfo blockInfo, List<byte[]> blockBytesList){
        try{
            for (int i = 1; i < blockBytesList.size(); i++) {
                if (!compare(blockInfo, blockBytesList.get(i))) {
                    log.error("Compare binlog 0 and binlog of {}, something error", i);
                    return false;
                }
            }
            return true;
        }
        catch (Exception ex){
            throw new DataStashException(ex);
        }

    }

    private boolean compare(BinlogBlockInfo blockInfo, byte[] blockBytes) throws Exception {
        BinlogBlockInfo tempBlockInfo = parser.getBinlogBlockInfo(blockBytes);
        // 1. compare block num
        if (blockInfo.getBlockNum() != tempBlockInfo.getBlockNum()) {
            log.error("block num not equal : {}, {}", blockInfo.getBlockNum(), tempBlockInfo.getBlockNum());
            return false;
        }

        // 2. compare data count
        if (blockInfo.getDataCount() != tempBlockInfo.getDataCount()) {
            log.error("block data count not equal : {}, {}", blockInfo.getDataCount(), tempBlockInfo.getDataCount());
            return false;
        }

        // 3. compare table datas
        if (!compareTableDatas(blockInfo.getTables(), tempBlockInfo.getTables())) {
            log.error("block {} table data not equal", blockInfo.getBlockNum());
            return false;
        }

        return true;
    }

    @UseTime
    private boolean compareTableDatas(Map<String, TableDataInfo> tables1, Map<String, TableDataInfo> tables2) throws Exception{

        for (Map.Entry<String, TableDataInfo> table : tables1.entrySet()) {

            if (!tables2.containsKey(table.getKey())) {
                log.error("table key is not find, {} ", table.getKey());
                return false;
            }
            log.debug("start process table : {}", table.getKey());
            // compare dirty entry
            if (!compareEntrys(table.getKey(), table.getValue().getDirtyEntrys(),
                    tables2.get(table.getKey()).getDirtyEntrys())) {
                log.error("Dirty entries are not equal.");
                return false;
            }

            // compare new entry
            if (!compareEntrys(table.getKey(), table.getValue().getNewEntrys(),
                    tables2.get(table.getKey()).getNewEntrys())) {
                log.error("New entries are not equal.");
                return false;
            }
        }

        return true;
    }

    private boolean compareEntrys(String tableName, TreeSet<EntryInfo> entries1, TreeSet<EntryInfo> entries2) throws Exception
              {
        log.debug("Begin to compare entries of {}", tableName);
        log.debug("entry1 : {}", JsonUtils.toJson(entries1));
        log.debug("entry2 : {}", JsonUtils.toJson(entries2));

        boolean firstNullFlag = false;
        boolean secondNullFlag = false;

        if (entries1 == null || entries1.size() == 0) {
            firstNullFlag = true;
        }

        if (entries2 == null || entries2.size() == 0) {
            secondNullFlag = true;
        }

        if (firstNullFlag && secondNullFlag)
            return true;
        if (firstNullFlag ^ secondNullFlag) {
            return false;
        }

        if (entries1.size() != entries2.size()) {
            log.error("Entries size is not equal, {}, {}", entries1.size(), entries2.size());
            return false;
        }

        if (tableName.equals(DBStaticTableConstants.SYS_HASH_2_HEADER_TABLE)) {
            if (entries1.size() != 1) {
                return false;
            }
            EntryInfo entry1 = entries1.first();
            EntryInfo entry2 = entries2.first();

            //Each block headers may collect multiple sigs
            if (!entry1.removeFieldEquals(entry2, "sigs")) {
                log.error("Remove value not equal.");
                return false;
            }
        }
        else if (tableName.equals(DBStaticTableConstants.SYS_HASH_2_BLOCK_TABLE)) {
            if (entries1.size() != 1) {
                return false;
            }
            EntryInfo entry1 = entries1.first();
            EntryInfo entry2 = entries2.first();

            if (!entry1.removeValueEquals(entry2)) {
                log.error("Remove value not equal.");
                return false;
            }

        }
        else if (tableName.equalsIgnoreCase(DBStaticTableConstants.SYS_TX_HASH_2_BLOCK_TABLE)) {
            // the id of SYS_TX_HASH_2_BLOCK_TABLE is not equal in different binlog file since from 2.2
            TreeSet<String> hashSet1 = entries1.stream().map(e -> e.getHash()).filter(s -> s != null)
                    .collect(Collectors.toCollection(TreeSet::new));
            TreeSet<String> hashSet2 = entries2.stream().map(e -> e.getHash()).filter(s -> s != null)
                    .collect(Collectors.toCollection(TreeSet::new));
            if (!(CollectionUtils.isEmpty(hashSet1) && CollectionUtils.isEmpty(hashSet2))
                    || !CollectionUtils.isEqualCollection(hashSet1, hashSet2)) {
                log.error("hashes are not equal in SYS_HASH_2_BLOCK_TABLE");
                return false;
            }

        } else {
            Iterator<EntryInfo> iter1 = entries1.iterator();
            Iterator<EntryInfo> iter2 = entries2.iterator();
            while (iter1.hasNext()) {
                EntryInfo entry1 = iter1.next();
                EntryInfo entry2 = iter2.next();
                if (!entry1.equals(entry2)){
                    return false;
                }
            }
        }


        log.debug("end process table : {}", tableName);
        return true;
    }

}
