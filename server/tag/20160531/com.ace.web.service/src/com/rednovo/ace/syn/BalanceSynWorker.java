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

import com.ace.database.service.AccountService;
import com.ace.database.service.SysConfigService;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.tools.PPConfiguration;

/**
 * 余额同步线程
 * 
 * @author yongchao.Yang/2016年3月6日
 */
public class BalanceSynWorker extends Thread {

	private volatile boolean isRun = true;
	private static BalanceSynWorker bsw;
	private static Logger logger = Logger.getLogger(BalanceSynWorker.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("account.balance.frquence");

	public static synchronized BalanceSynWorker getInstance() {
		if (bsw == null) {
			bsw = new BalanceSynWorker("BalanceSynWorker");
		}
		return bsw;
	}

	/**
	 * @param name
	 */
	private BalanceSynWorker(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[同步账号余额]");
			String synId = SysConfigService.getGlobalVal("BALANCE_SYN_ID");
			List<HashMap<String, String>> list = AccountService.getSynBalance(synId, 1000);
			if (list != null && list.size() > 0) {
				logger.info("[同步账户数据" + list.size() + "条]");
				int size = list.size();
				HashMap<String, String> map = list.get(size - 1);
				String userId = map.keySet().iterator().next();
				String[] val = map.get(userId).split("\\^");

				SysConfigService.setGlobalVal("BALANCE_SYN_ID", val[1]);

				// 同步更新数据到缓存
				for (HashMap<String, String> values : list) {
					String id = values.keySet().iterator().next();
					String[] value = values.get(id).split("\\^");
					UserManager.updataBalance(id, value[0]);
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
