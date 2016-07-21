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
 *                  fileName：ClientEventInvoker.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.client;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.communication.Invoker;
import com.rednovo.ace.entity.Message;

/**
 * @author yongchao.Yang/2014年10月21日
 */
public class ClientEventInvoker implements Invoker {
	private Logger logger = Logger.getLogger(ClientEventInvoker.class);

	public ClientEventInvoker() {}

	@Override
	public void onReady() {

	}

	@Override
	public void onConnectFailed() {
		logger.info("[client invoker] [连接失败]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rednovo.ace.communication.Invoker#onOpenSession(java.lang.String)
	 */
	@Override
	public void onConnectSucessed(String sessionId) {
		logger.info("[client invoker] [连接成功(" + sessionId + ")]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rednovo.ace.communication.Invoker#onCloseSession(java.lang.String)
	 */
	@Override
	public void onSuspend(String sessionId) {
		logger.info("[client invoker] [会话中断]");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rednovo.ace.communication.Invoker#onMessageIn(com.rednovo.ace.communication.Message)
	 */
	@Override
	public void onNewMessage(String sessionId, Message msg) {
		logger.info("\t\t[服务器响应 内容]:" + JSON.toJSONString(msg));
	}

}
