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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.webank.blockchain.data.stash.config.SystemPropertyConfig;
import com.webank.blockchain.data.stash.entity.RemoteServerInfo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * CheckManager
 *
 * @Description: CheckManager
 * @author maojiayu
 * @data Oct 22, 2019 6:05:45 PM
 *
 */
@Service
@Slf4j
public class CheckManager {

    @Autowired
    private List<RemoteServerInfo> sources;
    @Autowired
    private SystemPropertyConfig config;

    public void check() {
        for (RemoteServerInfo info : sources) {
            List<String> files = Arrays.stream(new File(info.getLocalFilePath()).list())
                    .filter(s -> StrUtil.endWith(s, config.getBinlogSuffix()))
                    .map(s -> StringUtils.substringBefore(s, ".")).filter(StringUtils::isNumeric)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(files)) {
                log.error("Binlog file path {} is empty. ", info.getLocalFilePath());
                System.exit(2);
            }

        }
    }

}
