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
 *                  fileName：UserSessionScaner.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server.handler;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.rednovo.ace.globalData.GlobalUserSessionMapping;
import com.rednovo.tools.PPConfiguration;

/**
 * @author yongchao.Yang/2014年11月8日
 */
public class UserSessionScaner extends Thread {

	private static LocalSessoinManager usm = LocalSessoinManager.getInstance();

	private volatile boolean isRun = true;

	private static long maxHeatBeatTime = PPConfiguration.getProperties("cfg.properties").getLong("heartBeat.maxtime");

	private static long waitTime = PPConfiguration.getProperties("cfg.properties").getLong("thread.UserSessionScaner.wait");

	private static final Logger logger = Logger.getLogger(UserSessionScaner.class);

	@Override
	public void run() {
		while (isRun) {
			try {
				long time = System.currentTimeMillis();
				// 监测过去会话
				Iterator<String> sessionIds = usm.getLocalSession().iterator();
				logger.info("[UserSessionScaner][本地线程][" + sessionIds + "]");
				while (sessionIds.hasNext()) {
					String sessionId = sessionIds.next();
					long time2 = Long.parseLong(usm.getSessionTime(sessionId));
					// logger.info("[UserSessionScaner][比对时间][session:" + sessionId + "][lastSessionTime:" + time2 + "][差值:" + (time - time2) + "]");
					if (time - time2 > maxHeatBeatTime) {// 超过15秒未更新，释放
						logger.info("[会话超时,回收所有资源(session:" + GlobalUserSessionMapping.getSessionUser(sessionId) + ")]");
						sessionIds.remove();
						// 如果此对话关联直播数据，则结束直播
						GlobalUserSessionMapping.finishPossibleShow(sessionId);
						// 释放次会话资源
						usm.releaseSession(sessionId);
					}
				}
				synchronized (this) {
					this.wait(waitTime);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
