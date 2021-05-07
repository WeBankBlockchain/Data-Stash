package com.webank.blockchain.data.stash.query.cache;


import org.apache.commons.collections4.map.LRUMap;

import java.util.Collections;
import java.util.Map;

public class MemoryCache implements Cache {
	
	private Integer cacheSize;
	//define map size is cacheSize
	private LRUMap<String, CacheEntry> map;
	//map thread safety 
	private Map<String, CacheEntry> cache;
	private Integer lastCommitNum = 0;
	
	public MemoryCache(Integer cacheSize) {
		//by spring inject, defaut set 1000
		if(cacheSize == null) {
			this.cacheSize = 1000;
		}else {
			this.cacheSize = cacheSize;
		}
		
		map = new LRUMap<String, CacheEntry>(this.cacheSize);
		cache = Collections.synchronizedMap(map);
	}
	
	@Override
	public CacheEntry get(String key) {
		return cache.get(key);
	}
	
	@Override
	public void set(String key, CacheEntry entry) {
		cache.put(key, entry);
	}
	
	@Override
	public void remove(String key) {
		cache.remove(key);
	}
	
	@Override
	public Integer getLastCommitNum() {
		return lastCommitNum;
	}

	@Override
	public void setLastCommitNum(Integer num) {
		lastCommitNum = num;
	}
	
	public Integer getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(Integer cacheSize) {
		this.cacheSize = cacheSize;
	}
}
