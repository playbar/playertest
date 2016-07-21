/*  ------------------------------------------------------------------------------ 
 *                  软件名称:他秀手机版
 *                  公司名称:多宝科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年7月15日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自多宝科技研发部，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.duobao.video.logic
 *                  fileName：UserService.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.ace.database.ds.UserTransactionManager;
import com.ace.database.module.GiftModule;
import com.ace.database.module.StatisticsGiftModule;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.GiftDetail;
import com.rednovo.ace.entity.StatisticsGift;

/**
 * @author lijiang/2016.6.22
 */
public class StatisticsGiftService {
	private static Logger logger = Logger.getLogger(StatisticsGiftService.class);

	public StatisticsGiftService() {}

	/**
	 * 获取需要同步的用户礼物信息
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author lijiang/2016.6.22
	 */
	public static ArrayList<GiftDetail> getSynGiftInfo(String synId, int maxCnt) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();// 事务管理
		ArrayList<GiftDetail> list = new ArrayList<GiftDetail>();
		GiftModule gm = new GiftModule();
		try {
			ut.begin();
			list = gm.getSynReceiveGiftDetailList(synId, maxCnt);
			ut.commit();
		} catch (Exception e) {
			logger.error("[获取订阅同步数据失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("获取订阅同步数据回滚事务错误]", e1);
			}
		} finally {
			gm.release();
		}
		return list;
	}

	/**
	 * 更新用户礼物统计信息
	 * 
	 * @param val
	 * @return
	 * @author lijiang/2016.6.22
	 */
	public static String updateSyngitInfo(HashMap<String, Integer> val) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();// 事务管理
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		StatisticsGiftModule sgm = new StatisticsGiftModule();
		try {
			Iterator<String> it = val.keySet().iterator();// 遍历
			while (it.hasNext()) {
				String key = it.next();// 获取KEY值
				String[] keys = key.split("_");// 截取KEY值里的各个ID
				String day = keys[0].toString();
				String senderId = keys[1].toString();
				String receiverId = keys[2].toString();
				String giftId = keys[3].toString();
				ut.begin();
				String cnt = sgm.getStatisticsGiftInfo(day, senderId, receiverId, giftId);// 查询礼物统计表是否有相符合的数据
				// 如果有就更新 没有就新增
				if (null != cnt && !"".equals(cnt)) {
					exeRes = sgm.updateStatisticsGiftInfo(day, senderId, receiverId, giftId, Integer.valueOf(cnt) + val.get(key));
				} else {
					exeRes = sgm.addStatisticsGiftInfo(day, senderId, receiverId, giftId, val.get(key));
				}
				ut.commit();
			}
		} catch (Exception e) {
			logger.error("[更新用户礼物统计信息失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("更新用户礼物统计信息回滚事务错误]", e1);
			}
		}finally {
			sgm.release();
		}
		return exeRes;
	}

	/**
	 * 获取统计后的消费收礼信息
	 * 
	 * @param starttime
	 * @param endTime
	 * @param senderId
	 * @param receiverId
	 * @return
	 * @author Wenhui.Wang
	 * @since 2016年6月23日
	 */
	public static ArrayList<StatisticsGift> getUserStatisticsGiftInfo(String starttime, String endTime, String senderId, String receiverId) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();// 事务管理
		StatisticsGiftModule sgm = new StatisticsGiftModule();
		ArrayList<StatisticsGift> statistics = null;
		try {
			ut.begin();
			statistics = sgm.getUserStatisticsGiftInfo(starttime, endTime, senderId, receiverId);
			ut.commit();
		} catch (Exception e) {
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("获取统计后的收礼信息回滚事务错误,查询条件：senderId=" + senderId + ",receiverId=" + receiverId + "]", e1);
			}
		} finally {
			sgm.release();
		}
		return statistics;
	}
}
