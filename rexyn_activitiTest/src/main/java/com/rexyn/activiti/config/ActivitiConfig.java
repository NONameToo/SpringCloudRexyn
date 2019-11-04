package com.rexyn.activiti.config;

import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author PCMSI
 * @Date 2019/10/29 12:01
 * @Version 1.0
 **/
@Configuration
public class ActivitiConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProcessEngine processEngine;

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
    public ProcessEngine createProcessEngine() {
        return processEngineConfiguration().buildProcessEngine();
    }

    /**
     * 创建几个工作流常用的Service,放到Spring容器里面
     * @return
     */
    @Bean
    public RepositoryService createRepositoryService() {
        return processEngine.getRepositoryService();
    }

    @Bean
    public RuntimeService createRuntimeService() {
        return processEngine.getRuntimeService();
    }

    @Bean
    public TaskService createTaskService() {
        return processEngine.getTaskService();
    }

    @Bean
    public HistoryService createHistoryService() {
        return processEngine.getHistoryService();
    }

    @Bean
    public ManagementService managementService() {
        return processEngine.getManagementService();
    }

    @Bean
    public DynamicBpmnService dynamicBpmnService() {
        return processEngine.getDynamicBpmnService();
    }
}
