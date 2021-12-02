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
package com.webank.blockchain.data.stash.fetch;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * HttpFileFetcher
 *
 * @Description: HttpFileFetcher
 * @author maojiayu
 * @data Aug 19, 2019 11:44:04 AM
 *
 */
@Slf4j
public class HttpFileFetcher {

    public static long downloadFile(String url, File destFile, StreamProgress streamProgress)
            throws FileNotFoundException {
        if (StrUtil.isBlank(url)) {
            throw new NullPointerException("[url] is null!");
        }
        if (null == destFile) {
            throw new NullPointerException("[destFile] is null!");
        }
        if (!destFile.exists()) {
            FileUtil.touch(destFile);
        }
        long fileSize = FileUtil.size(destFile);
        final HttpResponse response =
                HttpRequest.get(url).timeout(300 * 1000).header("Range", "bytes=" + fileSize + "-")
                        .header(Header.USER_AGENT, "Data Stash http").header(Header.CONNECTION, "keep-alive").execute();
        if (response.getStatus() != 206) {
            if (response.getStatus() == 416) {
                log.info("Already download all file.");
                return 0;
            }
            throw new HttpException("Server response error with status code: [{}]", response.getStatus());
        }
        return response.writeBody(new FileOutputStream(destFile.getAbsolutePath(), true), false, streamProgress);
    }

}
