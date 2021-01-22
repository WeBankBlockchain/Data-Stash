package com.webank.blockchain.data.stash.db.service;

import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.db.dao.SysCommitteeVotesInfoMapper;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.db.model.SysCommitteeVotesInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * @author aaronchu
 * @Description
 * @data 2021/01/07
 */
@Slf4j
@Service
@SuppressWarnings("unchecked")
public class SysCommitteeVotesInfoService extends DBBaseOperation implements StorageService {
    private static final int SYS_COMMITTEE_VOTES_BATCH = 2;

    @Autowired
    private SysCommitteeVotesInfoMapper mapper;

    @Override
    public void createSchema() throws SQLException {
        mapper.createTable(DBStaticTableConstants.SYS_COMMITTE_VOTES_TABLE);

        String detailTableName = DBStaticTableConstants.SYS_COMMITTE_VOTES_TABLE + DBStaticTableConstants.SYS_DETAIL_TABLE_POST_FIX;
        mapper.createDetailTable(detailTableName);
    }

    @Override
    public void storageTabelData(String tableName, TableDataInfo tableDataInfo) throws DataStashException {
        storage(tableName, tableDataInfo, SysCommitteeVotesInfo.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void batchSave(String tableName, List list) {

        log.debug("list : {}", JsonUtils.toJson(list));

        int batchLastIndex = SYS_COMMITTEE_VOTES_BATCH;

        for (int index = 0; index < list.size();) {

            if (SYS_COMMITTEE_VOTES_BATCH >= list.size() - index) {
                batchLastIndex = list.size();
                mapper.batchInsert((List<SysCommitteeVotesInfo>)list.subList(index, batchLastIndex));
                break;
            } else {
                mapper.batchInsert((List<SysCommitteeVotesInfo>)list.subList(index, batchLastIndex));
                index = batchLastIndex;
                batchLastIndex = index + SYS_COMMITTEE_VOTES_BATCH;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void batchSaveDetail(String tableName, List list) {
        int batchLastIndex = SYS_COMMITTEE_VOTES_BATCH;

        for (int index = 0; index < list.size();) {

            if (SYS_COMMITTEE_VOTES_BATCH >= list.size() - index) {
                batchLastIndex = list.size();
                mapper.batchInsertDetail((List<SysCommitteeVotesInfo>)list.subList(index, batchLastIndex));
                break;
            } else {
                mapper.batchInsertDetail((List<SysCommitteeVotesInfo>)list.subList(index, batchLastIndex));
                index = batchLastIndex;
                batchLastIndex = index + SYS_COMMITTEE_VOTES_BATCH;
            }
        }
    }
}
