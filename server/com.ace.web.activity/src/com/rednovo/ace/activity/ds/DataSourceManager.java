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
 *                  prj-name：com.popo.logic
 *                  fileName：com.popo.db.DataSourceManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.activity.ds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * 数据库资源管理服务
 * 
 * @author Administrator
 */
public class DataSourceManager {
	private static DataSourceManager dsMgr = null;
	private static HashMap<String, DataSource> dsMap = new HashMap<String, DataSource>();
	private static Logger log = Logger.getLogger(DataSourceManager.class.getName());

	/**
	 * 
	 * @author Administrator/上午11:36:19/2012
	 */
	public synchronized static DataSourceManager getInstance() {
		if (dsMgr == null) {
			log.debug("初始化连接池管理程序...");
			dsMgr = new DataSourceManager();
		}
		return dsMgr;
	}

	/**
	 * 私有构造方法，初始化各项数据源
	 */
	private DataSourceManager() {
		dsMap.clear();
		Context ctx = null;
		try {
			ctx = new InitialContext();
		} catch (NamingException e) {
			log.error("初始化JNDI上下文环境出现错误", e);
		}
		ArrayList<String> dsList = DataSourceUtil.getDataSourceList();
		for (String ds : dsList) {
			try {
				dsMap.put(ds, (DataSource) ctx.lookup("java:comp/env/jdbc/" + ds));
			} catch (NamingException ex) {
				log.error("初始化数据源出现错:" + ds, ex);
			}
		}
	}

	private DataSource getDataSource(String jndiName) {
		return dsMap.get(jndiName);
	}

	/**
	 * 获取数据库连接对象
	 * 
	 * @author Administrator/上午11:58:46/2012
	 */
	public Connection getConnection(String dbName) {
		DataSourceManager scm = getInstance();
		DataSource ds = scm.getDataSource(dbName);
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			log.debug("获取Connecton资源失败", e);
			return null;
		}
	}

	/**
	 * 释放资源,回收到连接池中
	 * 
	 * @author Administrator/上午11:58:08/2012
	 */
	public static boolean returnConn(Connection cnn) {
		try {
			if (!cnn.isClosed()) {
				cnn.close();
			}
			// log.debug("回收Connecton资源成功");
			return true;
		} catch (Exception e) {
			log.debug("回收Connecton资源失败", e);
			return false;
		}
	}

	/**
	 * 刷新数据库资源管理单例对象
	 * 
	 * @author Administrator/下午12:17:44/2012
	 */
	public void refresh() {
		dsMgr = null;
		getInstance();
	}
}
