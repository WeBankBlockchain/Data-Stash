/**
 * Copyright 2020 Webank.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.stash.config;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MybatisConfig
 *
 * @Description: MybatisConfig
 * @author graysonzhang
 * @data 2019-08-07 14:30:18
 *
 */
@Configuration
@MapperScan("com.webank.blockchain.data.stash.db.dao")
@MapperScan("com.webank.blockchain.data.stash.db.mapper")
public class MybatisConfig {
    
    @Autowired
    private DataSource dataSource;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
      SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
      sessionFactory.setDataSource(dataSource);
//      sessionFactory.setTypeAliasesPackage("com.webank.blockchain.data.stash.db.model");
      
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      sessionFactory.setMapperLocations(resolver.getResources("classpath*:sqlmap/*.xml"));
      return sessionFactory.getObject();
    }
    
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory){
        sqlSessionFactory.openSession(ExecutorType.BATCH, true);
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
