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

import java.util.List;

import com.webank.blockchain.data.stash.db.dao.ContractParaFuncInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.db.model.ContractParaFuncInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.utils.CommonUtil;

/**
 * 
 * ContractParaFuncInfoService
 *
 * @Description: ContractParaFuncInfoService
 * @author graysonzhang
 * @data 2019-09-24 15:34:36
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class ContractParaFuncInfoService extends DBBaseOperation implements StorageService {
	
	@Autowired
	private ContractParaFuncInfoMapper mapper;
	
	@Autowired
    private SystemPropertyConfig systemPropertyConfig;

	
	@SuppressWarnings("unchecked")
    @Override
	@Transactional
    public void storageTabelData(String tableName, TableDataInfo tableDataInfo) throws DataStashException {
	    storage(tableName, tableDataInfo, ContractParaFuncInfo.class);
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
                mapper.batchInsert(tableName, (List<ContractParaFuncInfo>)list.subList(index, batchLastIndex));
                break;
            } else {
                mapper.batchInsert(tableName, (List<ContractParaFuncInfo>)list.subList(index, batchLastIndex));
                index = batchLastIndex;
                batchLastIndex = index + (systemPropertyConfig.getBatchCount() - 1);
            }
        } 
    }

    @SuppressWarnings("unchecked")
    @Override
    public void batchSaveDetail(String tableName, List list) {
        String detailTableName = CommonUtil.getDetailTableName(tableName);
        mapper.createTable(detailTableName);
        int batchLastIndex = systemPropertyConfig.getBatchCount();

        for (int index = 0; index < list.size();) {
            
            if (systemPropertyConfig.getBatchCount() >= list.size() - index) {
                batchLastIndex = list.size();
                mapper.batchInsertDetail(detailTableName, (List<ContractParaFuncInfo>)list.subList(index, batchLastIndex));
                break;
            } else {
                mapper.batchInsertDetail(detailTableName, (List<ContractParaFuncInfo>)list.subList(index, batchLastIndex));
                index = batchLastIndex;
                batchLastIndex = index + (systemPropertyConfig.getBatchCount() - 1);
            }
        }
    } 
}
