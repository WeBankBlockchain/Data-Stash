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

import java.util.*;

import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.convertor.DBEntryConvertor;
import com.webank.blockchain.data.stash.db.model.BaseInfo;
import com.webank.blockchain.data.stash.entity.EntryInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * DBBaseOperation
 *
 * @Description: DBBaseOperation
 * @author graysonzhang
 * @data 2019-09-01 12:18:40
 *
 */
public abstract class DBBaseOperation<T extends BaseInfo> {
    
    @Autowired
    private DBEntryConvertor handle;
    
    
    public void storage(String tableName, TableDataInfo tableDataInfo, Class<T> clz) throws DataStashException {
        
        //storage dirty entry
        List<T> dirtyEntrys = batchConvert(tableDataInfo.getDirtyEntrys(), clz, tableName);
        processEntries(tableName, dirtyEntrys);
        
        //storage new entry
        List<T> newEntrys = batchConvert(tableDataInfo.getNewEntrys(), clz, tableName);
        processEntries(tableName, newEntrys); 
    }
    
    private void processEntries(String tableName,List<T> list){
        if(list != null && list.size() > 0){
            batchSaveDetail(tableName, list);
            batchSave(tableName, list);
        }
    }
    
    private List<T> batchConvert(TreeSet<EntryInfo> entrys, Class<T> clz, String tableName){
        List<T> list = new ArrayList<>();
        for (EntryInfo entry : entrys) {
            T record = null;
            try {
                record = clz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            list.add(handle.convert(record, entry, tableName));
        }
        return list;
    }
    
    public abstract void batchSave(String tableName, List<T> list);
    
    public abstract void batchSaveDetail(String tableName, List<T> list);

}
