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
package com.webank.blockchain.data.stash.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Entity
 *
 * @Description: Entity
 * @author graysonzhang
 * @author maojiayu
 * @data 2019-07-31 11:44:50
 *
 */
@Slf4j
public class ColumnInfo implements Comparable<ColumnInfo> {

    @Setter
    @Getter
    private String columnName;
    @Setter
    @Getter
    private String columnValue;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ColumnInfo))
            return false;
        final ColumnInfo columnInfo = (ColumnInfo) obj;
        if (this.columnName.equals(columnInfo.getColumnName())
                && this.columnValue.equals(columnInfo.getColumnValue())) {
            return true;
        } else {
            log.error("Column name or value not equal.");
            log.error("colunm1 {} {}", this.columnName, this.columnValue);
            log.error("colunm2 {} {}", columnInfo.getColumnName(), columnInfo.getColumnValue());
            return false;
        }
    }
    

    @Override
    public int compareTo(ColumnInfo o) {
        int i = this.columnName.compareTo(o.columnName);
        if (i != 0) {
            return i;
        } else {
            return this.columnValue.compareTo(o.columnValue);
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (columnName == null ? 0 : columnName.hashCode());
        result = 31 * result + (columnValue == null ? 0 : columnValue.hashCode());
        return result;
    }
}
