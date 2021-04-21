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
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import com.webank.blockchain.data.stash.db.dao.CheckPointInfoMapper;
import com.webank.blockchain.data.stash.db.model.CheckPointInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.blockchain.data.stash.db.dao.CheckPointInfoMapper;
import com.webank.blockchain.data.stash.db.model.CheckPointInfo;

/**
 * CommonsInfoService
 *
 * @Description: CommonsInfoService
 * @author graysonzhang
 * @data 2019-09-04 17:57:38
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class CheckPointInfoService extends DBBaseOperation {
	
	@Autowired
	private CheckPointInfoMapper mapper;


    @PostConstruct
    public void createSchema() {
        mapper.createTable();
    }
    
    public void save(CheckPointInfo checkPoint){
        mapper.insert(checkPoint);
    }
    
    public CheckPointInfo getByBlockNum(long blockNum){
        return mapper.selectByBlockNum(blockNum);
    }
	
	public List<TreeMap<String, String>> selectBlockDataByBlockNum(String tableName, long blockNum){
		return mapper.selectBlockDataByBlockNum(tableName, blockNum);
	}

    @Override
    public void batchSave(String tableName, List list) {

        
    }

    @Override
    public void batchSaveDetail(String tableName, List list) {

        
    }
    
    public void rollbackByBlockNum(long num){
        mapper.rollbackByBlockNum(num);
    }

    public long nextCheckpoint(){
        return mapper.nextCheckpoint();
    }
}
