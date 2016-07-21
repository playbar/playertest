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

import org.apache.log4j.Logger;

import com.ace.database.service.GiftService;
import com.rednovo.ace.entity.Gift;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.tools.PPConfiguration;

/**
 * @author yongchao.Yang/2016年3月6日
 */
public class GiftSynWorker extends Thread {

	private volatile boolean isRun = true;
	private static GiftSynWorker gsw;
	private static Logger logger = Logger.getLogger(GiftSynWorker.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("gift.syn.frequence");

	public static synchronized GiftSynWorker getInstance() {
		if (gsw == null) {
			gsw = new GiftSynWorker("GiftSynWorker");
		}
		return gsw;
	}

	/**
	 * @param name
	 */
	private GiftSynWorker(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[同步礼物数据]");
			ArrayList<Gift> list = GiftService.getSynGifts();
			if (list != null && list.size() > 0) {
				logger.info("[同步礼物数据" + list.size() + "条]");
				StaticDataManager.updateGift(list);
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
