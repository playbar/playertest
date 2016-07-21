/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年11月18日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：ClientHeartBeatRunner.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.client;

import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.tools.AndroidLogger;

/**
 * 心跳工作线程
 * 
 * @author yongchao.Yang/2014年11月18日
 */
public class ClientHeartBeatRunner extends Thread {
	private volatile boolean isRun = false, isBusy = false;
	private volatile long lastResponseTime;
	private ClientSession cs = ClientSession.getInstance();
	private static ClientHeartBeatRunner chbr;
	private long runFrequence = 10000, timeOut = 30000;

	/**
	 * 
	 */
	private ClientHeartBeatRunner() {
		System.out.println("[实例化心跳线程]");
	}

	/**
	 * 获取心跳线程实例，并启动该线程
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月19日下午7:00:13
	 */
	public synchronized static ClientHeartBeatRunner getInstance() {
		if (chbr == null) {
			chbr = new ClientHeartBeatRunner();
		}
		return chbr;
	}

	@Override
	public void run() {
		System.out.println("[ClientHeartBeat][-------------启动进入心跳------isRun=" + isRun + "-------]");
		// 初始化上次响应时间
		lastResponseTime = System.currentTimeMillis();
		while (isRun) {
			try {
				System.out.println("[ClientHeartBeat][开始心跳]");
				synchronized (this) {
					this.wait(runFrequence);// 每5秒发一次心跳
				}
				if (!isRun) {
					System.out.println("[ClientHeartBeatRunner][心跳结束]");
					break;
				}
				// 当系统正在发包，为避免因为网卡堵塞造成
				if (isBusy) {
					System.out.println("[ClientHeartBeat][系统处于忙碌状态,停止发送心跳包]");
					this.updatResponseTime(System.currentTimeMillis());
					continue;
				}
				long time = System.currentTimeMillis();
				// 超过timeout秒没有响应，则认为连接丢失，系统尝试重新连接
				// System.out.println("[上次心跳时间:" + lastResponseTime + "]");
				long inteval = time - lastResponseTime;
				if (inteval > timeOut) {
					System.out.println("[心跳超时时间][" + timeOut + "]");
					// 网络断开，清除所有待发消息，避免网卡拥堵，避免粘包数据
					// this.cs.setStauts(false);
					// eventMgr.fireSessionSuspendEvent(null);
					ClientMessageCache.getInstance().clearSendMsg();

					// eventMgr.fireOpenFailedEvent();
					System.out.println("\t\t[心跳响应超时,客户单断开此次连接,尝试重连]");
					cs.reOpen();
					// 睡眠2秒，等待链接成功
					// Thread.sleep(1000);
					lastResponseTime = System.currentTimeMillis();
				}
				// 当链路正常，则发消息
				if (this.cs.getStatus()) {
					System.out.println("[发送心跳数据]");
					Message msg = new Message();
					Summary smy = new Summary();
					msg.setSumy(smy);
					smy.setSenderId("");
					smy.setChatMode(ChatMode.PRIVATE.getValue());
					smy.setMsgType(MsgType.TXT_MSG.getValue());
					smy.setInteractMode(InteractMode.REQUEST.getValue());
					smy.setRequestKey(Constant.SysEvent.HEART_BEAT.getValue());
					smy.setMsgId("123456789");
					JSONObject jo = new JSONObject();
					jo.put("body", "ping");
					msg.setBody(jo);
					cs.sendImportMsg(msg);
				}

			} catch (Throwable ex) {
				System.out.println("--------------Thread Exception------------");
				AndroidLogger.printLog(ex);
			}
		}
		System.out.println("[ClientHeartBeatRunner][心跳,结束,退出]");
		chbr = null;
	}

	public void stopRun() {
		isRun = false;
		synchronized (this) {
			this.notifyAll();
		}
	}

	public void startRun() {
		if (isRun) {
			return;
		}
		isRun = true;
		chbr.start();
	}

	/**
	 * 系统开始忙碌，停止心跳
	 * 
	 * @author Yongchao.Yang
	 * @since 2015年4月27日下午4:41:54
	 */
	public void busy() {
		this.isBusy = true;
	}

	/**
	 * 系统开始清闲,开始心跳
	 * 
	 * @author Yongchao.Yang
	 * @since 2015年4月27日下午4:42:11
	 */
	public void idle() {
		this.isBusy = false;
	}

	/**
	 * 更新心跳最后响应时间
	 * 
	 * @param time
	 * @author Yongchao.Yang
	 * @since 2014年11月18日下午9:17:29
	 */
	public void updatResponseTime(long time) {
		this.lastResponseTime = time;
	}

}
