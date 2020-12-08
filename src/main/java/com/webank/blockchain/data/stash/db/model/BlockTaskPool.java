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
package com.webank.blockchain.data.stash.db.model;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * BlockTaskPool
 *
 * @Description: BlockTaskPool
 * @author maojiayu
 * @data Aug 28, 2019 3:35:57 PM
 *
 */
@Data
@Accessors(chain = true)
public class BlockTaskPool {
    private long pkId;
    private long blockHeight;
    private int certainty = 0;
    private Date blockTime = new Date(0);
    private Date updatetime = new Date();
    private int syncStatus = 0;
}
