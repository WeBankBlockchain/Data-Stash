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
package com.webank.blockchain.data.stash.fetch;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * BinlogFileInfo
 *
 * @Description: BinlogFileInfo
 * @author maojiayu
 * @data Aug 19, 2019 4:24:00 PM
 *
 */

@Data
@Accessors(chain = true)
public class BinlogFileInfo implements Comparable<BinlogFileInfo> {

    private String name;
    private LocalDateTime lastModifyTime;
    private long length;
    private Long index;

    @Override
    public int compareTo(BinlogFileInfo o) {
        return this.index.compareTo(o.getIndex());
    }

}
