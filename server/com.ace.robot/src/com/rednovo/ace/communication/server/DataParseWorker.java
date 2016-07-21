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
package com.rednovo.ace.communication.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.rednovo.ace.entity.Message;

/**
 * 数据处理线程
 * 
 * @author yongchao.Yang/2014年12月1日
 */
public class DataParseWorker extends Thread {
	private static Logger logger = Logger.getLogger(DataParseWorker.class);
	private volatile boolean isRun = true;
	private LocalMessageCache localMsgCache;
	private static DataParseWorker dhl;

	public static DataParseWorker getInstance() {
		if (dhl == null) {
			dhl = new DataParseWorker();
		}
		return dhl;
	}

	/**
	 * 
	 */
	private DataParseWorker() {
		localMsgCache = LocalMessageCache.getInstance();
	}

	@Override
	public void run() {
		while (isRun) {
			try {
				synchronized (dhl) {
					this.wait();
				}
				while (localMsgCache.getReceiveMsgCnt() > 0) {
					HashMap<String, LinkedList<Message>> msgMap = localMsgCache.getReceiveMsg();
					Iterator<String> sessionIds = msgMap.keySet().iterator();
					while (sessionIds.hasNext()) {
						String sessionId = sessionIds.next();
						LinkedList<Message> msgList = msgMap.get(sessionId);
						if (msgList.isEmpty()) {
							continue;
						}
						for (Message msg : msgList) {
							RequestDispatch.dispatch(sessionId, msg);
						}
					}
				}
			} catch (Exception e) {
				logger.error("数据处理线程异常", e);
			}
		}
	}

	public void wakeup() {
		synchronized (dhl) {
			this.notifyAll();
		}
	}
}
