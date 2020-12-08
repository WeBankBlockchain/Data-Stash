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
import com.webank.blockchain.data.stash.db.model.SysBlock2NoncesInfo;

/**
 * SysBlock2NoncesInfoMapper
 *
 * @Description: SysBlock2NoncesInfoMapper
 * @author graysonzhang
 * @data 2019-08-08 17:57:17
 *
 */
public interface SysBlock2NoncesInfoMapper extends BaseMapper {
    
    int deleteByPrimaryKey(Long id);

    int insert(SysBlock2NoncesInfo record);

    SysBlock2NoncesInfo selectByPrimaryKey(Long id);
    
    int insertDetail(SysBlock2NoncesInfo record);
    
    List<SysBlock2NoncesInfo> selectDetailByEntryId(Long id);
    
    int batchInsert(List<SysBlock2NoncesInfo> list);

    int batchInsertDetail(List<SysBlock2NoncesInfo> list);

    void deleteDetailByBlockNum(long num);

    List<SysBlock2NoncesInfo> selectDetailByBlockNum(long num);
}