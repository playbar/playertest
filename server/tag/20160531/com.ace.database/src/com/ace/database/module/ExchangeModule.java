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
 *                  fileName：UserFunImpl.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.module;

import java.math.BigDecimal;

import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.ExchangeBindInfo;
import com.rednovo.ace.entity.ExchangeDetail;
import com.rednovo.ace.entity.User;
import com.rednovo.ace.globalData.UserManager;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class ExchangeModule extends BasicModule {

	/**
	 * 
	 */
	public ExchangeModule() {
		// TODO Auto-generated constructor stub
	}

	public ExchangeBindInfo getBindInfo(String userId) {
		return this.getExchangeDao().getBindInfo(userId);
	}

	public String bind(String userId, String weChatId, String mobileId) {
		ExchangeBindInfo bind = new ExchangeBindInfo();
		bind.setUserId(userId);
		bind.setWeChatId(weChatId);
		bind.setMobileId(mobileId);

		return this.getExchangeDao().bind(bind);
	}

	/**
	 * 兑点申请
	 * 
	 * @param userId
	 * @param coinAmount
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月19日上午10:59:59
	 */
	public String request(String userId, BigDecimal coinAmount) {

		ExchangeBindInfo bind = this.getBindInfo(userId);
		User user = UserManager.getUser(userId);

		ExchangeDetail detail = new ExchangeDetail();
		detail.setCoinAmount(coinAmount);
		detail.setMobileId(bind.getMobileId());
		detail.setPayerId("");
		detail.setPayerName("");
		detail.setRmbAmount(coinAmount);
		detail.setStatus(Constant.OrderStatus.UNPAYED.getValue());
		detail.setUserId(userId);
		detail.setUserName(user.getNickName());
		detail.setWeChatId(bind.getWeChatId());
		return this.getExchangeDao().addRequest(detail);
	}

	/**
	 * 打款
	 * 
	 * @param exchangeId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月19日上午11:00:55
	 */
	public String applyRequest(String requestId, String status, String payerId) {
		String payerName = UserManager.getUser(payerId).getNickName();
		return this.getExchangeDao().applyRequest(requestId, status, payerId, payerName);
	}

}
