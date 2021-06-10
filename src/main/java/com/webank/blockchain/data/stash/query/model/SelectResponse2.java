package com.webank.blockchain.data.stash.query.model;

import java.util.List;
import java.util.Map;

public class SelectResponse2 {
	private List<Map<String, Object>> columnValue;

	public List<Map<String, Object>> getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(List<Map<String, Object>> columnValue) {
		this.columnValue = columnValue;
	}

}