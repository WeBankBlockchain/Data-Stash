/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.stash.db.service;


import com.webank.blockchain.data.stash.BaseTest;
import com.webank.blockchain.data.stash.db.model.SysBlock2NoncesInfo;
import org.junit.Test;

import com.webank.blockchain.data.stash.BaseTest;
import com.webank.blockchain.data.stash.db.model.SysBlock2NoncesInfo;

/**
 * SysBlock2NoncesInfoServiceTest
 *
 * @Description: SysBlock2NoncesInfoServiceTest
 * @author graysonzhang
 * @data 2019-08-08 18:13:33
 *
 */
public class SysBlock2NoncesInfoServiceTest extends BaseTest {
	
	@Test
	public void testSave() {
	    
		SysBlock2NoncesInfo sysBlock2NoncesInfo = new SysBlock2NoncesInfo();
		sysBlock2NoncesInfo.setId(123L);
		sysBlock2NoncesInfo.setStatus(0);
		sysBlock2NoncesInfo.setNum(0L);
		sysBlock2NoncesInfo.setNumber("123");
		sysBlock2NoncesInfo.setValue("test");
		
		//service.save(sysBlock2NoncesInfo);
	}
	
	@Test
	public void testSaveDetail() {
		
		SysBlock2NoncesInfo sysBlock2NoncesInfo = new SysBlock2NoncesInfo();
		sysBlock2NoncesInfo.setId(456L);
		sysBlock2NoncesInfo.setStatus(0);
		sysBlock2NoncesInfo.setNum(0L);
		sysBlock2NoncesInfo.setNumber("123");
		sysBlock2NoncesInfo.setValue("test");
		//service.saveDetail(sysBlock2NoncesInfo);
	}

}
