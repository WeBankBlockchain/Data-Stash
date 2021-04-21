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
import lombok.Setter;
import lombok.ToString;

/**
 * ExceptionCodeEnums
 *
 * @Description: ExceptionCodeEnums
 * @author graysonzhang
 * @data 2019-07-02 16:38:13
 *
 */
@Getter
@ToString
@AllArgsConstructor
public enum DataStashExceptionCodeEnums {
    
    DATA_STASH_BINLOG_CRC32_ERROR(1000, "binlog crc32 error"),
    DATA_STASH_BINLOG_VERSION_ERROR(1001, "binlog version error"),
    DATA_STASH_STORAGE_TYPE_ERROR(2000, "system storage type error"),
    DATA_STASH_ENTRY_TYPE_ERROR(2001, "system entry type error"),
    DATA_STASH_ENCRYPT_TYPE(2002, "encrypt type error"),
    DATA_STASH_LOCAL_BINLOG_DIR_CREATE_FAILED(2003, "binlog dir create failed"),
    DATA_STASH_BINLOG_VERIFY_ERROR(2004, "binlog verify error"),
    DATA_STASH_ENTRY_NOT_MATCH_TABLE_ERROR(3000, "entry doesn't match any tables"),
    DATA_STASH_ENTRY_NO_TABLE_ERROR(3001, "table does not exist in sys_tables"),
    DATA_STASH_BLOCK_BYTES_LIST_IS_NULL(4000,"block bytes list is empty");
    private int code;
    
    @Setter
    private String message;
}
