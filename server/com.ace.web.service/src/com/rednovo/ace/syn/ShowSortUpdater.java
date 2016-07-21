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
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.tools.PPConfiguration;

/**
 * @author yongchao.Yang/2016年3月6日
 */
public class ShowSortUpdater extends Thread {

	private volatile boolean isRun = true;
	private static ShowSortUpdater lsw;
	private static Logger logger = Logger.getLogger(ShowSortUpdater.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("show.sort.update.frequence");

	public static synchronized ShowSortUpdater getInstance() {
		if (lsw == null) {
			lsw = new ShowSortUpdater("ShowSortUpdater");
		}
		return lsw;
	}

	/**
	 * @param name
	 */
	private ShowSortUpdater(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[更新直播排序]");
			List<String> list = ShowService.updateSort(0, 1000);
			LiveShowManager.updateShowSort(list);
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
