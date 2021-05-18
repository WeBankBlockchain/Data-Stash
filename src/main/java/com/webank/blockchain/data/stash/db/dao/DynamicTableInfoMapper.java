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

import com.webank.blockchain.data.stash.db.model.DynamicTableInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * DynamicTableInfoMapper
 *
 * @Description: DynamicTableInfoMapper
 * @author graysonzhang
 * @data 2019-09-24 16:45:42
 *
 */
public interface DynamicTableInfoMapper extends BaseMapper {
    
    void insertOrUpdate(@Param("tableName")String tableName, @Param("dynamicTableInfo") DynamicTableInfo record);
    
    int insertDetail(@Param("tableName")String tableName, @Param("dynamicTableInfo") DynamicTableInfo record);
    
    int existTable(String tableName);
    
    int batchInsert(@Param("tableName")String tableName, @Param("list")List<DynamicTableInfo> list, @Param("fields")String fields);

    int batchInsertDetail(@Param("tableName")String tableName, @Param("list")List<DynamicTableInfo> list, @Param("fields")String fields);

    void deleteDetailByBlockNum(long blockNum);

    List<Object> selectDetailByBlockNum(long l);
        
}