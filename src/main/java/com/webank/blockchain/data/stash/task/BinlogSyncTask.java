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

import com.webank.blockchain.data.stash.config.ReadPropertyConfig;
import com.webank.blockchain.data.stash.manager.CheckManager;
import com.webank.blockchain.data.stash.manager.CleanManager;
import com.webank.blockchain.data.stash.manager.DownloadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.webank.blockchain.data.stash.config.ReadPropertyConfig;
import com.webank.blockchain.data.stash.manager.BlockReadManager;
import com.webank.blockchain.data.stash.manager.CheckManager;
import com.webank.blockchain.data.stash.manager.CleanManager;
import com.webank.blockchain.data.stash.manager.DownloadManager;

import lombok.Data;

/**
 * BinlogSyncTask
 *
 * @Description: BinlogSyncTask
 * @author maojiayu
 * @data Aug 27, 2019 5:16:23 PM
 *
 */
@Component
@Data
public class BinlogSyncTask implements ApplicationRunner {

    @Autowired
    private DownloadManager downloadManager;
    @Autowired
    private BlockReadManager blockReadManager;
    @Autowired
    private CheckManager checkManager;
    @Autowired
    private CleanManager cleanManager;
    @Autowired
    private ReadPropertyConfig readConfig;
    private boolean button = true;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (readConfig.getFiles() < 3) {
            readConfig.setFiles(3);
        }

        while (true) {
            downloadManager.download();
            checkManager.check();
            blockReadManager.read();
            cleanManager.clean();
            Thread.sleep(5000);
            if (!button) {
                break;
            }
        }
    }

}
