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

import com.google.common.collect.Lists;
import com.webank.blockchain.data.stash.constants.CyptoConstants;
import com.webank.blockchain.data.stash.crypto.CyptoInterface;
import com.webank.blockchain.data.stash.crypto.StandardCryptoService;
import com.webank.blockchain.data.stash.crypto.sm.SMCryptoService;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.db.service.*;
import com.webank.blockchain.data.stash.entity.RemoteServerInfo;
import com.webank.blockchain.data.stash.enums.DataStashExceptionCodeEnums;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.store.LedgerTablesStorage;
import com.webank.blockchain.data.stash.store.StateTablesStorage;
import com.webank.blockchain.data.stash.thread.DataStashThreadFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private static final Set<String> LEDGER_SERVICE_CLASSES = new HashSet<String>(){
        {
            add(SysBlock2NoncesInfoService.class.getSimpleName().toLowerCase());
            add(SysHash2BlockInfoService.class.getSimpleName().toLowerCase());
            add(SysHash2HeaderInfoService.class.getSimpleName().toLowerCase());
            add(SysNumber2HashInfoService.class.getSimpleName().toLowerCase());
            add(SysTxHash2BlockInfoService.class.getSimpleName().toLowerCase());
        }
    };
    @Bean
    public LedgerTablesStorage ledgerDBStorage(Map<String, StorageService> services) throws DataStashException {
        return new LedgerTablesStorage(extractLedgerServices(services), ledgerPool());
    }

    @Bean
    public StateTablesStorage stateDBStorage(Map<String, StorageService> services) throws DataStashException{
        return new StateTablesStorage(extractLedgerServices(services), extractStateServices(services), statePool());
    }


    public ThreadPoolExecutor ledgerPool(){
        //Dont use unbound arrays, otherwise OOM will happen!
        return new ThreadPoolExecutor(systemPropertyConfig.getLedgerThreads(),systemPropertyConfig.getLedgerThreads(),
                0, TimeUnit.DAYS, new LinkedBlockingQueue<>(systemPropertyConfig.getLedgerQueueSize()), new DataStashThreadFactory("ledgerPool"),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }


    public ThreadPoolExecutor statePool(){
        return new ThreadPoolExecutor(systemPropertyConfig.getStateThreads(),systemPropertyConfig.getStateThreads(),
                0, TimeUnit.DAYS, new LinkedBlockingQueue<>(systemPropertyConfig.getStateQueueSize()), new DataStashThreadFactory("statePool"),
                new ThreadPoolExecutor.CallerRunsPolicy());
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

    private Map<String, StorageService> extractLedgerServices(Map<String, StorageService> allServices){
        Map<String, StorageService> services = new HashMap<>();
        for(Map.Entry<String, StorageService> serviceEntry: allServices.entrySet()){
            String bean = serviceEntry.getKey().toLowerCase();
            if(LEDGER_SERVICE_CLASSES.contains(bean)){
                services.put(serviceEntry.getKey(), serviceEntry.getValue());
            }
        }
        return services;
    }

    private Map<String, StorageService> extractStateServices(Map<String, StorageService> allServices){
        Map<String, StorageService> services = new HashMap<>();
        for(Map.Entry<String, StorageService> serviceEntry: allServices.entrySet()){
            String bean = serviceEntry.getKey().toLowerCase();
            if(!LEDGER_SERVICE_CLASSES.contains(bean)){
                services.put(serviceEntry.getKey(), serviceEntry.getValue());
            }
        }
        return services;
    }
}
