package com.webank.blockchain.data.stash.store;

import com.webank.blockchain.data.stash.aspect.UseTime;
import com.webank.blockchain.data.stash.constants.DBDynamicTableConstants;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.db.face.TablesStorage;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.thread.MultiPartsTask;
import com.webank.blockchain.data.stash.utils.StringStyleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Only process state data inside
 * @author aaronchu
 * @Description
 * @date 2021/11/03
 */
@Slf4j
public class StateTablesStorage implements TablesStorage {


    private Map<String, StorageService> ledgerTableServices;
    private Map<String, StorageService> stateTableServices;

    private ThreadPoolExecutor statePool;

    public StateTablesStorage(Map<String, StorageService> ledgerTableServices, Map<String, StorageService> stateTableServices, ThreadPoolExecutor statePool ){
        this.ledgerTableServices = ledgerTableServices;
        this.stateTableServices = stateTableServices;
        this.statePool = statePool;
    }

    @PostConstruct
    @Transactional
    public void initSchema() throws SQLException {
        for(Map.Entry<String, StorageService> entry : stateTableServices.entrySet()){
            entry.getValue().createSchema();
        }
    }


    @UseTime
    public void processSingleTable(String tableName, TableDataInfo tableDataInfo) throws DataStashException {
        String serviceName = StringStyleUtils.underline2upper(tableName) + DBDynamicTableConstants.DB_SERVICE_POST_FIX;
        log.info("storage state table: {}", tableName);
        if (stateTableServices.containsKey(serviceName)) {
            stateTableServices.get(serviceName).storeTableData(tableName, tableDataInfo);
        } else if (tableName.startsWith(DBDynamicTableConstants.CONTRACT_DATA_PRE_FIX)) {
            stateTableServices.get(DBDynamicTableConstants.CONTRACT_DATA_INFO_SERVICE).storeTableData(tableName,
                    tableDataInfo);
        } else if (tableName.startsWith(DBDynamicTableConstants.CONTRAACT_PARAFUNC_FIX)) {
            stateTableServices.get(DBDynamicTableConstants.CONTRACT_PARAFUNC_INFO_SERVICE).storeTableData(tableName,
                    tableDataInfo);
        } else {
            stateTableServices.get(DBDynamicTableConstants.DYNAMIC_TABLE_INFO_SERVICE).storeTableData(tableName,
                    tableDataInfo);
        }

    }

    @Override
    public Map<String, TableDataInfo> fetchInterestedTables(BinlogBlockInfo blockInfo) {
        Map<String, TableDataInfo> interestedTables = blockInfo.getTables().entrySet().stream()
                .filter(
                        e -> {
                            String serviceName = StringStyleUtils.underline2upper(e.getKey()) + DBDynamicTableConstants.DB_SERVICE_POST_FIX;
                            return !ledgerTableServices.containsKey(serviceName);
                        })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return interestedTables;
    }

    @Override
    public void processTables(Map<String, TableDataInfo> tables, MultiPartsTask task) {
        //Process sys_table first to ensure create table before inserting records
        TableDataInfo sysTableData = tables.get(DBStaticTableConstants.SYS_TABLES_TABLE);
        if ((sysTableData != null) && (sysTableData.getNewEntrys() != null)
                && (sysTableData.getNewEntrys().size() > 0)) {
            log.info("create tables, process first");
            String serviceName = StringStyleUtils.underline2upper(DBStaticTableConstants.SYS_TABLES_TABLE)
                    + DBDynamicTableConstants.DB_SERVICE_POST_FIX;
            stateTableServices.get(serviceName).storeTableData(DBStaticTableConstants.SYS_TABLES_TABLE, sysTableData);
            tables.remove(DBStaticTableConstants.SYS_TABLES_TABLE);
        }

        //Executing them concurrently
        CountDownLatch latch = new CountDownLatch(tables.size());
        tables.entrySet().forEach(e -> {
            this.statePool.execute(() -> {
                try {
                    processSingleTable(e.getKey(), e.getValue());
                } catch (Exception ex) {
                    task.getExceptionHandler().accept(task.getBlockNum(), ex);
                } finally {
                    latch.countDown();
                }
            });
        });
        try {
            latch.await();
        } catch (InterruptedException ex) {
        }
        task.finishPart();
        log.info("finish state tables part for block:{}", task.getBlockNum());
    }
}
