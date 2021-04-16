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
package com.webank.blockchain.data.stash.db.rollback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.webank.blockchain.data.stash.db.mapper.BinlogOffsetMapper;
import com.webank.blockchain.data.stash.db.model.DylamicTableInfo;
import com.webank.blockchain.data.stash.db.model.SysTablesInfo;
import com.webank.blockchain.data.stash.db.service.CheckPointInfoService;
import com.webank.blockchain.data.stash.db.service.DylamicTableInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.blockchain.data.stash.db.mapper.BinlogOffsetMapper;
import com.webank.blockchain.data.stash.db.model.DylamicTableInfo;
import com.webank.blockchain.data.stash.db.model.SysTablesInfo;
import com.webank.blockchain.data.stash.db.service.CheckPointInfoService;
import com.webank.blockchain.data.stash.db.service.DylamicTableInfoService;
import com.webank.blockchain.data.stash.db.service.SysTablesInfoService;
import com.webank.blockchain.data.stash.entity.ColumnInfo;
import com.webank.blockchain.data.stash.entity.EntryInfo;
import com.webank.blockchain.data.stash.utils.CommonUtil;
import com.webank.blockchain.data.stash.utils.JsonUtils;
import com.webank.blockchain.data.stash.utils.ObjectBuildUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * RollBackService
 *
 * @Description: RollBackService
 * @author graysonzhang
 * @data 2019-10-08 18:56:34
 *
 */
@Service
@Slf4j
public class RollBackService {

    @Autowired
    private SysTablesInfoService service;
    @Autowired
    private CheckPointInfoService checkPointInfoService;
    @Autowired
    private DylamicTableInfoService dylamicTableInfoService;

    public boolean rollBack(long blockNum) {

        List<SysTablesInfo> tables = service.selectAllTables();

        for (SysTablesInfo table : tables) {

            log.info("table name : {} in sys tables", table.getTableName());

            // 1. delete detail entry info
            String detailTableName = CommonUtil.getDetailTableName(table.getTableName());
            service.deleteByBlockNum(detailTableName, blockNum);

            // 2. get block data by block num
            List<Map<String, Object>> entrys = service.selectBlockDataByBlockNum(table.getTableName(), blockNum);
            log.debug("size : {}, table : {}, content : {}", entrys.size(), table.getTableName(),
                    JsonUtils.toJson(entrys));

            for (Map<String, Object> map : entrys) {

                log.debug("id : {}", map.get("_id_"));

                long id = Long.valueOf(map.get("_id_").toString());

                List<Map<String, Object>> detailEntrys = service.selectTopOneById(detailTableName, id);

                if (detailEntrys == null || detailEntrys.size() != 1) {
                    service.deleteById(table.getTableName(), id);
                } else {
                    Map<String, Object> columnMap = detailEntrys.get(0);
                    EntryInfo entryInfo = new EntryInfo();

                    entryInfo.setId(id);
                    entryInfo.setHash(columnMap.get("_hash_").toString());
                    entryInfo.setStatus(Integer.valueOf(columnMap.get("_status_").toString()));
                    entryInfo.setNum(Long.valueOf(columnMap.get("_num_").toString()));

                    columnMap.remove("pk_id");
                    columnMap.remove("_id_");
                    columnMap.remove("_hash_");
                    columnMap.remove("_status_");
                    columnMap.remove("_num_");

                    List<ColumnInfo> colums = new ArrayList<ColumnInfo>();
                    for (String key : columnMap.keySet()) {
                        ColumnInfo columnInfo = new ColumnInfo();
                        columnInfo.setColumnName(key);
                        columnInfo.setColumnValue(columnMap.get(key).toString());
                        colums.add(columnInfo);
                    }

                    entryInfo.setColumns(colums);

                    DylamicTableInfo dylamicTableInfo = new DylamicTableInfo();
                    ObjectBuildUtil.buildToDynamicObj(entryInfo, dylamicTableInfo);

                    dylamicTableInfoService.save(table.getTableName(), dylamicTableInfo);

                }
            }
        }
        checkPointInfoService.deleteByBlockNum(blockNum);
        return true;

    }

}
