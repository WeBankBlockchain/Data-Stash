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
import com.webank.blockchain.data.stash.db.dao.ContractDataInfoMapper;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.db.model.ContractDataInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ContractDataInfoService
 *
 * @Description: ContractDataInfoService
 * @author graysonzhang
 * @data 2019-08-08 14:14:38
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ContractDataInfoService extends DBBaseOperation implements StorageService {
	
	@Autowired
	private ContractDataInfoMapper mapper;
	
	@Autowired
    private SystemPropertyConfig systemPropertyConfig;

	@SuppressWarnings("unchecked")
	@Transactional
    @Override
    public void storeTableData(String tableName, TableDataInfo tableDataInfo) throws DataStashException {
	    storage(tableName, tableDataInfo, ContractDataInfo.class);
    }

    @Override
    public void createSchema() {
        
    }

    @SuppressWarnings("unchecked")
    @Override
    public void batchSave(String tableName, List list) {
        mapper.createTable(tableName);
        int batchLastIndex = systemPropertyConfig.getBatchCount();

        for (int index = 0; index < list.size();) {
            
            if (systemPropertyConfig.getBatchCount() >= list.size() - index) {
                batchLastIndex = list.size();
                mapper.batchInsert(tableName, (List<ContractDataInfo>)list.subList(index, batchLastIndex));
                break;
            } else {
                mapper.batchInsert(tableName, (List<ContractDataInfo>)list.subList(index, batchLastIndex));
                index = batchLastIndex;
                batchLastIndex = index + (systemPropertyConfig.getBatchCount() - 1);
            }
        }        
    }

    @SuppressWarnings("unchecked")
    @Override
    public void batchSaveDetail(String tableName, List list) {
        String detailTableName = CommonUtil.getDetailTableName(tableName);
        mapper.createDetailTable(detailTableName);
        int batchLastIndex = systemPropertyConfig.getBatchCount();

        for (int index = 0; index < list.size();) {
            
            if (systemPropertyConfig.getBatchCount() >= list.size() - index) {
                batchLastIndex = list.size();
                mapper.batchInsertDetail(detailTableName, (List<ContractDataInfo>)list.subList(index, batchLastIndex));
                break;
            } else {
                mapper.batchInsertDetail(detailTableName, (List<ContractDataInfo>)list.subList(index, batchLastIndex));
                index = batchLastIndex;
                batchLastIndex = index + (systemPropertyConfig.getBatchCount() - 1);
            }
        }       
    }
       
}
