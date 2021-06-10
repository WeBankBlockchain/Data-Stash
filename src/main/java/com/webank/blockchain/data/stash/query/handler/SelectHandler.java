package com.webank.blockchain.data.stash.query.handler;

import com.webank.blockchain.data.stash.query.dao.SqlExecutor;
import com.webank.blockchain.data.stash.query.model.SelectRequest;
import com.webank.blockchain.data.stash.query.model.SelectResponse2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aaronchu
 * @Description
 * @data 2021/03/31
 */
@Slf4j
@Component
public class SelectHandler {

    @Autowired
    private SqlTranslateHandler sqlTranslateHandler;
    @Autowired
    private SqlExecutor sqlExecutor;

    public SelectResponse2 select(SelectRequest request) throws Exception {
        SelectResponse2 response = new SelectResponse2();
        String conditionSql = sqlTranslateHandler.toSql(request);
        List<Map<String, Object>> data = sqlExecutor.execute(conditionSql,request);
        List<Map<String, Object>> allValues = new ArrayList<>();
        if (!data.isEmpty()) {
            log.debug("condition sql:{} has data", conditionSql);
            for (Map<String, Object> line : data) {
                Map<String, Object> map = new HashMap<>();
                for (String field : line.keySet()) {
                    map.put(field, line.get(field));
                }
                allValues.add(map);
            }
        } else {
            log.debug("condition sql:{} has no data", conditionSql);
        }
        response.setColumnValue(allValues);
        return response;
    }

}
