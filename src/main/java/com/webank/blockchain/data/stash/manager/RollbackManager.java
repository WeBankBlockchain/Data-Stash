package com.webank.blockchain.data.stash.manager;

import com.webank.blockchain.data.stash.db.mapper.BlockTaskPoolMapper;
import com.webank.blockchain.data.stash.db.model.BlockTaskPool;
import com.webank.blockchain.data.stash.db.model.DynamicTableInfo;
import com.webank.blockchain.data.stash.db.model.SysTablesInfo;
import com.webank.blockchain.data.stash.db.service.CheckPointInfoService;
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

import java.util.*;

@Service
@Slf4j
public class RollbackManager {

    @Autowired
    private BlockTaskPoolMapper blockTaskPoolMapper;

    @Autowired
    private CheckPointInfoService checkPointInfoService;

    @Autowired
    private SysTablesInfoService sysTableInfoService;

    @Autowired
    private DynamicTableInfoService dynamicTableInfoService;

    public void rollbackUnfinished(){
        /**
         * 1. Find last finished data
         */
        BlockTaskPool blockTaskPool = this.blockTaskPoolMapper.getLastFinishedBlock();
        if(blockTaskPool == null) return;
        long lastFinishedBlock = blockTaskPool.getBlockHeight();
        long rollbackFrom = lastFinishedBlock + 1;
        /**
         * 2. Find block tables(_sys_, c_,cp_,u_...)
         */
        List<SysTablesInfo> tables = findTablesToRollback();
        /**
         * 3. Rollback detail table
         */
        rollbackTables(tables, rollbackFrom);
        /**
         * 4. Rebuild current table in case the current status are killed
         */
        rebuildCurrentTable(tables, lastFinishedBlock);

        /**
         * 5. Clear other tables(checkpoint, block_task_pool. Such tables does not have "_num_" column)
         */
        clearOtherTables(rollbackFrom);
        log.info("roll back uncleaned data complete. Start data stashing now.");
    }




    private List<SysTablesInfo> findTablesToRollback() {
        return this.sysTableInfoService.selectAllTables();
    }


    private void rollbackTables(List<SysTablesInfo> tables, long rollbackFrom) {
        for (SysTablesInfo t : tables) {
            log.info("start rolling back {}..", t.getTableName());
            this.sysTableInfoService.rollbackFrom(t.getTableName(), rollbackFrom);
            log.info("table {} rolled back.", t);
        }
    }

    private void rebuildCurrentTable(List<SysTablesInfo> tables, long topBlockNum) {
        for (SysTablesInfo table : tables) {
            log.info("table name : {} in sys tables", table.getTableName());
            // 1. delete detail entry info
            String detailTableName = CommonUtil.getDetailTableName(table.getTableName());
            // 2. get block data by block num
            List<Map<String, Object>> entrys = sysTableInfoService.selectBlockDataByBlockNum(table.getTableName(), topBlockNum);
            log.debug("size : {}, table : {}, content : {}", entrys.size(), table.getTableName(),
                    JsonUtils.toJson(entrys));

            for (Map<String, Object> map : entrys) {

                log.debug("id : {}", map.get("_id_"));

                long id = Long.valueOf(map.get("_id_").toString());

                List<Map<String, Object>> detailEntrys = sysTableInfoService.selectTopOneById(detailTableName, id);

                if (detailEntrys == null || detailEntrys.size() != 1) {
                    sysTableInfoService.deleteById(table.getTableName(), id);
                } else {
                    Map<String, Object> columnMap = detailEntrys.get(0);
                    EntryInfo entryInfo = new EntryInfo();

                    entryInfo.setId(id);
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
        }

    }

    private void clearOtherTables(long rollbackFrom) {
        this.checkPointInfoService.rollbackByBlockNum(rollbackFrom);
        this.blockTaskPoolMapper.rollbackByBlockNumber(rollbackFrom);
    }
}
