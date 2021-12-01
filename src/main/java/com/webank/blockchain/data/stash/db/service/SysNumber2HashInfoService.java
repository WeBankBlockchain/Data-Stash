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

import com.webank.blockchain.data.stash.db.dao.SysNumber2HashInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.db.model.SysNumber2HashInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;

/**
 * 
 * SysCnsInfoService
 *
 * @Description: SysCnsInfoService
 * @author graysonzhang
 * @data 2019-08-08 14:19:51
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class SysNumber2HashInfoService extends DBBaseOperation implements StorageService {
	
	@Autowired
	private SysNumber2HashInfoMapper mapper;
	
	@Autowired
    private SystemPropertyConfig systemPropertyConfig;
	
	@Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
	public void createSchema() {
	    
	    mapper.createTable(DBStaticTableConstants.SYS_NUMBER_2_HASH_TABLE);

		String detailTableName = DBStaticTableConstants.SYS_NUMBER_2_HASH_TABLE + DBStaticTableConstants.SYS_DETAIL_TABLE_POST_FIX;
		mapper.createDetailTable(detailTableName);
	}
	
	@SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void storeTableData(String tableName, TableDataInfo tableDataInfo) throws DataStashException {
        storage(tableName, tableDataInfo, SysNumber2HashInfo.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void batchSave(String tableName, List list) {
        
        int batchLastIndex = systemPropertyConfig.getBatchCount();

        for (int index = 0; index < list.size();) {
            
            if (systemPropertyConfig.getBatchCount() >= list.size() - index) {
                batchLastIndex = list.size();
                List<SysNumber2HashInfo> subList = (List<SysNumber2HashInfo>)list.subList(index, batchLastIndex);
                for (SysNumber2HashInfo sysNumber2HashInfo : subList) {
                	sysNumber2HashInfo.setHash(sysNumber2HashInfo.getValue());
				}
                mapper.batchInsert(subList);
                break;
            } else {
                mapper.batchInsert((List<SysNumber2HashInfo>)list.subList(index, batchLastIndex));
                index = batchLastIndex;
                batchLastIndex = index + (systemPropertyConfig.getBatchCount() - 1);
            }
        }    
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void batchSaveDetail(String tableName, List list) {
        int batchLastIndex = systemPropertyConfig.getBatchCount();

        for (int index = 0; index < list.size();) {
            
            if (systemPropertyConfig.getBatchCount() >= list.size() - index) {
                batchLastIndex = list.size();
                List<SysNumber2HashInfo> subList = (List<SysNumber2HashInfo>)list.subList(index, batchLastIndex);
                for (SysNumber2HashInfo sysNumber2HashInfo : subList) {
                	sysNumber2HashInfo.setHash(sysNumber2HashInfo.getValue());
				}
                mapper.batchInsertDetail(subList);
                break;
            } else {
                mapper.batchInsertDetail((List<SysNumber2HashInfo>)list.subList(index, batchLastIndex));
                index = batchLastIndex;
                batchLastIndex = index + (systemPropertyConfig.getBatchCount() - 1);
            }
        } 
    } 
}
