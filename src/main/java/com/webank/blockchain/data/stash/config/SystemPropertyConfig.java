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

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

import java.io.File;

/**
 * SysConfig
 *
 * @Description: SysConfig
 * @author graysonzhang
 * @data 2019-07-31 16:01:58
 *
 */
@Configuration
@ConfigurationProperties("system")
@Data
public class SystemPropertyConfig {

    private String binlogAddress;
    private String localBinlogPath;
    private String binlogSuffix = "binlog";

    private int binlogVerify= 1;
    private int checkPointVerify = 1;

    private int batchCount = 5;
    private int encryptType = 0;

    private int parseThreads = Runtime.getRuntime().availableProcessors();
    private int sqlThreads = 100;
    private int parseQueueSize = 500;
    private int sqlQueueSize = 500;


    public String getLocalBinlogPath() {
        if (StringUtils.isEmpty(this.localBinlogPath)) {
            this.localBinlogPath = "binlogcache" + File.separator;
        }
        if (!StringUtils.endsWith(this.localBinlogPath, File.separator)) {
            return this.localBinlogPath + File.separator;
        }
        return this.localBinlogPath;
    }
}
