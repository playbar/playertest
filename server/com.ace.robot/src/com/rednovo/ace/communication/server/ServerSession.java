/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月21日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：ServerSession.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.communication.EventInvokerManager;
import com.rednovo.ace.communication.Invoker;
import com.rednovo.ace.entity.Message;
import com.rednovo.tools.PPConfiguration;

/**
 * @author yongchao.Yang/2014年10月21日
 */
public class ServerSession {

	private static EventInvokerManager eventMgr = EventInvokerManager.getInstance();
	private static ServerSession cs;
	private String serverId;
	private static Logger logger = Logger.getLogger(ServerSession.class);
	private ServerWorkerPool swp;

	/**
	 * 
	 * @param invoker {@link Invoker} 会话事件回调接口
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月21日上午11:45:01
	 */
	public static ServerSession getInstance() {
		if (!eventMgr.hasInvoker()) {
			System.out.println("[error] [请先注册事件回调对象]");
			return null;
		}
		if (cs == null) {
			cs = new ServerSession();
		}
		return cs;
	}

	private ServerSession() {
		this.serverId = PPConfiguration.getProperties("cfg.properties").getString("server.id");
		swp = ServerWorkerPool.getInstance();
	}

	/**
	 * 返回当前服务器ID
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月9日上午12:47:45
	 */
	public String getServerId() {
		return this.serverId;
	}

	/**
	 * 通过开始新线程启动客户端socket监听程序.
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月21日下午3:01:56
	 */
	public void listen() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ServerListener.getInstance().listen();
				} catch (Exception e) {
					logger.error("线程异常", e);
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 发送消息
	 * 
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2014年10月21日上午11:19:00
	 */
	public final void sendLocalMessage(String sessionId, Message msg) {
		LocalMessageCache.getInstance().addSendMsg(sessionId, msg);
		ServerWriter writer = swp.getWriter(sessionId);
		if (writer == null) {
			logger.error("[session:" + sessionId + "][找不到socket writer,消息发送失败][ " + JSON.toJSONString(msg) + "]");
			return;
		}
		swp.getWriter(sessionId).wakeUp();
	}

}
