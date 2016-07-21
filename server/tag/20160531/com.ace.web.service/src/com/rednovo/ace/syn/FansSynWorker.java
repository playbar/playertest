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

import java.util.List;

import org.apache.log4j.Logger;

import com.ace.database.service.SysConfigService;
import com.ace.database.service.UserService;
import com.rednovo.ace.globalData.UserRelationManager;
import com.rednovo.tools.PPConfiguration;

/**
 * @author yongchao.Yang/2016年3月6日
 */
public class FansSynWorker extends Thread {

	private volatile boolean isRun = true;
	private static FansSynWorker fsw;
	private static Logger logger = Logger.getLogger(FansSynWorker.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("fans.syn.frequence");

	public static synchronized FansSynWorker getInstance() {
		if (fsw == null) {
			fsw = new FansSynWorker("FansSynWorker");
		}
		return fsw;
	}

	/**
	 * @param name
	 */
	private FansSynWorker(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[同步粉丝数据]");
			String synId = SysConfigService.getGlobalVal("FANS_SYN_ID");
			List<String> list = UserService.getSynFans(synId, 1000);
			if (list != null && list.size() > 0) {
				logger.info("[同步粉丝数据" + list.size() + "条]");
				int size = list.size();
				String[] str = list.get(size - 1).split("\\^");
				SysConfigService.setGlobalVal("FANS_SYN_ID", str[2]);

				// 同步更新数据到缓存
				for (String s : list) {
					String[] ss = s.split("\\^");
					UserRelationManager.addFans(ss[0], ss[1]);
				}
			}
			synchronized (this) {
				try {
					this.wait(wait);// 10分钟同步一次
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
