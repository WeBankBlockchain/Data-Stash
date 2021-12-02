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

import com.webank.blockchain.data.stash.constants.DBTableTypeConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * SQLUtil
 *
 * @Description: SQLUtil
 * @author graysonzhang
 * @data 2019-08-18 16:36:55
 *
 */
public class SQLUtil {
    
    public static String getSql(String tableName, String key, String[] values, int tableType) {
        
        tableName = formatStr(tableName);
        key = formatStr(key);
        
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append("`").append(tableName).append("`").append(" (\n");
        if(tableType == DBTableTypeConstants.NEW_DETAIL){ 
            sql.append(" `pk_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,\n");
        }
        sql.append(" `_id_` bigint(20) unsigned NOT NULL,\n")
                .append(" `_hash_` varchar(128) NOT NULL DEFAULT '0x00',\n")
                .append(" `_num_` bigint(20) DEFAULT NULL,\n")
                .append(" `_status_` int not null,\n")
                .append("`").append(key).append("`").append(" varchar(255) default '',\n");
        
        if (!"".equals(values[0].trim())) {
            for (String value : values) {
                sql.append(" `").append(formatStr(value)).append("` mediumtext,\n");
            }
        }
        if(tableType == DBTableTypeConstants.NEW_TABLE){
            sql.append(" PRIMARY KEY( `_id_` ),\n");
        }else if(tableType == DBTableTypeConstants.NEW_DETAIL){
            sql.append(" PRIMARY KEY( `pk_id` ),\n");
        }
        sql.append(" KEY(`").append(key).append("`),\n").append(" KEY(`_id_`),\n").append(" KEY(`_num_`),\n").append(" UNIQUE KEY(`_id_`, `_num_`)\n")
                .append(")ENGINE=InnoDB default charset=utf8;");
        return sql.toString();
    }
    
    public static String replaceString(String str) {
        String replaceStr = str;
        replaceStr = replaceStr.replace('\'', '_');
        replaceStr = replaceStr.replace('\"', '_');
        return replaceStr;

    }

    public static String formatStr(String str) {
        String strSql = str;
        strSql = strSql.replace("\\", "\\\\");
        strSql = strSql.replace("`", "\\`");
        return strSql;
    }
    
    public static String convertStr(String sqlStr){
        StringBuffer result = new StringBuffer();
        String[] strArr = StringUtils.split(sqlStr, ",");
        for (int i = 0; i < strArr.length; i++) {
            if(i < strArr.length - 1){
                result.append("`").append(strArr[i]).append("`").append(",");
            }else{
                result.append("`").append(strArr[i]).append("`");
            }       
        }
        return result.toString();
    }
}
