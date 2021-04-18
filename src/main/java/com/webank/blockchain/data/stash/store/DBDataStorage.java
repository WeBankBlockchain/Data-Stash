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
package com.webank.blockchain.data.stash.store;

import java.sql.SQLException;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.webank.blockchain.data.stash.db.face.DataStorage;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.utils.StringStyleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.webank.blockchain.data.stash.aspect.UseTime;
import com.webank.blockchain.data.stash.constants.DBDynamicTableConstants;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DBStorageHandler
 *
 * @Description: DBStorageHandler
 * @author graysonzhang
 * @data 2019-08-10 00:58:28
 *
 */
@Slf4j
public class DBDataStorage implements DataStorage {

    @Autowired
    private Map<String, StorageService> storageServices;

    @PostConstruct
    @Transactional
    public void initSchema() throws SQLException {
        for(Map.Entry<String, StorageService> entry : storageServices.entrySet()){
            entry.getValue().createSchema();
        }
    }

    @Override
    @UseTime
    public void storeBlock(BinlogBlockInfo blockInfo) throws DataStashException {

        TableDataInfo sysTableData = blockInfo.getTables().get(DBStaticTableConstants.SYS_TABLES_TABLE);
        if ((sysTableData != null) && (sysTableData.getNewEntrys() != null)
                && (sysTableData.getNewEntrys().size() > 0)) {
            String serviceName = StringStyleUtils.underline2upper(DBStaticTableConstants.SYS_TABLES_TABLE)
                    + DBDynamicTableConstants.DB_SERVICE_POST_FIX;
            storageServices.get(serviceName).storeTableData(DBStaticTableConstants.SYS_TABLES_TABLE, sysTableData);
            blockInfo.getTables().remove(DBStaticTableConstants.SYS_TABLES_TABLE);
        }
        blockInfo.getTables().entrySet().forEach(e -> storeData(e.getKey(), e.getValue()));
    }

    @Override
    @UseTime
    public void storeData(String tableName, TableDataInfo tableDataInfo) throws DataStashException {
        log.info("storage table name : {}", tableName);
        String serviceName = StringStyleUtils.underline2upper(tableName) + DBDynamicTableConstants.DB_SERVICE_POST_FIX;
        if (storageServices.containsKey(serviceName)) {
            storageServices.get(serviceName).storeTableData(tableName, tableDataInfo);
        } else if (tableName.startsWith(DBDynamicTableConstants.CONTRACT_DATA_PRE_FIX)) {
            storageServices.get(DBDynamicTableConstants.CONTRACT_DATA_INFO_SERVICE).storeTableData(tableName,
                    tableDataInfo);
        } else if (tableName.startsWith(DBDynamicTableConstants.CONTRAACT_PARAFUNC_FIX)) {
            storageServices.get(DBDynamicTableConstants.CONTRACT_PARAFUNC_INFO_SERVICE).storeTableData(tableName,
                    tableDataInfo);
        } else {
            storageServices.get(DBDynamicTableConstants.DYNAMIC_TABLE_INFO_SERVICE).storeTableData(tableName,
                    tableDataInfo);
        }
    }

}
