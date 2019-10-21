package com.rexyn.activiti.dev;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * @ClassName fushaokai
 * @Description 完成当前用户的某个任务
 * @Author PCMSI
 * @Date 2019/10/10 11:04
 * @Version 1.0
 **/
public class ActivitiCompleteTask {
    public static void main(String[] args) {
        // 1.创建processEngine对象
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
            System.out.println("===================================");
        }

        if(list!=null && list.size()>0){
            // 5.完成某个任务
            taskService.complete(list.get(0).getId());  // 完成某个任务是某个task的id
            System.out.println("完成任务id= " + list.get(0).getId());
        }
    }
}
