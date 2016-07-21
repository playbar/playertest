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
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.tools.DateUtil;

/**
 * @author yongchao.Yang/2016年3月6日
 */
public class ForbiddenUserCleaner extends Thread {

	private volatile boolean isRun = true;
	private static ForbiddenUserCleaner usw;
	private static Logger logger = Logger.getLogger(ForbiddenUserCleaner.class);

	public static synchronized ForbiddenUserCleaner getInstance() {
		if (usw == null) {
			usw = new ForbiddenUserCleaner("ForbiddenUserCleaner");
		}
		return usw;
	}

	/**
	 * @param name
	 */
	private ForbiddenUserCleaner(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[扫描禁言用户时限]");
			try {
				ArrayList<String> clearList = new ArrayList<String>();
				Map<String, String> map = LiveShowManager.getAllForbiddenUser();
				if (map != null && map.size() > 0) {
					String time = DateUtil.getTimeInMillis();
					Iterator<String> keys = map.keySet().iterator();
					while (keys.hasNext()) {
						String key = keys.next();
						String val = map.get(key);
						if (time.compareTo(val) >= 0) {
							clearList.add(key);
						}
					}
				}
				if (!clearList.isEmpty()) {
					LiveShowManager.removeForbiddenUser(clearList);
				}

				synchronized (this) {
					try {
						this.wait(50000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				logger.error("禁言清理线程异常", e);
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
