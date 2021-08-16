package com.webank.blockchain.data.stash.query.handler;

import com.webank.blockchain.data.stash.query.enums.ConditionOP;
import com.webank.blockchain.data.stash.query.model.SelectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author aaronchu
 * @Description
 * @data 2021/03/26
 */
@Slf4j
@Component
public class SqlTranslateHandler {

    public String toSql(SelectRequest request) throws Exception {

        String key = request.getKey();
        List<List<String>> condition = request.getCondition();
        log.debug("key:{} condition:{}", key, condition);

        StringBuilder sb = new StringBuilder();
        if (condition != null) {
            for (List<String> cond : condition) {
                if (cond.size() < 3) {
                    throw new Exception("Invalid cond:" + cond.stream().reduce((a, b) -> a + ", " + b));
                }
                String strKeyEscape = getStrSql(cond.get(0));

                if (ConditionOP.valueOf(Integer.parseInt(cond.get(1))) ==  ConditionOP.ConditionOp.eq) {
                    sb.append(" and `").append(strKeyEscape).append("` = ");
                    sb.append("'").append(getStrSql(cond.get(2)));
                    sb.append("'");
                }
                else if (ConditionOP.valueOf(Integer.parseInt(cond.get(1))) ==  ConditionOP.ConditionOp.ne) {
                    sb.append(" and `").append(strKeyEscape).append("` !=");
                    sb.append("'").append(getStrSql(cond.get(2)));
                    sb.append("'");
                }
                else if (ConditionOP.valueOf(Integer.parseInt(cond.get(1))) ==  ConditionOP.ConditionOp.gt) {
                    sb.append(" and `").append(strKeyEscape).append("` >");
                    sb.append("'").append(getStrSql(cond.get(2)));
                    sb.append("'");
                }
                else if (ConditionOP.valueOf(Integer.parseInt(cond.get(1))) ==  ConditionOP.ConditionOp.ge) {
                    sb.append(" and `").append(strKeyEscape).append("` >= ");
                    sb.append("'").append(getStrSql(cond.get(2)));
                    sb.append("'");
                }
                else if (ConditionOP.valueOf(Integer.parseInt(cond.get(1))) ==  ConditionOP.ConditionOp.lt) {
                    sb.append(" and `").append(strKeyEscape).append("` < ");
                    sb.append("'").append(getStrSql(cond.get(2)));
                    sb.append("'");
                }
                else if (ConditionOP.valueOf(Integer.parseInt(cond.get(1))) ==  ConditionOP.ConditionOp.le) {
                    sb.append(" and `").append(strKeyEscape).append("` <=");
                    sb.append("'").append(getStrSql(cond.get(2)));
                    sb.append("'");
                }
                else {
                    log.error("error condition op:{}", cond.get(1));
                }

            }
        }
        String conditionsql = sb.toString();
        log.debug("condition sql:{}", conditionsql);
        return conditionsql;
    }


    private String getStrSql(String str) {
        String strSql = str;
        strSql = strSql.replace("\\", "\\\\");
        strSql = strSql.replace("`", "\\`");
        return strSql;
    }
}
