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

import org.apache.commons.lang.StringUtils;

import com.ace.database.service.ExchangeService;
import com.rednovo.ace.constant.Constant.OperaterStatus;
import com.rednovo.ace.entity.ExchangeBindInfo;
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
		System.out.println("userId:" + userId + ",weChatId:" + weChatId + ",mobileId:" + mobileId + ",verifyCode:" + verifyCode);

		String code = UserManager.getVerifyCode(mobileId);
		System.out.println("code:" + code);
		if (!code.equals(verifyCode)) {
			this.setError("216");
			return;
		}
		String exeCode = ExchangeService.bind(userId, weChatId, mobileId);
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
		ExchangeBindInfo ebi = ExchangeService.getBindInfo(userId);
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

		ExchangeBindInfo bind = ExchangeService.getBindInfo(userId);

		String code = UserManager.getVerifyCode(bind.getMobileId());
		if (!code.equals(verifyCode)) {
			this.setError("216");
			return;
		}

		String exeCode = ExchangeService.request(userId, new BigDecimal(coinAmount));

		if (OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

}
