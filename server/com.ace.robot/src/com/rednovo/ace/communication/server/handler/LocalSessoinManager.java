/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年11月8日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：UserSessionManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server.handler;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import com.rednovo.ace.communication.server.ServerWorkerPool;
import com.rednovo.ace.globalData.GlobalUserSessionMapping;

/**
 * 用户会话管理
 * 
 * @author yongchao.Yang/2014年11月8日
 */
public class LocalSessoinManager {

	private Logger logger = Logger.getLogger(LocalSessoinManager.class);

	/**
	 * 会话心跳记录
	 */
	private HashMap<String, String> lastSessionTime = new HashMap<String, String>();
	/**
	 * 用户会话映射表
	 */

	private static LocalSessoinManager usm;
	private ServerWorkerPool workerPool;

	/**
	 * 获取单例
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月8日下午10:07:07
	 */
	public static synchronized LocalSessoinManager getInstance() {
		if (usm == null) {
			usm = new LocalSessoinManager();
		}
		return usm;
	}

	/**
	 * 
	 */
	private LocalSessoinManager() {
		logger.info("[UserSessionManager][初始化]");
		workerPool = ServerWorkerPool.getInstance();
	}

	/**
	 * 结束过期的会话
	 * 
	 * @param sessionId
	 * @author Yongchao.Yang
	 * @since 2014年11月8日下午10:50:16
	 */
	public void releaseSession(String sessionId) {
		// 删除全局映射数据
		GlobalUserSessionMapping.expireSession(sessionId);
		if (sessionId != null) {
			workerPool.stopWorker(sessionId);
			this.lastSessionTime.remove(sessionId);
			logger.info("[LocalSessoinManager][结束会话][" + sessionId + "]");
		}

	}

	/**
	 * 更新会话时间
	 * 
	 * @param userId
	 * @param sessionId
	 * @author Yongchao.Yang
	 * @since 2014年11月18日下午4:54:42
	 */
	public void updateSessionTime(String sessionId) {
		long time = System.currentTimeMillis();
//		logger.info("[LocalSessionManager][更新会话时间][" + sessionId + "][" + time + "]");
		this.lastSessionTime.put(sessionId, String.valueOf(time)); // 更新会话心跳
	}

	/**
	 * 注册会话通道。启动该通道读写线程，并将该会话纳入到生命周期管理器中
	 * 
	 * @param key
	 * @author Yongchao.Yang
	 * @since 2014年11月19日下午1:01:53
	 */
	public void registChannel(SelectionKey key) {
		this.workerPool.registChannel(key);
		this.updateSessionTime(String.valueOf(key.attachment()));
	}

	/**
	 * 获取会话最后心跳
	 * 
	 * @param sessionId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月8日下午10:16:13
	 */
	public String getSessionTime(String sessionId) {
		// synchronized (lastSessionTime) {
		return this.lastSessionTime.get(sessionId);
		// }
	}

	/**
	 * 获取本服务器所有会话
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月8日下午10:21:02
	 */
	public Set<String> getLocalSession() {
		return this.lastSessionTime.keySet();
	}

}
