package com.webank.blockchain.data.stash.db.service;

import java.util.List;
import java.util.TreeMap;

import com.webank.blockchain.data.stash.BaseTest;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.utils.JsonUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.webank.blockchain.data.stash.BaseTest;
import com.webank.blockchain.data.stash.constants.DBStaticTableConstants;
import com.webank.blockchain.data.stash.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckPointInfoServiceTest extends BaseTest {
	
	@Autowired
	private CheckPointInfoService service;
	
	@Test
	public void testSelectByNum() {
		List<TreeMap<String, String>> list = service.selectBlockDataByBlockNum(DBStaticTableConstants.SYS_TABLES_TABLE, 0);
		log.info("list size : {}", list.size());
		
		log.info("list : {}", JsonUtils.toJson(list));
	}

}
