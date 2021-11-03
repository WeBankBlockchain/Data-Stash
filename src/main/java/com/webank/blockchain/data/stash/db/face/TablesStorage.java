package com.webank.blockchain.data.stash.db.face;

import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.query.model.TableData;
import com.webank.blockchain.data.stash.thread.MultiPartsTask;

import java.util.Map;

public interface TablesStorage {

    Map<String, TableDataInfo> fetchInterestedTables(BinlogBlockInfo blockInfo);

    void processTables(Map<String, TableDataInfo> tables, MultiPartsTask task);
}
