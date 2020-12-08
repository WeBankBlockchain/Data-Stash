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
import org.apache.ibatis.annotations.Param;
import com.webank.blockchain.data.stash.db.model.ContractParaFuncInfo;


/**
 * ContractDataInfoMapper
 *
 * @Description: ContractDataInfoMapper
 * @author graysonzhang
 * @data 2019-08-08 14:19:33
 *
 */
public interface ContractParaFuncInfoMapper extends BaseMapper {
    
    int deleteByPrimaryKey(@Param("tableName")String tableName, @Param("id")Long id);

    int insert(@Param("tableName")String tableName, @Param("contractParaFuncInfo")ContractParaFuncInfo record);

    ContractParaFuncInfo selectByPrimaryKey(@Param("tableName")String tableName, @Param("id")Long id);
    
    int insertDetail(@Param("tableName")String tableName, @Param("contractParaFuncInfo")ContractParaFuncInfo record);
    
    List<ContractParaFuncInfo> selectDetailByEntryId(@Param("tableName")String tableName, @Param("id")Long id);
    
    int existTable(String tableName);
    
    int batchInsert(@Param("tableName")String tableName, @Param("list")List<ContractParaFuncInfo> list);

    int batchInsertDetail(@Param("tableName")String tableName, @Param("list")List<ContractParaFuncInfo> list);

    void deleteDetailByBlockNum(@Param("tableName")String tableName,@Param("num")long num);

    List<ContractParaFuncInfo> selectDetailByBlockNum(@Param("tableName")String tableName, @Param("num")long num);
}