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
package com.webank.blockchain.data.stash.parser;

import java.util.*;

import com.webank.blockchain.data.stash.constants.DBDynamicTableConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.webank.blockchain.data.stash.aspect.UseTime;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.entity.ColumnInfo;
import com.webank.blockchain.data.stash.entity.EntryInfo;
import com.webank.blockchain.data.stash.entity.RtnObjInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.utils.BytesUtil;
import com.webank.blockchain.data.stash.utils.JsonUtils;

import cn.hutool.core.util.HexUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * BlockBytesParser
 *
 * @Description: BlockBytesParser
 * @author graysonzhang
 * @data 2019-08-29 17:55:11
 *
 */
@Service
@Slf4j
public class BlockBytesParser {

    @UseTime
    public BinlogBlockInfo getBinlogBlockInfo(byte[] blockBytes) throws DataStashException {

        BinlogBlockInfo binlogBlockInfo = new BinlogBlockInfo();

        // 1. get block number
        long blockNum = BytesUtil.byte8FromSrcToLong(blockBytes, 0);
        binlogBlockInfo.setBlockNum(blockNum);
        log.debug("block bytes parser num : {}", blockNum);
        // 2. get data count
        int dataCount = BytesUtil.byte4FromSrcToInt(blockBytes, 8);
        binlogBlockInfo.setDataCount(dataCount);

        int dataIndex = 12;
        log.debug("data count : {}", dataCount);
        for (int i = 0; i < dataCount; i++) {

            int nameLen = blockBytes[dataIndex] & 0xFF;

            byte[] tableNameBytes = BytesUtil.subBytes(blockBytes, dataIndex + 1, nameLen);
            String tableName = new String(tableNameBytes);
            log.debug("tableName: {}", tableName);

            RtnObjInfo<TableDataInfo> blockData = processBlockData(tableName, blockBytes, dataIndex, blockNum, nameLen);

            binlogBlockInfo.getTables().put(tableName, blockData.getObj());

            dataIndex = blockData.getIndex();
        }

        return binlogBlockInfo;
    }

    private RtnObjInfo<TableDataInfo> processBlockData(String tableName, byte[] blockBytes, int dataIndex,
            long blockNum, int tableNameLen) throws DataStashException {

        RtnObjInfo<TableDataInfo> blockDataRtn = new RtnObjInfo<>();

        // 1. get table fields
        int fieldsLen = BytesUtil.byte4FromSrcToInt(blockBytes, dataIndex + tableNameLen + 1);
        String[] fields = fieldsProcess(blockBytes, dataIndex + tableNameLen + 5, fieldsLen);

        // 2. process dirty entry
        int dirtyEntryCount = BytesUtil.byte4FromSrcToInt(blockBytes, dataIndex + tableNameLen + fieldsLen + 5);
        log.debug("block height {}, dirtyEntryCount: {}", blockNum, dirtyEntryCount);

        int entryIndex = dataIndex + tableNameLen + fieldsLen + 9;

        TreeSet<EntryInfo> dirtyEntrys = new TreeSet<>();
        for (int i = 0; i < dirtyEntryCount; i++) {
            RtnObjInfo<EntryInfo> entryData = processEntry(tableName, fields, blockBytes, entryIndex, blockNum);
            dirtyEntrys.add(entryData.getObj());
            entryIndex = entryData.getIndex();
        }

        // 3. process new entry
        int newEntryCount = BytesUtil.byte4FromSrcToInt(blockBytes, entryIndex);
        log.debug("block height {}, newEntryCount: {}", blockNum, newEntryCount);

        entryIndex += 4;

        TreeSet<EntryInfo> newEntrys = new TreeSet<>();
        for (int i = 0; i < newEntryCount; i++) {
            RtnObjInfo<EntryInfo> entryData = processEntry(tableName, fields, blockBytes, entryIndex, blockNum);
            newEntrys.add(entryData.getObj());
            entryIndex = entryData.getIndex();
        }

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setDirtyEntrys(dirtyEntrys);
        tableDataInfo.setNewEntrys(newEntrys);

        blockDataRtn.setObj(tableDataInfo);
        blockDataRtn.setIndex(entryIndex);

        return blockDataRtn;

    }

