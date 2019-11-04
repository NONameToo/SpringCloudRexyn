package com.rexyn.activiti.service;

import com.rexyn.activiti.common.ActivitiConstant;
import com.rexyn.activiti.common.ActivitiDecision;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.db.DbSqlSessionFactory;
import org.activiti.engine.impl.db.ListQueryParameterObject;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName fushaokai
 * @Description 常用的activiti相关方法
 * @Author PCMSI
 * @Date 2019/10/29 15:15
 * @Version 1.0
 **/

@Service
@Transactional
public class AcitivitiService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private static ProcessEngine processEngine;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private DynamicBpmnService dynamicBpmnService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private ActivitiConstant activitiConstant;  // activiti常量包

    public String processInstanceId;


    /**
     * 部署流程
     * @param key
     */
    public void deploy(String key) {
        repositoryService.createDeployment().addClasspathResource(activitiConstant.BPMNPATH + key + ".bpmn").addClasspathResource(activitiConstant.BPMNPATH + key + ".png").deploy();
    }

    /**
     * 开启流程实例
     * @param key
     * @param curMap
     * @return
     */
    public String createInstance(String key, Map<String, Object> curMap) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key,curMap);
        processInstanceId = processInstance.getId();
        System.out.println("流程实例ID:"+processInstanceId);
        return processInstanceId;
    }

    /**
     * 根据流程实例id获取当前流程节点的任务名字
     * @param processInstanceId
     * @return
     */
    public String getTaskNameByProcessInstanceId(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).list().get(0).getName();
    }

    public void completeTask(String taskId,String action,Map<String,Object> map,String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        Task task = tasks.get(0);
        ActivitiDecision activitiDecision = new ActivitiDecision();
        activitiDecision.setAction(action);
        map.put("activitiDecision", activitiDecision);
        taskService.complete(task.getId(), map);
    }


    /**
     * 根据key判断流程是否已经部署
     * @param key
     * @return
     */
    public boolean isDeployed(String key){
        // 获取流程定义查询对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        // 配置查询对象
        processDefinitionQuery.processDefinitionKey(key).orderByProcessDefinitionVersion().desc();
        List<ProcessDefinition> pds = processDefinitionQuery.list();
        if(pds != null && pds.size() != 0){
            return true;
        }
        return false;
    }

    /**
     * 更新流程定义文件会删除所有此模块的流程数据
     * @param key
     */
    public void updateDeploy(String key){

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.processDefinitionKey(key).orderByProcessDefinitionVersion().desc();
        List<ProcessDefinition> pds = processDefinitionQuery.list();
        if(pds != null && pds.size() != 0){
            for(ProcessDefinition pd : pds){
                repositoryService.deleteDeployment(pd.getDeploymentId(), true); // 级联删除
            }
        }
        deploy(key);
    }

    /**
     * 获取流程图的png文件流
     * @param key
     * @return
     */
    public void getProcessImage(String key,HttpServletResponse response) throws IOException {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.processDefinitionKey(key).orderByProcessDefinitionVersion().desc();
        ProcessDefinition pds = processDefinitionQuery.list().get(0);
        String deployId = "";
        if(pds != null){
            deployId = pds.getDeploymentId();
        }
        InputStream imageInputStream = repositoryService.getResourceAsStream(deployId, activitiConstant.BPMNPATH + key + ".png");
        IOUtils.copy(imageInputStream,response.getOutputStream());
    }


    /**
     * 跟踪流程执行情况用红色框在流程图上标识路线跟节点
     * @param processDefinitionId
     * @param resourceName
     * @param processInstanceId
     * @param response
     * @throws IOException
     */
    public void getActivitiProccessImage(String processDefinitionId, String resourceName,String processInstanceId, HttpServletResponse response) throws IOException {
        ProcessDefinitionQuery pdq = repositoryService.createProcessDefinitionQuery();
        ProcessDefinition pd = pdq.processDefinitionId(processDefinitionId).singleResult();
        if(resourceName.endsWith(".png") && StringUtils.isEmpty(processInstanceId) == false)
        {
            getActivitiCurrentProccessImage(processInstanceId,response);  // 查看所有节点情况
            //ProcessDiagramGenerator.generateDiagram(pde, "png", getRuntimeService().getActiveActivityIds(processInstanceId));
        }
        else
        {
            // 通过接口读取
            InputStream resourceAsStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), resourceName);

            // 输出资源内容到相应对象
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        }
    }


    /**
     * 只看当前节点
     * @param processDefinitionId
     * @param resourceName
     * @param processInstanceId
     * @param response
     * @throws IOException
     */
    public void getActivitiProccessCurrentImage(String processDefinitionId, String resourceName,String processInstanceId, HttpServletResponse response) throws IOException {
        ProcessDefinitionQuery pdq = repositoryService.createProcessDefinitionQuery();
        ProcessDefinition pd = pdq.processDefinitionId(processDefinitionId).singleResult();
        if(resourceName.endsWith(".png") && StringUtils.isEmpty(processInstanceId) == false)
        {
            getActivitiCurrentProccessCurrentImage(processInstanceId,response); // 只看当前节点
            //ProcessDiagramGenerator.generateDiagram(pde, "png", getRuntimeService().getActiveActivityIds(processInstanceId));
        }
        else
        {
            // 通过接口读取
            InputStream resourceAsStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), resourceName);

            // 输出资源内容到相应对象
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        }
    }


    /**
     * 获取流程当前的坐标,即当前流程所在位置
     * 获取流程图像，已执行节点和流程线高亮显示
     * @param
     * @return
     */
    public void getActivitiCurrentProccessImage(String processInstanceId, HttpServletResponse response)
    {
        //logger.info("[开始]-获取流程图图像");
        try {
            //  获取历史流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();

            if (historicProcessInstance == null) {
                //throw new BusinessException("获取流程实例ID[" + pProcessInstanceId + "]对应的历史流程实例失败！");
            }
            else
            {
                // 获取流程定义
                ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                        .getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());

//                 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
                List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().asc().list();
//============================================================================================
//                // 这段是解决流程退回后,图上无法表现出现在流程具体在哪里
//                ArrayList<String> idList = new ArrayList<>();
//                List<HistoricTaskInstance> a = historyService.createHistoricTaskInstanceQuery().executionId("7").list();
//                for (HistoricTaskInstance historicTaskInstance :a){
//                    System.out.println(historicTaskInstance.getDeleteReason());
//                    if(historicTaskInstance.getDeleteReason() != null){
//                        idList.add(historicTaskInstance.getId());
//                    }
//                }
//                //  将回退了的流程的id去掉
//                ArrayList<HistoricActivityInstance> arr = new ArrayList<>();
//                // 如果回退过流程,而且包含有网关,那么,把网关的节点去掉
//                ArrayList<String> idList2 = new ArrayList<>();
//                if (idList !=null && idList.size()>0){
//                    for(HistoricActivityInstance historicActivityInstance:historicActivityInstanceList){
//                        if(historicActivityInstance.getActivityType().endsWith("Gateway")){
//                            idList2.add(historicActivityInstance.getTaskId());
//                        }
//                    }
//                }
//
//                for (HistoricActivityInstance activityInstance :
//                        historicActivityInstanceList) {
//                    if (!idList.contains(activityInstance.getTaskId()) && !idList2.contains(activityInstance.getTaskId())){
//                        arr.add(activityInstance);
//                    }
//                    //logger.info("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : " +activityInstance.getActivityName());
//                }
//============================================================================================


                // 已执行的节点ID集合
                List<String> executedActivityIdList = new ArrayList<String>();
                int index = 1;
                //logger.info("获取已经执行的节点ID");
                for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                    executedActivityIdList.add(activityInstance.getActivityId());

                    //logger.info("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : " +activityInstance.getActivityName());
                    index++;
                }

                BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

                // 已执行的线集合
                List<String> flowIds = new ArrayList<String>();
                // 获取流程走过的线 (getHighLightedFlows是下面的方法)
                flowIds = getHighLightedFlows(bpmnModel,processDefinition, historicActivityInstanceList);

                // 获取流程图图像字符流
//                ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
                ProcessDiagramGenerator pec = new DefaultProcessDiagramGenerator();
                //配置字体
                InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds,"宋体","微软雅黑","黑体",null,2.0);

