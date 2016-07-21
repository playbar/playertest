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
package com.rednovo.ace.communication.server.broadcast;

import org.apache.log4j.Logger;

import com.rednovo.ace.communication.server.LocalMessageCache;
import com.rednovo.ace.communication.server.ServerWorkerPool;
import com.rednovo.ace.communication.server.ServerWriter;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Server;

/**
 * 服务器间通信-会话服务
 * 
 * @author yongchao.Yang/2014年10月21日
 */
public class BroadCaster {
	private volatile boolean isConnected = false;
	private BroadCastListener broadCastListener;
	private Server server; // 本广播服务要连接的服务

	private String sessionId;// 广播服务在本地的会话ID,Writer和Reader要托管在ServerWokrPool中

	private ServerWorkerPool swp = ServerWorkerPool.getInstance();

	private static Logger logger = Logger.getLogger(BroadCaster.class);

	public BroadCaster(Server s) {
		this.server = s;
	}

	/**
	 * 通过开始新线程启动客户端socket监听程序.
	 * 
	 * @author Yongchao.Yang
	 * @throws Exception
	 * @since 2014年10月21日下午3:01:56
	 */
	protected void open() throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					broadCastListener = new BroadCastListener(server);
					broadCastListener.listen();;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		Thread.sleep(2000);

	}

	/**
	 * 发送消息，然后阻塞等待
	 * 
	 * @param msg
	 * @author Yongchao.Yang
	 * @throws Exception
	 * @since 2014年10月21日上午11:19:00
	 */
	public final void broadCast(Message msg) {
		LocalMessageCache.getInstance().addSendMsg(sessionId, msg);
		ServerWriter writer = swp.getWriter(sessionId);
		if (writer == null) {
			logger.error("session:" + sessionId + ",找不到socket writer,消息发送失败");
			return;
		}
		swp.getWriter(sessionId).wakeUp();
	}

	public void setSessionId(String sid) {
		this.sessionId = sid;
	}

	/**
	 * 关闭会话
	 * 
	 * @author Yongchao.Yang
	 * @throws Exception
	 * @since 2014年10月21日上午11:19:48
	 */
	public final void close() throws Exception {
		// 关闭读写线程
		swp.stopWorker(sessionId);
		// 关闭listener
		this.broadCastListener.closeListener();
	}

	/**
	 * 重新连接服务器
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年11月9日上午1:37:23
	 */
	public final void reOpen() {
		try {
			System.out.println("[BroadCaster][开启新会话]");
			this.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置会话
	 * 
	 * @param isOk
	 * @author Yongchao.Yang
	 * @since 2014年12月3日下午12:38:38
	 */
	protected void setStauts(boolean isOk) {
		this.isConnected = isOk;
	}

	/**
	 * 获取会话状态
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年12月3日下午12:39:28
	 */
	public boolean getStatus() {
		return this.isConnected;
	}

}
