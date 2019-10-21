package com.rexyn.activiti.config;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author PCMSI
 * @Date 2019/10/9 12:01
 * @Version 1.0
 **/
@Configuration
public class ActivitiConfig {

    @Autowired
    private DataSource dataSource;

    /**
     * 初始化配置,创建25张表
     * @return
     */
    @Bean
    public StandaloneProcessEngineConfiguration processEngineConfiguration() {
        StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
        // 指定数据源
        configuration.setDataSource(dataSource);
        // 是否自动生成表结构,如果没有表,就会自动生成那25张表
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        configuration.setAsyncExecutorActivate(false);
        return configuration;
    }

    /**
     * 创建引擎
     * @return ProcessEngine
     */

    @Bean
    public ProcessEngine processEngine() {
        return processEngineConfiguration().buildProcessEngine();
    }
}
