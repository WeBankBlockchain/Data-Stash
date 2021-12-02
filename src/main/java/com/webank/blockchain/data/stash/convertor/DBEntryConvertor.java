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
package com.webank.blockchain.data.stash.convertor;

import com.webank.blockchain.data.stash.constants.DBDynamicTableConstants;
import com.webank.blockchain.data.stash.constants.DataStorageTypeConstants;
import com.webank.blockchain.data.stash.db.model.BaseInfo;
import com.webank.blockchain.data.stash.entity.EntryInfo;
import com.webank.blockchain.data.stash.utils.ObjectBuildUtil;
import org.springframework.stereotype.Service;

/**
 * EntryConvertHandler
 *
 * @Description: EntryConvertHandler
 * @author graysonzhang
 * @data 2019-08-09 15:16:31
 *
 */
@Service
public class DBEntryConvertor {

    @SuppressWarnings("unchecked")
    public <T extends BaseInfo> T convert(T record, EntryInfo entry, String tableName) {
        record.setId(entry.getId());
        if(entry.getHash() == null){
            record.setHash(DataStorageTypeConstants.DEFALT_HASH);
        }else{
            record.setHash(entry.getHash());
        }
        
        record.setStatus(entry.getStatus());
        record.setNum(entry.getNum());

        if (tableName.startsWith(DBDynamicTableConstants.SYS_TABLE_PRE_FIX)
                || tableName.startsWith(DBDynamicTableConstants.CONTRAACT_PARAFUNC_FIX)
                || tableName.startsWith(DBDynamicTableConstants.CONTRACT_DATA_PRE_FIX)) {
            return (T) ObjectBuildUtil.buildToStaticObj(entry, record);
        } else {
            return (T) ObjectBuildUtil.buildToDynamicObj(entry, record);
        }
    }
}
