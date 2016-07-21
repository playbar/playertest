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
 *                  fileName：IncomeDetail.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.entity;

import java.math.BigDecimal;

/**
 * @author yongchao.Yang/2016年3月5日
 */
public class IncomeDetail {

	private String id;
	private String userId;
	private String userName;
	private String relatedUserId;
	private String relatedUserName;
	private BigDecimal amount;
	private String channel;
	private String description;
	private String createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRelatedUserId() {
		return relatedUserId;
	}

	public void setRelatedUserId(String relatedUserId) {
		this.relatedUserId = relatedUserId;
	}

	public String getRelatedUserName() {
		return relatedUserName;
	}

	public void setRelatedUserName(String relatedUserName) {
		this.relatedUserName = relatedUserName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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
	
	

	/**
	 * 
	 */
	public IncomeDetail() {
		// TODO Auto-generated constructor stub
	}

}
