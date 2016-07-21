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
import java.util.Collections;

import org.apache.log4j.Logger;

import com.ace.database.service.UserService;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.globalData.UIDPoolManager;
import com.rednovo.tools.PPConfiguration;

/**
 * 号池警报器
 * 
 * @author yongchao.Yang/2016年3月6日
 */
public class PIDAlarmWorker extends Thread {

	private volatile boolean isRun = true;
	private static PIDAlarmWorker bsw;
	private static Logger logger = Logger.getLogger(PIDAlarmWorker.class);
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("pid.status.check.frequence");

	public static synchronized PIDAlarmWorker getInstance() {
		if (bsw == null) {
			bsw = new PIDAlarmWorker("PIDAlarmWorker");
		}
		return bsw;
	}

	/**
	 * @param name
	 */
	private PIDAlarmWorker(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[监测号池状态]");
			long size = UIDPoolManager.getPoolSize();
			if (size < 100) {// 小于100往号池中增加新号
				ArrayList<String> list = UserService.getPid("1");
				if (list == null || list.size() == 0) {
					logger.error("[系统未设置号池号段]");
				} else {
					String[] strs = list.get(0).split("\\^");
					int id = Integer.parseInt(strs[0]);
					long beginId = Long.valueOf(strs[1]), endId = Long.valueOf(strs[2]);
					logger.info("[系统初始化号池,号段范围 " + beginId + "-" + endId + "]");
					ArrayList<String> uids = new ArrayList<String>();
					for (long i = beginId; i <= endId; ) {
						uids.add(String.valueOf(i++));
					}
					// 打乱顺序
					Collections.shuffle(uids);
					UIDPoolManager.fillPidPool(uids);
					if (!Constant.OperaterStatus.SUCESSED.getValue().equals(UserService.updatePIDStatus(id, "0"))) {
						logger.error("[号池加载异常]");

					};
					logger.info("[号池加载完毕]");
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
