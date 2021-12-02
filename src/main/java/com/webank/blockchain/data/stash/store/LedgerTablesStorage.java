package com.webank.blockchain.data.stash.store;

import com.webank.blockchain.data.stash.constants.DBDynamicTableConstants;
import com.webank.blockchain.data.stash.db.face.StorageService;
import com.webank.blockchain.data.stash.db.face.TablesStorage;
import com.webank.blockchain.data.stash.entity.BinlogBlockInfo;
import com.webank.blockchain.data.stash.entity.TableDataInfo;
import com.webank.blockchain.data.stash.thread.MultiPartsTask;
import com.webank.blockchain.data.stash.utils.StringStyleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Only process ledger data inside
 * @author aaronchu
 * @Description
 * @date 2021/11/03
 */
@Slf4j
public class LedgerTablesStorage implements TablesStorage {


    private Map<String, StorageService> ledgerTableServices;
    private ThreadPoolExecutor ledgerPool;

    public LedgerTablesStorage(Map<String, StorageService> ledgerTableServices, ThreadPoolExecutor ledgerPool){
        this.ledgerTableServices = ledgerTableServices;
        this.ledgerPool = ledgerPool;
    }

    @PostConstruct
    @Transactional
    public void initSchema() throws SQLException {
        for(Map.Entry<String, StorageService> entry : ledgerTableServices.entrySet()){
            entry.getValue().createSchema();
        }
    }


    @Override
    public Map<String, TableDataInfo> fetchInterestedTables(BinlogBlockInfo blockInfo) {
        Map<String, TableDataInfo> interestedTables = blockInfo.getTables().entrySet().stream()
                .filter(
                        e -> {
                            String serviceName = StringStyleUtils.underline2upper(e.getKey()) + DBDynamicTableConstants.DB_SERVICE_POST_FIX;
                            return ledgerTableServices.containsKey(serviceName);
                        })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return interestedTables;
    }

    @Override
    public void processTables(final Map<String, TableDataInfo> tables, MultiPartsTask task) {
        this.ledgerPool.execute(()->{
            try{
                tables.entrySet().forEach(e->{
                    processSingleTable(e.getKey(), e.getValue());
                });
                task.finishPart();
                log.info("finish ledger tables part for block:{}", task.getBlockNum());
            }
            catch (Exception ex){
                task.getExceptionHandler().accept(task.getBlockNum(), ex);
            }
        });
    }

    private void processSingleTable(String tableName, TableDataInfo tableDataInfo){
        String serviceName = StringStyleUtils.underline2upper(tableName) + DBDynamicTableConstants.DB_SERVICE_POST_FIX;
        log.info("storage ledger table name : {}", tableName);
        if (ledgerTableServices.containsKey(serviceName)) {
            ledgerTableServices.get(serviceName).storeTableData(tableName, tableDataInfo);
        }
        else{
            throw new RuntimeException("Unrecognized ledger table:"+tableName);
        }
    }
}
