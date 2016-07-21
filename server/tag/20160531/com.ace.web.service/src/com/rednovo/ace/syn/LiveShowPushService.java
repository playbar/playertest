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
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.entity.LiveShow;
import com.rednovo.ace.entity.User;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.ace.globalData.UserRelationManager;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;
import com.rednovo.tools.push.JPushService;

/**
 * @author zukang.song/2016年3月6日
 */
public class LiveShowPushService extends Thread {

	private volatile boolean isRun = true;
	private static LiveShowPushService usw;
	private static Logger logger = Logger.getLogger(LiveShowPushService.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("liveshow.push.frequence");

	public static synchronized LiveShowPushService getInstance() {
		if (usw == null) {
			usw = new LiveShowPushService("LiveShowPushService");
		}
		return usw;
	}

	/**
	 * @param name
	 */
	private LiveShowPushService(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[推送服务开启]");
			Iterator<String> stars = UserManager.getPushStar().iterator();
			List<String> showIds = LiveShowManager.getSortList(1, 100000);

			while (stars.hasNext()) {
				try {
					String starId = stars.next();
					if (!showIds.contains(starId)) {
						continue;
					}
					List<String> fanslist = UserRelationManager.getFans(starId, 1, 10000);

					ArrayList<String> androidUserList = new ArrayList<String>(), appleUserList = new ArrayList<String>();
					for (String userId : fanslist) {
						String devNo = UserManager.getUserDevice(userId);
						if (!Validator.isEmpty(devNo)) {
							if (devNo.endsWith("_0")) {
								androidUserList.add(devNo.substring(0, devNo.length() - 2));
							} else if (devNo.endsWith("_1")) {
								appleUserList.add(devNo.substring(0, devNo.length() - 2));
							}
						}
					}

					User user = UserManager.getUser(starId);

					HashMap<String, String> exeData = new HashMap<String, String>();
					LiveShow s = LiveShowManager.getShow(starId);
					exeData.put("downStreanUrl", PPConfiguration.getProperties("cfg.properties").getString("cdn.downstream.url")+starId);
					exeData.put("showId", starId);
					user.setExtendData(exeData);

					Map<String, String> extarMap = new HashMap<String, String>();
					extarMap.put("type", "3");
					extarMap.put("user", JSON.toJSONString(user));
					String title = StaticDataManager.getSysConfig("liveShowPushTitle");
					title = title.replaceAll("\\$", user.getNickName());
					JPushService.push(title, extarMap, androidUserList, appleUserList, true);
				} catch (Exception e) {
					logger.error("上播推送服务异常", e);
				}

			}
			try {
				synchronized (this) {
					this.wait(wait);
				}
			} catch (Exception e1) {
				logger.error("上播推送服务异常", e1);
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
