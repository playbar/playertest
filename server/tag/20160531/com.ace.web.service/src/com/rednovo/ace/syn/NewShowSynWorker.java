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

import com.ace.database.service.ShowService;
import com.ace.database.service.SysConfigService;
import com.rednovo.ace.entity.LiveShow;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.tools.PPConfiguration;

/**
 * @author yongchao.Yang/2016年3月6日
 */
public class NewShowSynWorker extends Thread {

	private volatile boolean isRun = true;
	private static NewShowSynWorker lsw;
	private static Logger logger = Logger.getLogger(NewShowSynWorker.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("liveshow.syn.frequence");

	public static synchronized NewShowSynWorker getInstance() {
		if (lsw == null) {
			lsw = new NewShowSynWorker("LiveShowSynWorker");
		}
		return lsw;
	}

	/**
	 * @param name
	 */
	private NewShowSynWorker(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[同步新增直播列表]");
			String synId = SysConfigService.getGlobalVal("SHOW_SYN_ID");
			List<LiveShow> list = ShowService.getSynLiveShow(synId, 1000);

			if (list != null && list.size() > 0) {
				logger.info("[同步直播数据" + list.size() + "条]");
				int size = list.size();
				synId = list.get(size - 1).getSchemaId();
				SysConfigService.setGlobalVal("SHOW_SYN_ID", synId);
				// 同步更新数据到缓存
				for (LiveShow show : list) {
					LiveShowManager.addShow(show);
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
