package com.rexyn.activiti.dev;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;

import java.text.SimpleDateFormat;

/**
 * @ClassName fushaokai
 * @Description
 * 将流程部署到activiti对应的表里面去
 * @Author PCMSI
 * @Date 2019/10/9 16:39
 * @Version 1.0
 **/
public class ActivitiDeployment {
    /**
     * 1.注意,这里是单独运行activiti,所以,要使用到activiti.cfg.xml文件
     * 2.将流程部署到activiti对应的表里面去具体影响到了哪几张表:
     *  act_re_deployment  部署信息
     *  act_re_procdef      流程定义的一些信息
     *  act_ge_bytearray    流程定义的一些文件,如npmn,png文件
     *
     * @param args
     */

    public static void main(String[] args) {
        // 1.创建processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.得到相关的repositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3.进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/BPMTestFile.bpmn")    //指定bpmn文件
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
}
