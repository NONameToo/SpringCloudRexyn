package com.rexyn.activiti.controller;

import com.rexyn.activiti.service.AcitivitiService;
import entity.Result;
import entity.StatusCode;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author PCMSI
 * @Date 2019/10/31 15:39
 * @Version 1.0
 **/
@RestController
@CrossOrigin
@RequestMapping("/activiti")
public class ActivitiController {
    @Autowired
    private AcitivitiService acitivitiService;


    /**
     * 部署流程
     * @param key
     * @return
     */
    @RequestMapping(value = "/deploy/{key}",method = RequestMethod.GET)
    public Result deploy(@PathVariable("key") String key){
        acitivitiService.deploy(key);
        return new Result(true, StatusCode.OK,"部署流程成功!");
    }

    /**
     * 判断流程是否已经部署
     * @param key
     * @return
     */
    @RequestMapping(value = "/isDeployed/{key}",method = RequestMethod.GET)
    public Result isDeployed(@PathVariable("key") String key){
        boolean deployed = acitivitiService.isDeployed(key);
        if(deployed){
            return new Result(true, StatusCode.OK,"流程已经是部署状态!");
        }else{
            return new Result(false, StatusCode.ERROR,"流程未部署!");
        }
    }

    /**
     * 更新流程定义,会级联删除旧的流程实例
     * @param key
     * @return
     */
    @RequestMapping(value = "/updateDeploy/{key}",method = RequestMethod.GET)
    public Result updateDeploy(@PathVariable("key") String key){
        acitivitiService.updateDeploy(key);
        return new Result(true, StatusCode.OK,"更新流程定义成功!");
    }



    /**
     * 开启流程实例
     * @param key
     * @param curMap
     * @return
     */
    @RequestMapping(value = "/createInstance/{key}",method = RequestMethod.POST)
    public Result createInstance(@PathVariable String key, @RequestBody Map<String, Object> curMap) {
        String instance = acitivitiService.createInstance(key, curMap);
        return new Result(true, StatusCode.OK,"启流程实例成功!",instance);
    }

    /**
     * 根据流程实例id获取当前流程节点的任务名字
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "/getTaskNameByProcessInstanceId/{processInstanceId}",method = RequestMethod.GET)
    public Result getTaskNameByProcessInstanceId(String processInstanceId) {
        String taskName = acitivitiService.getTaskNameByProcessInstanceId(processInstanceId);
        return new Result(true, StatusCode.OK,"根据流程实例id获取当前流程节点的任务名字成功!",taskName);
    }


    /**
     * 完成任务
     * @param taskId
     * @param action
     * @param map
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "/completeTask/{taskId}/{action}/{processInstanceId}",method = RequestMethod.POST)
    public Result completeTask(@PathVariable("taskId") String taskId,@PathVariable("action")String action,@RequestBody Map<String,Object> map,@PathVariable("processInstanceId")String processInstanceId) {
        acitivitiService.completeTask(taskId,action,map,processInstanceId);
        return new Result(true, StatusCode.OK,"完成任务成功!");
    }

    /**
     * 查看流程图
     * @param key
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/processImage/{key}",method = RequestMethod.GET)
    public void getImageStream(@PathVariable("key") String key, HttpServletResponse response) throws IOException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");
        acitivitiService.getProcessImage(key,response);
    }

    /**
     * 获取流程执行[进度]图片
     * @param processDefinitionId
     * @param resourceName
     * @param processInstanceId
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/currentProccessImage/{processDefinitionId}/{resourceName}/{processInstanceId}",method = RequestMethod.GET)
    public void currentProccessImage(@PathVariable(value = "processDefinitionId") String processDefinitionId,@PathVariable("resourceName") String resourceName, @PathVariable("processInstanceId") String processInstanceId, HttpServletResponse response) throws Exception {
        // 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        if (!resourceName.endsWith(".png")){
            resourceName = resourceName + ".png";
        }
        response.setContentType("image/png");
        acitivitiService.getActivitiProccessImage(processDefinitionId, resourceName, processInstanceId, response);
    }



    /**
     * 获取流程执行[进度]图片,只显示当前节点
     * @param processDefinitionId
     * @param resourceName
     * @param processInstanceId
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/currentProccessCurrentImage/{processDefinitionId}/{resourceName}/{processInstanceId}",method = RequestMethod.GET)
    public void getActivitiCurrentProccessCurrentImage(@PathVariable(value = "processDefinitionId") String processDefinitionId,@PathVariable("resourceName") String resourceName, @PathVariable("processInstanceId") String processInstanceId, HttpServletResponse response) throws Exception {
        // 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        if (!resourceName.endsWith(".png")){
            resourceName = resourceName + ".png";
        }
        response.setContentType("image/png");
        acitivitiService.getActivitiProccessCurrentImage(processDefinitionId, resourceName, processInstanceId, response);
    }


    /**
     * 获取历史流程节点
     * @param oldProcessInstanceId
     * @return
     */
    @RequestMapping(value = "/getHisTask/{oldProcessInstanceId}",method = RequestMethod.GET)
    public Result getHisTask(@PathVariable("oldProcessInstanceId") String oldProcessInstanceId){
        List<HistoricTaskInstance> hisTask = acitivitiService.getHisTask(oldProcessInstanceId);
        return new Result(true, StatusCode.OK,"获取历史流程节点成功!",hisTask);
    }

    /**
     * 查询是否可以回退流程
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "/getCurrentAndBackTask/{processInstanceId}",method = RequestMethod.GET)
    public Result  getCurrentAndBackTask(@PathVariable("processInstanceId") String processInstanceId){
        Map<String, String> map = acitivitiService.getCurrentAndBackTask(processInstanceId);

        if(map == null){
            return new Result(false, StatusCode.ERROR,"不能回退流程，找不到相应的流程节点!");

        }else{
            return new Result(true, StatusCode.OK,"可以回退流程!",map);
        }
    }

    @RequestMapping(value = "/dbBackTo/{currentTaskId}/{backToTaskId}",method = RequestMethod.POST)
    public Result  dbBackTo(@PathVariable("currentTaskId") String currentTaskId,@PathVariable("backToTaskId") String backToTaskId) {
//        int i = acitivitiService.dbBackTo(currentTaskId, backToTaskId);
        acitivitiService.jump(currentTaskId);

//        if (i == 0) {
//            return new Result(false, StatusCode.ERROR, "不能回退流程，找不到相应的流程节点!");
//        } else if (i == 1) {
//            return new Result(true, StatusCode.OK, "回退流程成功!");
//
//        } else if (i == 2) {
//            return new Result(false, StatusCode.ERROR, "任务未找到!");
//
//        } else if (i == 3) {
//            return new Result(false, StatusCode.ERROR, "操作失败,回滚事务!");
//        }
//
        return new Result(true, StatusCode.OK,"退回流程成功!");

    }

}
