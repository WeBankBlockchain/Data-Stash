/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.stash.db.service;

import com.webank.blockchain.data.stash.BaseTest;
import com.webank.blockchain.data.stash.db.model.DylamicTableInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.webank.blockchain.data.stash.BaseTest;
import com.webank.blockchain.data.stash.db.model.DylamicTableInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * UserTableInfoServiceTest
 *
 * @Description: UserTableInfoServiceTest
 * @author graysonzhang
 * @data 2019-08-08 15:01:49
 *
 */
@Slf4j
public class UserTableInfoServiceTest extends BaseTest {
    
    @Autowired
    private DylamicTableInfoService userTableInfoService;
    
    @Test
    public void testSave(){
        DylamicTableInfo userTableInfo = new DylamicTableInfo();
        userTableInfo.setId(123L);
        userTableInfo.setStatus(0);
        userTableInfo.setNum(0L);
        
        log.info("userTableInfo : {}", userTableInfo);
        
        String tableName = "test_table";
        
        userTableInfoService.save(tableName,userTableInfo);
    }
    
}
