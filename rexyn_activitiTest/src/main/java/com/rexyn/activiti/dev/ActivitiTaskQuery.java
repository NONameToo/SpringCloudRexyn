package com.rexyn.activiti.dev;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * @ClassName fushaokai
 * @Description 查询当前用户的任务列表
 * @Author PCMSI
 * @Date 2019/10/10 10:36
 * @Version 1.0
 **/
public class ActivitiTaskQuery {
    public static void main(String[] args) {
        // 1.获取到ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
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
        }
    }
}
