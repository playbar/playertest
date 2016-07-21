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
import java.util.List;

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

	public ExchangeBindInfo getBindInfo(String userId, String channel) {
		return this.getExchangeDao().getBindInfo(userId, channel);
	}

	public String bind(String userId, String weChatId, String mobileId, String userName, String channel) {
		ExchangeBindInfo bind = new ExchangeBindInfo();
		bind.setUserId(userId);
		bind.setWeChatId(weChatId);
		bind.setMobileId(mobileId);
		bind.setUserName(userName);
		bind.setChannel(channel);

		return this.getExchangeDao().bind(bind);
	}

	/**
	 * 兑点申请
	 * 
	 * @param userId
	 * @param coinAmount
	 * @return
	 * @author Yongchao.Yang
	 * @param channel
	 * @param rate
	 * @since 2016年3月19日上午10:59:59
	 */
	public String request(String userId, String channel, BigDecimal rmbAmount, BigDecimal rate) {

		ExchangeBindInfo bind = this.getBindInfo(userId, channel);
		User user = UserManager.getUser(userId);

		ExchangeDetail detail = new ExchangeDetail();
		detail.setCoinAmount(rmbAmount.multiply(rate));
		detail.setMobileId(bind.getMobileId());
		detail.setPayerId("");
		detail.setPayerName("");
		detail.setRmbAmount(rmbAmount);
		detail.setStatus("0");
		detail.setUserId(userId);
		detail.setUserName(user.getNickName());
		detail.setWeChatId(bind.getWeChatId());
		detail.setRealName(bind.getUserName());
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

	/**
	 * 根据userId查询提现记录
	 * 
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年6月24日下午5:40:28
	 */
	public List<ExchangeDetail> getExchangeDetailList(String userId, String startTime, String endTime, int page, int pageSize) {
		return this.getExchangeDao().getExchangeDetailList(userId, startTime, endTime, page, pageSize);
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
	public List<ExchangeDetail> getExchangeStepList(String step, String status, int page, int pageSize) {
		return this.getExchangeDao().getExchangeStepList(step,status, page, pageSize);
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
	public String updateStepStatus(String id, String step, String status, String applyId, String auditId, String des) {
		return this.getExchangeDao().updateStepStatus(id, step, status, applyId, auditId, des);
	}

}
