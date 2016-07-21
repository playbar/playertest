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
 *                  fileName：GoodInfo.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.entity;

import java.math.BigDecimal;

/**
 * @author yongchao.Yang/2016年3月4日
 */
public class GoodInfo implements Comparable<GoodInfo> {

	/**
	 * 
	 */
	public GoodInfo() {

	}

	private String id;
	private String name;
	private BigDecimal rmbPrice;
	private BigDecimal coinPrice;
	private String status;
	private int sortId;
	private String type;
	private String description;
	private String isCombined;
	private String updateTime;
	private String createTime;
	private String schemaId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getRmbPrice() {
		return rmbPrice;
	}

	public void setRmbPrice(BigDecimal rmbPrice) {
		this.rmbPrice = rmbPrice;
	}

	public BigDecimal getCoinPrice() {
		return coinPrice;
	}

	public void setCoinPrice(BigDecimal coinPrice) {
		this.coinPrice = coinPrice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIsCombined() {
		return isCombined;
	}

	public void setIsCombined(String isCombined) {
		this.isCombined = isCombined;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	@Override
	public int compareTo(GoodInfo o) {
		return Integer.valueOf(sortId).compareTo(Integer.valueOf(o.sortId));
	}

}
