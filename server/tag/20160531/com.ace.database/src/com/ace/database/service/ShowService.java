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
import java.util.List;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.ace.database.ds.UserTransactionManager;
import com.ace.database.module.ShowModule;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.LiveShow;
import com.rednovo.ace.globalData.LiveShowManager;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class ShowService {
	private static Logger logger = Logger.getLogger(ShowService.class);

	public ShowService() {}

	/**
	 * 新建直播
	 * 
	 * @param userId
	 * @param showImg
	 * @param title
	 * @param position
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月25日下午12:49:04
	 */
	public static String addShow(String userId, String showImg, String title, String position) {
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ShowModule sm = new ShowModule();
		try {
			ut.begin();
			// 判断该用户是否有未结算的直播,如有则需要先结算
			List<String> list = LiveShowManager.getSortList(1, 1000);
			if (list.contains(userId)) {
				// 获取结算数据
				HashMap<String, String> data = LiveShowManager.getShowExtData(userId);
				exeRes = sm.refreshData(userId, data.get("LENGTH"), data.get("MEMBER"), data.get("FANS"), data.get("SUPPORT"), data.get("COIN"), data.get("SHARE"));
				if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
					ut.rollback();
					return exeRes;
				}
				exeRes = sm.moveShowData(userId);
				if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
					ut.rollback();
					return exeRes;
				}
			}

			// 创建新直播
			exeRes = sm.addShow(userId, title, showImg, position);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
			} else {
				ut.commit();
			}
		} catch (Exception e) {
			try {
				logger.error("[用户" + userId + " 开播失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			sm.release();
		}
		return exeRes;
	}

	public static ArrayList<LiveShow> getSynLiveShow(String showId, int maxCnt) {
		ArrayList<LiveShow> list = new ArrayList<LiveShow>();

		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ShowModule uf = new ShowModule();
		try {
			ut.begin();
			list = uf.getSynLiveShow(showId, maxCnt);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
			} else {
				ut.commit();
			}
		} catch (Exception e) {
			try {
				logger.error("[获取待同步直播数据失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			uf.release();
		}
		return list;

	}

	public static ArrayList<String> updateSort(int page, int pageSize) {
		ArrayList<String> list = new ArrayList<String>();

		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ShowModule uf = new ShowModule();
		try {
			ut.begin();
			list = uf.updateSort(page, pageSize);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
			} else {
				ut.commit();
			}
		} catch (Exception e) {
			try {
				logger.error("[获取直播列表数据失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			uf.release();
		}
		return list;

	}

	public static LiveShow getLiveShow(String showId) {
		LiveShow ls = null;
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ShowModule uf = new ShowModule();
		try {
			ut.begin();
			ls = uf.getLiveShow(showId);
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取直播数据失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			uf.release();
		}
		return ls;

	}

	/**
	 * 结束直播
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月25日下午12:52:43
	 */
	public static String finishShow(String userId) {
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ShowModule uf = new ShowModule();
		try {
			ut.begin();
			HashMap<String, String> data = LiveShowManager.getShowExtData(userId);
			exeRes = uf.refreshData(userId, data.get("LENGTH"), data.get("MEMBER"), data.get("FANS"), data.get("SUPPORT"), data.get("COIN"), data.get("SHARE"));
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
				return exeRes;
			}
			exeRes = uf.moveShowData(userId);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
			} else {
				ut.commit();
			}
		} catch (Exception e) {
			try {
				logger.error("[用户" + userId + " 开播失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			uf.release();
		}
		return exeRes;
	}

	/**
	 * 获取所有线上直播数据
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年4月21日下午10:50:04
	 */
	public static ArrayList<LiveShow> getShow(int page, int pageSize) {
		ArrayList<LiveShow> list = new ArrayList<LiveShow>();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ShowModule uf = new ShowModule();
		try {
			ut.begin();
			list = uf.getShow(page, pageSize);
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取所有直播数据失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			uf.release();
		}
		return list;
	}

}
