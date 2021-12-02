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

import com.webank.blockchain.data.stash.db.model.SysConfigInfo;

import java.util.List;

/**
 * SysConfigInfoMapper
 *
 * @Description: SysConfigInfoMapper
 * @author graysonzhang
 * @data 2019-08-08 17:59:59
 *
 */
public interface SysConfigInfoMapper extends BaseMapper {
    
    int deleteByPrimaryKey(Long id);

    int insert(SysConfigInfo record);
    
    SysConfigInfo selectByKey(String key);

    SysConfigInfo selectByPrimaryKey(Long id);
    
    int insertDetail(SysConfigInfo record);
    
    List<SysConfigInfo> selectDetailByEntryId(Long id);
    
    int batchInsert(List<SysConfigInfo> list);

    int batchInsertDetail(List<SysConfigInfo> list);

    void deleteDetailByBlockNum(long blockNum);

    List<SysConfigInfo> selectDetailByBlockNum(long l);
}