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
import java.util.ArrayList;
import java.util.List;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.ace.database.ds.UserTransactionManager;
import com.ace.database.module.AccountModule;
import com.ace.database.module.ExchangeModule;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.OperaterStatus;
import com.rednovo.ace.constant.Constant.OrderStatus;
import com.rednovo.ace.constant.Constant.logicType;
import com.rednovo.ace.entity.ExchangeBindInfo;
import com.rednovo.ace.entity.ExchangeDetail;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class ExchangeService {
	private static Logger logger = Logger.getLogger(ExchangeService.class);

	public ExchangeService() {}

	public static ExchangeBindInfo getBindInfo(String userId, String channel) {
		ExchangeBindInfo ebi = null;
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ExchangeModule eb = new ExchangeModule();
		try {
			ut.begin();
			ebi = eb.getBindInfo(userId, channel);
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

	public static String bind(String userId, String weChatId, String mobileId, String userName, String channel) {
		String exeCode = OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ExchangeModule eb = new ExchangeModule();
		try {
			ut.begin();
			exeCode = eb.bind(userId, weChatId, mobileId, userName, channel);
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

	public static String request(String userId, String channel, BigDecimal rmbAmount, BigDecimal rate) {
		String exeCode = OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ExchangeModule eb = new ExchangeModule();
		AccountModule am = new AccountModule();
		try {
			ut.begin();
			// 扣币
			exeCode = am.reduceIncome(userId, userId, rmbAmount.multiply(rate), logicType.APPLY_CASH);
			if (!OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
				ut.rollback();
				return exeCode;
			}
			// 添加明细
			exeCode = eb.request(userId, channel, rmbAmount, rate);
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

	/**
	 * A豆兑换金币
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月17日下午2:24:29
	 */
	public static String income2Coin(String userId, String income, String rate) {

		String exeCode = OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		AccountModule am = new AccountModule();
		try {
			ut.begin();
			// 扣income
			exeCode = am.reduceIncome(userId, userId, new BigDecimal(income), logicType.EXCHANGE);
			if (!OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
				ut.rollback();
				return exeCode;
			}
			// 添加balance
			exeCode = am.addMoney(userId, userId, new BigDecimal(income).multiply(new BigDecimal(rate)), Constant.logicType.EXCHANGE);
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
			am.release();
		}
		return exeCode;

	}

	/**
	 * 根据userId查询提现记录
	 * 
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 * @author @author Wangwen.wang
	 * @since 2016年6月22日
	 */
	public static List<ExchangeDetail> getExchangeDetailList(String userId, String startTime, String endTime, int page, int pageSize) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ExchangeModule eb = new ExchangeModule();
		List<ExchangeDetail> exchangeList = new ArrayList<ExchangeDetail>();
		try {
			ut.begin();
			exchangeList = eb.getExchangeDetailList(userId, startTime, endTime, page, pageSize);
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[根据多个条件查询exchangeDetail表失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			eb.release();
		}
		return exchangeList;
	}

	/**
	 * 根据审核状态查询提现记录
	 * 
	 * @param step
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @param status 
	 * @since 2016年6月24日下午5:39:03
	 */
	public static List<ExchangeDetail> getExchangeStepList(String step, String status, int page, int pageSize) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ExchangeModule eb = new ExchangeModule();
		List<ExchangeDetail> exchangeList = new ArrayList<ExchangeDetail>();
		try {
			ut.begin();
			exchangeList = eb.getExchangeStepList(step,status, page, pageSize);
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[根据多个条件查询exchangeDetail表失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			eb.release();
		}
		return exchangeList;
	}

	/**
	 * 修改提现状态
	 * 
	 * @param id
	 * @param step
	 * @param status
	 * @param applyId
	 * @param auditId
	 * @param des
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年6月24日下午5:39:19
	 */
	public static String updateStepStatus(String id, String step, String status, String applyId, String auditId, String des) {
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		ExchangeModule exchangeModule = new ExchangeModule();
		try {
			ut.begin();
			exeRes = exchangeModule.updateStepStatus(id, step, status, applyId, auditId, des);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
			} else {
				ut.commit();
			}
		} catch (Exception e) {
			try {
				ut.rollback();
				logger.error("[修改用户权限 " + id + " 失败]", e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			exchangeModule.release();
		}
		return exeRes;
	}

}
