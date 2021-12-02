package com.webank.blockchain.data.stash.db.dao;

import com.webank.blockchain.data.stash.db.model.SysCommitteeVotesInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author aaronchu
 * @Description
 * @data 2021/01/07
 */
public interface SysCommitteeVotesInfoMapper extends BaseMapper  {

    int deleteByPrimaryKey(Long id);

    int insert(SysCommitteeVotesInfo record);

    SysCommitteeVotesInfo selectByPrimaryKey(Long id);

    SysCommitteeVotesInfo selectByBlockNumber(Long id);

    int insertDetail(SysCommitteeVotesInfo record);

    List<SysCommitteeVotesInfo> selectDetailByEntryId(Long id);

    int batchInsert(@Param("list")List<SysCommitteeVotesInfo> list);

    int batchInsertDetail(@Param("list")List<SysCommitteeVotesInfo> list);

    void deleteDetailByBlockNum(long num);

    List<SysCommitteeVotesInfo> selectDetailByBlockNum(long num);


}
