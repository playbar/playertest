/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月6日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.web.service
 *                  fileName：UserSynWorker.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.syn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ace.database.service.OrderService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.Server;
import com.rednovo.ace.globalData.GlobalUserSessionMapping;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.ace.globalData.ServerRoutManager;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * 余额同步线程
 * 
 * @author yongchao.Yang/2016年3月6日
 */
public class ServerCleaner extends Thread {

	private volatile boolean isRun = true;
	private static ServerCleaner sc;
	private static Logger logger = Logger.getLogger(ServerCleaner.class);

	public static synchronized ServerCleaner getInstance() {
		if (sc == null) {
			sc = new ServerCleaner("ServerCleaner");
		}
		return sc;
	}

	/**
	 * @param name
	 */
	private ServerCleaner(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			try {
				// 清理僵尸会话数据
				logger.info("[同步服务器配置]");
				ArrayList<Server> list = OrderService.getServerList();
				for (Server server : list) {
					if (!"1".equals(server.getStatus())) {// 处理下线服务器

					}
				}

				synchronized (this) {
					try {
						this.wait(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception ex) {
				logger.error("[GlobalCacheCleaner]清理缓存失败", ex);
			}
		}
	}

	public void stopRun() {
		this.isRun = false;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下线服务器
	 * 
	 * @param serverId
	 * @author Yongchao.Yang
	 * @since 2016年5月26日下午3:56:35
	 */
	private void offServer(String serverId) {
		ServerRoutManager.offServer(serverId);

		Iterator<String> serverSessions = GlobalUserSessionMapping.getServerSession(serverId).iterator();
		while (serverSessions.hasNext()) {
			String sid = serverSessions.next();
			GlobalUserSessionMapping.expireSession(sid);
		}

	}

}
