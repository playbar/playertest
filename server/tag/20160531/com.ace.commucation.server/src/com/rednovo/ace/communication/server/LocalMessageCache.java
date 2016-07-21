/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播手机版
 *                  公司名称:多宝科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月11日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：LocalMessageCache.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.rednovo.ace.entity.Message;

/**
 * 消息缓存
 * 
 * @author yongchao.Yang/2014年10月11日
 */
public class LocalMessageCache {
	Logger log = Logger.getLogger(LocalMessageCache.class);
	private static HashMap<String, LinkedList<Message>> sendMsg = new HashMap<String, LinkedList<Message>>(), receiveMsg = new HashMap<String, LinkedList<Message>>();
	private static LocalMessageCache cache;

	public static LocalMessageCache getInstance() {
		if (cache == null) {
			cache = new LocalMessageCache();
		}
		return cache;
	}

	private LocalMessageCache() {
		log.info("[LocalMessageCache][初始化]");
	}

	/**
	 * 获取下一条缓存数据
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月18日下午2:09:19
	 */
	public HashMap<String, LinkedList<Message>> getReceiveMsg() {
		HashMap<String, LinkedList<Message>> msgs = new HashMap<String, LinkedList<Message>>();
		synchronized (receiveMsg) {
			Iterator<String> keys = receiveMsg.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				msgs.put(key, receiveMsg.get(key));
				keys.remove();
			}
		}
		return msgs;
	}

	/**
	 * 接受消息增加消息
	 * 
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2014年10月18日下午2:10:46
	 */
	public void addReceiveMsg(String sessionId, Message msg) {
		synchronized (receiveMsg) {
			LinkedList<Message> list = receiveMsg.get(sessionId);
			if (list == null) {
				list = new LinkedList<Message>();
				receiveMsg.put(sessionId, list);
			}
			list.add(msg);
		}
	}

	/**
	 * 向缓存中压入消息
	 * 
	 * @param isSend boolean true 待发消息 false 待收消息
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2014年10月14日上午11:11:11
	 */
	public void addSendMsg(String sessionId, Message msg) {
		synchronized (sendMsg) {
			LinkedList<Message> msgList = sendMsg.get(sessionId);
			if (msgList == null) {
				msgList = new LinkedList<Message>();
				sendMsg.put(sessionId, msgList);
			}
			msgList.add(msg);
		}
	}

	/**
	 * 获取下一个待发消息
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月14日上午11:13:28
	 */
	public LinkedList<Message> getSendMsg(String sessionId) {
		LinkedList<Message> list = new LinkedList<Message>();
		synchronized (sendMsg) {
			LinkedList<Message> msgList = sendMsg.get(sessionId);
			while (!msgList.isEmpty()) {
				list.add(msgList.remove());
			}
		}
		return list;
	}

	/**
	 * 获取待发消息条数
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月14日上午11:19:29
	 */
	public int getSendMsgCnt(String sessionId) {
		synchronized (sendMsg) {
			LinkedList<Message> msgList = sendMsg.get(sessionId);
			if (msgList != null) {
				return msgList.size();
			}
			return 0;
		}
	}

	/**
	 * 获取未读消息片段个数
	 * 
	 * @param clientId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月16日下午9:55:40
	 */
	public int getReceiveMsgCnt() {
		synchronized (receiveMsg) {
			return receiveMsg.size();
		}
	}
}
