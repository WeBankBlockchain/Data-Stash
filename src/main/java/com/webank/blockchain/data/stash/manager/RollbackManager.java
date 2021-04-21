package com.webank.blockchain.data.stash.manager;

import com.webank.blockchain.data.stash.db.mapper.BlockTaskPoolMapper;
import com.webank.blockchain.data.stash.db.model.BlockTaskPool;
import com.webank.blockchain.data.stash.db.model.SysTablesInfo;
import com.webank.blockchain.data.stash.db.service.CheckPointInfoService;
import com.webank.blockchain.data.stash.db.service.DynamicTableInfoService;
import com.webank.blockchain.data.stash.db.service.SysTablesInfoService;
import com.webank.blockchain.data.stash.utils.CommonUtil;
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

    @Autowired
    private RecoverSnapshotService recoverSnapshotService;

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
        rebuildCurrentTable();

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
            this.sysTableInfoService.rollbackFrom(CommonUtil.getDetailTableName(t.getTableName()), rollbackFrom);
            log.info("table {} rolled back.", t);
        }
    }

    private void rebuildCurrentTable() {
        recoverSnapshotService.recoverSnapshotFromDetailTables();

    }

    private void clearOtherTables(long rollbackFrom) {
        this.checkPointInfoService.rollbackByBlockNum(rollbackFrom);
        this.blockTaskPoolMapper.rollbackByBlockNumber(rollbackFrom);
    }
}
