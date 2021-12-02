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


import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.db.dao.DynamicTableInfoMapper;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.db.model.DynamicTableInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.utils.CommonUtil;
import com.webank.blockchain.data.stash.utils.JsonUtils;
import com.webank.blockchain.data.stash.utils.SQLUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 
 * DynamicTableInfoService
 *
 * @Description: DynamicTableInfoService
 * @author graysonzhang
 * @data 2019-09-24 16:44:12
 *
 */
@SuppressWarnings("rawtypes")
@Service
@Slf4j
public class DynamicTableInfoService extends DBBaseOperation implements StorageService {

	@Autowired
	private DynamicTableInfoMapper mapper;
	
	@Autowired
    private SystemPropertyConfig systemPropertyConfig;
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void storeTableData(String tableName, TableDataInfo tableDataInfo) throws DataStashException {
        storage(tableName, tableDataInfo, DynamicTableInfo.class);
    }       

    @Override
    public void createSchema() {
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void batchSave(String tableName, List list) {  
        
        int batchLastIndex = systemPropertyConfig.getBatchCount();
        
        DynamicTableInfo entry = (DynamicTableInfo)list.get(0);
        String fields = entry.getFields();
        log.debug("list size: {}", list.size());

        for (int index = 0; index < list.size();) {
            log.debug("index : {}", index);
            
            if (systemPropertyConfig.getBatchCount() >= list.size() - index) {
                batchLastIndex = list.size();
                log.debug("list:{}", JsonUtils.toJson((List<DynamicTableInfo>)list.subList(index, batchLastIndex)));
                mapper.batchInsert(SQLUtil.convertStr(tableName), (List<DynamicTableInfo>)list.subList(index, batchLastIndex), fields);
                break;
            } else {
                mapper.batchInsert(SQLUtil.convertStr(tableName), (List<DynamicTableInfo>)list.subList(index, batchLastIndex), fields);
                index = batchLastIndex;
                batchLastIndex = index + (systemPropertyConfig.getBatchCount() - 1);
            }
        }     
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void batchSaveDetail(String tableName, List list) {
        int batchLastIndex = systemPropertyConfig.getBatchCount();

        DynamicTableInfo entry = (DynamicTableInfo)list.get(0);
        String fields = entry.getFields();
        
        String detailTableName = CommonUtil.getDetailTableName(tableName);
        for (int index = 0; index < list.size();) {
            log.debug("index : {}", index);
            if (systemPropertyConfig.getBatchCount() >= list.size() - index) {
                mapper.batchInsertDetail(SQLUtil.convertStr(detailTableName), (List<DynamicTableInfo>)list.subList(index, list.size()), fields);
                break;
            } else {
                mapper.batchInsertDetail(SQLUtil.convertStr(detailTableName), (List<DynamicTableInfo>)list.subList(index, batchLastIndex), fields);
                index = batchLastIndex;
                batchLastIndex = index + (systemPropertyConfig.getBatchCount() - 1);
            }
        }
    } 
    
    public void save(String tableName, DynamicTableInfo record){
        mapper.insertOrUpdate(SQLUtil.convertStr(tableName), record);
    }
    
    public void checkTable(String tableName){
        mapper.existTable(tableName);
    }
    
    
}
