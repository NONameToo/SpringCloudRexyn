package com.rexyn.activiti;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @ClassName fushaokai
 * @Description 采用的SpringBoot,将流程部署到activiti对应的表里面去
 * @Author PCMSI
 * @Date 2019/10/10 9:26
 * @Version 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiTestCreate25Tables.class)
public class ActivitiTest {

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 部署npmn文件
     * 注意:
     * 1.这里的部署的方式是采用的SpringBoot,所以配置的东西都在application.yml里面
     * 2.在配置类里面已经加载了创建好了ProcessEngine,所以这里直接注入拿来用
     */
    @Test
    public void deploymentActiviti(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("npmn/BPMTestFile.bpmn")    //指定bpmn文件
                .addClasspathResource("png/BPMTestFile.png")      //指定流程图文件
                .name("流程测试")                                  // 指定流程名称
                .deploy();
        // 4.打印相关信息
        System.out.println("流程id是:" +deployment.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String time = simpleDateFormat.format(deployment.getDeploymentTime());
        System.out.println("流程时间是:" + time);
        System.out.println("流程名字是:" +deployment.getName());

    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstance(){
        // 2.获取RuntimeService对象
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3.创建流程实例,以流程定义的唯一标识key来区分
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("TestProcess");

        System.out.println("流程部署的id:" + processInstance.getDeploymentId());
        System.out.println("流程实例的id:" + processInstance.getId());
        System.out.println("流程节点id(活动id):" + processInstance.getActivityId());
    }

    /**
     * 查询当前用户的任务列表
     */
    @Test
    public void taskQuery(){
        // 2.获取TaskService对象
        TaskService taskService = processEngine.getTaskService();
        // 3.根据流程定义的key及负责人(流程图里面的assignee)查询任务列表
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("TestProcess")   // 查询任务的条件1: 根据process的key
                .taskAssignee("ZhangSan")               // 查询任务的条件2: 根据负责人
                .list();
        // 4.任务列表的展示
        for(Task oneTask:list){
            System.out.println("流程实例的id:" + oneTask.getProcessInstanceId());
            System.out.println("任务的id:" + oneTask.getId());
            System.out.println("任务负责人:" + oneTask.getAssignee());
            System.out.println("任务名称:" + oneTask.getName());
            System.out.println("===================================");
        }
    }

    /**
     * 完成当前用户的某个任务
     */
    @Test
    public void completeTask(){
        // 2.获取TaskService对象
        TaskService taskService = processEngine.getTaskService();
        // 3.根据流程定义的key及负责人(流程图里面的assignee)查询任务列表
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("TestProcess")   // 查询任务的条件1: 根据process的key
                .taskAssignee("ZhangSan")               // 查询任务的条件2: 根据负责人
                .list();
        // 4.任务列表的展示
        for(Task oneTask:list){
            System.out.println("流程实例的id:" + oneTask.getProcessInstanceId());
            System.out.println("任务的id:" + oneTask.getId());
            System.out.println("任务负责人:" + oneTask.getAssignee());
            System.out.println("任务名称:" + oneTask.getName());
            System.out.println("===================================");
        }

        if(list!=null && list.size()>0){
            // 5.完成某个任务
            taskService.complete(list.get(0).getId());  // 完成某个任务是某个task的id
            System.out.println("完成任务id= " + list.get(0).getId());
        }
    }






}
