/*  ------------------------------------------------------------------------------ 
 *                  软件名称:
 *                  公司名称:
 *                  开发作者:ZuKang.Song
 *       			开发时间:2016年5月17日/2016
 *    				All Rights Reserved 2016-2016
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.commucation.server
 *                  fileName：ReceiveMessageManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.daemon;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.rednovo.ace.communication.server.ServerHelpler;
import com.rednovo.ace.communication.server.handler.LocalSessoinManager;
import com.rednovo.ace.globalData.GlobalUserSessionMapping;

/**
 * 全局会话监测线程。通过本地会话同缓存会话进行比较，剔除缓存中不存在的会话
 * 
 * @author ZuKang.Song/2016年5月17日
 */
public class LocalSessionCleaner extends Thread {
	private volatile boolean isRun = true;
	private static Logger logger = Logger.getLogger(LocalSessionCleaner.class);

	private static LocalSessionCleaner gsc = null;

	public static LocalSessionCleaner getInstance() {
		if (gsc == null) {
			gsc = new LocalSessionCleaner("OutMsgSender");
		}
		return gsc;
	}

	private LocalSessionCleaner(String name) {
		super(name);
	}

	@Override
	public void run() {
		logger.info("----全局会话监测线程启动----------");
		while (isRun) {
			try {
				synchronized (this) {
					this.wait(1000);
				}
				Set<String> localSessions = LocalSessoinManager.getInstance().getLocalSession();
				String serverId = ServerHelpler.getLocalHost().getId();
				Iterator<String> cacheSessions = GlobalUserSessionMapping.getServerSession(serverId).iterator();
				while (cacheSessions.hasNext()) {
					String cssid = cacheSessions.next();
					if (!localSessions.contains(cssid)) {
						logger.info("[LocalSessionCleaner][删除过期会话" + cssid + "]");
						GlobalUserSessionMapping.removeServerSession(serverId, cssid);
						cacheSessions.remove();
						GlobalUserSessionMapping.expireSession(cssid);
					}
				}

			} catch (Exception e) {
				logger.error("OutMsgSender异常", e);
			}
		}

	}
}