//                response.setContentType("image/png");
                OutputStream os = response.getOutputStream();
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                imageStream.close();
            }
            //logger.info("[完成]-获取流程图图像");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //logger.error("【异常】-获取流程图失败！" + e.getMessage());
            //throw new BusinessException("获取流程图失败！" + e.getMessage());
        }
    }


    /**
     * 只看当前节点
     * @param
     * @return
     */
    public void getActivitiCurrentProccessCurrentImage(String processInstanceId, HttpServletResponse response)
    {
        //logger.info("[开始]-获取流程图图像");
        try {
            //  获取历史流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();

            if (historicProcessInstance == null) {
                //throw new BusinessException("获取流程实例ID[" + pProcessInstanceId + "]对应的历史流程实例失败！");
            }
            else
            {
                // 获取流程定义
                ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                        .getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());

//                 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
                List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().desc().list();


                // 已执行的节点ID集合
                List<String> executedActivityIdList = new ArrayList<String>();
                int index = 1;
                //logger.info("获取已经执行的节点ID");
                for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                    if(!activityInstance.getActivityType().endsWith("Gateway") && !activityInstance.getActivityType().endsWith("Event")){
                        executedActivityIdList.add(activityInstance.getActivityId());
                    }

                    //logger.info("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : " +activityInstance.getActivityName());
                    index++;
                }

                BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

                // 已执行的线集合
                List<String> flowIds = new ArrayList<String>();
                // 获取流程走过的线 (getHighLightedFlows是下面的方法)
                flowIds = getHighLightedFlows(bpmnModel,processDefinition, historicActivityInstanceList);

                // 获取流程图图像字符流
//                ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
                ProcessDiagramGenerator pec = new DefaultProcessDiagramGenerator();

                //配置字体
                List<String> executedActivityIdList2 = new ArrayList<String>();
                executedActivityIdList2.add(executedActivityIdList.get(0));
                List<String> flowIds2 = new ArrayList<String>();
                InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList2, flowIds2,"宋体","微软雅黑","黑体",null,2.0);

