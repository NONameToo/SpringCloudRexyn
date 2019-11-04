package com.rexyn.activiti.common;

import java.io.Serializable;


/**
 * 完成节点task任务时,处理的结果,是统一还是不同意
 */
public class ActivitiDecision implements Serializable{
	
	private static final long serialVersionUID = 3L;	

	public String action = "";
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean getDecide() {
		if(action.equals("approve")){
			return true;
		}else{
			return false;
		}
	}
}
