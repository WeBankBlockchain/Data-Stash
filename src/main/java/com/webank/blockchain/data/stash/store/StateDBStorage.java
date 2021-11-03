package com.webank.blockchain.data.stash.store;

import com.webank.blockchain.data.stash.aspect.UseTime;
import com.webank.blockchain.data.stash.constants.DBDynamicTableConstants;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.db.face.DataStorage;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.exception.DataStashException;
import com.webank.blockchain.data.stash.utils.StringStyleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Only process state data inside
 * @author aaronchu
 * @Description
 * @date 2021/11/03
 */
@Slf4j
public class StateDBStorage implements DataStorage {


    private Map<String, StorageService> ledgerServices;
    private Map<String, StorageService> stateServices;

    private ThreadPoolExecutor stateInsertionThreadPool;

    public StateDBStorage(Map<String, StorageService> ledgerServices, Map<String, StorageService> stateServices, int poolSize){
        this.ledgerServices = ledgerServices;
        this.stateServices = stateServices;
        this.stateInsertionThreadPool = new ThreadPoolExecutor(poolSize,poolSize, 0, TimeUnit.DAYS,
                new LinkedBlockingQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @PostConstruct
    @Transactional
    public void initSchema() throws SQLException {
        for(Map.Entry<String, StorageService> entry : stateServices.entrySet()){
            entry.getValue().createSchema();
        }
    }

    @Override
    @UseTime
    public void storeBlock(BinlogBlockInfo blockInfo) throws DataStashException {
        //Process sys_table first to ensure create table before inserting records
        TableDataInfo sysTableData = blockInfo.getTables().get(DBStaticTableConstants.SYS_TABLES_TABLE);
        if ((sysTableData != null) && (sysTableData.getNewEntrys() != null)
                && (sysTableData.getNewEntrys().size() > 0)) {
            log.info("create tables, process first");
            String serviceName = StringStyleUtils.underline2upper(DBStaticTableConstants.SYS_TABLES_TABLE)
                    + DBDynamicTableConstants.DB_SERVICE_POST_FIX;
            stateServices.get(serviceName).storeTableData(DBStaticTableConstants.SYS_TABLES_TABLE, sysTableData);
       //     blockInfo.getTables().remove(DBStaticTableConstants.SYS_TABLES_TABLE);
        }
        //Extract all stata tables
        Map<String, TableDataInfo> stateTables = blockInfo.getTables().entrySet().stream().filter(e->{
            if(Objects.equals(e.getKey(), DBStaticTableConstants.SYS_TABLES_TABLE)){
                return false;
            }
            String serviceName = StringStyleUtils.underline2upper(e.getKey()) + DBDynamicTableConstants.DB_SERVICE_POST_FIX;
            return !this.ledgerServices.containsKey(serviceName);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        //Executing them concurrently
        AtomicReference<Exception> err = new AtomicReference<>();
        try {

            CountDownLatch countDownLatch = new CountDownLatch(stateTables.size());
            stateTables.entrySet().forEach(e -> {
                this.stateInsertionThreadPool.execute(() -> {
                    try {
                        storeData(e.getKey(), e.getValue());
                    } catch (Exception ex) {
                        err.set(ex);
                        log.error("Error handling state table", ex);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            });
            countDownLatch.await();
        }
        catch (Exception ex){}

        //Propagate possible exceptions to stop the process
        if(err.get() != null){
            throw new RuntimeException("Exception happen during state tables", err.get());
        }
    }

    @Override
    @UseTime
    public void storeData(String tableName, TableDataInfo tableDataInfo) throws DataStashException {
        String serviceName = StringStyleUtils.underline2upper(tableName) + DBDynamicTableConstants.DB_SERVICE_POST_FIX;
        if(this.ledgerServices.containsKey(serviceName)){
            return;
        }
        log.info("storage state table: {}", tableName);
        if (stateServices.containsKey(serviceName)) {
            stateServices.get(serviceName).storeTableData(tableName, tableDataInfo);
        } else if (tableName.startsWith(DBDynamicTableConstants.CONTRACT_DATA_PRE_FIX)) {
            stateServices.get(DBDynamicTableConstants.CONTRACT_DATA_INFO_SERVICE).storeTableData(tableName,
                    tableDataInfo);
        } else if (tableName.startsWith(DBDynamicTableConstants.CONTRAACT_PARAFUNC_FIX)) {
            stateServices.get(DBDynamicTableConstants.CONTRACT_PARAFUNC_INFO_SERVICE).storeTableData(tableName,
                    tableDataInfo);
        } else {
            stateServices.get(DBDynamicTableConstants.DYNAMIC_TABLE_INFO_SERVICE).storeTableData(tableName,
                    tableDataInfo);
        }
    }

}
