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
package com.webank.blockchain.data.stash.db.dao;

import java.util.List;
import java.util.Map;

import com.webank.blockchain.data.stash.db.model.SysTablesInfo;
import org.apache.ibatis.annotations.Param;
import com.webank.blockchain.data.stash.db.model.SysTablesInfo;

/**
 * SysTablesInfoMapper
 *
 * @Description: SysTablesInfoMapper
 * @author graysonzhang
 * @data 2019-08-08 18:04:06
 *
 */
public interface SysTablesInfoMapper extends BaseMapper {
    
    int deleteByPrimaryKey(@Param("tableName")String tableName, @Param("id")Long id);

    int insert(SysTablesInfo record);

    SysTablesInfo selectByPrimaryKey(Long id);
    
    void insertSysTables();
    
    int existTable(@Param("tableName")String tableName, @Param("tableSchema")String tableSchema);
    
    List<SysTablesInfo> selectTables();
    
    void createTableBySql(@Param("sql")String sql);
    
    List<Map<String, String>> getTableSchema(String tableName);
    
    int batchInsert(List<SysTablesInfo> list);

    int batchInsertDetail(List<SysTablesInfo> list);

    void deleteByBlockNum(@Param("tableName")String tableName, @Param("blockNum")long blockNum);
    
    List<Map<String, Object>> selectTableDataByNum(@Param("tableName")String tableName, @Param("blockNum")Long blockNum);
    
    List<Map<String, Object>> selectTopByEntryId(@Param("tableName")String tableName, @Param("id")Long id);
    
}