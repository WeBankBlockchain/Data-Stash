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

import com.webank.blockchain.data.stash.db.model.SysHash2HeaderInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author aaronchu
 * @Description
 * @data 2020/10/22
 */
public interface SysHash2HeaderInfoMapper extends BaseMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysHash2HeaderInfo record);

    SysHash2HeaderInfo selectByPrimaryKey(Long id);

    SysHash2HeaderInfo selectByBlockNumber(Long id);

    int insertDetail(SysHash2HeaderInfo record);

    List<SysHash2HeaderInfo> selectDetailByEntryId(Long id);

    int batchInsert(@Param("list")List<SysHash2HeaderInfo> list);

    int batchInsertDetail(@Param("list")List<SysHash2HeaderInfo> list);

    void deleteDetailByBlockNum(long num);

    List<SysHash2HeaderInfo> selectDetailByBlockNum(long num);
}
