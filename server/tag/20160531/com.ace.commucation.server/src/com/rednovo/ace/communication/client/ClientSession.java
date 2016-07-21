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
package com.rednovo.ace.communication.client;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.communication.EventInvokerManager;
import com.rednovo.ace.communication.Invoker;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Server;
import com.rednovo.tools.AndroidLogger;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * @author yongchao.Yang/2014年10月21日
 */
public class ClientSession {
	private static EventInvokerManager eventMgr = EventInvokerManager.getInstance();
	private static ClientSession cs;
	private volatile boolean isConnected = false;

	/**
	 * 
	 * @param invoker {@link Invoker} 会话事件回调接口
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月21日上午11:45:01
	 */
	public static synchronized ClientSession getInstance() {
		if (!eventMgr.hasInvoker()) {
			System.out.println("[error] [请先注册事件回调对象]");
			return null;
		}
		if (cs == null) {
			cs = new ClientSession();
		}
		return cs;
	}

	private ClientSession() {

	}

	/**
	 * 通过开始新线程启动客户端socket监听程序.
	 * 
	 * @author Yongchao.Yang
	 * @throws Exception
	 * @since 2014年10月21日下午3:01:56
	 */
	public void openSession() throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Server s = null;
				try {
					int index = 0;
					while (index++ < 10) {
						String res = HttpSender.urlRequest("http://172.16.150.23:8080/service/008-001", null);
						// String res = HttpSender.urlRequest("http://api.17ace.cn/service/008-001", null);

						if (Validator.isEmpty(res)) {
							System.out.println("[ClientSession][找不到SessoinServe服务器列表，尝试" + index + "次]");
							Thread.sleep(index * 2000);
							continue;
						}

						s = JSON.parseObject(JSON.parseObject(res).getString("server"), Server.class);
						break;
					}

					if (s == null) {
						System.out.println("[ClientSession][找不到SessoinServe服务器列表，返回退出]");
						return;
					}

				} catch (Exception e) {
					AndroidLogger.printLog(e);
					return;
				}

				try {
					ClientListener.getInstance().listen(s.getIp(), s.getPort());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 发送消息，然后阻塞等待
	 * 
	 * @param msg
	 * @author Yongchao.Yang
	 * @throws Exception
	 * @since 2014年10月21日上午11:19:00
	 */
	public final void sendMessage(Message msg) {
		sendMessage(msg, false);
	}

	/**
	 * 优先发送高级别消息
	 * 
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2016年3月15日下午1:08:06
	 */
	public final void sendImportMsg(Message msg) {
		sendMessage(msg, true);
	}

	private final void sendMessage(Message msg, boolean isImportant) {
		if (!this.getStatus()) {
			System.out.println("[ClientSession][会话为断开状态,停止发送消息]");
			return;
		}
		if (isImportant) {
			ClientMessageCache.getInstance().addSendMsg(msg, true);
		} else {
			ClientMessageCache.getInstance().addSendMsg(msg, false);
		}
		ClientWriter writer = ClientWorkerPool.getInstance().getWriter();
		if (writer == null) {
			System.out.println("[ClientSession][发送消息失败，writer为null]");
			return;
		}
		// 唤醒消息写线程
		ClientWorkerPool.getInstance().getWriter().wakeUp();
	}

	/**
	 * 关闭会话，心跳程序继续
	 * 
	 * @author Yongchao.Yang
	 * @throws Exception
	 * @since 2014年10月21日上午11:19:48
	 */
	public final void closeSession() throws Exception {
		if (ClientSession.getInstance().getStatus()) {
			eventMgr.fireSessionSuspendEvent(null);
		}
		ClientSession.getInstance().setStauts(false);
		// 结束线程
		ClientWorkerPool.getInstance().stopWorker();
		// 关闭单例
		ClientListener.getInstance().closeListener();
	}

	/**
	 * 停止心跳
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月18日下午12:25:38
	 */
	public final void stopHeartBeat() {
		ClientHeartBeatRunner.getInstance().stopRun();
	}

	/**
	 * 重新连接服务器
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年11月9日上午1:37:23
	 */
	public final void reOpen() {
		try {
			// System.out.println("[ClientSession][准备重连,关闭当前会话]");
			this.closeSession();
			// System.out.println("[ClientSession][开启新会话]");
			this.openSession();
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
