package com.rexyn.activiti.common;

import org.springframework.stereotype.Component;

/**
 * @ClassName fushaokai
 * @Description 工作流模块相关常量
 * @Author PCMSI
 * @Date 2019/10/29 15:21
 * @Version 1.0
 **/
@Component
public class ActivitiConstant {
    // bpmn和png相关的文件所在目录resouce/npmn文件下
    public static final String BPMNPATH = "bpmn/";

    public static final int I_NO_OPERATION = 0;

    public static final int I_DONE = 1;

    public static final int I_TASK_NOT_FOUND = 2;

    public static final int I_ROLLBACK = 3;

}
