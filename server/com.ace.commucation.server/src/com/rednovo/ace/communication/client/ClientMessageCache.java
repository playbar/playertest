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
package com.rednovo.ace.communication.client;

import java.util.LinkedList;

import com.rednovo.ace.entity.Message;

/**
 * 消息缓存
 * 
 * @author yongchao.Yang/2014年10月11日
 */
public class ClientMessageCache {
	private LinkedList<Message> sendMsg = new LinkedList<Message>(), receiveMsg = new LinkedList<Message>();

	// 等待服务器回执的消息消息列表，如果超时没有回应，则自动返回超时
//	private HashMap<String, String> unResponseMsg = new HashMap<String, String>();
	private static ClientMessageCache cache;

	// private static Logger logger = Logger.getLogger(MessageCache.class);

	public synchronized static ClientMessageCache getInstance() {
		if (cache == null) {
			cache = new ClientMessageCache();
		}
		return cache;
	}

	private ClientMessageCache() {}

	/**
	 * 获取下一条缓存数据
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月18日下午2:09:19
	 */
	public LinkedList<Message> getReceiveMsg() {
		LinkedList<Message> msgList = new LinkedList<Message>();
		synchronized (receiveMsg) {
			while (!this.receiveMsg.isEmpty()) {
				msgList.add(this.receiveMsg.removeFirst());
			}
		}
		return msgList;
	}

	/**
	 * 接受消息增加消息
	 * 
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2014年10月18日下午2:10:46
	 */
	public void addReceiveMsg(Message msg) {
		synchronized (receiveMsg) {
			this.receiveMsg.add(msg);
			// 当收到系统回执，自动删除该记录
//			this.unResponseMsg.remove(msg.getSumy().getMsgId());
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
	public void addSendMsg(Message msg, boolean isImportant) {
		synchronized (sendMsg) {
			if (isImportant) {
				this.sendMsg.addFirst(msg);
			} else {
				this.sendMsg.add(msg);
			}
//			this.unResponseMsg.put(msg.getSumy().getMsgId(), String.valueOf(System.currentTimeMillis()));
		}
	}

	/**
	 * 获取下一个待发消息
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月14日上午11:13:28
	 */
	public LinkedList<Message> getSendMsg() {
		LinkedList<Message> list = new LinkedList<Message>();
		synchronized (sendMsg) {
			while (!sendMsg.isEmpty()) {
				list.add(sendMsg.remove());
			}
		}
		return list;
	}

	/**
	 * 获取未回执的消息集合
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2015年4月29日上午8:10:02
	 */
//	public HashMap<String, String> getUnResponseMsg() {
//		return this.unResponseMsg;
//	}

	/**
	 * 获取待发消息条数
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月14日上午11:19:29
	 */
	public int getSendMsgCnt() {
		synchronized (sendMsg) {
			return this.sendMsg.size();
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
			return this.receiveMsg.size();
		}
	}

	/**
	 * 清除待发消息
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年12月3日下午1:13:25
	 */
	protected void clearSendMsg() {
		synchronized (sendMsg) {
			this.sendMsg.clear();
		}
	}
}
