package com.webank.blockchain.data.stash.query.dao;

import com.webank.blockchain.data.stash.db.mapper.DataMapper;
import com.webank.blockchain.data.stash.query.cache.MemoryCache;
import com.webank.blockchain.data.stash.query.model.SelectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author aaronchu
 * @Description
 * @data 2021/03/31
 */
@Slf4j
@Component
public class SqlExecutor {

    @Autowired
    private DataMapper dataMapper;

    public  List<Map<String, Object>> execute(String conditionSql, SelectRequest request){

        String table = request.getTable();
        Integer num = request.getNum();
        String key = request.getKey();

        Table info = getTable(table);
        List<Map<String, Object>> data = null;
        log.debug("key:{} table:{} number:{} index{}  key_value:{} condition:{}", key, table, num,
                info.indicesEqualString(), info.getKey(), conditionSql);

        String _table = "`" +
                table +
                "`";
        data = dataMapper.queryData(_table, num, info.indicesEqualString(), info.getKey(), key,
                conditionSql);

        return data;
    }

    private Table getTable(String table_name) {
        List<Map<String, String>> fields = dataMapper.getTable(table_name);
        Table table = null;
        if (!fields.isEmpty()) {
            table = new Table();
            table.setName(table_name);
            String key = fields.get(0).get("key_field");
            table.setKey(key);
            List<String> indices = Arrays.asList(key);
            table.setCache(new MemoryCache(3));
            table.setIndices(indices);
        } else {
            log.error("Cannot find the table: {}", table_name);
        }
        return table;
    }

}
