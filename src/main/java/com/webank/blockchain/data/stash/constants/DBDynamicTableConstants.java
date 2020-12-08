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
package com.webank.blockchain.data.stash.constants;

/**
 * DBServiceConstants
 *
 * @Description: DBServiceConstants
 * @author graysonzhang
 * @data 2019-08-10 00:56:53
 *
 */
public class DBDynamicTableConstants {
    
    
    /** sys and contract table prefix */
    public static final String SYS_TABLE_PRE_FIX = "_sys_";
    //public static final String CONTRACT_DATA_PRE_FIX = "_contract_data_";
    //public static final String CONTRAACT_PARAFUNC_FIX = "_contract_parafunc_";
    
    public static final String CONTRACT_DATA_PRE_FIX = "c_";
    public static final String CONTRAACT_PARAFUNC_FIX = "cp_";
    
    /**  service info */
    public static final String DB_SERVICE_POST_FIX = "InfoService";
    public static final String CONTRACT_DATA_INFO_SERVICE = "contractDataInfoService";
    public static final String CONTRACT_PARAFUNC_INFO_SERVICE = "contractParaFuncInfoService";
    public static final String DYLAMIC_TABLE_INFO_SERVICE = "dylamicTableInfoService";

}
