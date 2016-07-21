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
 *                  fileName：Invoker.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication;

import com.rednovo.ace.entity.Message;

/**
 * 
 * 会话事件回调接口
 * 
 * @author yongchao.Yang/2014年10月21日
 */
public interface Invoker {

	/**
	 * 通信已经准备好
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年2月29日下午7:54:32
	 */
	public void onReady();

	/**
	 * 连接失败
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月21日下午3:54:06
	 */
	public void onConnectFailed();

	/**
	 * 建立连接
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月21日上午11:08:11
	 */
	public void onConnectSucessed(String sessionId);

	/**
	 * 网络断掉
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月21日上午11:10:25
	 */
	public void onSuspend(String sessionId);

	/**
	 * 捕获新消息
	 * 
	 * @author Yongchao.Yang
	 * @param sessionId TODO
	 * @param msg TODO
	 * @since 2014年10月21日上午11:21:43
	 */
	public void onNewMessage(String sessionId, Message msg);

}
