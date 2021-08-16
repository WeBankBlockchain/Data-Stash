package com.webank.blockchain.data.stash.utils;

import com.webank.blockchain.data.stash.db.model.DynamicTableInfo;
import com.webank.blockchain.data.stash.entity.ColumnInfo;
import com.webank.blockchain.data.stash.entity.EntryInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author aaronchu
 * @Description
 * @date 2021/08/16
 */
public class ObjectBuildUtilTest {

    @Test
    public void testConvertEscapeChar() {
        String str = "aaa";
        String converted = ObjectBuildUtil.convertEscapeChar(str);

        Assert.assertEquals("aaa", converted);

        str = "'aaa'";
        converted = ObjectBuildUtil.convertEscapeChar(str);

        Assert.assertEquals("\\'aaa\\'", converted);
    }

    @Test
    public void testBuildStr() {
        EntryInfo entryInfo = new EntryInfo();
        entryInfo.setId(1);
        entryInfo.setHash("0x00");
        entryInfo.setNum(1);
        entryInfo.setStatus(1);

        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("aa");
        columnInfo.setColumnValue("aa");
        entryInfo.setColumns(Arrays.asList(columnInfo));
        DynamicTableInfo r = new DynamicTableInfo();
        ObjectBuildUtil.buildToDynamicObj(entryInfo, r);

        Assert.assertEquals("1,'0x00',1,1, 'aa'", r.getValues());

        columnInfo.setColumnValue("'aa'");
        r = new DynamicTableInfo();
        ObjectBuildUtil.buildToDynamicObj(entryInfo, r);

        Assert.assertEquals("1,'0x00',1,1, '\\'aa\\''", r.getValues());
    }

}