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

import java.util.Date;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.webank.blockchain.data.stash.db.model.BlockTaskPool;

/**
 * BlockTaskPoolMapper
 *
 * @Description: BlockTaskPoolMapper
 * @author maojiayu
 * @data Aug 28, 2019 2:56:56 PM
 *
 */
public interface BlockTaskPoolMapper {

    @Update("CREATE TABLE if not exists `block_task_pool` (`pk_id` bigint(20) NOT NULL AUTO_INCREMENT,\n"
            + "  `block_height` bigint(20) DEFAULT NULL,\n" + "  `length` bigint(20) DEFAULT 0,\n"
            + "  `block_timestamp` datetime(6) NOT NULL,\n" + "  `updatetime` datetime(6) DEFAULT NULL,\n"
            + "  `sync_status` int(11) DEFAULT NULL,\n" + "  `certainty` int(11) DEFAULT 0,\n"
            + "  PRIMARY KEY (`pk_id`),\n" + "  UNIQUE KEY `block_height` (`block_height`),\n"
            + "  KEY `sync_status_block_task_pool` (`sync_status`),\n"
            + "  KEY `certainty_block_task_pool` (`certainty`)\n"
            + ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;")
    public void createTable();

    @Insert("insert into block_task_pool(block_height, certainty ,block_timestamp, updatetime, sync_status)\n"
            + "values(#{blockHeight}, #{certainty}, #{blockTime}, #{updatetime}, #{syncStatus})")
    int insert(BlockTaskPool blockTaskPool);

    @Select("SELECT * FROM block_task_pool WHERE block_height = #{blockHeight}")
    @Results({ @Result(property = "pkId", column = "pk_id"), @Result(property = "blockHeight", column = "block_height"),
            @Result(property = "binlogName", column = "binlog_name"),
            @Result(property = "blockTime", column = "block_timestamp"),
            @Result(property = "syncStatus", column = "sync_status") })
    BlockTaskPool getByBlockHeight(long blockHeight);
    

    @Select("SELECT * FROM block_task_pool order by block_height desc limit 1;")
    @Results({ @Result(property = "pkId", column = "pk_id"), @Result(property = "blockHeight", column = "block_height"),
            @Result(property = "blockTime", column = "block_timestamp"),
            @Result(property = "syncStatus", column = "sync_status") })
    BlockTaskPool getLatestOne();

    @Select("SELECT * FROM block_task_pool WHERE sync_status = #{syncStatus} order by block_height desc limit 1;")
    @Results({ @Result(property = "pkId", column = "pk_id"), @Result(property = "blockHeight", column = "block_height"),
            @Result(property = "blockTime", column = "block_timestamp"),
            @Result(property = "syncStatus", column = "sync_status") })
    BlockTaskPool getLatestOneBySyncStatus(int syncStatus);
    
    @Update("update block_task_pool set block_timestamp=#{block_timestamp} where block_height=#{block_height}")
    void updateBlockTimestampByBlockHeight(@Param("block_timestamp") Date blockTimeStamp,
            @Param("block_height") long blockHeight);

    @Update("update block_task_pool set sync_status=#{sync_status} where block_height=#{block_height}")
    void updateSyncStatusByBlockHeight(@Param("sync_status") int syncStatus, @Param("block_height") long blockHeight);

}
