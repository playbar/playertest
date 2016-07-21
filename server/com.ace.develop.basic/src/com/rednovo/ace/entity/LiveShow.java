/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月22日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：LiveShow.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.entity;

import com.rednovo.tools.PPConfiguration;

/**
 * @author yongchao.Yang/2014年10月22日
 */
public class LiveShow implements Comparable<LiveShow> {
	// private String id;
	private String showId;
	private String userId;
	private String title;
	private String position;// 主播位置
	private String startTime;// 开始时间
	private String downStreamUrl;// 直播间拉流地址
	private String length;// 直播时长
	private String supportCnt;// 点赞个数
	private String coinCnt;// 获取金币个数
	private String shareCnt;// 分享次数
	private String memberCnt;// 观众个数
	private long sortCnt;
	private String fansCnt;

	private String createTime;
	private String schemaId;

	public String getFansCnt() {
		return fansCnt;
	}

	public void setFansCnt(String fansCnt) {
		this.fansCnt = fansCnt;
	}

	public void setDownStreamUrl(String downStreanUrl) {
		this.downStreamUrl = downStreanUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getSupportCnt() {
		return supportCnt;
	}

	public void setSupportCnt(String supportCnt) {
		this.supportCnt = supportCnt;
	}

	public String getCoinCnt() {
		return coinCnt;
	}

	public void setCoinCnt(String coinCnt) {
		this.coinCnt = coinCnt;
	}

	public String getShareCnt() {
		return shareCnt;
	}

	public void setShareCnt(String shareCnt) {
		this.shareCnt = shareCnt;
	}

	public String getMemberCnt() {
		return memberCnt;
	}

	public void setMemberCnt(String memberCnt) {
		this.memberCnt = memberCnt;
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

	public long getSortCnt() {
		return sortCnt;
	}

	public void setSortCnt(long sortCnt) {
		this.sortCnt = sortCnt;
	}

	public String getDownStreamUrl() {
		return PPConfiguration.getProperties("cfg.properties").getString("cdn.downstream.url") + this.getUserId();
	}

	public String getShowId() {
		return this.getUserId();
	}

	public void setShowId(String showId) {
		this.showId = showId;
	}
	
	
	@Override
	public int compareTo(LiveShow s) {
		return Long.valueOf(s.getSortCnt()).compareTo(Long.valueOf(sortCnt));
	}

}
