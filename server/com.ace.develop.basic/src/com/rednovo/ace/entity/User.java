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
 *                  fileName：User.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.entity;

import java.util.Map;

/**
 * @author yongchao.Yang/2014年10月22日
 */
public class User {
	private String uuid;

	private String userId;
	private String tokenId;
	private String passWord;
	private long subscribeCnt;
	private String sex;
	private String nickName;
	private String channel = "4";// 默认为本地用户
	private String rank;
	private int basicScore;
	private String signature;
	private String profile;
	private String showImg;
	private String isActive = "1"; // 0 冻结 1解冻
	private String isAuthen = "0";// 1 已认证 0 未认证 2 正在审核 3 认证失败
	private String isForbidShow = "0";// 0 未禁播 1 禁播
	private String createTime;
	private String updateTime;
	private String schemaId;
	private String deviceType;// 设备类型0安卓 1ios
	/**
	 * 用户扩展数据
	 */
	private Map<String, String> extendData;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String id) {
		this.userId = id;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String age) {
		this.rank = age;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String photo) {
		this.profile = photo;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String lastLoginTime) {
		this.createTime = lastLoginTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getBasicScore() {
		return basicScore;
	}

	public void setBasicScore(int basicScore) {
		this.basicScore = basicScore;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(String createTime) {
		this.schemaId = createTime;
	}

	public boolean equals(Object o) {
		User user = (User) o;
		if (this.getUserId().equals(user.getUserId())) {
			return true;
		}
		return false;
	}

	/**
	 * @return the userExtend
	 */
	public Map<String, String> getExtendData() {
		return extendData;
	}

	/**
	 * @param userExtend the userExtend to set
	 */
	public void setExtendData(Map<String, String> userExtend) {
		this.extendData = userExtend;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public long getSubscribeCnt() {
		return subscribeCnt;
	}

	public void setSubscribeCnt(long subscribeCnt) {
		this.subscribeCnt = subscribeCnt;
	}

	public String getShowImg() {
		return showImg;
	}

	public void setShowImg(String showImg) {
		this.showImg = showImg;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getIsAuthen() {
		return isAuthen;
	}

	public void setIsAuthen(String isAuthen) {
		this.isAuthen = isAuthen;
	}

	public String getIsForbidShow() {
		return isForbidShow;
	}

	public void setIsForbidShow(String isForbidShow) {
		this.isForbidShow = isForbidShow;
	}

}
