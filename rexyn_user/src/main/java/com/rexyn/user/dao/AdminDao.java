package com.rexyn.user.dao;

import com.rexyn.user.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface AdminDao extends JpaRepository<Admin,String>,JpaSpecificationExecutor<Admin>{
	public Admin findByLoginname(String loginname);
}
