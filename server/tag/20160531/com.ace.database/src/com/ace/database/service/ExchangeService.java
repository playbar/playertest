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

import java.math.BigDecimal;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.ace.database.ds.UserTransactionManager;
import com.ace.database.module.AccountModule;
import com.ace.database.module.ExchangeModule;
import com.rednovo.ace.constant.Constant.OperaterStatus;
import com.rednovo.ace.constant.Constant.OrderStatus;
import com.rednovo.ace.constant.Constant.logicType;
import com.rednovo.ace.entity.ExchangeBindInfo;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class ExchangeService {
	private static Logger logger = Logger.getLogger(ExchangeService.class);

	public ExchangeService() {}

	public static ExchangeBindInfo getBindInfo(String userId) {
		ExchangeBindInfo ebi = null;
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ExchangeModule eb = new ExchangeModule();
		try {
			ut.begin();
			ebi = eb.getBindInfo(userId);
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取用户 (" + userId + ")兑点绑定信息 失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			eb.release();
		}
		return ebi;

	}

	public static String bind(String userId, String weChatId, String mobileId) {
		String exeCode = OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ExchangeModule eb = new ExchangeModule();
		try {
			ut.begin();
			exeCode = eb.bind(userId, weChatId, mobileId);
			if (OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
				ut.commit();
			} else {
				ut.rollback();
			}
		} catch (Exception e) {
			try {
				logger.error("[用户 (" + userId + ")兑点绑定信息 失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			eb.release();
		}
		return exeCode;
	}

	public static String request(String userId, BigDecimal coinAmount) {
		String exeCode = OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ExchangeModule eb = new ExchangeModule();
		AccountModule am = new AccountModule();
		try {
			ut.begin();
			// 扣币
			exeCode = am.reduceIncome(userId, userId, coinAmount, logicType.EXCHANGE);
			if (!OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
				ut.rollback();
				return exeCode;
			}
			// 添加明细
			exeCode = eb.request(userId, coinAmount);
			if (!OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
				ut.rollback();
				return exeCode;
			}
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[用户 (" + userId + ")申请兑点失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			eb.release();
			am.release();
		}
		return exeCode;

	}

	/**
	 * 打款
	 * 
	 * @param requestId
	 * @param status
	 * @param payerId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月19日上午11:20:23
	 */
	public static String applyRequest(String requestId, OrderStatus status, String payerId) {
		String exeCode = OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ExchangeModule eb = new ExchangeModule();
		AccountModule am = new AccountModule();
		try {
			ut.begin();
			// 添加明细
			exeCode = eb.applyRequest(requestId, status.getValue(), payerId);
			if (!OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
				ut.rollback();
				return exeCode;
			}
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[" + requestId + "打款失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			eb.release();
			am.release();
		}
		return exeCode;
	}

}
