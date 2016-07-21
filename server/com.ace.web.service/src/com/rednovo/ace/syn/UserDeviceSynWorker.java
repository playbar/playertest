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

import org.apache.log4j.Logger;

import com.ace.database.service.SysConfigService;
import com.ace.database.service.UserService;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.tools.PPConfiguration;

/**
 * @author yongchao.Yang/2016年3月6日
 */
public class UserDeviceSynWorker extends Thread {

	private volatile boolean isRun = true;
	private static UserDeviceSynWorker usw;
	private static Logger logger = Logger.getLogger(UserDeviceSynWorker.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("user.device.syn.frquence");

	public static synchronized UserDeviceSynWorker getInstance() {
		if (usw == null) {
			usw = new UserDeviceSynWorker("UserPushSynWorker");
		}
		return usw;
	}

	/**
	 * @param name
	 */
	private UserDeviceSynWorker(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[同步用户推送设备数据]");
			String synId = SysConfigService.getGlobalVal("USER_DEVICE_SYN_ID");

			ArrayList<String> list = UserService.getSynUserDevice(synId, 1000);
			if (list != null && list.size() > 0) {
				logger.info("[同步用户设备数据" + list.size() + "条]");
				int size = list.size();
				synId = list.get(size - 1).split("\\^")[3];
				SysConfigService.setGlobalVal("USER_DEVICE_SYN_ID", synId);

				HashMap<String, String> map = new HashMap<String, String>();
				for (String info : list) {
					String[] infos = info.split("\\^");
					String userId = infos[0];
					String tokenId = infos[1];
					String deviceType = infos[2];

					map.put(userId, tokenId + "_" + deviceType);
				}
				UserManager.updateUserDevice(map);

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
