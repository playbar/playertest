/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月4日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.database
 *                  fileName：AccountFun.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.module;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Translater;
import com.rednovo.ace.entity.AccountDetail;
import com.rednovo.ace.entity.IncomeDetail;
import com.rednovo.tools.DateUtil;

/**
 * 账户操作
 * 
 * @author yongchao.Yang/2016年3月4日
 */
public class AccountModule extends BasicModule {

	/**
	 * 
	 */
	public AccountModule() {

	}

	public BigDecimal getAccountBalance(String userId) {
		return this.getAccountDao().getBalance(userId);

	}

	/**
	 * 加钱
	 * 
	 * @param detail
	 * @param changeAmount
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日上午11:54:31
	 */
	public String addMoney(String userId, String relateUserId, BigDecimal money, Constant.logicType type) {
		return this.updateBlance(userId, relateUserId, money, type, Constant.ChangeType.ADD, "用户" + relateUserId + "给用户" + userId + "添加金币" + money.floatValue() + " [" + Translater.getLogicName(type) + "]");
	}

	/**
	 * 扣钱
	 * 
	 * @param detail
	 * @param changeAmount
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日上午11:55:13
	 */
	public String reduceMoney(String userId, String relateUserId, BigDecimal money, Constant.logicType type) {
		BigDecimal balance = this.getAccountDao().getBalance(userId);
		// 判断账户余额
		if (balance == null) {
			return "100";
		}
		if (balance.compareTo(money) < 0) {
			return "101";
		}
		return this.updateBlance(userId, relateUserId, money.multiply(new BigDecimal(-1)), type, Constant.ChangeType.REDUCE, "用户" + relateUserId + "给用户" + userId + "扣减金币" + money.floatValue() + " [" + Translater.getLogicName(type) + "]");
	}

	/**
	 * 添加收入进账
	 * 
	 * @param userId
	 * @param amount
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月5日下午3:56:15
	 */
	public String addIncome(String userId, String relateUserId, BigDecimal money, Constant.logicType type) {
		return this.updateIncome(userId, relateUserId, money, type, Constant.ChangeType.ADD, relateUserId + "给" + userId + "增加收入" + money.floatValue() + "[" + Translater.getLogicName(type) + "]");
	}

	/**
	 * 减少收入进账
	 * 
	 * @param userId
	 * @param amount
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月5日下午3:56:25
	 */
	public String reduceIncome(String userId, String relateUserId, BigDecimal money, Constant.logicType type) {
		if (this.getAccountDao().getIncome(userId).compareTo(money) < 0) {
			return "101";
		}
		return this.updateIncome(userId, relateUserId, money.multiply(new BigDecimal(-1)), type, Constant.ChangeType.REDUCE, relateUserId + "给" + userId + "减少收入" + money.floatValue() + "[" + Translater.getLogicName(type) + "]");
	}

	/**
	 * 查询账户进出账明细
	 * 
	 * @param userId
	 * @param type
	 * @param beginTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午3:04:18
	 */
	public HashMap<String, ArrayList<AccountDetail>> getAccountDetailList(String userId, Constant.ChangeType type, String beginTime, String endTime, int page, int pageSize) {
		return this.getAccountDao().getAccountDetailList(userId, beginTime, endTime, type, page, pageSize);
	}

	/**
	 * 查询收入进出账明细
	 * 
	 * @param userId
	 * @param type
	 * @param beginTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午3:16:03
	 */
	public HashMap<String, ArrayList<IncomeDetail>> getIncomeDetailList(String userId, Constant.ChangeType type, String beginTime, String endTime, int page, int pageSize) {
		return this.getAccountDao().getIncomeDetailList(userId, beginTime, endTime, type, page, pageSize);
	}

	/**
	 * 修改账户余额，添加交易明细
	 * 
	 * @param userId
	 * @param relateUserId
	 * @param money
	 * @param type
	 * @param changeType
	 * @param des
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月5日上午11:59:08
	 */
	private String updateBlance(String userId, String relateUserId, BigDecimal money, Constant.logicType type, Constant.ChangeType changeType, String des) {
		AccountDetail acd = new AccountDetail();
		acd.setUserId(userId);
		acd.setUserName("");
		acd.setRelateUserId(relateUserId);
		acd.setRelateUserName("");
		acd.setChannel(type.getValue());
		acd.setAmount(money);
		acd.setDesciption(des);
		acd.setCreateTime(DateUtil.getStringDate());

		String exeRes = this.getAccountDao().addAccountDetail(acd, changeType);

		if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
			return exeRes;
		} else {
			return this.getAccountDao().updateBlance(userId, money);
		}

	}

	/**
	 * 修改收入
	 * 
	 * @param userId
	 * @param relateUserId
	 * @param money
	 * @param type
	 * @param changeType
	 * @param des
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月5日下午4:44:40
	 */
	private String updateIncome(String userId, String relateUserId, BigDecimal amount, Constant.logicType type, Constant.ChangeType changeType, String des) {
		IncomeDetail detail = new IncomeDetail();

		detail.setUserId(userId);
		detail.setUserName("");
		detail.setRelatedUserId(relateUserId);
		detail.setRelatedUserName("");
		detail.setAmount(amount);
		detail.setChannel(type.getValue());
		detail.setDescription(des);
		detail.setCreateTime(DateUtil.getStringDate());

		String exeRes = this.getAccountDao().addIncomeDetail(detail, changeType);

		if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
			return exeRes;
		} else {
			return this.getAccountDao().updateIncome(userId, amount);
		}
	}

	public ArrayList<HashMap<String, String>> getSynBalance(String synId, int maxCnt) {
		return this.getAccountDao().getSynBalance(synId, maxCnt);

	}

	public ArrayList<HashMap<String, String>> getSynIncome(String synId, int maxCnt) {
		return this.getAccountDao().getSynIncome(synId, maxCnt);

	}
}
