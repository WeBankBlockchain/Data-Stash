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

import com.webank.blockchain.data.stash.constants.DataStorageTypeConstants;
import com.webank.blockchain.data.stash.entity.ColumnInfo;
import com.webank.blockchain.data.stash.entity.EntryInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;

/**
 * ObjectBuildUtil
 *
 * @Description: ObjectBuildUtil
 * @author graysonzhang
 * @data 2019-08-09 16:23:54
 *
 */
@Slf4j
public class ObjectBuildUtil {
    
    public static Object buildToStaticObj(EntryInfo entry, Object obj){
        List<ColumnInfo> columns = entry.getColumns();
        Field[] fields = obj.getClass().getDeclaredFields();
        int i = 0;
        for(ColumnInfo columnInfo : columns){
            fields[i].setAccessible(true);
            try {
                fields[i].set(obj, columnInfo.getColumnValue());
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error("there is no {} param", columnInfo.getColumnName());
                e.printStackTrace();
            }
            i++;
        }
        
        return obj;       
    }  
    
    public static Object buildToDynamicObj(EntryInfo entry, Object obj){
        List<ColumnInfo> columns = entry.getColumns();
        Field[] fieldList = obj.getClass().getDeclaredFields();
        
        StringBuffer fields = new StringBuffer();
        StringBuffer values = new StringBuffer();

        if(entry.getHash() == null){
            entry.setHash(DataStorageTypeConstants.DEFALT_HASH);
        }
        
        values.append(entry.getId()).append(",'").append(entry.getHash()).append("',").append(entry.getStatus()).append(",").append(entry.getNum());
        for(ColumnInfo columnInfo : columns){
            String columnName = columnInfo.getColumnName();
            fields.append(", ").append("`").append(columnName).append("`");
            values.append(", '").append(convertEscapeChar(columnInfo.getColumnValue())).append("'");
        }
        try {
            fieldList[0].setAccessible(true);
            fieldList[1].setAccessible(true);
            //fieldList[2].setAccessible(true);

            fieldList[0].set(obj, fields.toString());
            fieldList[1].set(obj, values.toString());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("the column params is error");
            e.printStackTrace();
        }
        return obj; 
    }

    //If value contains single quote, it should be add escape char. For example, 'aaa'->\'aaa\', while aaa->aaa
    public static String convertEscapeChar(String value){
        if(value == null) return value;
        if(!value.contains("'")) return value;
        return value.replace("'","\\'");
    }

}
