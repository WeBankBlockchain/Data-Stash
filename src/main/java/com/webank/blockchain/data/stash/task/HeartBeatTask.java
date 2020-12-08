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
package com.webank.blockchain.data.stash.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.db.service.DylamicTableInfoService;

import lombok.extern.slf4j.Slf4j;

/**
 * HeartBeatTask
 *
 * @Description: HeartBeatTask
 * @author graysonzhang
 * @data 2019-11-01 10:41:06
 *
 */
@Component
@EnableScheduling
@Slf4j
public class HeartBeatTask {

    @Autowired
    private DylamicTableInfoService service;
    
    @Scheduled(fixedDelay = 60 * 1000)
    public void sendHeartBeat() throws Exception {
        log.debug("Send heart beat start..");
        service.checkTable(DBStaticTableConstants.SYS_CONFIG_TABLE);  
        log.debug("Send heart beat end..");
    }
}
