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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * ReadPropertyConfig
 *
 * @Description: ReadPropertyConfig
 * @author maojiayu
 * @data Oct 31, 2019 3:47:33 PM
 *
 */
@Configuration
@ConfigurationProperties("read")
@Data
public class ReadPropertyConfig {
    private int files = 3;
    private String clean = "yes";
    private String backupSuffix = "ok";

}
