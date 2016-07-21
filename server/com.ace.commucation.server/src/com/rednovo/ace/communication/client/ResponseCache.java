/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月24日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：ResponseCache.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.client;

import java.util.HashMap;

import com.rednovo.ace.entity.Message;

/**
 * 消息缓存
 * 
 * @author yongchao.Yang/2014年10月24日
 */
public class ResponseCache {
	private HashMap<String, Message> cache;
	private static ResponseCache rc;

	public static ResponseCache getInstance() {
		if (rc == null) {
			rc = new ResponseCache();
		}
		return rc;
	}

	/**
	 * 
	 */
	private ResponseCache() {
		cache = new HashMap<String, Message>();
	}

	/**
	 * 添加消息
	 * 
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2014年10月24日下午12:26:38
	 */
	public void addMsg(Message msg) {
//		System.out.println("[client msgCache:" + msg.getSumy().getMsgId() + "]");
		this.cache.put(msg.getSumy().getMsgId(), msg);
		synchronized (this) {
			this.notifyAll();
		}
	}

	/**
	 * 
	 * @param msgId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月24日下午12:27:21
	 */
	public Message getMsg(String msgId) {
		return this.cache.remove(msgId);
	}

}
