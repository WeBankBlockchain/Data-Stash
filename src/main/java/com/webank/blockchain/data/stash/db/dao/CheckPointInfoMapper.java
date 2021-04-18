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
import java.util.TreeMap;

import org.apache.ibatis.annotations.Param;

import com.webank.blockchain.data.stash.db.model.CheckPointInfo;

/**
 * CommonsInfoMapper
 *
 * @Description: CommonsInfoMapper
 * @author graysonzhang
 * @data 2019-09-04 17:46:01
 *
 */
public interface CheckPointInfoMapper {
    
    /**    
     * create check point table
     *       
     * @return void       
     */
    void createTable();
	/**
	 * insert checkpoint
	 * 
	 * @param checkPointInfo
	 * @return void
	 */
	void insert(CheckPointInfo checkPointInfo);
	/**    
	 * select check point by block number 
	 * 
	 * @param blockNum
	 * @return      
	 * @return CheckPointInfo       
	 */
	CheckPointInfo selectByBlockNum(long blockNum);
    
    /**    
     * select entry or entry detail list by block number  
     * 
     * @param blockNum
     * @return      
     * @return List<Object>       
     */
	List<TreeMap<String, String>> selectBlockDataByBlockNum(@Param("tableName")String tableName, @Param("blockNum")Long blockNum);
	
	void rollbackByBlockNum(long num);

}
