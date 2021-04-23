package com.webank.blockchain.data.stash.db.sharding;

import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public final class BlockShardingDatabaseAlgorithm implements PreciseShardingAlgorithm<Long>, RangeShardingAlgorithm<Long> {

    @Override
    public String doSharding(final Collection<String> databaseNames, final PreciseShardingValue<Long> shardingValue) {
        String databaseName = null;
        for (String each : databaseNames) {
            if (each.endsWith(shardingValue.getValue() % databaseNames.size() +1 + "")) {
                databaseName = each;
                break;
            }
        }
        if (StringUtils.isNotEmpty(databaseName)) {
            return databaseName;
        }
        throw new UnsupportedOperationException();
    }

    /**
     * For rollback sql: delete * from xxx where _num_ >= xxx
     * @param collection
     * @param rangeShardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {

        return collection;
    }
}