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

import cn.hutool.core.io.StreamProgress;
import cn.hutool.http.HttpException;
import com.webank.blockchain.data.stash.config.ReadPropertyConfig;
import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.db.mapper.BlockTaskPoolMapper;
import com.webank.blockchain.data.stash.db.model.BlockTaskPool;
import com.webank.blockchain.data.stash.entity.RemoteServerInfo;
import com.webank.blockchain.data.stash.fetch.BinlogFileDir;
import com.webank.blockchain.data.stash.fetch.DefaultStreamProgress;
import com.webank.blockchain.data.stash.fetch.HttpFileFetcher;
import com.webank.blockchain.data.stash.fetch.HttpFileScanner;
import com.webank.blockchain.data.stash.utils.BinlogFileUtils;
import com.webank.blockchain.data.stash.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * DownloadManager
 *
 * @Description: DownloadManager
 * @author maojiayu
 * @data Aug 27, 2019 5:23:42 PM
 *
 */
@Service
@Slf4j
public class DownloadManager {
    @Autowired
    private List<RemoteServerInfo> sources;
    @Autowired
    private SystemPropertyConfig systemPropertyConfig;
    @Autowired
    private ReadPropertyConfig readPropertyConfig;
    @Autowired
    private BlockTaskPoolMapper blockTaskPoolMapper;

    public void download() throws IOException, InterruptedException {
        for (int i = 0; i < sources.size(); i++) {
            downloadFirstItem(sources.get(i));
        }
    }

    public BinlogFileDir scanWithRetry(String url, int maxTimes) throws IOException {
        BinlogFileDir dir = HttpFileScanner.scan(url);
        if (maxTimes > 1 && dir.getSize() <= 0) {
            return scanWithRetry(url, maxTimes - 1);
        } else {
            return dir;
        }
    }

    public TreeSet<Long> downloadFirstItem(RemoteServerInfo server) throws IOException, InterruptedException {
        TreeSet<Long> localFiles =
                BinlogFileUtils.getFileIds(server.getLocalFilePath(), systemPropertyConfig.getBinlogSuffix());
        BinlogFileDir dir = scanWithRetry(server.getUrl(), 3);
        log.info("Scan remote item {}, size: {}", 0, dir.getSize());
        if (dir.getSize() == 0) {
            return localFiles;
        }
        TreeSet<Long> remote = dir.getBinlogFileInfoList().stream().map(e -> e.getIndex())
                .collect(Collectors.toCollection(TreeSet::new));

        // start from previous block task.
        long last = 0;
        if (!CollectionUtils.isEmpty(localFiles)) {
            last = localFiles.last();
            log.info("Download start from last local file: {}", last);
        } else {
            BlockTaskPool b =
                    blockTaskPoolMapper.getLastFinishedBlock();
            if (b != null && b.getBlockHeight() > 0) {
                last = remote.floor(b.getBlockHeight());
            }
            log.info("Download start from last task: {}", last);
        }
//        download(server, last);
        localFiles.add(last);
        while (localFiles.size() < readPropertyConfig.getFiles()) {
            if (last >= remote.last()) {
                break;
            }
            long todo = remote.higher(last);
            log.debug("remote: {}", JsonUtils.toJson(remote));
            log.info("next to do is {}", todo);
            download(server, todo);
            localFiles.add(todo);
            if (todo == remote.last()) {
                log.info("Nothing to download.");
                Thread.sleep(5 * 1000L);
                break;
            }
            last = todo;
        }
        return localFiles;
    }

    public void download(RemoteServerInfo server, long index) throws IOException {
        String fileName = index + "." + systemPropertyConfig.getBinlogSuffix();
        File destFile = new File(server.getLocalFilePath() + fileName);
        log.info("Begin to download {}, binlog file: {} ", server.getUrl(), index);
        downloadWithRetry(server.getUrl() + fileName, destFile, new DefaultStreamProgress(), 3);
    }

    public void downloadWithRetry(String url, File destFile, StreamProgress streamProgress, int maxTimes)
            throws IOException {
        try {
            HttpFileFetcher.downloadFile(url, destFile, streamProgress);
        } catch (HttpException e) {
            log.error("Exception {}", e.getMessage());
            if (maxTimes > 1) {
                downloadWithRetry(url, destFile, streamProgress, maxTimes - 1);
            }
        }
    }
}
