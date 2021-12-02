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

import java.util.Iterator;
import java.util.List;

/**
 * EntryInfo
 *
 * @Description: EntryInfo
 * @author graysonzhang
 * @author maojiayu
 * @data 2019-07-31 15:33:18
 *
 */
@Slf4j
public class EntryInfo implements Comparable<EntryInfo> {

    @Setter
    @Getter
    private long id;
    @Setter
    @Getter
    private String hash;
    @Setter
    @Getter
    private int status;
    @Setter
    @Getter
    private long num;
    @Setter
    @Getter
    private List<ColumnInfo> columns;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof EntryInfo))
            return false;
        final EntryInfo entry = (EntryInfo) obj;
        if ((this.hash != null && entry.getHash() == null) || this.hash == null && entry.getHash() != null) {
            return false;
        }

        if (this.hash != null && entry.getHash() != null && !this.hash.equals(entry.getHash())) {
            return false;
        }

        if (this.id == entry.getId() && this.num == entry.getNum() && this.columns.equals(entry.getColumns())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean removeValueEquals(Object obj) {

        if (this == obj)
            return true;
        if (!(obj instanceof EntryInfo))
            return false;
        final EntryInfo entry = (EntryInfo) obj;

        if ((this.hash != null && entry.getHash() == null) || this.hash == null && entry.getHash() != null) {
            log.error("hash not equal");
            return false;
        }

        if (this.hash != null && entry.getHash() != null && !this.hash.equals(entry.getHash())) {
            log.error("hash not equal");
            return false;
        }
        if (this.id == entry.getId() && this.num == entry.getNum()
                && removeValueListCompare(this.columns, entry.getColumns())) {
            return true;
        } else {
            log.error("id or number not equal.");
            return false;
        }
    }

    public boolean removeFieldEquals(Object obj, String ignoredField) {

        if (this == obj)
            return true;
        if (!(obj instanceof EntryInfo))
            return false;
        final EntryInfo entry = (EntryInfo) obj;

        if ((this.hash != null && entry.getHash() == null) || this.hash == null && entry.getHash() != null) {
            log.error("hash not equal");
            return false;
        }

        if (this.hash != null && entry.getHash() != null && !this.hash.equals(entry.getHash())) {
            log.error("hash not equal");
            return false;
        }
        if (this.id == entry.getId() && this.num == entry.getNum()
                && removeFieldListCompare(this.columns, entry.getColumns(), ignoredField)) {
            return true;
        } else {
            log.error("id or number not equal.");
            return false;
        }
    }


    private boolean removeFieldListCompare(List<ColumnInfo> columns1, List<ColumnInfo> columns2, String ignoreField) {
        if (columns1.size() != columns2.size()) {
            log.error("column not equal.");
            return false;
        }
        Iterator<ColumnInfo> iter1 = columns1.iterator();
        Iterator<ColumnInfo> iter2 = columns2.iterator();
        while (iter1.hasNext()) {
            ColumnInfo c1 = iter1.next();
            ColumnInfo c2 = iter2.next();
            if (!c1.getColumnName().equals(ignoreField) && !c1.equals(c2)) {
                log.error("");
                return false;
            }
        }
        return true;
    }

    private boolean removeValueListCompare(List<ColumnInfo> columns1, List<ColumnInfo> columns2) {
        if (columns1.size() != columns2.size()) {
            log.error("column not equal.");
            return false;
        }
        Iterator<ColumnInfo> iter1 = columns1.iterator();
        Iterator<ColumnInfo> iter2 = columns2.iterator();
        while (iter1.hasNext()) {
            ColumnInfo c1 = iter1.next();
            ColumnInfo c2 = iter2.next();
            if (!c1.getColumnName().equals("value") && !c1.equals(c2)) {
                log.error("");
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(EntryInfo o) {
        if (this.id == o.id) {
            return Long.valueOf(this.num).compareTo(Long.valueOf(o.num));
        } else {
            return Long.valueOf(this.id).compareTo(Long.valueOf(o.id));
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
