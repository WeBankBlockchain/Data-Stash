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
package com.webank.blockchain.data.stash.manager;

import java.io.File;
import java.util.List;
import java.util.TreeSet;

import com.webank.blockchain.data.stash.db.mapper.BlockTaskPoolMapper;
import com.webank.blockchain.data.stash.db.model.BlockTaskPool;
import com.webank.blockchain.data.stash.entity.RemoteServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.blockchain.data.stash.config.ReadPropertyConfig;
import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.utils.BinlogFileUtils;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * CleanManager
 *
 * @Description: CleanManager
 * @author maojiayu
 * @data Oct 31, 2019 8:58:40 PM
 *
 */
@Service
@Slf4j
public class CleanManager {
    @Autowired
    private List<RemoteServerInfo> sources;
    @Autowired
    private BlockTaskPoolMapper blockTaskPoolMapper;
    @Autowired
    private SystemPropertyConfig systemPropertyConfig;
    @Autowired
    private ReadPropertyConfig readPropertyConfig;

    public void clean() {
        BlockTaskPool latest =
                blockTaskPoolMapper.getLastFinishedBlock();
        if(latest == null) return;
        for (RemoteServerInfo server : sources) {
            TreeSet<Long> localFiles =
                    BinlogFileUtils.getFileIds(server.getLocalFilePath(), systemPropertyConfig.getBinlogSuffix());
            long floor = localFiles.floor(latest.getBlockHeight());
            for (long l : localFiles) {
                if (l < floor) {
                    remove(server, l);
                }
            }
        }
    }

    public void remove(RemoteServerInfo server, long index) {
        String fileName = index + "." + systemPropertyConfig.getBinlogSuffix();
        File destFile = new File(server.getLocalFilePath() + fileName);
        log.info("Delete file: {}", destFile.getAbsolutePath());
        if (readPropertyConfig.getClean().equalsIgnoreCase("yes")) {
            FileUtil.del(destFile);
        } else {
            FileUtil.rename(destFile, fileName + "." + readPropertyConfig.getBackupSuffix(), false, true);
        }
    }

}
