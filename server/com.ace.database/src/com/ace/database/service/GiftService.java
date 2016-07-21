package com.ace.database.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.ace.database.ds.UserTransactionManager;
import com.ace.database.module.AccountModule;
import com.ace.database.module.GiftModule;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.OperaterStatus;
import com.rednovo.ace.constant.Constant.logicType;
import com.rednovo.ace.entity.Gift;
import com.rednovo.ace.entity.GiftDetail;

public class GiftService {
	private static Logger logger = Logger.getLogger(GiftService.class);

	public GiftService() {}

	/**
	 * 赠送礼物
	 * 
	 * @param senderId
	 * @param receiverId
	 * @param showId
	 * @param giftId
	 * @param giftCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午1:01:25
	 */
	public static String sendGift(String senderId, String receiverId, String showId, String giftId, int giftCnt) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		AccountModule af = new AccountModule();
		GiftModule gm = new GiftModule();
		String exeRes = Constant.OperaterStatus.SUCESSED.getValue();
		try {
			ut.begin();

			Gift g = gm.getGift(giftId);
			BigDecimal amount = g.getSendPrice().multiply(new BigDecimal(giftCnt));
			BigDecimal income = g.getTransformPrice().multiply(new BigDecimal(giftCnt));
			// 扣币
			exeRes = af.reduceMoney(senderId, receiverId, amount, logicType.SEND_GIFT);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				logger.error("[" + senderId + "赠送礼物,扣币失败]");
				ut.rollback();
				return exeRes;
			}

			// 赠送礼物
			exeRes = gm.addGiftDetail(senderId, receiverId, giftId, giftCnt);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				logger.error("[" + senderId + "赠送礼物,添加明细失败]");
				ut.rollback();
				return exeRes;
			}

			// 增加返点
			exeRes = af.addIncome(receiverId, senderId, income, logicType.SEND_GIFT);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				logger.error("[" + senderId + "赠送礼物,增加兑点收入失败]");
				ut.rollback();
				return exeRes;
			}

			ut.commit();

		} catch (Exception e) {
			try {
				logger.error("[" + senderId + "赠送礼物失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			af.release();
			gm.release();
		}
		return exeRes;
	}

	/**
	 * 获取礼物进出账明细
	 * 
	 * @param userId
	 * @param beginTime
	 * @param endTime
	 * @param type
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午4:41:48
	 */
	public static HashMap<String, ArrayList<GiftDetail>> getGiftDetailList(String userId, String beginTime, String endTime, Constant.ChangeType type, int page, int pageSize) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		GiftModule gm = new GiftModule();
		HashMap<String, ArrayList<GiftDetail>> map = null;
		try {
			ut.begin();

			map = gm.getGiftDetailList(userId, beginTime, endTime, type, page, pageSize);

			ut.commit();

		} catch (Exception e) {
			try {
				logger.error("[查询" + userId + "礼物进出账明细失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			gm.release();
		}
		return map;
	}

	/**
	 * 获取待同步礼物信息
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日上午12:36:09
	 */
	public static ArrayList<Gift> getSynGifts() {
		ArrayList<Gift> list = null;
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		GiftModule gm = new GiftModule();
		try {
			ut.begin();
			list = gm.getSynGift();
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取待同步礼物数据失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			gm.release();
		}
		return list;
	}
	
	/**
	 * 添加或修改礼物
	 * 
	 * @param gift
	 * @return res 操作状态(成功或者失败)
	 * @author lxg
	 * @since 2016年5月7日上午16:36:09
	 */
	public static String addOrUpdateGift(Gift gift) {
		String res = OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		GiftModule gm = new GiftModule();
		try {
			ut.begin();
			res = gm.addOrUpdateGift(gift);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(res)) {
				ut.rollback();
			} else {
				ut.commit();
			}
		} catch (Exception e) {
			try {
				logger.error("[添加或者修改礼物失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			gm.release();
		}
		return res;
	}
	
	/**
	 * 获取待同步礼物信息
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日上午12:36:09
	 */
	public static List<Gift> getGiftByStatus(String status) {
		List<Gift> list = null;
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		GiftModule gm = new GiftModule();
		try {
			ut.begin();
			list = gm.getGiftByStatus(status);
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取指定上架，下架，待上架数据失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			gm.release();
		}
		return list;
	}
	
	/**
	 * 查询礼物根据礼物id
	 * @param id
	 * @return
	 */
	public static Gift getGiftById(String id){
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		GiftModule gm = new GiftModule();
		Gift gift = null;
		try {
			ut.begin();
			gift = gm.getGift(id);
			ut.commit();

		} catch (Exception e) {
			try {
				logger.error("[查询" + id + "礼物详情]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			gm.release();
		}
		return gift;
	}
}
