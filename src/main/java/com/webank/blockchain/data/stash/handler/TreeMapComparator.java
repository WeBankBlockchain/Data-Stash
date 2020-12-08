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
package com.webank.blockchain.data.stash.handler;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * TreeMapComparator
 *
 * @Description: TreeMapComparator
 * @author maojiayu
 * @data Oct 24, 2019 7:40:08 PM
 *
 */
public class TreeMapComparator implements Comparator<TreeMap<String, String>> {

   
    @Override
    public int compare(TreeMap<String, String> o1, TreeMap<String, String> o2) {
        Long id1 = Long.parseLong(String.valueOf(o1.get("_id_")));
        Long id2 = Long.parseLong(String.valueOf(o2.get("_id_")));
        return id1.compareTo(id2);
    }

}
