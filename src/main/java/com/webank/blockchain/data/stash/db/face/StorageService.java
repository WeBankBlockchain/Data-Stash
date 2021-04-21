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
package com.webank.blockchain.data.stash.db.face;

import java.sql.SQLException;

import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;

/**
 * DBService
 *
 * @Description: DBService
 * @author graysonzhang
 * @data 2019-08-09 17:33:22
 *
 */
public interface StorageService {
    
    public void createSchema() throws SQLException; 
    
    public void storeTableData(String tableName, TableDataInfo tableDataInfo) throws DataStashException;

}
