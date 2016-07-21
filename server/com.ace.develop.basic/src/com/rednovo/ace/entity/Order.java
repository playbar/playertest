/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月3日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.web.service
 *                  fileName：Order.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.entity;

import java.math.BigDecimal;

import com.rednovo.ace.constant.Constant;

/**
 * @author yongchao.Yang/2016年3月3日
 */
public class Order {

	/**
	 * 
	 */
	public Order() {
		// TODO Auto-generated constructor stub
	}

	private String orderId;
	private String thirdId;
	private String payerId;
	private String payerName;
	private String receiverId;
	private String receiveName;
	private String goodId;
	private String goodName;
	private int goodCnt;
	private String orderDes;
	private BigDecimal rmbAmount;
	private BigDecimal coinAmount;
	private Constant.payChannel payChannel;
	private String orderChannel; //由那个主播推荐的充值订单
	private BigDecimal payedAmount;
	private String rate;
	private String createTime;
	private String openTime;
	private String openUserId;
	private String openUserName;
	private String status;
	private String channel;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getOrderDes() {
		return orderDes;
	}

	public void setOrderDes(String orderDes) {
		this.orderDes = orderDes;
	}

	public BigDecimal getRmbAmount() {
		return rmbAmount;
	}

	public void setRmbAmount(BigDecimal rmbAmount) {
		this.rmbAmount = rmbAmount;
	}

	public BigDecimal getCoinAmount() {
		return coinAmount;
	}

	public void setCoinAmount(BigDecimal coinAmount) {
		this.coinAmount = coinAmount;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String orderTime) {
		this.createTime = orderTime;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getOpenUserId() {
		return openUserId;
	}

	public void setOpenUserId(String operatorId) {
		this.openUserId = operatorId;
	}

	public String getOpenUserName() {
		return openUserName;
	}

	public void setOpenUserName(String operatorName) {
		this.openUserName = operatorName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGoodId() {
		return goodId;
	}

	public void setGoodId(String goodId) {
		this.goodId = goodId;
	}

	public String getGoodName() {
		return goodName;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	public int getGoodCnt() {
		return goodCnt;
	}

	public void setGoodCnt(int goodCnt) {
		this.goodCnt = goodCnt;
	}

	public Constant.payChannel getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(Constant.payChannel payChannel) {
		this.payChannel = payChannel;
	}

	public BigDecimal getPayedAmount() {
		return payedAmount;
	}

	public void setPayedAmount(BigDecimal payedAmount) {
		this.payedAmount = payedAmount;
	}

	public String getThirdId() {
		return thirdId;
	}

	public void setThirdId(String thirdId) {
		this.thirdId = thirdId;
	}

	/**
	 * @return the orderChannel
	 */
	public String getOrderChannel() {
		return orderChannel;
	}

	/**
	 * @param orderChannel the orderChannel to set
	 */
	public void setOrderChannel(String orderChannel) {
		this.orderChannel = orderChannel;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

}
