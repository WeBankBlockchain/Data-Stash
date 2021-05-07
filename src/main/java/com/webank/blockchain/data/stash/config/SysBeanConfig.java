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
package com.webank.blockchain.data.stash.config;

import java.io.File;
import java.util.List;

import com.webank.blockchain.data.stash.crypto.CyptoInterface;
import com.webank.blockchain.data.stash.db.face.DataStorage;
import com.webank.blockchain.data.stash.entity.RemoteServerInfo;
import com.webank.blockchain.data.stash.enums.DataStashExceptionCodeEnums;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.store.DBDataStorage;
import com.webank.blockchain.data.stash.constants.CyptoConstants;
import com.webank.blockchain.data.stash.crypto.StandardCryptoService;
import com.webank.blockchain.data.stash.crypto.sm.SMCryptoService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;

/**
 * SysStorageConfig
 *
 * @Description: SysStorageConfig
 * @author graysonzhang
 * @author maojiayu
 * @data 2019-08-09 14:16:45
 *
 */
@Configuration
public class SysBeanConfig {

    @Autowired
    private SystemPropertyConfig systemPropertyConfig;

    @Bean
    public DataStorage initDataStorage() throws DataStashException {
        return new DBDataStorage();
    }

    @Bean
    public List<RemoteServerInfo> getRemoteServerInfoList() throws DataStashException {
        String address = systemPropertyConfig.getBinlogAddress();
        String[] tokens = StringUtils.split(address, ",");
        if (ArrayUtils.isEmpty(tokens)) {
            throw new RuntimeException(
                    "The config of system.binlogAddress is empty, please check the application.properties.");
        }
        List<RemoteServerInfo> list = Lists.newArrayListWithCapacity(tokens.length);
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if(!token.endsWith("/")){
                token+="/";
            }
            RemoteServerInfo info = new RemoteServerInfo();
            String dir = systemPropertyConfig.getLocalBinlogPath() + i + File.separator;
            info.setUrl(token).setIp(StringUtils.substringAfter(StringUtils.substringBefore(token, ":"), "//"))
                    .setItem(i).setLocalFilePath(dir);
            File dirFile = new File(dir);
            if(!dirFile.exists() && !new File(dir).mkdirs()){
                throw new DataStashException(DataStashExceptionCodeEnums.DATA_STASH_LOCAL_BINLOG_DIR_CREATE_FAILED);
            }
            list.add(info);
        }
        return list;
    }

    @Bean
    public CyptoInterface cyptoInterfaceBean() throws DataStashException {
        int encryptType = systemPropertyConfig.getEncryptType();
        switch (encryptType) {
            case CyptoConstants.ECDSA:
                return new StandardCryptoService();
            case CyptoConstants.GM:
                return new SMCryptoService();
            default:
                throw new DataStashException(DataStashExceptionCodeEnums.DATA_STASH_ENCRYPT_TYPE);
        }
    }

}
