/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年5月31日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.commucation.server
 *                  fileName：DestroyHook.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.hook;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.rednovo.ace.communication.server.ServerHelpler;
import com.rednovo.ace.globalData.GlobalUserSessionMapping;

/**
 * @author yongchao.Yang/2016年5月31日
 */
public class DestroyHook {
	private static Logger logger = Logger.getLogger(DestroyHook.class);
	static {
		logger.info("[开始注册钩子]");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("[服务结束，执行销毁线程]");
				// 结束该服务节点所有会话信息
				String serverId = ServerHelpler.getLocalHost().getId();
				Iterator<String> cacheSessions = GlobalUserSessionMapping.getServerSession(serverId).iterator();
				while (cacheSessions.hasNext()) {
					String cssid = cacheSessions.next();
					cacheSessions.remove();
					GlobalUserSessionMapping.finishPossibleShow(cssid);
					GlobalUserSessionMapping.expireSession(cssid);

				}
			}
		});
	}

	public static void registHook() {
		logger.info("[注册JVM销毁钩子程序]");
	}
}
