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

import java.util.ArrayList;
import java.util.List;

import com.rednovo.ace.entity.User;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class UserModule extends BasicModule {

	/**
	 * 
	 */
	public UserModule() {
		// TODO Auto-generated constructor stub
	}

	public String addUser(User user) {
		return this.getUserDao().addUser(user);
	}

	public User getUser(String userId) {
		return this.getUserDao().getUser(userId);
	}

	public List<User> getSynUser(String synId, int maxCnt) {
		return this.getUserDao().getSynUser(synId, maxCnt);
	}

	public ArrayList<String> getPid(String status) {
		return this.getUserDao().getNextPid(status);
	}

	public String updatePIDStatus(int id, String status) {
		return this.getUserDao().updatePIDStatus(id, status);
	}

	public String subscribe(String userId, String starId) {
		return this.getUserDao().subscribe(userId, starId);
	}

	public String removeSubscribe(String userId, String starId) {
		return this.getUserDao().removeSubscribe(userId, starId);
	}

	public ArrayList<String> getSynFans(String synId, int maxCnt) {
		return this.getUserDao().getSynFans(synId, maxCnt);
	}

	public ArrayList<String> getSynScribe(String synId, int maxCnt) {
		return this.getUserDao().getSynScribe(synId, maxCnt);
	}

	public String updateProfile(String userId, String url) {
		return this.getUserDao().updateProfile(userId, url);
	}

	public String updateShowImg(String userId, String url) {
		return this.getUserDao().updateShowImg(userId, url);
	}

	public String updatePasswd(String userId, String newPasswd) {
		return this.getUserDao().updatePasswd(userId, newPasswd);
	}

	public String updateNickName(String userId, String nickName) {
		return this.getUserDao().updateNickName(userId, nickName);
	}

	public String updateSignature(String userId, String signature) {
		return this.getUserDao().updateSignature(userId, signature);
	}

	public String updatePositon(String userId, String position) {
		return this.getUserDao().updatePositon(userId, position);
	}

	public String updateSex(String userId, String sex) {
		return this.getUserDao().updateSex(userId, sex);
	}

	public ArrayList<String> getSynUserIdDevice(String synId, int maxCnt) {
		return this.getUserDao().getSynUserDevice(synId, maxCnt);
	}

	/**
	 * 更新用户推送设备信息
	 * 
	 * @param userId
	 * @param tokenId
	 * @param deviceNo
	 * @param deviceType
	 * @param provider
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午3:05:00
	 */
	public String updateUserDevice(String userId, String tokenId, String deviceNo, String deviceType, String provider) {
		return this.getUserDao().updateUserDevice(userId, tokenId, deviceNo, deviceType, provider);
	}

	public String updateStatus(String userId, String status) {
		return this.getUserDao().updateStatus(userId, status);
	}

}
