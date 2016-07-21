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
 *                  fileName：ServerEventInvoker.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.communication.Invoker;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Server;
import com.rednovo.ace.globalData.ServerRoutManager;

/**
 * @author yongchao.Yang/2014年10月21日
 */
public class ServerEventInvoker implements Invoker {
	private static Logger logger = Logger.getLogger(ServerEventInvoker.class);
	private LocalMessageCache localMsgCache = null;

	/**
	 * 
	 */
	public ServerEventInvoker() {
		localMsgCache = LocalMessageCache.getInstance();
	}

	@Override
	public void onReady() {
		// 注册服务器,便于其他在线服务器建立广播连接
		this.registHost();
	}

	@Override
	public void onConnectFailed() {
		logger.info("[ServerEventInvoker][连接失败]");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rednovo.ace.communication.Invoker#onOpenSession(java.lang.String)
	 */
	@Override
	public void onConnectSucessed(String sessionId) {
		logger.info("[ServerEventInvoker][创建新连接]----->>[" + sessionId + "]<<-----");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rednovo.ace.communication.Invoker#onCloseSession(java.lang.String)
	 */
	@Override
	public void onSuspend(String sessionId) {
		logger.info("[ServerEventInvoker][关闭连接]----->>[" + sessionId + "]<<------");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rednovo.ace.communication.Invoker#onMessageIn(com.rednovo.ace.communication.Message)
	 */
	@Override
	public void onNewMessage(String sessionId, Message msg) {
		// logger.info("\t\t[server][捕获到新消息]");
		if (msg.getSumy().getInteractMode().equals(InteractMode.REQUEST.getValue())) {
			localMsgCache.addReceiveMsg(sessionId, msg);
			// 唤醒消息处理线程
			DataParseWorker.getInstance().wakeup();
		} else if (msg.getSumy().getRequestKey().equals("009-003")) {
			// 正常情况服务器是不会接受InteractMode.RESPONSE的消息，后期新增服务器间相互通信
			// 之所以只针对009-003(服务期间心跳)，是因为服务器转发消息也会受到回执
			// 处理需要同步返回的消息
			logger.info("[MSG][" + JSON.toJSONString(msg) + "]");
			BlockMessageListener.getInstance().addNewMsgId(msg.getSumy().getMsgId());
		}
	}

	/**
	 * 获取本地服务器
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年2月29日上午11:04:49
	 */
	private void registHost() {
		Server s = ServerHelpler.getLocalHost();
		ServerRoutManager.registServer(s);
	}

}
