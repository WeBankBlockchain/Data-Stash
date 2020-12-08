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
package com.webank.blockchain.data.stash.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * BlockTaskPoolSyncStatusEnum
 *
 * @Description: BlockTaskPoolSyncStatusEnum
 * @author maojiayu
 * @data Oct 11, 2019 10:22:26 AM
 *
 */
@Getter
@AllArgsConstructor
public enum BlockTaskPoolSyncStatusEnum {

    INIT(0, "init"), DOING(1, "doing"), Done(2, "done"), ERROR(3, "error");

    private int syncStatus;
    private String statusInfo;
}
