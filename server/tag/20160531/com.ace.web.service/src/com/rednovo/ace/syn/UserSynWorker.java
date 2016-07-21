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

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.ace.database.service.SysConfigService;
import com.ace.database.service.UserService;
import com.rednovo.ace.entity.User;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.solr.SearchEngines;
import com.rednovo.tools.PPConfiguration;

/**
 * @author yongchao.Yang/2016年3月6日
 */
public class UserSynWorker extends Thread {

	private volatile boolean isRun = true;
	private static UserSynWorker usw;
	private static Logger logger = Logger.getLogger(UserSynWorker.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("user.syn.frequence");

	public static synchronized UserSynWorker getInstance() {
		if (usw == null) {
			usw = new UserSynWorker("UserSynWorker");
		}
		return usw;
	}

	/**
	 * @param name
	 */
	private UserSynWorker(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[同步用户数据]");
			String synId = SysConfigService.getGlobalVal("USER_SYN_ID");

			List<User> list = UserService.getSynUser(synId, 1000);
			if (list != null && list.size() > 0) {
				logger.info("[同步用户数据" + list.size() + "条]");
				int size = list.size();
				synId = list.get(size - 1).getSchemaId();
				SysConfigService.setGlobalVal("USER_SYN_ID", synId);

				// // 同步更新数据到缓存
				for (User user : list) {
					// 添加到缓存
					UserManager.updateUser(user);
					// 创建索引
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("id", user.getUserId());
					params.put("name", user.getNickName());
					try {
						SearchEngines.addIndex(params);
						logger.info("[创建索引:" + user.getUserId() + "]");
					} catch (Exception e) {
						logger.info("[创建用户(" + user.getUserId() + ")索引失败条]");
					}
				}
			}
			synchronized (this) {
				try {
					this.wait(wait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
}
