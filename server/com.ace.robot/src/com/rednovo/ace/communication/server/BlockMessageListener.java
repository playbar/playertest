/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月22日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.commucation.server
 *                  fileName：BlockMessageListener.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * @author yongchao.Yang/2016年3月22日
 */
public class BlockMessageListener extends Thread {
	private static BlockMessageListener bms = null;
	private ArrayList<String> blockPool = new ArrayList<String>();;
	private ArrayList<String> okPool = new ArrayList<String>();;
	private LinkedList<String> newResId = new LinkedList<String>();
	private volatile boolean isRun = true;
	private static Object lock = new Object();
	private static Logger logger = Logger.getLogger(BlockMessageListener.class);

	public static synchronized BlockMessageListener getInstance() {
		if (bms == null) {
			bms = new BlockMessageListener();
		}
		return bms;
	}

	@Override
	public void run() {
		logger.info("[BlockMessageListener][开始侦听同步消息回执]");
		while (isRun) {
			try {
				synchronized (this) {
					try {
						this.wait(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					while (newResId.size() > 0) {
						logger.info("[------------------------------------------------]");
						logger.info("[BlockMessageList][size:" + newResId.size() + "]");
						logger.info("[BlockMessageList][" + newResId.toString() + "]");
						logger.info("[------------------------------------------------]");
						String newId = newResId.removeFirst();
						if (blockPool.contains(newId)) {
							blockPool.remove(newId);
							okPool.add(newId);
							synchronized (lock) {
								lock.notifyAll();
							}
						}
					}

				}

			} catch (Exception e) {
				logger.error("[BlockMessageListener][侦听异常]", e);
				e.printStackTrace();
			}

		}

	}

	/**
	 * 
	 */
	private BlockMessageListener() {}

	public void addBlockMsgId(String msgId) {
		this.blockPool.add(msgId);
	}

	public void clearBlockMsgId(String msgId) {
		this.blockPool.remove(msgId);
		this.okPool.remove(msgId);
	}

	public boolean ifResponse(String msgId) {
		return this.okPool.contains(msgId);
	}

	public void addNewMsgId(String msgId) {
		this.newResId.addLast(msgId);
	}

	public static Object getLocker() {
		return lock;
	}
}
