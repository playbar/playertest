/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年12月1日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：MessageListener.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.client;

import java.util.LinkedList;

import com.rednovo.ace.communication.EventInvokerManager;
import com.rednovo.ace.entity.Message;

/**
 * 数据处理线程
 * 
 * @author yongchao.Yang/2014年12月1日
 */
public class ClientMessageListener extends Thread {
	private volatile boolean isRun = false;
	private ClientMessageCache mc;
	private EventInvokerManager eventMgr = EventInvokerManager.getInstance();
	private static ClientMessageListener cml;

	public synchronized static ClientMessageListener getInstance() {
		if (cml == null) {
			cml = new ClientMessageListener();
		}
		return cml;
	}

	/**
	 * 
	 */
	private ClientMessageListener() {
		mc = ClientMessageCache.getInstance();
	}

	@Override
	public void run() {
		while (isRun) {
			try {
				synchronized (cml) {
					this.wait();
				}
				while (mc.getReceiveMsgCnt() > 0) {
					LinkedList<Message> msgList = mc.getReceiveMsg();
					for (Message msg : msgList) {
						eventMgr.fireNewMessageEvent(null, msg);
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println("数据处理线程异常");
			}
		}
	}

	public void stopRun() {
		this.isRun = false;
	}

	public void wakeUp() {
		synchronized (cml) {
			cml.notifyAll();
		}
	}

	public void startRun() {
		if (!isRun) {
			isRun = true;
			cml.start();
		}
	}
}
