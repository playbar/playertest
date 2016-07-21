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
 *                  fileName：EventInvokerManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication;

import com.rednovo.ace.entity.Message;


/**
 * 事件回调对象管理器
 * 
 * @author yongchao.Yang/2014年10月21日
 */
public class EventInvokerManager {

	private static EventInvokerManager manager = null;
	private Invoker invoker;

	public static EventInvokerManager getInstance() {
		if (manager == null) {
			manager = new EventInvokerManager();
		}
		return manager;
	}

	/**
	 * 注册客户端会话事件回调对象
	 * 
	 * @param invoker
	 * @author Yongchao.Yang
	 * @since 2014年10月21日下午12:10:46
	 */
	public void registInvoker(Invoker invoker) {
		this.invoker = invoker;
	}

	/**
	 * 获取客户端会话事件回调对象
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月21日下午12:11:15
	 */
	public Invoker getInvoker() {
		return this.invoker;
	}

	/**
	 * 是否注册客户端事件
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月21日下午12:13:50
	 */
	public boolean hasInvoker() {
		if (this.invoker == null) {
			return false;
		}
		return true;
	}
	
	public void fireReadyEvent() {
		if (hasInvoker()) {
			this.invoker.onReady();;
		}
	}

	/**
	 * 触发连接成功事件
	 * 
	 * @param sessionId
	 * @author Yongchao.Yang
	 * @since 2014年10月22日下午1:37:02
	 */
	public void fireOpenSuccessEvent(String sessionId) {
		if (hasInvoker()) {
			this.invoker.onConnectSucessed(sessionId);
		}
	}

	/**
	 * 触发连接失败事件
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月22日下午2:37:39
	 */
	public void fireOpenFailedEvent() {
		if (hasInvoker()) {
			this.invoker.onConnectFailed();
		}
	}

	/**
	 * 触发新消息事件
	 * 
	 * @param sessionId
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2014年10月22日下午2:37:54
	 */
	public void fireNewMessageEvent(String sessionId, Message msg) {
		if (hasInvoker()) {
			this.invoker.onNewMessage(sessionId, msg);
		}
	}

	/**
	 * 触发连接中断事件
	 * 
	 * @param sessionId
	 * @author Yongchao.Yang
	 * @since 2014年10月22日下午2:38:07
	 */
	public void fireSessionSuspendEvent(String sessionId) {
		if (hasInvoker()) {
			this.invoker.onSuspend(sessionId);
		}
	}

	/**
	 * 
	 */
	private EventInvokerManager() {}

}
