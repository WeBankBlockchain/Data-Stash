package com.webank.blockchain.data.stash.manager;

import com.webank.blockchain.data.stash.db.mapper.BlockTaskPoolMapper;
import com.webank.blockchain.data.stash.db.model.DynamicTableInfo;
import com.webank.blockchain.data.stash.db.model.SysTablesInfo;
import com.webank.blockchain.data.stash.db.service.DynamicTableInfoService;
import com.webank.blockchain.data.stash.db.service.SysTablesInfoService;
import com.webank.blockchain.data.stash.entity.ColumnInfo;
import com.webank.blockchain.data.stash.entity.EntryInfo;
import com.webank.blockchain.data.stash.utils.CommonUtil;
import com.webank.blockchain.data.stash.utils.JsonUtils;
import com.webank.blockchain.data.stash.utils.ObjectBuildUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * cucurrent replace into will cause deadlock
 */
@Service
@Slf4j
public class RecoverSnapshotManager {

    @Autowired
    private SysTablesInfoService sysTableInfoService;

    @Autowired
    private DynamicTableInfoService dynamicTableInfoService;


    public void recoverSnapshotFromDetailTables() {
        log.info("Start rebuilding current table from detail table");
        List<SysTablesInfo> tables = sysTableInfoService.selectAllTables();
        for (SysTablesInfo table : tables) {
            log.info("table name : {} in sys tables", table.getTableName());
            // 1. delete detail entry info
            String detailTableName = CommonUtil.getDetailTableName(table.getTableName());
            // 2. get block data by block num
            List<Map<String, Object>> detailEntrys = sysTableInfoService.selectDataForTopBlock(detailTableName);

            if (detailEntrys == null || detailEntrys.isEmpty()) {
                continue;
            }
            for(Map<String, Object> columnMap: detailEntrys){
                EntryInfo entryInfo = new EntryInfo();

                entryInfo.setId(Long.parseLong(columnMap.get("_id_").toString()));
                entryInfo.setHash(columnMap.get("_hash_").toString());
                entryInfo.setStatus(Integer.valueOf(columnMap.get("_status_").toString()));
                entryInfo.setNum(Long.valueOf(columnMap.get("_num_").toString()));

                columnMap.remove("pk_id");
                columnMap.remove("_id_");
                columnMap.remove("_hash_");
                columnMap.remove("_status_");
                columnMap.remove("_num_");

                List<ColumnInfo> colums = new ArrayList<ColumnInfo>();
                for (String key : columnMap.keySet()) {
                    ColumnInfo columnInfo = new ColumnInfo();
                    columnInfo.setColumnName(key);
                    columnInfo.setColumnValue(columnMap.get(key).toString());
                    colums.add(columnInfo);
                }

                entryInfo.setColumns(colums);

                DynamicTableInfo dynamicTableInfo = new DynamicTableInfo();
                ObjectBuildUtil.buildToDynamicObj(entryInfo, dynamicTableInfo);

                dynamicTableInfoService.save(table.getTableName(), dynamicTableInfo);
            }
        }
        log.info("Rebuilding current table from detail table complete");

    }
}
