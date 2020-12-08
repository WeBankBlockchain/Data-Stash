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

import com.webank.blockchain.data.stash.db.dao.SysHash2HeaderInfoMapper;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.db.model.SysHash2HeaderInfo;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * @author aaronchu
 * @Description
 * @data 2020/10/22
 */
@Service
@Slf4j
public class SysHash2HeaderInfoService extends DBBaseOperation implements StorageService {

    private static final int SYS_HASH_2_HEADER_BATCH = 2;

    @Autowired
    private SysHash2HeaderInfoMapper mapper;

    @Override
    public void createSchema() throws SQLException {
        mapper.createTable(DBStaticTableConstants.SYS_HASH_2_HEADER_TABLE);

        String detailTableName = DBStaticTableConstants.SYS_HASH_2_HEADER_TABLE + DBStaticTableConstants.SYS_DETAIL_TABLE_POST_FIX;
        mapper.createDetailTable(detailTableName);
    }

    @Override
    public void storageTabelData(String tableName, TableDataInfo tableDataInfo) throws DataStashException {
        storage(tableName, tableDataInfo, SysHash2HeaderInfo.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void batchSave(String tableName, List list) {

        log.debug("list : {}", JsonUtils.toJson(list));

        int batchLastIndex = SYS_HASH_2_HEADER_BATCH;

        for (int index = 0; index < list.size();) {

            if (SYS_HASH_2_HEADER_BATCH >= list.size() - index) {
                batchLastIndex = list.size();
                mapper.batchInsert((List<SysHash2HeaderInfo>)list.subList(index, batchLastIndex));
                break;
            } else {
                mapper.batchInsert((List<SysHash2HeaderInfo>)list.subList(index, batchLastIndex));
                index = batchLastIndex;
                batchLastIndex = index + SYS_HASH_2_HEADER_BATCH;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void batchSaveDetail(String tableName, List list) {
        int batchLastIndex = SYS_HASH_2_HEADER_BATCH;

        for (int index = 0; index < list.size();) {

            if (SYS_HASH_2_HEADER_BATCH >= list.size() - index) {
                batchLastIndex = list.size();
                mapper.batchInsertDetail((List<SysHash2HeaderInfo>)list.subList(index, batchLastIndex));
                break;
            } else {
                mapper.batchInsertDetail((List<SysHash2HeaderInfo>)list.subList(index, batchLastIndex));
                index = batchLastIndex;
                batchLastIndex = index + SYS_HASH_2_HEADER_BATCH;
            }
        }
    }
}
