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
	private String channel;
	private String rank;
	private int basicScore;
	private String signature;
	private String profile;
	private String showImg;
	private String status = "1"; //1 激活 2冻结 3封禁
	private String createTime;
	private String updateTime;
	private String schemaId;
	//private String type;//1非签约主播 2 签约主播
	private String deviceType;//设备类型0安卓 1ios
	private String certify; //空或者空字符串为未审核 ， 0 审核中 1审核成功 2审核失败
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getCertify() {
		return certify;
	}

	public void setCertify(String certify) {
		this.certify = certify;
	}

}
