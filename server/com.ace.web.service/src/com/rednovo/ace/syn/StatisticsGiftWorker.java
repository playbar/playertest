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
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.ace.database.service.StatisticsGiftService;
import com.ace.database.service.SysConfigService;
import com.ace.database.service.UserService;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.GiftDetail;
import com.rednovo.tools.PPConfiguration;

/**
 * 用户送礼同步线程
 * @author lijiang /2016.6.22
 *
 */
public class StatisticsGiftWorker extends Thread {
	
	private volatile boolean isRun = true;//线程开启状态
	private static StatisticsGiftWorker sgw;
	private static Logger logger = Logger.getLogger(StatisticsGiftWorker.class);//日志记录功能
	private static long wait = PPConfiguration.getProperties("cfg.properties").getLong("statistics.gift.frquence");//读取配置文件，获取线程配置信息

	public static synchronized StatisticsGiftWorker getInstance() {
		if (sgw == null) {
			sgw = new StatisticsGiftWorker("StatisticsGiftWorker");
		}
		return sgw;
	}

	/**
	 * @param name
	 */
	private StatisticsGiftWorker(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			logger.info("[同步用户送礼数据]");
				try {
					String synId = SysConfigService.getGlobalVal("STATISTICS_GIFT_DAY_ID");//获取数据库记录上次已同步ID
					//同步送礼信息 maxCnt：每次获取数据条数
					ArrayList<GiftDetail> list = StatisticsGiftService.getSynGiftInfo(synId, 1000);
					if (list != null && list.size() > 0) {
					logger.info("[同步用户送礼数据" + list.size() + "条]");
					HashMap<String,Integer> map=new HashMap<String,Integer>();
					for (GiftDetail GiftDetail : list) {//循环遍历实体List
						//把礼物创建年月日、收礼用户ID、送礼用户ID、礼物ID 拼接成字符串
						StringBuffer key=new StringBuffer();
						key.append(GiftDetail.getCreateTime().substring(0, 10)+"_"+GiftDetail.getRelateUserId()+"_"+GiftDetail.getUserId()+"_"+GiftDetail.getGiftId());
						//判断MAP里是否已存在该KEY 否则为0
						int cnt=map.get(key.toString()) != null ? map.get(key.toString()):0;
						map.put(key.toString(), cnt+GiftDetail.getGiftCnt());
					}
					String exeRes=StatisticsGiftService.updateSyngitInfo(map);
					if( Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)){//同步成功才记录更新ID
						int size = list.size();
						synId = String.valueOf(list.get(size - 1).getId());
						SysConfigService.setGlobalVal("STATISTICS_GIFT_DAY_ID", synId);
					}
				}
				synchronized (this) {
						this.wait(wait);
				}
			} catch (Exception e) {
				logger.error("[同步用户送礼数据失败]", e);
			}
		}
	}

	/**
	 * 关闭用户送礼同步线程
	 */
	public void stopRun() {
		this.isRun = false;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
