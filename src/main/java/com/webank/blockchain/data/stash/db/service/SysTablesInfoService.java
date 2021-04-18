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
package com.webank.blockchain.data.stash.db.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.constants.DBTableTypeConstants;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.db.model.SysTablesInfo;
import com.webank.blockchain.data.stash.entity.EntryInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.utils.CommonUtil;
import com.webank.blockchain.data.stash.utils.SQLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.webank.blockchain.data.stash.db.dao.SysTablesInfoMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SysTablesInfoService
 *
 * @Description: SysTablesInfoService
 * @author graysonzhang
 * @data 2019-08-08 14:25:39
 *
 */
@SuppressWarnings("rawtypes")
@Service
@Slf4j
public class SysTablesInfoService extends DBBaseOperation implements StorageService {

    @Autowired
    private SysTablesInfoMapper mapper;
    
    @Autowired
    private SystemPropertyConfig systemPropertyConfig;
    
    @Autowired
    private DataSource dataSource;

    @Override
    @Transactional
    public void createSchema() throws SQLException{
        try(Connection connection = dataSource.getConnection()){
            int tableCount =
                    mapper.existTable(DBStaticTableConstants.SYS_TABLES_TABLE, connection.getCatalog());
            if (tableCount == 0) {
                mapper.createTable(DBStaticTableConstants.SYS_TABLES_TABLE);
                mapper.insertSysTables();
                String detailTableName = DBStaticTableConstants.SYS_TABLES_TABLE + DBStaticTableConstants.SYS_DETAIL_TABLE_POST_FIX;
                mapper.createDetailTable(detailTableName);
            }
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void storeTableData(String tableName, TableDataInfo tableDataInfo) throws DataStashException {
        storage(tableName, tableDataInfo, SysTablesInfo.class);
        for (EntryInfo entry : tableDataInfo.getNewEntrys()) {
            createTableByTableName(CommonUtil.getValueColumn(entry.getColumns(), "table_name"));
        }
    }
	
	@SuppressWarnings("unchecked")
    @Override
    public void batchSave(String tableName, List list) {
        
	    int batchLastIndex = systemPropertyConfig.getBatchCount();

        for (int index = 0; index < list.size();) {
            
            if (systemPropertyConfig.getBatchCount() >= list.size() - index) {
                batchLastIndex = list.size();
                mapper.batchInsert((List<SysTablesInfo>)list.subList(index, batchLastIndex));
                break;
            } else {
                mapper.batchInsert((List<SysTablesInfo>)list.subList(index, batchLastIndex));
                index = batchLastIndex;
                batchLastIndex = index + (systemPropertyConfig.getBatchCount() - 1);
            }
        }    
    }
    
    @Override
    public void batchSaveDetail(String tableName, List list) {
        
    }

    public List<SysTablesInfo> selectAllTables() {
        return mapper.selectTables();
    }

    private void createTableByTableName(String tableName) {

        List<Map<String, String>> fields = mapper.getTableSchema(tableName);
        log.debug("fields=" + fields.toString());
        String key = fields.get(0).get("key_field");
        String value_field = fields.get(0).get("value_field");
        String[] values = value_field.split(",");

        String entrySql = SQLUtil.getSql(tableName, key, values, DBTableTypeConstants.NEW_TABLE);
        mapper.createTableBySql(entrySql);
        log.info("create table : {}", tableName);
        // 2. create entry detail table
        String detailTableName = CommonUtil.getDetailTableName(tableName);
        String entryDetailSql = SQLUtil.getSql(detailTableName, key, values, DBTableTypeConstants.NEW_DETAIL);
        mapper.createTableBySql(entryDetailSql);
        log.info("create detail table : {}", detailTableName);
    }
    
    public void deleteByBlockNum(String tableName, long blockNum){
        mapper.deleteByBlockNum(tableName, blockNum);
    }
    
    public List<Map<String, Object>> selectBlockDataByBlockNum(String tableName, long blockNum){
        return mapper.selectTableDataByNum(tableName, blockNum);
    } 
    
    public List<Map<String, Object>> selectDataForTopBlock(String tableName){
        return mapper.selectDataForTopBlock(tableName);
    }
    
    public void deleteById(String tableName, long id){
        mapper.deleteByPrimaryKey(tableName, id);
    }

    public void rollbackFrom(String table, long block){
        this.mapper.rollbackTableFromBlock(table, block);
    }
}
