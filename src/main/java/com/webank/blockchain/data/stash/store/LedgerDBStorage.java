package com.webank.blockchain.data.stash.store;

import com.webank.blockchain.data.stash.constants.DBDynamicTableConstants;
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
 * Only process ledger data inside
 * @author aaronchu
 * @Description
 * @date 2021/11/03
 */
@Slf4j
public class LedgerDBStorage implements DataStorage {


    private Map<String, StorageService> ledgerTableServices;

    public LedgerDBStorage(Map<String, StorageService> ledgerTableServices){
        this.ledgerTableServices = ledgerTableServices;
    }

    @PostConstruct
    @Transactional
    public void initSchema() throws SQLException {
        for(Map.Entry<String, StorageService> entry : ledgerTableServices.entrySet()){
            entry.getValue().createSchema();
        }
    }

    @Override
    public void storeBlock(BinlogBlockInfo blockInfo) throws DataStashException {
        blockInfo.getTables().entrySet().forEach(e -> storeData(e.getKey(), e.getValue()));
    }

    @Override
    public void storeData(String tableName, TableDataInfo tableDataInfo) throws DataStashException {
        String serviceName = StringStyleUtils.underline2upper(tableName) + DBDynamicTableConstants.DB_SERVICE_POST_FIX;
        if(!ledgerTableServices.containsKey(serviceName)){
            return;
        }
        log.info("storage ledger table name : {}", tableName);
        if (ledgerTableServices.containsKey(serviceName)) {
            ledgerTableServices.get(serviceName).storeTableData(tableName, tableDataInfo);
        }
    }
}
