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
 *                  fileName：com.popo.db.DBReleaser.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.ds;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库资源回收程序
 * 
 * @author Administrator
 */
public class DBReleaser {
	/**
	 * 关闭相关资源
	 * 
	 * @param conn
	 * @param statement
	 * @param result
	 * @return
	 * @author Yongchao.Yang/2012-8-18/2012
	 */
	public static boolean release(Connection conn, Statement statement, ResultSet result) {
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			DataSourceManager.returnConn(conn);
		}
		return true;
	}

	/**
	 * 关闭相关资源
	 * 
	 * @param conn
	 * @param statement
	 * @return
	 * @author Yongchao.Yang/2012-8-18/2012
	 */
	public static boolean release(Connection conn, Statement statement) {
		return release(conn, statement, null);
	}

	/**
	 * 关闭相关资源
	 * 
	 * @param conn
	 * @return
	 * @author Yongchao.Yang/2012-8-18/2012
	 */
	public static boolean release(Connection conn) {
		return release(conn, null, null);
	}

	/**
	 * 关闭相关资源
	 * 
	 * @param stement
	 * @return
	 * @author Yongchao.Yang/2012-8-18/2012
	 */
	public static boolean release(Statement stement) {
		return release(null, stement, null);
	}

	/**
	 * 关闭相关资源
	 * 
	 * @param res
	 * @return
	 * @author Yongchao.Yang/2012-8-18/2012
	 */
	public static boolean release(ResultSet res) {
		return release(null, null, res);
	}

	/**
	 * 关闭相关资源
	 * 
	 * @param statement
	 * @param res
	 * @return
	 * @author Yongchao.Yang/2012-8-18/2012
	 */
	public static boolean release(Statement statement, ResultSet res) {
		return release(null, statement, res);
	}

}
