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
 *                  fileName：AccountChageDetail.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.entity;

import java.math.BigDecimal;

/**
 * @author yongchao.Yang/2016年3月3日
 */
public class GiftDetail {

	/**
	 * 
	 */
	public GiftDetail() {}

	private int id;
	private String userId;
	private String userName;
	private String relateUserId;// 相关人
	private String relateUserName;// 相关人
	private String giftId;// 礼物id
	private String giftName;// 礼物名称
	private int giftCnt;// 礼物数量
	private BigDecimal price;// 单价
	private BigDecimal totalValue;// 总价
	private String channel;// 接收 赠送
	private String description;// 描述
	private String createTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRelateUserId() {
		return relateUserId;
	}

	public void setRelateUserId(String relateUserId) {
		this.relateUserId = relateUserId;
	}

	public String getGiftId() {
		return giftId;
	}

	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}

	public String getGiftName() {
		return giftName;
	}

	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}

	public int getGiftCnt() {
		return giftCnt;
	}

	public void setGiftCnt(int giftCnt) {
		this.giftCnt = giftCnt;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRelateUserName() {
		return relateUserName;
	}

	public void setRelateUserName(String relateUserName) {
		this.relateUserName = relateUserName;
	}

}