    private RtnObjInfo<EntryInfo> processEntry(String tableName, String[] fields, byte[] binlogBytes, int entryIndex,
            long blockNum) throws DataStashException {

        RtnObjInfo<EntryInfo> entryData = new RtnObjInfo<>();
        EntryInfo entry = new EntryInfo();

        // 1. get entry Id
        long id = BytesUtil.byte8FromSrcToLong(binlogBytes, entryIndex);
        entry.setId(id);

        // 2. get entry status
        int status = binlogBytes[entryIndex + 8] & 0xFF;
        entry.setStatus(status);

        // 3. set block num
        entry.setNum(blockNum);

        // 4. handle usedFlags
        int fieldsLength = fields.length - 3;
        int usedFlagBytesCount = (int)Math.ceil(fieldsLength/8.0);
        // 5. get other column value
        int valueIndex = entryIndex + 9 + usedFlagBytesCount;
        List<ColumnInfo> columns = new ArrayList<ColumnInfo>();
        List<byte[]> columnBytes = new ArrayList<byte[]>();
        Map<String, ColumnInfo> entryContext = new HashMap<>();
        for (int i = 0; i < fieldsLength; i++) {
            int valueLen = BytesUtil.byte4FromSrcToInt(binlogBytes, valueIndex);
            byte[] b = BytesUtil.subBytes(binlogBytes, valueIndex + 4, valueLen);
            String value = new String(b);
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.setColumnName(fields[i]);
            columnInfo.setColumnValue(value);

            columns.add(columnInfo);
            columnBytes.add(b);
            entryContext.put(fields[i], columnInfo);

            valueIndex = valueIndex + valueLen + 4;
        }

        //6. Convert binary data
        for (int i = 0; i < fieldsLength; i++) {
            ColumnInfo columnInfo = columns.get(i);
            byte[] b = columnBytes.get(i);
            if (isBinaryField(tableName, fields[i], entryContext)){
                columnInfo.setColumnValue(HexUtil.encodeHexStr(b));
            }
        }

        entry.setColumns(columns);
        entryData.setObj(entry);
        entryData.setIndex(valueIndex);

        return entryData;
    }

    private boolean isBinaryField(String tableName, String fieldName, Map<String, ColumnInfo> entryContext){
        String utf8FieldValue = entryContext.get(fieldName).getColumnValue();
        if (tableName.equalsIgnoreCase("_sys_hash_2_block_")
                || tableName.equalsIgnoreCase("_sys_block_2_nonces_")
        ){
            if(fieldName.equalsIgnoreCase("value")){
                return !StringUtils.startsWithIgnoreCase(utf8FieldValue, "0x");
            }
        }

        if (tableName.equalsIgnoreCase("_sys_hash_2_header_")){
            if(fieldName.equalsIgnoreCase("value") || fieldName.equalsIgnoreCase("sigs")
                    ){
                return !StringUtils.startsWithIgnoreCase(utf8FieldValue, "0x");
            }
        }

        if(tableName.startsWith(DBDynamicTableConstants.CONTRACT_DATA_PRE_FIX) && fieldName.equalsIgnoreCase("value")){
            ColumnInfo keyColumn = entryContext.get("key");
            if(keyColumn == null) return false;
            String keyColumnVal = keyColumn.getColumnValue();
            if("code".equalsIgnoreCase(keyColumnVal) || "codeHash".equalsIgnoreCase(keyColumnVal)){
                return !StringUtils.startsWithIgnoreCase(utf8FieldValue, "0x");
            }
        }
        return false;
    }

    private String[] fieldsProcess(byte[] binlogBytes, int fieldsIndex, int fieldsLen) {
        String fieldsStr = new String(BytesUtil.subBytes(binlogBytes, fieldsIndex, fieldsLen));
        log.debug("fieldsStr : {}", JsonUtils.toJson(fieldsStr));
        return fieldsStr.split(",");
    }
}
