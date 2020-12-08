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
package com.webank.blockchain.data.stash.db.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.webank.blockchain.data.stash.db.model.BinlogOffset;

/**
 * BinlogOffset
 *
 * @Description: BinlogOffset
 * @author maojiayu
 * @data Aug 29, 2019 5:53:04 PM
 *
 */
public interface BinlogOffsetMapper {
    @Update("CREATE TABLE if not exists `binlog_offset` (\n" + "  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,\n"
            + "  `block_height` bigint(20) DEFAULT NULL,\n" + "  `binlog_name` varchar(128) DEFAULT '',\n"
            + "  `offset` bigint(20) DEFAULT '0',\n" + "  `length` bigint(20) DEFAULT '0',\n"
            + "  `updatetime` datetime(6) DEFAULT NULL,\n" + "  `item` int(11) DEFAULT NULL,\n"
            + "  PRIMARY KEY (`pk_id`),\n" + "  UNIQUE KEY `block_height` (`block_height`,`item`)\n"
            + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;")
    public void createTable();

    @Insert("insert into binlog_offset(block_height, binlog_name ,offset,length, updatetime, item)\n"
            + "values(#{blockHeight}, #{binlogName}, #{offset}, #{length}, #{updatetime}, #{item})")
    int insert(BinlogOffset binlogOffset);

    @Select("SELECT * FROM binlog_offset WHERE block_height = #{block_height} and item = #{item}")
    @Results({ @Result(property = "pkId", column = "pk_id"), @Result(property = "blockHeight", column = "block_height"),
            @Result(property = "binlogName", column = "binlog_name") })
    BinlogOffset getByBlockHeightAndItem(@Param("block_height") long blockHeight, @Param("item") int item);

    @Delete("delete from binlog_offset WHERE block_height = #{block_height} and item = #{item}")
    int deleteByBlockHeightAndItem(@Param("block_height") long blockHeight, @Param("item") int item);

    @Delete("delete from binlog_offset WHERE block_height = #{block_height}")
    int deleteByBlockHeight(@Param("block_height") long blockHeight);
}
