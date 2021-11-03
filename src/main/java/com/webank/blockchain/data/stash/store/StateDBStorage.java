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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Map;

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


    public StateDBStorage(Map<String, StorageService> ledgerServices, Map<String, StorageService> stateServices){
        this.ledgerServices = ledgerServices;
        this.stateServices = stateServices;
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
            blockInfo.getTables().remove(DBStaticTableConstants.SYS_TABLES_TABLE);
        }
        blockInfo.getTables().entrySet().forEach(e -> storeData(e.getKey(), e.getValue()));
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
