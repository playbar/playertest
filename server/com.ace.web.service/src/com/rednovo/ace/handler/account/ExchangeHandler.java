/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播移动
 *                  公司名称:美播娱乐
 *                  开发作者:sg.z
 *       			开发时间:2014年7月29日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自美播娱乐研发部，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：meibo-admin
 *                  fileName：UserHandler.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.handler.account;

import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang.StringUtils;

import com.ace.database.service.AccountService;
import com.ace.database.service.ExchangeService;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.OperaterStatus;
import com.rednovo.ace.entity.ExchangeBindInfo;
import com.rednovo.ace.entity.ExchangeDetail;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class ExchangeHandler extends BasicServiceAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if (StringUtils.equals("001-002", key)) {// 读取用户资料
		} else if ("001-019".equals(key)) {// 修改签名
			this.getIncomeBalance();
		} else if ("001-024".equals(key)) {
			this.getBindInfo();
		} else if ("001-025".equals(key)) {
			this.bind();
		} else if ("001-026".equals(key)) {
			this.request();
		} else if ("001-038".equals(key)) {
			this.income2Coin();
		} else if ("001-045".equals(key)) {
			this.getExchangeByDetail();
		} else if ("001-046".equals(key)) {
			this.getExchangeByStep();
		} else if ("001-047".equals(key)) {
			this.updateStepStatus();
		}

	}

	/**
	 * A豆兑换金币
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月17日下午2:12:00
	 */
	private void income2Coin() {
		String userId = this.getWebHelper().getString("userId");
		String income = this.getWebHelper().getString("income");
		BigDecimal incomebalance = AccountService.getIncomeBalance(userId);
		if (Validator.isEmpty(income)) {
			income = "0";
		}
		String systemId = PPConfiguration.getProperties("cfg.properties").getString("rate.system");
		// income * rate = balance
		String rate = StaticDataManager.getSysConfig(systemId + ".rate.income2coin");
		String exeCode = ExchangeService.income2Coin(userId, income, rate);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setValue("income", (incomebalance.subtract(new BigDecimal(income))) + "");
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}

	}

	/**
	 * 获取兑点余额
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月15日下午11:13:57
	 */
	private void getIncomeBalance() {
		String userId = this.getWebHelper().getString("userId");
		String balance = UserManager.getIncome(userId);
		if (Validator.isEmpty(balance)) {
			balance = "0";
		}
		String systemId = PPConfiguration.getProperties("cfg.properties").getString("rate.system");
		String rate = StaticDataManager.getSysConfig(systemId + ".rate.income2rmb");
		this.setValue("balance", balance);
		this.setValue("rate", rate);// 兑点汇率
		this.setSuccess();

	}

	/**
	 * 绑定兑点信息
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月19日上午10:00:47
	 */
	private void bind() {
		String userId = this.getWebHelper().getString("userId");
		String weChatId = this.getWebHelper().getString("weChatId");
		String mobileId = this.getWebHelper().getString("mobileId");
		String verifyCode = this.getWebHelper().getString("verifyCode");
		String userName = this.getWebHelper().getString("userName");
		String channel = this.getWebHelper().getString("channel", "0");
		String code = UserManager.getVerifyCode(mobileId);
		System.out.println("code:" + code);
		if (code == null || !code.equals(verifyCode)) {
			this.setError("216");
			return;
		}
		String exeCode = ExchangeService.bind(userId, weChatId, mobileId, userName, channel);
		if (OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			UserManager.delVerifyCode(mobileId);
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 获取绑定信息
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月19日上午10:05:16
	 */
	private void getBindInfo() {
		String userId = this.getWebHelper().getString("userId");
		String channel = this.getWebHelper().getString("channel");
		ExchangeBindInfo ebi = ExchangeService.getBindInfo(userId, channel);
		if (ebi == null) {
			this.setError("104");
		} else {
			this.setValue("bind", ebi);
			this.setSuccess();
		}
	}

	/**
	 * 兑点申请
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月19日上午10:26:48
	 */
	private void request() {
		String userId = this.getWebHelper().getString("userId");
		String rmbAmount = this.getWebHelper().getString("rmbAmount");
		String coinAmount = this.getWebHelper().getString("coinAmount");
		String verifyCode = this.getWebHelper().getString("verifyCode");
		String mobileId = this.getWebHelper().getString("mobileId");
		String channel = this.getWebHelper().getString("channel", "0");
		if (Validator.isEmpty(mobileId)) {
			ExchangeBindInfo bind = ExchangeService.getBindInfo(userId, channel);
			mobileId = bind.getMobileId();
		}
		String code = UserManager.getVerifyCode(mobileId);
		if (!verifyCode.equals(code)) {
			this.setError("216");
			return;
		}
		String systemId = PPConfiguration.getProperties("cfg.properties").getString("rate.system");
		String rate = StaticDataManager.getSysConfig(systemId + ".rate.income2rmb");
		String exeCode = ExchangeService.request(userId, channel, new BigDecimal(rmbAmount), new BigDecimal(rate));

		if (OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 提现记录
	 * 
	 * @author Wangwen.wang
	 * @since 2016年6月22日
	 */
	private void getExchangeByDetail() {
		String userId = this.getWebHelper().getString("userId");
		String startTime = this.getWebHelper().getString("startTime");
		String endTime = this.getWebHelper().getString("endTime");
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");
		if (page <= 0) {
			page = 1;
		}
		if (pageSize <= 0) {
			pageSize = 10;
		}
		List<ExchangeDetail> exchangeList = ExchangeService.getExchangeDetailList(userId, startTime, endTime, page, pageSize);
		if (exchangeList == null) {
			this.setError("300");
		} else {
			this.setSuccess();
			this.setValue("exchangeList", exchangeList);
		}
	}

	/**
	 * 根据审核状态查询提现记录
	 * 
	 * @author Wangwen.wang
	 * @since 2016年6月22日
	 */
	private void getExchangeByStep() {
		String step = this.getWebHelper().getString("step");
		String status = this.getWebHelper().getString("status");
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");
		if (page <= 0) {
			page = 1;
		}
		if (pageSize <= 0) {
			pageSize = 10;
		}
		List<ExchangeDetail> exchangeList = ExchangeService.getExchangeStepList(step,status, page, pageSize);
		if (exchangeList == null) {
			this.setError("300");
		} else {
			this.setSuccess();
			this.setValue("exchangeList", exchangeList);
		}
	}

	/**
	 * 修改审核的状态
	 * 
	 * @author Wangwen.wang
	 * @since 2016年6月22日
	 */
	private void updateStepStatus() {
		String id = this.getWebHelper().getString("id");
		String step = this.getWebHelper().getString("step");
		String status = this.getWebHelper().getString("status");
		String applyId = this.getWebHelper().getString("applyId");
		String auditId = this.getWebHelper().getString("auditId");
		String des = this.getWebHelper().getString("des");

		String exeCode = ExchangeService.updateStepStatus(id, step, status, applyId, auditId, des);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}

	}
}
