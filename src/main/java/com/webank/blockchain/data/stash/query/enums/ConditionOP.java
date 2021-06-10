package com.webank.blockchain.data.stash.query.enums;

/**
 * @author wesleywang
 * @Description:
 * @date 2021/4/16
 */
public class ConditionOP {

    public enum ConditionOp
    {
        eq,
        ne,
        gt,
        ge,
        lt,
        le,
        op_max
    }


    public static ConditionOp valueOf(int value)
    {
        if(0==value)
        {
            return ConditionOp.eq;
        }

        else if(1 == value)
        {
            return ConditionOp.ne;
        }

        else if(2 == value)
        {
            return ConditionOp.gt;
        }

        else if(3 == value)
        {
            return ConditionOp.ge;
        }
        else if(4 == value)
        {
            return ConditionOp.lt;
        }
        else if(5 == value)
        {
            return ConditionOp.le;
        }
        return ConditionOp.op_max;
    }

    public static int valueOf(ConditionOp value)
    {
        if(ConditionOp.eq ==value)
        {
            return 0;
        }

        else if(ConditionOp.ne == value)
        {
            return 1;
        }

        else if(ConditionOp.gt == value)
        {
            return 2;
        }

        else if(ConditionOp.ge == value)
        {
            return 3;
        }
        else if(ConditionOp.lt == value)
        {
            return 4;
        }
        else if(ConditionOp.le == value)
        {
            return 5;
        }
        return 6;
    }

    private ConditionOp op;
    private String	value;
    public ConditionOp getOp() {
        return op;
    }
    public void setOp(ConditionOp op) {
        this.op = op;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }



}
