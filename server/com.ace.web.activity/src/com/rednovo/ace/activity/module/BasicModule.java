/*  ------------------------------------------------------------------------------ 
 * 
 *    				软件名称:泡泡娱乐交友平台(手机版)
 *    				公司名称:北京双迪信息技术有限公司
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2012-8-16/2012
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容仅限于北京双迪信息技术有限公司内部使用 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.popo.transaction
 *                  fileName：com.popo.func.BasicFun.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.activity.module;

import java.sql.Connection;

import com.rednovo.ace.activity.dao.ServiceUserDao;
import com.rednovo.ace.activity.dao.UserDao;
import com.rednovo.ace.activity.ds.DBReleaser;
import com.rednovo.ace.activity.ds.DataSourceManager;
import com.rednovo.ace.activity.ds.DataSourceUtil;

/**
 * FUNCTION层对象基础类.用户管理各个功能对象中Dao的生成和相关资源的释放
 * 
 * @author YongChao.Yang
 * 
 */
public class BasicModule {
	private UserDao userDao;
	private Connection userPoolConn,serviceUserPoolConn;
	private ServiceUserDao serviceUserDao;

	// 设备号
	private String deviceNo;

	/**
	 * 获取用户持久层操作DAO对象
	 * 
	 * @return
	 * @author YongChao.Yang/2012-9-5/2012
	 */
	public UserDao getUserDao() {
		if (this.userDao == null) {
			this.userDao = new UserDao(this.getUserPoolConn());
		}
		return this.userDao;
	}

	private Connection getUserPoolConn() {
		if (this.userPoolConn == null) {
			this.userPoolConn = DataSourceManager.getInstance().getConnection(DataSourceUtil.getActivityUserDataSource());
		}
		return this.userPoolConn;
	}

	/**
	 * 获取用户持久层操作DAO对象
	 * 
	 * @return
	 * @author YongChao.Yang/2012-9-5/2012
	 */
	public ServiceUserDao getserviceUserDao() {
		if (this.serviceUserDao == null) {
			this.serviceUserDao = new ServiceUserDao(this.getserviceUserPoolConn());
		}
		return this.serviceUserDao;
	}

	private Connection getserviceUserPoolConn() {
		if (this.serviceUserPoolConn == null) {
			this.serviceUserPoolConn = DataSourceManager.getInstance().getConnection(DataSourceUtil.getServiceUserDataSource());
		}
		return this.serviceUserPoolConn;
	}
	
	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	/**
	 * 释放Function层实例所涉及到的所有数据库连接资源
	 * 
	 * @author YongChao.Yang/2012-9-5/2012
	 */
	public void release() {
		if (this.userDao != null) {
			DBReleaser.release(this.userPoolConn);
		}
	}
}
