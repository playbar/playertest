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
 *                  fileName：com.popo.db.UserTransactionManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.activity.ds;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

/**
 * 通过容器注入的UserTransaction对象管理JTA事务对象
 * 
 * @author Administrator
 */
public class UserTransactionManager {
	private static UserTransaction ut = null;
	private static Logger log = Logger.getLogger(UserTransactionManager.class.getName());
	static {
		Context ctx;
		try {
			log.debug("初始化事务管理器...");
			ctx = new InitialContext();
			ut = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
		} catch (NamingException e) {
			log.error("初始化事务管理器失败", e);
		}
	}

	/**
	 * 获取UserTransaction对象
	 * 
	 * @author Administrator/下午1:50:22/2012
	 */
	public static UserTransaction getUserTransaction() {
		return ut;
	}
}
