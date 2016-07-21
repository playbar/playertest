/*  ------------------------------------------------------------------------------ 
 * 
 *    				软件名称:泡泡娱乐交友平台(手机版)
 *    				公司名称:北京双迪B信息技术有限公司
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2012-8-16/2012
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容仅限于北京双迪信息技术有限公司内部使用 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.popo.logic
 *                  fileName：com.popo.func.dao.BasicDao.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.dao;

import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * @author Administrator
 * 
 */
public class BasicDao {
	/**
	 * 注入的连接对象
	 */
	private Connection conn;
	/**
	 * 日志对象
	 */
	private Logger log;

	public BasicDao(Connection connection) {
		this.conn = connection;
		this.log = Logger.getLogger(this.getClass());
	}

	public Connection getConnnection() {
		return this.conn;
	}

	public Logger getLogger() {
		return this.log;
	}

}
