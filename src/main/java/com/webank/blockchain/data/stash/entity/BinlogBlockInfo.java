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

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * BlockInfo
 *
 * @Description: BlockInfo
 * @author graysonzhang
 * @data 2019-08-29 16:50:14
 *
 */
@Data
@Accessors(chain = true)
public class BinlogBlockInfo {
    
    private long blockNum;
    private int dataCount;
    private Map<String, TableDataInfo> tables = new TreeMap<String, TableDataInfo>();

}
