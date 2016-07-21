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
 *                  fileName：Gift.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.entity;

import java.math.BigDecimal;

/**
 * @author yongchao.Yang/2016年3月4日
 */
public class Gift implements Comparable<Gift> {

	private String id;
	private String name;
	private String pic;
	private BigDecimal sendPrice;
	private BigDecimal transformPrice;
	private String isCombined;
	private int sortId;
	private String status;
	private String createTime;
	private String schemaId;
	private String type;

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

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public BigDecimal getSendPrice() {
		return sendPrice;
	}

	public void setSendPrice(BigDecimal sendPrice) {
		this.sendPrice = sendPrice;
	}

	public BigDecimal getTransformPrice() {
		return transformPrice;
	}

	public void setTransformPrice(BigDecimal transformPrice) {
		this.transformPrice = transformPrice;
	}

	public String getIsCombined() {
		return isCombined;
	}

	public void setIsCombined(String isCombined) {
		this.isCombined = isCombined;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	@Override
	public int compareTo(Gift o) {
		return Integer.valueOf(sortId).compareTo(Integer.valueOf(o.sortId));
	}

	/**
	 * 
	 */
	public Gift() {

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
