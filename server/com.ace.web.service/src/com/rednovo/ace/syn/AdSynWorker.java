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

import com.ace.database.service.OrderService;
import com.rednovo.ace.entity.AD;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.tools.PPConfiguration;

/**
 * @author yongchao.Yang/2016年3月6日
 */
public class AdSynWorker extends Thread {

	private volatile boolean isRun = true;
	private static AdSynWorker adw;
	private static Logger logger = Logger.getLogger(AdSynWorker.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("ad.syn.frequence");

	public static synchronized AdSynWorker getInstance() {
		if (adw == null) {
			adw = new AdSynWorker("AdSynWorker");
		}
		return adw;
	}

	/**
	 * @param name
	 */
	private AdSynWorker(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[Ad同步数据]");
			List<AD> list = OrderService.getADList("1");
			if (list != null && list.size() > 0) {
				StaticDataManager.updateShowBannerList(list);
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
