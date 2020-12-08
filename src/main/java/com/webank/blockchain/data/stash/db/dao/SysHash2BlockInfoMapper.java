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

import com.webank.blockchain.data.stash.db.model.SysHash2BlockInfo;

/**
 * SysHash2BlockInfoMapper
 *
 * @Description: SysHash2BlockInfoMapper
 * @author graysonzhang
 * @data 2019-08-08 18:03:25
 *
 */
public interface SysHash2BlockInfoMapper extends BaseMapper {
    
    int deleteByPrimaryKey(Long id);

    int insert(SysHash2BlockInfo record);

    SysHash2BlockInfo selectByPrimaryKey(Long id);
    
    SysHash2BlockInfo selectByBlockNumber(Long id);
    
    int insertDetail(SysHash2BlockInfo record);
    
    List<SysHash2BlockInfo> selectDetailByEntryId(Long id);
    
    int batchInsert(@Param("list")List<SysHash2BlockInfo> list);

    int batchInsertDetail(@Param("list")List<SysHash2BlockInfo> list);

    void deleteDetailByBlockNum(long num);

    List<SysHash2BlockInfo> selectDetailByBlockNum(long num);
}