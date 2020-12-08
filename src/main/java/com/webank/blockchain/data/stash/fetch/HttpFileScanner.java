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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import com.webank.blockchain.data.stash.utils.UnixDateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.webank.blockchain.data.stash.utils.UnixDateTimeUtils;
import cn.hutool.http.Header;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

/**
 * HttpFileScanner
 *
 * @Description: HttpFileScanner
 * @author maojiayu
 * @data Aug 19, 2019 3:38:59 PM
 *
 */
@Service
public class HttpFileScanner {

    public static BinlogFileDir scan(String url) throws IOException {
        final HttpResponse response = HttpRequest.get(url).timeout(5 * 1000)
                .header(Header.USER_AGENT,
                       "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:68.0) Gecko/20100101 Firefox/68.0")
                .keepAlive(true)
                .execute();
        if (response.getStatus() != 200) {
            throw new HttpException("Server response error with status code: [{}]", response.getStatus());
        }
        String body = response.body();
        String[] s = StringUtils.split(body, "\n");
        BinlogFileDir dir = new BinlogFileDir();
        for (int i = 4; i < s.length - 2; i++) {
            Optional<BinlogFileInfo> fileInfo = parseFile(s[i]);
            if (fileInfo.isPresent()) {
                dir.getBinlogFileInfoList().add(fileInfo.get());
            }
        }
        return dir;
    }

    public static Optional<BinlogFileInfo> parseFile(String line) {
        if (StringUtils.contains(line, "</a>") && StringUtils.startsWith(line, "<a href=\"")) {
            String[] tokens = StringUtils.splitByWholeSeparator(line, "          ");
            if (tokens.length < 3) {
                return Optional.empty();
            }
            long length = Long.parseLong(tokens[2].trim());
            LocalDateTime modifyTime = UnixDateTimeUtils.parse(tokens[1]);
            String fileName = StringUtils.substringBefore(StringUtils.substringAfter(tokens[0], "\">"), "</a>");
            BinlogFileInfo binlogFileInfo = new BinlogFileInfo();
            String s = StringUtils.substringBefore(fileName, ".");
            if (!StringUtils.isNumeric(s) || !StringUtils.endsWithIgnoreCase(fileName, ".binlog")) {
                return Optional.empty();
            }
            binlogFileInfo.setLastModifyTime(modifyTime).setLength(length).setName(fileName)
                    .setIndex(Long.parseLong(s));
            return Optional.of(binlogFileInfo);
        } else {
            return Optional.empty();
        }
    }
}
