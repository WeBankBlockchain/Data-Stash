package com.webank.blockchain.data.stash.db.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DataMapper {

	 List<Map<String, Object> > queryData(@Param("table")String table,
			@Param("num")Integer num,
			@Param("indices_equal")String indices_equal,
			@Param("key_field")String keyField,
			@Param("key_value")String keyValue,
			@Param("query_condition")String QueryCondition);
	 List<Map<String, String>> getTable(@Param("table_name")String table_name);

	 List<Map<String, Object>> selectTableDataByNum(@Param("tableName")String table_name, @Param("num")long num, @Param("preIndex")long preIndex, @Param("pageSize")int pageSize);

}
