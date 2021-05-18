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
package com.webank.blockchain.data.stash.exception;

import com.webank.blockchain.data.stash.enums.DataStashExceptionCodeEnums;

/**
 * PkeyMgrException
 *
 * @Description: PkeyMgrException
 * @author graysonzhang
 * @data 2019-07-10 15:23:35
 *
 */
public class DataStashException extends RuntimeException {
    
    /** @Fields serialVersionUID : TODO */
    private static final long serialVersionUID = 893822168485972751L;
    private DataStashExceptionCodeEnums codeEnum;

    public DataStashException(DataStashExceptionCodeEnums codeEnum) {
        super(codeEnum.getMessage());
        this.codeEnum = codeEnum;
    }

    public DataStashException(String msg) {
        super(msg);
        this.codeEnum.setMessage(msg);
    }

    public DataStashException(Throwable cause) {
        super(cause);
    }

    public DataStashExceptionCodeEnums getCodeMessageEnums() {
        return codeEnum;
    }
    

}
