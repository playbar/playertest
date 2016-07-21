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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.globalData.GlobalUserSessionMapping;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * 余额同步线程
 * 
 * @author yongchao.Yang/2016年3月6日
 */
public class ZombieShowCleaner extends Thread {

	private volatile boolean isRun = true;
	private static ZombieShowCleaner gcc;
	private static Logger logger = Logger.getLogger(ZombieShowCleaner.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("zombieshow.clean.fresquence");

	public static synchronized ZombieShowCleaner getInstance() {
		if (gcc == null) {
			gcc = new ZombieShowCleaner("GlobalCacheCleaner");
		}
		return gcc;
	}

	/**
	 * @param name
	 */
	private ZombieShowCleaner(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			try {
				// 清理僵尸会话数据
				HashMap<String, List<String>> ssm = new HashMap<String, List<String>>();

				logger.info("[清理僵尸直播]");

				synchronized (this) {
					this.wait(10000);
				}

				// 清理僵尸直播数据
				List<String> showIds = LiveShowManager.getSortList(1, 10000);
				for (String showId : showIds) {

					String sid = GlobalUserSessionMapping.getUserSession(showId);
					// 当主播没有会话消息，则认为已经下线
					if (Validator.isEmpty(sid)) {
						logger.info("[清理僵尸直播][" + showId + "]");
						this.removeShow(showId);
						continue;
					}

					String serverId = GlobalUserSessionMapping.getSessionServer(sid);
					// TODO 临时增加ssm.get(serverId)!=null的判断 2016-05-25 yyc 修改
					if (ssm.get(serverId) != null && !ssm.get(serverId).contains(sid)) {
						logger.info("[清理僵尸直播][" + showId + "]");
						this.removeShow(showId);
						continue;
					}

					// 当前直播对应的主播不在此房间，则回收
					String showId2 = LiveShowManager.getSessionShow(sid);
					if (!showId.equals(showId2)) {
						logger.info("[清理僵尸直播][" + showId + "]");
						this.removeShow(showId);
					}
				}

				synchronized (this) {
					try {
						this.wait(wait);
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

	private void removeShow(String showId) {
	
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("showId", showId);
		String res = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/002-014", params);
		
//		String res = HttpSender.urlRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/002-014", "showId=" + showId);
		JSONObject response = JSON.parseObject(res);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(response.getString("exeStatus"))) {
			// 从缓存中删除直播列表
			LiveShowManager.removeSortShow(showId);
		}
	}
}
