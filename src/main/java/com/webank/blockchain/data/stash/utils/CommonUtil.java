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
package com.webank.blockchain.data.stash.utils;

import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.entity.ColumnInfo;

import java.util.List;

/**
 * ColumnUtil
 *
 * @Description: ColumnUtil
 * @author graysonzhang
 * @data 2019-09-06 17:58:27
 *
 */
public class CommonUtil {
    
    public static String getValueColumn(List<ColumnInfo> columns, String columnName){
        for(ColumnInfo column : columns){
            if(column.getColumnName().equals(columnName)){
                return column.getColumnValue();
            }
        }
        return null;
    }
    
    public static String getDetailTableName(String tableName){
        String detailTableName;
        if(!tableName.endsWith("_")){
            detailTableName = tableName + "_" + DBStaticTableConstants.SYS_DETAIL_TABLE_POST_FIX;
        }else{
            detailTableName = tableName + DBStaticTableConstants.SYS_DETAIL_TABLE_POST_FIX;
        }
        return detailTableName;
    }
}
