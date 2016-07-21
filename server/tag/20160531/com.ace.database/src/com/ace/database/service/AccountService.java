/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月5日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.web.service
 *                  fileName：AccountService.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.ace.database.ds.UserTransactionManager;
import com.ace.database.module.AccountModule;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.AccountDetail;
import com.rednovo.ace.entity.IncomeDetail;

/**
 * @author yongchao.Yang/2016年3月5日
 */
public class AccountService {

	private static Logger logger = Logger.getLogger(AccountService.class);

	/**
	 * 
	 */
	public AccountService() {
		// TODO Auto-generated constructor stub
	}

	public static BigDecimal getAccountBalance(String userId) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		AccountModule am = new AccountModule();
		try {
			ut.begin();
			BigDecimal balacne = am.getAccountBalance(userId);
			ut.commit();
			return balacne;

		} catch (Exception e) {
			try {
				logger.error("[获取" + userId + "金币余额失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			am.release();
		}
		return null;
	}

	/**
	 * 获取账户明细
	 * 
	 * @param userId
	 * @param type
	 * @param beginTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午3:13:15
	 */
	public static HashMap<String, ArrayList<AccountDetail>> getAccountDetail(String userId, Constant.ChangeType type, String beginTime, String endTime, int page, int pageSize) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		HashMap<String, ArrayList<AccountDetail>> val = null;
		AccountModule am = new AccountModule();
		try {
			ut.begin();
			val = am.getAccountDetailList(userId, type, beginTime, endTime, page, pageSize);
			ut.commit();
			return val;

		} catch (Exception e) {
			try {
				logger.error("[查询" + userId + "账户明细失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			am.release();
		}
		return null;

	}

	/**
	 * 查询兑点进出账明细
	 * 
	 * @param userId
	 * @param type
	 * @param beginTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午3:18:13
	 */
	public static HashMap<String, ArrayList<IncomeDetail>> getIncomeDetail(String userId, Constant.ChangeType type, String beginTime, String endTime, int page, int pageSize) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		HashMap<String, ArrayList<IncomeDetail>> val = null;
		AccountModule am = new AccountModule();
		try {
			ut.begin();
			val = am.getIncomeDetailList(userId, type, beginTime, endTime, page, pageSize);
			ut.commit();
			return val;

		} catch (Exception e) {
			try {
				logger.error("[查询" + userId + "兑点进出账明细失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			am.release();
		}
		return null;
	}

	/**
	 * 获取待同步余额数据
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日上午12:52:19
	 */
	public static ArrayList<HashMap<String, String>> getSynBalance(String synId, int maxCnt) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ArrayList<HashMap<String, String>> val = null;
		AccountModule am = new AccountModule();
		try {
			ut.begin();
			val = am.getSynBalance(synId, maxCnt);
			ut.commit();
			return val;

		} catch (Exception e) {
			try {
				logger.error("[查询待同步账户数据失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			am.release();
		}
		return null;
	}

	public static ArrayList<HashMap<String, String>> getSynIncome(String synId, int maxCnt) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ArrayList<HashMap<String, String>> val = null;
		AccountModule am = new AccountModule();
		try {
			ut.begin();
			val = am.getSynIncome(synId, maxCnt);
			ut.commit();
			return val;

		} catch (Exception e) {
			try {
				logger.error("[查询待同步兑点账户数据失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			am.release();
		}
		return null;
	}

	/**
	 * 加、减金币
	 * @param userId 被操作人（加金币，或者减金币的人）
	 * @param relateUserId 操作人
	 * @param amount  如果是负数  则是扣除金币；如果是正数，则是添加金币
	 * @return
	 */
	public static String updateBalance(String userId,String relateUserId, String amount) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		AccountModule am = new AccountModule();
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			ut.begin();
			BigDecimal num = new BigDecimal(amount);
			BigDecimal balance = am.getAccountBalance(userId);
			Constant.logicType type = Constant.logicType.SYS_ADD_MONEY;
			if(num.longValue() < 0){
				type = Constant.logicType.SYS_DEC_MONEY;
				if(Math.abs(num.longValue()) >  balance.longValue()){
					num = new BigDecimal(-balance.longValue());
				}
			}
			
			exeRes = am.addMoney(userId, relateUserId, num, type);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
				return exeRes;
			}
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[修改" + userId + "金币余额失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			am.release();
		}
		return exeRes;
	}
	
}
