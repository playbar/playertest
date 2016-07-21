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

import org.apache.log4j.Logger;

import com.ace.database.service.OrderService;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.tools.PPConfiguration;

/**
 * 余额同步线程
 * 
 * @author yongchao.Yang/2016年3月6日
 */
public class SysConfigSynWorker extends Thread {

	private volatile boolean isRun = true;
	private static SysConfigSynWorker scs;
	private static Logger logger = Logger.getLogger(SysConfigSynWorker.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("sys.config.fresquence");

	public static synchronized SysConfigSynWorker getInstance() {
		if (scs == null) {
			scs = new SysConfigSynWorker("SysConfigSynWorker");
		}
		return scs;
	}

	/**
	 * @param name
	 */
	private SysConfigSynWorker(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[同步系统参数]");
			HashMap<String, String> map = OrderService.getSysConfig();

			if (map != null && map.size() > 0) {
				StaticDataManager.addSysConfig(map);
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