//                response.setContentType("image/png");
                OutputStream os = response.getOutputStream();
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                imageStream.close();
            }
            //logger.info("[完成]-获取流程图图像");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //logger.error("【异常】-获取流程图失败！" + e.getMessage());
            //throw new BusinessException("获取流程图失败！" + e.getMessage());
        }
    }








    /**
     * 获取已经完成的流程节点列表
     * @param bpmnModel
     * @param processDefinitionEntity
     * @param historicActivityInstances
     * @return
     */
    public List<String> getHighLightedFlows(BpmnModel bpmnModel,ProcessDefinitionEntity processDefinitionEntity,List<HistoricActivityInstance> historicActivityInstances) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //24小时制
        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId

        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            // 对历史流程节点进行遍历
            // 得到节点定义的详细信息
            FlowNode activityImpl = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(i).getActivityId());
            List<FlowNode> sameStartTimeNodes = new ArrayList<FlowNode>();// 用以保存后续开始时间相同的节点
            FlowNode sameActivityImpl1 = null;
            HistoricActivityInstance activityImpl_ = historicActivityInstances.get(i);// 第一个节点
            HistoricActivityInstance activityImp2_;

            for (int k = i + 1; k <= historicActivityInstances.size() - 1; k++) {
                activityImp2_ = historicActivityInstances.get(k);// 后续第1个节点

                if (activityImpl_.getActivityType().equals("userTask") && activityImp2_.getActivityType().equals("userTask") &&
                        df.format(activityImpl_.getStartTime()).equals(df.format(activityImp2_.getStartTime()))) //都是usertask，且主节点与后续节点的开始时间相同，说明不是真实的后继节点
                {
                } else {
                    sameActivityImpl1 = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(k).getActivityId());//找到紧跟在后面的一个节点
                    break;
                }
            }
            sameStartTimeNodes.add(sameActivityImpl1); // 将后面第一个节点放在时间相同节点的集合里
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);// 后续第二个节点

                if (df.format(activityImpl1.getStartTime()).equals(df.format(activityImpl2.getStartTime()))) {// 如果第一个节点和第二个节点开始时间相同保存
                    FlowNode sameActivityImpl2 = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {// 有不相同跳出循环
                    break;
                }
            }
            List<SequenceFlow> pvmTransitions = activityImpl.getOutgoingFlows(); // 取出节点的所有出去的线

            for (SequenceFlow pvmTransition : pvmTransitions) {// 对所有的线进行遍历
                FlowNode pvmActivityImpl = (FlowNode) bpmnModel.getMainProcess().getFlowElement(pvmTransition.getTargetRef());// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }


    //获取历史流程节点
    public List<HistoricTaskInstance> getHisTask(String oldProcessInstanceId){
        return historyService.createHistoricTaskInstanceQuery().executionId(oldProcessInstanceId).list();
    }

    // 回退流程
    public Map<String,String> getCurrentAndBackTask(String processInstanceId){
        Map<String,String> taskMap = new HashMap<String,String>();
        String currentTaskId;//当前节点
        String currentTaskName;
        String backToTaskId;//当前节点前任意一点
        String backToTaskName;

        List<HistoricTaskInstance> currTaskList =  historyService
                .createHistoricTaskInstanceQuery().executionId(processInstanceId).list();
        if(currTaskList == null || currTaskList.size()<3){
            return null;
        }
        currentTaskId = currTaskList.get(currTaskList.size()-1).getId();
        currentTaskName = currTaskList.get(currTaskList.size()-1).getName();
        backToTaskId = currTaskList.get(currTaskList.size()-2).getId();
        backToTaskName = currTaskList.get(currTaskList.size()-2).getName();
        taskMap.put("currentId",currentTaskId);
        taskMap.put("currentName",currentTaskName);
        taskMap.put("backId",backToTaskId);
        taskMap.put("backName",backToTaskName);

        return taskMap;
    }


    /*
     *
     * 任务回退
     *
     * 需求：从当前任务 任意回退至已审批任务
     * 方法：通过activiti源代码里的sqlSession直接修改数据库
     *
     * 第一步 完成历史TASK覆盖当前TASK
     * 用hi_taskinst修改当前ru_task
     * ru_task.ID_=hi_taskinst.ID_
     * ru_task.NAME_=hi_taskinst.NAME_
     * ru_task.TASK_DEF_KEY_=hi_taskinst.TASK_DEF_KEY_
     *
     * 第二步
     * 修改当前任务参与人列表
     * ru_identitylink 用ru_task.ID_去ru_identitylink 索引
     * ru_identitylink.TASK_ID_=hi_taskinst.ID_
     * ru_identitylink.USER_ID=hi_taskinst.ASSIGNEE_
     *
     * 第三步修改流程记录节点 把ru_execution的ACT_ID_ 改为hi_taskinst.TASK_DEF_KEY_
     *
     */

    /*
     * 实现回退方法
     */
    public int dbBackTo(String currentTaskId,String backToTaskId){
//    	if(taskService == null){
//			startUp();
//		}

        int result = activitiConstant.I_NO_OPERATION;
        SqlSession sqlSession = getSqlSession();
        TaskEntity currentTaskEntity = getCurrentTaskEntity(currentTaskId);
        HistoricTaskInstanceEntity backToHistoricTaskInstanceEntity;

        if(currentTaskEntity == null ){
            return 0;
//        	//如果taskEntity查询结果为空，则代表此流程已结束，若要退回，则上一个节点即为当前历史节点的currentId
//        	backToHistoricTaskInstanceEntity = getHistoryTaskEntity(currentTaskId);
//        	//初始化currentTaskEntity
//        	currentTaskEntity = new TaskEntity();
//        	currentTaskEntity.setId(backToHistoricTaskInstanceEntity.getId());
//        	currentTaskEntity.setRevision(1);
//        	ExecutionEntity executionEntity = new ExecutionEntity();
//        	executionEntity.setId(backToHistoricTaskInstanceEntity.getExecutionId());
//        	executionEntity.setRevision(5);
//        	executionEntity.setProcessDefinitionId(backToHistoricTaskInstanceEntity.getProcessDefinitionId());
//        	executionEntity.setActive(true);
//        	ActivityImpl activity = getActivitiImp(backToHistoricTaskInstanceEntity.getProcessDefinitionId(), backToHistoricTaskInstanceEntity.getProcessDefinitionKey());
//        	executionEntity.setActivity(activity);
//        	executionEntity.setEventScope(false);
//        	executionEntity.setScope(true);
//        	executionEntity.setConcurrent(false);
//        	executionEntity.setCachedEntityState(2);
//        	sqlSession.insert("insertExecution", executionEntity);
//
//        	currentTaskEntity.setExecutionId(backToHistoricTaskInstanceEntity.getExecutionId());
//        	currentTaskEntity.setProcessInstance(executionEntity);
//        	currentTaskEntity.setProcessInstanceId(backToHistoricTaskInstanceEntity.getProcessInstanceId());
//        	currentTaskEntity.setProcessDefinitionId(backToHistoricTaskInstanceEntity.getProcessDefinitionId());
//        	currentTaskEntity.setName(backToHistoricTaskInstanceEntity.getName());
//        	currentTaskEntity.setTaskDefinitionKey(backToHistoricTaskInstanceEntity.getTaskDefinitionKey());
//        	currentTaskEntity.setAssignee(backToHistoricTaskInstanceEntity.getAssignee());
//        	currentTaskEntity.setPriority(50);
//        	currentTaskEntity.setCreateTime(backToHistoricTaskInstanceEntity.getStartTime());
//        	currentTaskEntity.setSuspensionState(1);
//        	currentTaskEntity.setTenantId("");
//        	currentTaskEntity.setFormKey(backToHistoricTaskInstanceEntity.getFormKey());
//        	sqlSession.insert("insertTask", currentTaskEntity);
//        	IdentityLinkEntity identityLinkEntity = new IdentityLinkEntity();
//        	identityLinkEntity.setId(backToHistoricTaskInstanceEntity.getExecutionId() + 5);
//        	identityLinkEntity.setTask(currentTaskEntity);
//            identityLinkEntity.setUserId(currentTaskEntity.getAssignee());
//            identityLinkEntity.setType("participant");
//            sqlSession.insert("insertIdentityLink", identityLinkEntity);
        }else{
            backToHistoricTaskInstanceEntity = getHistoryTaskEntity(backToTaskId);
        }
        String processDefinitionId = currentTaskEntity.getProcessDefinitionId();
        String executionId = currentTaskEntity.getExecutionId();
        String currentTaskEntityId = currentTaskEntity.getId();
        String backToHistoricTaskInstanceEntityId = backToHistoricTaskInstanceEntity.getId();
        String backToTaskDefinitionKey = backToHistoricTaskInstanceEntity.getTaskDefinitionKey();
        String backToAssignee = backToHistoricTaskInstanceEntity.getAssignee();
        boolean success = false;
        try
        {
            // 1.
            StepOne_use_hi_taskinst_to_change_ru_task(sqlSession, currentTaskEntity, backToHistoricTaskInstanceEntity);
            // 2.
            StepTwo_change_ru_identitylink(sqlSession, currentTaskEntityId, backToHistoricTaskInstanceEntityId,
                    backToAssignee);
            // 3.
            StepThree_change_ru_execution(sqlSession, executionId, processDefinitionId, backToTaskDefinitionKey);
            historyService.deleteHistoricTaskInstance(currentTaskId);
            success = true;
        }
        catch (Exception e)
        {
            throw new ActivitiException("回退异常", e);
        }
        finally
        {
            if (success)
            {
                sqlSession.commit();
                result = activitiConstant.I_DONE;
            }
            else
            {
                sqlSession.rollback();
                result = activitiConstant.I_ROLLBACK;
            }
            sqlSession.close();
        }
        return result;
    }

//    第三步修改流程记录节点 把ru_execution的ACT_ID_ 改为hi_taskinst.TASK_DEF_KEY_
//    private static void StepThree_change_ru_execution(SqlSession sqlSession, String executionId,
//                                                      String processDefinitionId, String backToTaskDefinitionKey) throws Exception
//    {
//        List<ExecutionEntity> currentExecutionEntityList = sqlSession.selectList("selectExecution", executionId);
//        if (currentExecutionEntityList.size() > 0)
//        {
//            ActivityImpl activity = getActivitiImp(processDefinitionId, backToTaskDefinitionKey);
//
//            Iterator<ExecutionEntity> execution = currentExecutionEntityList.iterator();
//            while (execution.hasNext())
//            {
//                ExecutionEntity e = execution.next();
//                e.setActivity(activity);
//                p(sqlSession.update("updateExecution", e));
//            }
//        }
//    }

    private  void StepThree_change_ru_execution(SqlSession sqlSession, String executionId,
                                                      String processDefinitionId, String backToTaskDefinitionKey) throws Exception
    {
        List<ExecutionEntity> currentExecutionEntityList = sqlSession.selectList("selectExecution", executionId);
        if (currentExecutionEntityList.size() > 0)
        {
            HistoricActivityInstance his = getActivitiImp(processDefinitionId, backToTaskDefinitionKey);

            Iterator<ExecutionEntity> execution = currentExecutionEntityList.iterator();
            while (execution.hasNext())
            {
                ExecutionEntity e = execution.next();
                if (his != null) {
                    e.setEventName(his.getActivityName());
                    e.setProcessInstanceId(his.getActivityId());
                }
                p(sqlSession.update("updateExecution", e));
            }
        }
    }

    private  void StepTwo_change_ru_identitylink(SqlSession sqlSession, String currentTaskEntityId,
                                                       String backToHistoricTaskInstanceEntityId, String backToAssignee) throws Exception
    {
        ListQueryParameterObject para = new ListQueryParameterObject();
        para.setParameter(currentTaskEntityId);
        List<IdentityLinkEntity> currentTaskIdentityLinkEntityList = sqlSession.selectList("selectIdentityLinksByTask",
                para);
        if (currentTaskIdentityLinkEntityList.size() > 0)
        {
            Iterator<IdentityLinkEntity> identityLinkEntityList = currentTaskIdentityLinkEntityList.iterator();
            IdentityLinkEntity identityLinkEntity;
//            TaskEntity tmpTaskEntity;
//            tmpTaskEntity = new TaskEntity();

            TaskEntityImpl tmpTaskEntity = new TaskEntityImpl();

            while (identityLinkEntityList.hasNext())
            {
                identityLinkEntity = identityLinkEntityList.next();
                identityLinkEntity.setTask(tmpTaskEntity);
                identityLinkEntity.setUserId(backToAssignee);
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("id", identityLinkEntity.getId());
                sqlSession.delete("deleteIdentityLink", parameters);
                sqlSession.insert("insertIdentityLink", identityLinkEntity);
            }
        }
    }

    private static void StepOne_use_hi_taskinst_to_change_ru_task(SqlSession sqlSession, TaskEntity currentTaskEntity,
                                                                  HistoricTaskInstanceEntity backToHistoricTaskInstanceEntity) throws Exception
    {
        sqlSession.delete("deleteTask", currentTaskEntity);
        currentTaskEntity.setName(backToHistoricTaskInstanceEntity.getName());
        currentTaskEntity.setTaskDefinitionKey(backToHistoricTaskInstanceEntity.getTaskDefinitionKey());
        currentTaskEntity.setId(backToHistoricTaskInstanceEntity.getId());
        sqlSession.insert("insertTask", currentTaskEntity);
    }

    public static void p(Object o)
    {
        System.out.println(o);
    }

//    private static ActivityImpl getActivitiImp(String processDefinitionId, String taskDefinitionKey)
//    {
//        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
//                .getDeployedProcessDefinition(processDefinitionId);
//        List<ActivityImpl> activitiList = processDefinition.getActivities();
//        boolean b;
//        Object activityId;
//        for (ActivityImpl activity : activitiList)
//        {
//            activityId = activity.getId();
//            b = activityId.toString().equals(taskDefinitionKey);
//            if (b)
//            {
//                return activity;
//            }
//        }
//        return null;
//    }

    private  HistoricActivityInstance getActivitiImp(String processDefinitionId, String taskDefinitionKey)
    {
        // 根据流程实例来查询
        HistoricActivityInstanceQuery historicQuery = historyService.createHistoricActivityInstanceQuery();
        // 设置流程实例的id
        List<HistoricActivityInstance> historicActivityInstanceList = historicQuery
                .processDefinitionId(processDefinitionId)
                .orderByHistoricActivityInstanceStartTime().asc() // 根据节点开始的时间升序进行排序
                .list();
        boolean b;
        Object historicActivityInstanceId;
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList)
        {
            historicActivityInstanceId = historicActivityInstance.getId();
            b = historicActivityInstanceId.toString().equals(taskDefinitionKey);
            if (b)
            {
                return historicActivityInstance;
            }
        }
        return null;
    }


    private TaskEntity getCurrentTaskEntity(String id)
    {
        return (TaskEntity) taskService.createTaskQuery().taskId(id).singleResult();
    }

    private HistoricTaskInstanceEntity getHistoryTaskEntity(String id)
    {
        return (HistoricTaskInstanceEntity) historyService.createHistoricTaskInstanceQuery()
                .taskId(id).singleResult();
    }

    private SqlSession getSqlSession()
    {
        DbSqlSessionFactory dbSqlSessionFactory = (DbSqlSessionFactory)((ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration()).getSessionFactories().get(DbSqlSession.class);
        SqlSessionFactory sqlSessionFactory = dbSqlSessionFactory.getSqlSessionFactory();
        return sqlSessionFactory.openSession();
    }



    //跳转方法
    public void jump(String taskId){
        //当前任务
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
//        TaskEntity currentTask = getCurrentTaskEntity(taskId);
        //获取流程定义

        Process process = repositoryService.getBpmnModel(currentTask.getProcessDefinitionId()).getMainProcess();
//        Process process = ProcessDefinitionUtil.getBpmnModel(currentTask.getProcessDefinitionId()).getMainProcess();
        //获取目标节点定义
//        FlowNode targetNode = (FlowNode)process.getFlowElement("startTask");
//        FlowNode targetNode = (FlowNode)process.getFlowElement("reviewTask");
        FlowNode targetNode = (FlowNode)process.getFlowElement("usertask1");
        //删除当前运行任务
        String executionEntityId = managementService.executeCommand(new DeleteTaskCmd(currentTask.getId()));
        //流程执行到来源节点
        managementService.executeCommand(new SetFLowNodeAndGoCmd(targetNode, executionEntityId));
    }
    //删除当前运行时任务命令，并返回当前任务的执行对象id
//这里继承了NeedsActiveTaskCmd，主要时很多跳转业务场景下，要求不能时挂起任务。可以直接继承Command即可
    public class DeleteTaskCmd extends NeedsActiveTaskCmd<String> {
        public DeleteTaskCmd(String taskId){
            super(taskId);
        }
        public String execute(CommandContext commandContext, TaskEntity currentTask){
            //获取所需服务
            TaskEntityManagerImpl taskEntityManager = (TaskEntityManagerImpl)commandContext.getTaskEntityManager();
            //获取当前任务的来源任务及来源节点信息
            ExecutionEntity executionEntity = currentTask.getExecution();
            //删除当前任务,来源任务
            taskEntityManager.deleteTask(currentTask, "jumpReason", false, false);
            return executionEntity.getId();
        }
        public String getSuspendedTaskException() {
            return "挂起的任务不能跳转";
        }
    }
    //根据提供节点和执行对象id，进行跳转命令
    public class SetFLowNodeAndGoCmd implements Command<Void> {
        private FlowNode flowElement;
        private String executionId;
        public SetFLowNodeAndGoCmd(FlowNode flowElement,String executionId){
            this.flowElement = flowElement;
            this.executionId = executionId;
        }

        public Void execute(CommandContext commandContext){
            //获取目标节点的来源连线
            List<SequenceFlow> flows = flowElement.getIncomingFlows();
            if(flows==null || flows.size()<1){
                throw new ActivitiException("回退错误，目标节点没有来源连线");
            }
            //随便选一条连线来执行，时当前执行计划为，从连线流转到目标节点，实现跳转
            ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);
            executionEntity.setCurrentFlowElement(flows.get(0));
            commandContext.getAgenda().planTakeOutgoingSequenceFlowsOperation(executionEntity, true);
            return null;
        }
    }




}
