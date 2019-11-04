package com.rexyn.activiti;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipInputStream;

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

//    @Autowired
//    private ProcessEngine processEngine;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    /**
     * 部署npmn文件  (第一种部署方式)
     * 注意:
     * 1.这里的部署的方式是采用的SpringBoot,所以配置的东西都在application.yml里面
     * 2.在配置类里面已经加载了创建好了ProcessEngine,所以这里直接注入拿来用
     */
    @Test
    public void deploymentActiviti(){
//        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/BPMTestFile3.bpmn")    //指定bpmn文件
                .addClasspathResource("bpmn/BPMTestFile3.png")      //指定流程图文件
                .name("流程测试3")                                  // 指定流程名称
                .deploy();
        // 4.打印相关信息
        System.out.println("流程id是:" +deployment.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String time = simpleDateFormat.format(deployment.getDeploymentTime());
        System.out.println("流程时间是:" + time);
        System.out.println("流程名字是:" +deployment.getName());
    }
    /**
     * 部署ZIP文件  (第二种部署方式)
     * 首先将bpmn文件和png问价打成一个Zip包
     * 使用流的方式读取文件
     * 使用Zip流的方式部署
     * 注意:
     * 1.这里的部署的方式是采用的SpringBoot,所以配置的东西都在application.yml里面
     * 2.在配置类里面已经加载了创建好了ProcessEngine,所以这里直接注入拿来用
     */
    @Test
    public void deploymentActiviti2(){
        // 获取Zip文件的流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("bpmn/BPMTestFile3.zip");
        // 将InputStream 转换成 ZipStream流
        ZipInputStream zipInputStream = new ZipInputStream(is);

//        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)  //指定bpmn文件
                .name("流程测试3")                                  // 指定流程名称
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
//        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3.创建流程实例,以流程定义的唯一标识key来区分
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("TestProcess");

        System.out.println("流程部署的id:" + processInstance.getDeploymentId());
        System.out.println("流程实例的id:" + processInstance.getId());
        System.out.println("流程节点id(活动id):" + processInstance.getActivityId());
    }


    /**
     * 启动流程实例 -- 并添加业务键
     */
    @Test
    public void startProcessInstanceAndBusinessKey(){
        // 2.获取RuntimeService对象
//        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3.创建流程实例,以流程定义的唯一标识key来区分,第二个参数为业务标识,也就是业务键,这里假设这个业务键为666
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("TestProcess2","666");

        System.out.println("流程部署的id:" + processInstance.getDeploymentId());
        System.out.println("流程实例的id:" + processInstance.getId());
        System.out.println("流程节点id(活动id):" + processInstance.getActivityId());
        System.out.println("流程的业务键编号:" + processInstance.getBusinessKey());
    }

    /**
     * 查询当前用户的任务列表
     */
    @Test
    public void taskQuery(){
        // 2.获取TaskService对象
//        TaskService taskService = processEngine.getTaskService();
        // 3.根据流程定义的key及负责人(流程图里面的assignee)查询任务列表
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("Land")   // 查询任务的条件1: 根据process的key
//                .taskAssignee("LiSi")               // 查询任务的条件2: 根据负责人
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
     * 查询当前用户的任务列表
     */
    @Test
    public void taskQuery1(){
        // 2.获取TaskService对象
//        TaskService taskService = processEngine.getTaskService();
        // 3.根据流程定义的key及负责人(流程图里面的assignee)查询任务列表
        List<Task> list = taskService.createTaskQuery()
//                .processDefinitionKey("TestProcess")   // 查询任务的条件1: 根据process的key
//                .taskAssignee("ZhangSan")               // 查询任务的条件2: 根据负责人
                .taskId("7510")
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
     * 完成当前用户的某个任务(张三完成自己的任务)
     */
    @Test
    public void completeTask(){
        // 2.获取TaskService对象
//        TaskService taskService = processEngine.getTaskService();
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

    /**
     * 完成当前用户的某个任务(李四完成自己的任务)
     */
    @Test
    public void completeTask2(){
        // 2.获取TaskService对象
//        TaskService taskService = processEngine.getTaskService();
        // 3.根据流程定义的key及负责人(流程图里面的assignee)查询任务列表
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("TestProcess")   // 查询任务的条件1: 根据process的key
                .taskAssignee("LiSi")               // 查询任务的条件2: 根据负责人
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

    /**
     * 完成当前用户的某个任务(总经理LaoWan完成自己的任务)
     */
    @Test
    public void completeTask3(){
        // 2.获取TaskService对象
//        TaskService taskService = processEngine.getTaskService();
        // 3.根据流程定义的key及负责人(流程图里面的assignee)查询任务列表
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("TestProcess")   // 查询任务的条件1: 根据process的key
                .taskAssignee("LaoWan")               // 查询任务的条件2: 根据负责人
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

    /**
     * 查询流程定义相关信息
     */
    @Test
    public void queryProcessDefinition(){
        // 2.获取RepositoryService对象
//        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3.获取ProcessDefinitionQuery对象,它就是一个查询器
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        // 4.设置查询条件
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery
                .processDefinitionKey("Land")
                .orderByProcessDefinitionVersion()  // 以流程定义的版本号排序
                .desc()             // 降序,新版本的排在最前面
                .list();

        for(ProcessDefinition processDefinition:processDefinitionList){
            System.out.println("流程定义的id:" + processDefinition.getId());
            System.out.println("流程定义的名称:" + processDefinition.getName());
            System.out.println("流程定义的key:" + processDefinition.getKey());
            System.out.println("流程定义的版本号:" + processDefinition.getVersion());
            System.out.println("流程部署的id:" + processDefinition.getDeploymentId());
            System.out.println("===================================");
        }
    }

    /**
     * 删除流程定义(有流程实例没走完的,不能删除,非要删除,需要使用级联删除)
     */
    @Test
    public void deleteProcessDefinition(){
        // 2.获取RepositoryService对象
//        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3.删除流程定义,参数是流程部署的id,可以先查询出来,获得id再删除
        repositoryService.deleteDeployment("30001");
        // 3.级联删除
        repositoryService.deleteDeployment("1",true);
    }

    /**
     * 流程定义的资源文件的查看(bpmn,png)
     * 比如讲已经部署的流程的资源文件导出到文件
     */
    @Test
    public void queryBpmnFile(){
        // 2.获取RepositoryService对象
//        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.deleteDeployment("30001");
        // 3.级联删除
        repositoryService.deleteDeployment("1",true);
    }


    /**
     * 查询流程定义相关信息-----获取流程定义的bpmn文件和png
     */
    @Test
    public void queryProcessDefinitionNpmnAndPng() throws IOException {
        // 2.获取RepositoryService对象
//        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3.获取ProcessDefinitionQuery对象,它就是一个查询器
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        // 4.设置查询条件
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery
                .processDefinitionKey("TestProcess2")
                .orderByProcessDefinitionVersion()  // 以流程定义的版本号排序
                .desc()             // 降序,新版本的排在最前面
                .list();

        for(ProcessDefinition processDefinition:processDefinitionList){
            System.out.println("流程定义的id:" + processDefinition.getId());
            System.out.println("流程定义的名称:" + processDefinition.getName());
            System.out.println("流程定义的key:" + processDefinition.getKey());
            System.out.println("流程定义的版本号:" + processDefinition.getVersion());
            System.out.println("流程部署的id:" + processDefinition.getDeploymentId());
            System.out.println("===================================");
        }

        if(processDefinitionList !=null && processDefinitionList.size()>0){
            ProcessDefinition processDefinition = processDefinitionList.get(0);
            String deploymentId = processDefinition.getDeploymentId();
            // 第一个参数是部署id,第二个参数是资源名称
            //processDefinition.getResourceName() 获取bpmn文件的名称
            InputStream bpmnInputstream = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
            // processDefinition.getDiagramResourceName() 获取png文件的名称
            InputStream pngInputstream = repositoryService.getResourceAsStream(deploymentId, processDefinition.getDiagramResourceName());
            // 构建OutputStream流输出文件
            FileOutputStream bpmnOutputstream = new FileOutputStream("D:\\微服务\\newProject\\file\\activiti\\FileOutPutStream\\" + processDefinition.getResourceName());
            FileOutputStream pngOutputstream = new FileOutputStream("D:\\微服务\\newProject\\file\\activiti\\FileOutPutStream\\" + processDefinition.getDiagramResourceName());
            // 输入流输出流的转换,使用commons-io包来解决
            IOUtils.copy(bpmnInputstream,bpmnOutputstream);
            IOUtils.copy(pngInputstream,pngOutputstream);

            bpmnOutputstream.close();
            pngOutputstream.close();
            bpmnInputstream.close();
            pngInputstream.close();
        }
    }


    /**
     * 历史记录查询  -- 使用HistoryService
     */
    @Test
    public void historyQuery(){
//        HistoryService historyService = processEngine.getHistoryService();
        // 根据流程实例来查询
        HistoricActivityInstanceQuery historicQuery = historyService.createHistoricActivityInstanceQuery();
        // 设置流程实例的id
        List<HistoricActivityInstance> historicActivityInstanceList = historicQuery.
                processInstanceId("17508")
                .orderByHistoricActivityInstanceStartTime().asc() // 根据节点开始的时间升序进行排序
                .list();
        for(HistoricActivityInstance historicActivityInstance:historicActivityInstanceList){
            System.out.println("ActivityId:" + historicActivityInstance.getActivityId());
            System.out.println("ActivityName:" + historicActivityInstance.getActivityName());
            System.out.println("ActivityType:" + historicActivityInstance.getActivityType());
            System.out.println("Assignee:" + historicActivityInstance.getAssignee());
            System.out.println("TaskId:" + historicActivityInstance.getTaskId());
            System.out.println("ProcessInstanceId:" + historicActivityInstance.getProcessInstanceId());
            System.out.println("===================================");
        }
    }


    /**
     * 流程定义的挂起与激活(全部都挂起,即该流程定义下的所有实例都被挂起) 使用的是RepositoryService
     *
     * 业务场景:
     * 旧的流程需要被废弃,使用新的流程,但是旧的流程有许多流程实例没有走完,没走完的流程全部不走了
     * 1.如果没有走完的流程实例直接不继续走了,需要对流程全部进行挂起
     * 2.挂起的流程定义将不能开启新的流程实例
     *
     */
    @Test
    public void suspendProcessDefine(){
//        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 根据流程定义的唯一key来获取流程定义对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("TestProcess2")
                .singleResult();

        String processId = processDefinition.getId();

        // 该流程定义的实例是否为挂起状态(suspended暂停)
        boolean suspended = processDefinition.isSuspended();
        if(suspended){
            // 如果是挂起状态,可以激活
            repositoryService.activateProcessDefinitionById(processId,true,null);
            System.out.println("流程定义:" + processId + "已被激活!");

        }else{
            // 如果是激活状态,将其挂起
            repositoryService.suspendProcessDefinitionById(processId,true,null);
            System.out.println("流程定义:" + processId + "已被挂起!");
        }
    }

    /**
     * 流程实例的挂起与激活(单个) 使用的RuntimeServices
     * 业务场景:
     * 太多的实例,先处理某一些,只挂起(暂停)某一个实例
     */
    @Test
    public void suspendProcessInstance(){
//        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 查询流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("2501") // 根据具体的实例id来挂起
                .singleResult();

        String processInstanceId = processInstance.getId();

        // 该流程定义的实例是否为挂起状态(suspended暂停)
        boolean suspended = processInstance.isSuspended();
        if(suspended){
            // 如果是挂起状态,可以激活
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程定义:" + processInstanceId + "已被激活!");

        }else{
            // 如果是激活状态,将其挂起
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程定义:" + processInstanceId + "已被挂起!");
        }
    }
//==================================================================================
    /**
     * 启动流程实例 -- 并添加业务键 -- 并指定执行人
     */
    @Test
    public void startProcessInstanceAndBusinessKeyAndAssignee(){
        // 2.获取RuntimeService对象
//        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 为assignee动态赋值,动态指定执行人
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("assignee0","AAAA");
        hashMap.put("assignee1","BBBB");
        hashMap.put("assignee2","CCCC");

        // 3.创建流程实例,以流程定义的唯一标识key来区分,第二个参数为业务标识,也就是业务键,这里假设这个业务键为666
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("TestProcess3","666",hashMap);

        System.out.println("流程部署的id:" + processInstance.getDeploymentId());
        System.out.println("流程实例的id:" + processInstance.getId());
        System.out.println("流程节点id(活动id):" + processInstance.getActivityId());
        System.out.println("流程的业务键编号:" + processInstance.getBusinessKey());
    }
}
