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
package com.webank.blockchain.data.stash.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * BinlogFileUtils
 *
 * @Description: BinlogFileUtils
 * @author maojiayu
 * @data Oct 31, 2019 4:11:41 PM
 *
 */
@Slf4j
public class BinlogFileUtils {

    public static long floor(String path, String binlogSuffix, long blockNumber) {
        TreeSet<Long> filesSet = getFileIds(path, binlogSuffix);
        return filesSet.floor(blockNumber);
    }
    
    public static TreeSet<Long> getFileIds(String path, String binlogSuffix) {

        List<String> files = Arrays.asList(new File(path).list());
        log.debug("{}", JsonUtils.toJson(files));
        TreeSet<Long> filesSet = files.stream().filter(s -> StrUtil.endWith(s, binlogSuffix))
                .map(s -> StringUtils.substringBefore(s, ".")).filter(StringUtils::isNumeric).map(Long::parseLong)
                .collect(Collectors.toCollection(TreeSet::new));
        return filesSet;
    }

}
