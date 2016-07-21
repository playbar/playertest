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
 *                  fileName：TaskTrigger2.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.syn;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.rednovo.ace.robot.EnterEventRobot;
import com.rednovo.ace.robot.NewLiveShowRobot;
import com.rednovo.tools.PPConfiguration;

/**
 * @author yongchao.Yang/2016年3月6日
 */
public class TaskTrigger implements ServletContextListener {

	private static Logger logger = Logger.getLogger(TaskTrigger.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		boolean isSynRun = PPConfiguration.getProperties("cfg.properties").getBoolean("data.syn.run");
		logger.info("[数据同步开关设置为:" + isSynRun + "]");
		if (isSynRun) {
			logger.info("[加载用户同步线程]");
			UserSynWorker usw = UserSynWorker.getInstance();
			usw.start();

			logger.info("[加载账户同步线程]");
			BalanceSynWorker bsw = BalanceSynWorker.getInstance();
			bsw.start();

			logger.info("[加载礼物同步线程]");
			GiftSynWorker gsw = GiftSynWorker.getInstance();
			gsw.start();

			logger.info("[加载商品同步线程]");
			GoodSynWorker gsw2 = GoodSynWorker.getInstance();
			gsw2.start();

			logger.info("[加载兑点账户同步线程]");
			IncomeSynWorker isw = IncomeSynWorker.getInstance();
			isw.start();

			logger.info("[加载号池警报线程]");
			PIDAlarmWorker paw = PIDAlarmWorker.getInstance();
			paw.start();

			logger.info("[加载直播数据同步线程]");
			NewShowSynWorker lss = NewShowSynWorker.getInstance();
			lss.start();

			logger.info("[加载排序更新线程]");
			ShowSortUpdater ssu = ShowSortUpdater.getInstance();
			ssu.start();

			logger.info("[加载粉丝同步线程]");
			FansSynWorker fsw = FansSynWorker.getInstance();
			fsw.start();

			logger.info("[加载订阅同步线程]");
			SubscribeSynWorker ssw = SubscribeSynWorker.getInstance();
			ssw.start();

			logger.info("[加载系统参数同步线程]");
			SysConfigSynWorker ccyw = SysConfigSynWorker.getInstance();
			ccyw.start();

			// logger.info("[加载僵尸数据回收线程]");
			// GlobalCacheCleaner.getInstance().start();

			logger.info("[加载AD同步线程]");
			AdSynWorker adw = AdSynWorker.getInstance();
			adw.start();

			logger.info("[加载禁言清理线程]");
			ForbiddenUserCleaner fuc = ForbiddenUserCleaner.getInstance();
			fuc.start();

			logger.info("[加载用户设备同步线程]");
			UserDeviceSynWorker upsw = UserDeviceSynWorker.getInstance();
			upsw.start();

			logger.info("[加载开播推送线程]");
			LiveShowPushService lsps = LiveShowPushService.getInstance();
			lsps.start();
		}

		boolean isRobotRun = PPConfiguration.getProperties("cfg.properties").getBoolean("robot.run");
		logger.info("[机器人开关设置为:" + isRobotRun + "]");
		if (isRobotRun) {
			logger.info("[加载进出房间机器人]");
			new EnterEventRobot("").start();
			// logger.info("[加载点赞机器人]");
			// new SupportRobot("").start();
			// logger.info("[加载广播消息机器人]");
			// new SystemMessageRobot("").start();
		}

	}
}
