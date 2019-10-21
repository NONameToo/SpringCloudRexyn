package com.rexyn.activiti.dev;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;

/**
 * @ClassName fushaokai
 * @Description 启动流程实例
 * @Author PCMSI
 * @Date 2019/10/10 10:10
 * @Version 1.0
 **/
public class ActivitiStartInstance {

    public static void main(String[] args) {
        // 1.获取到ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RuntimeService对象
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3.创建流程实例,以流程定义的唯一标识key来区分
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("TestProcess");

        System.out.println("流程部署的id:" + processInstance.getDeploymentId());
        System.out.println("流程实例的id:" + processInstance.getId());
        System.out.println("流程节点id(活动id):" + processInstance.getActivityId());
    }
}
