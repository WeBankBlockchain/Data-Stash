package com.webank.blockchain.data.stash.query.dao;



import com.webank.blockchain.data.stash.query.cache.Cache;

import java.util.List;

public class Table {
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public List<String> getIndices() {
		return indices;
	}
	
	public void setIndices(List<String> indices) {
		this.indices = indices;
	}
	
	public String indicesEqualString() {
		StringBuffer sb = new StringBuffer();
		
		for(String column: indices) {
			sb.append(" and `tt`.`");
			sb.append(column);
			sb.append("` = `");
			sb.append(column);
			sb.append("`");
		}
		
		return sb.toString();
	}

	public Cache getCache() {
		return cache;
	}
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	private String name;
	private String key;
	private List<String> indices;
	private Cache cache;
}
