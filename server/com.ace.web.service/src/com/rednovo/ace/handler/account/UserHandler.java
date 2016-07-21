/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播移动
 *                  公司名称:美播娱乐
 *                  开发作者:sg.z
 *       			开发时间:2014年7月29日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自美播娱乐研发部，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：meibo-admin
 *                  fileName：UserHandler.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.handler.account;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import com.ace.database.service.UserMenuService;
import com.ace.database.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.OperaterStatus;
import com.rednovo.ace.constant.Constant.userStatus;
import com.rednovo.ace.entity.LiveShow;
import com.rednovo.ace.entity.User;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.ace.globalData.UIDPoolManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.ace.globalData.UserRelationManager;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.solr.SearchEngines;
import com.rednovo.tools.KeyGenerator;
import com.rednovo.tools.MD5;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.UserAssistant;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class UserHandler extends BasicServiceAdapter {
	Logger logger = Logger.getLogger(UserHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if ("001-001".equals(key)) {// 读取用户资料
			this.getUser();
		} else if ("001-007".equals(key)) {// 新建用户
			this.createUser();
		} else if ("001-013".equals(key)) {// 登录
			this.login();
		} else if ("001-015".equals(key)) {// 修改图像
			this.updateProfile();
		} else if ("001-016".equals(key)) {// 修改密码
			this.updatePasswd();
		} else if ("001-017".equals(key)) {// 修改昵称
			this.updateNickName();
		} else if ("001-018".equals(key)) {// 修改签名
			this.updateSignature();
		} else if ("001-021".equals(key)) {// 用户检索
			this.searchUser();
		} else if ("001-022".equals(key)) {// 找回密码
			this.resetPasswd();
		} else if ("001-023".equals(key)) {// 修改性别
			this.updateSex();
		} else if ("001-027".equals(key)) {// 修改用户推送设备
			this.updateUserDevice();
		} else if ("001-028".equals(key)) {// 修改用户状态
			this.updateUserStatus();
		} else if ("001-031".equals(key)) { // 添加用户审核信息
			this.addUserAuth();
		} else if ("001-030".equals(key)) { // 获取用户认证状态
			this.getUserAuthStatus();
		} else if ("001-034".equals(key)) { // manager用户登录
			this.managerLogin();
		}
	}

	/**
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月13日下午1:49:38
	 */
	private void managerLogin() {
		String userId = this.getWebHelper().getString("userId");
		String passwd = this.getWebHelper().getString("passwd");
		// 判断是否为manager用户
		if (!UserMenuService.isManager(userId)) {
			this.setError("0");
			return;
		}
		// 判断密码是否正确
		User u = UserManager.getUser(userId);
		if (u == null || !MD5.md5(passwd).equals(u.getPassWord())) {
			this.setError("0");
			return;
		}
		this.setSuccess();
	}

	/**
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月2日下午7:44:58
	 */
	private void updateUserStatus() {
		String userId = this.getWebHelper().getString("userId");
		String isActive = this.getWebHelper().getString("isActive");
		String isAuthen = this.getWebHelper().getString("isAuthen");
		String isForbidShow = this.getWebHelper().getString("isForbidShow");
		String key = this.getWebHelper().getString("key");
		String type = this.getWebHelper().getString("type");
		logger.info(userId + "_" + isActive + "_" + isAuthen + "_" + isForbidShow + "_" + key);
		String exeCode = UserService.updateStatus(userId, isActive, isAuthen, isForbidShow);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			if (!Validator.isEmpty(key) && !isForbidShow.equals("0")) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("userId", userId);
				if ("001-003".equals(key)) {
					params.put("type", type);
				}
				String res = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("redis.server.url") + "/" + key + "", params);
				JSONObject response = JSON.parseObject(res);
				if (Constant.OperaterStatus.SUCESSED.getValue().equals(response.getString("exeStatus"))) {
					this.setSuccess();
				}
			} else {
				this.setSuccess();
			}
		} else {
			this.setError(exeCode);
		}

	}

	/**
	 * 获取用户认证状态 auth 1 已认证 0 未认证 2 正在审核 3 认证失败
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月2日下午2:39:52
	 */
	private void getUserAuthStatus() {
		String userId = this.getWebHelper().getString("userId");
		// 获取认证状态
		User user = UserManager.getUser(userId);
		String auth = user.getIsAuthen();
		if (Validator.isEmpty(auth)) {
			auth = Constant.CertifyProcess.NOT.getValue();
		}
		if (Constant.CertifyProcess.NOT.getValue().equals(auth) || Constant.CertifyProcess.FAILED.getValue().equals(auth)) {
			String url = PPConfiguration.getProperties("cfg.properties").getString("http.server.url");
			this.setValue("authUrl", url.substring(0, url.length() - 8) + "/app/auth.html?userId=" + userId);
		}
		this.setValue("auth", auth);
		this.setSuccess();
	}

	/**
	 * 添加用户审核信息
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月1日下午6:47:59
	 */
	private void addUserAuth() {
		String userId = this.getWebHelper().getString("userId");
		String name = this.getWebHelper().getString("name");
		String cardType = this.getWebHelper().getString("cardType");
		String phone = this.getWebHelper().getString("phone");
		String cardId = this.getWebHelper().getString("cardId");
		String bankId = this.getWebHelper().getString("bankId");
		String bankName = this.getWebHelper().getString("bankName");
		String bankProvince = this.getWebHelper().getString("bankProvince");
		String bankCity = this.getWebHelper().getString("bankCity");
		String bankAddress = this.getWebHelper().getString("bankAddress");
		byte[] frontImg = this.getWebHelper().getBytes("frontImg");
		byte[] backImg = this.getWebHelper().getBytes("backImg");
		byte[] holdImg = this.getWebHelper().getBytes("holdImg");
		if (frontImg == null || backImg == null || holdImg == null || Validator.isEmpty(userId) || Validator.isEmpty(name) || Validator.isEmpty(cardType) || Validator.isEmpty(phone) || Validator.isEmpty(cardId) || Validator.isEmpty(bankId) || Validator.isEmpty(bankName)
				|| Validator.isEmpty(bankProvince) || Validator.isEmpty(bankCity) || Validator.isEmpty(bankAddress)) {
			this.getWebHelper().getRequest().setAttribute("exeStatus", "0");
			// this.setError("218");
			return;
		}
		// 拼装图片路径
		String path = UserAssistant.getUserAbsoluteDir(userId);
		String sign = "-auth";
		String suffix = ".png";
		String relativeDir = UserAssistant.getUserRelativeDir(userId);
		String frontName = KeyGenerator.createUniqueId();
		String operaterStatus = writeFile(path, frontName, frontImg, sign, suffix);
		if (operaterStatus.equals(Constant.OperaterStatus.FAILED)) {
			this.getWebHelper().getRequest().setAttribute("exeStatus", "0");
			return;
		}

		String backName = KeyGenerator.createUniqueId();
		operaterStatus = writeFile(path, backName, backImg, sign, suffix);
		if (operaterStatus.equals(Constant.OperaterStatus.FAILED)) {
			this.getWebHelper().getRequest().setAttribute("exeStatus", "0");
			return;
		}

		String holdName = KeyGenerator.createUniqueId();
		operaterStatus = writeFile(path, holdName, holdImg, sign, suffix);
		if (operaterStatus.equals(Constant.OperaterStatus.FAILED)) {
			this.getWebHelper().getRequest().setAttribute("exeStatus", "0");
			return;
		}
		String frontUrl = PPConfiguration.getProperties("cfg.properties").getString("img.server.root.url") + relativeDir + "/" + frontName + sign + suffix;
		String backUrl = PPConfiguration.getProperties("cfg.properties").getString("img.server.root.url") + relativeDir + "/" + backName + sign + suffix;
		String holdUrl = PPConfiguration.getProperties("cfg.properties").getString("img.server.root.url") + relativeDir + "/" + holdName + sign + suffix;
		String exeCode = UserService.addUserAuth(userId, name, phone, cardType, cardId, bankId, bankName, bankProvince, bankCity, bankAddress, frontUrl, backUrl, holdUrl);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.getWebHelper().getRequest().setAttribute("exeStatus", "1");
		} else {
			this.getWebHelper().getRequest().setAttribute("exeStatus", "0");
		}

	}

	/**
	 * 写入数据到文件中
	 * 
	 * @param path 项目绝对路径
	 * @param uuid 随机id
	 * @param data 图片数据
	 * @param suffix 图片后缀
	 * @author lxg
	 * @return
	 */
	private String writeFile(String path, String uuid, byte[] data, String sign, String suffix) {
		File file = new File(path + File.separator + uuid + sign + suffix);
		FileOutputStream fis;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fis = new FileOutputStream(file);
			fis.write(data);
			fis.flush();
			fis.close();
		} catch (Exception e) {
			logger.error("上传图像失败", e);
			e.printStackTrace();
			return "0";
		}
		return "1";
	}

	/**
	 * 获取用户资料
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月3日上午10:34:46
	 */
	private void getUser() {
		String starId = this.getWebHelper().getString("starId");
		String userId = this.getWebHelper().getString("userId");
		// 主播id，查询禁言状态时用
		String showId = this.getWebHelper().getString("showId");

		// 先从缓存中获取，如果没有则读取数据库
		User user = UserManager.getUser(starId);
		if (user == null) {
			this.setError("204");
			return;
		}
		HashMap<String, String> exeData = new HashMap<String, String>();
		// 判断关系
		if (!Validator.isEmpty(userId) && !userId.equals(starId)) {
			List<String> list = UserRelationManager.getSubscribe(userId, 1, 10000);

			if (list.contains(starId)) {
				exeData.put("relatoin", "1");// 订阅
			} else {
				exeData.put("relatoin", "0");// 未订阅
			}
		}
		// 提取扩展字段
		String postion = UserManager.getExtData(starId, "position");
		String fansCnt = String.valueOf(UserRelationManager.getFansCnt(starId));
		String subscribeCnt = String.valueOf(UserRelationManager.getSubscribeCnt(starId));

		exeData.put("postion", postion);// 上次直播位置
		exeData.put("fansCnt", fansCnt);// 粉丝数
		exeData.put("subscribeCnt", subscribeCnt);// 订阅数
		// 判断该用户是否直播
		String isShow = "0";
		List<String> showIds = LiveShowManager.getSortList(1, 100000);
		if (showIds.contains(starId)) {
			isShow = "1";
			exeData.put("showId", starId);
			LiveShow s = LiveShowManager.getShow(starId);
			exeData.put("downStreanUrl", s.getDownStreamUrl());
		}
		exeData.put("isShow", isShow);
		// 查看禁言状态 0为禁言 1为正常, 当showid不为空的时候表示在直播间内查看主播查看用户
		if (!Validator.isEmpty(showId)) {
			String isForbiddenUser = "0";
			boolean isUserForbidden = LiveShowManager.isUserForbidden(starId, showId);
			isForbiddenUser = isUserForbidden ? "0" : "1";
			exeData.put("isForbiddenUser", isForbiddenUser);
		}
		user.setExtendData(exeData);
		this.setSuccess();
		// 去掉无用字段
		user.setCreateTime(null);
		user.setPassWord(null);
		user.setSchemaId(null);
		user.setTokenId(null);
		user.setUpdateTime(null);

		this.setValue("user", user);
	}

	/**
	 * 找回密码
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月18日下午5:39:34
	 */
	private void resetPasswd() {
		String mobileId = this.getWebHelper().getString("mobileId");
		String verifyCode = this.getWebHelper().getString("verifyCode");
		String newPasswd = this.getWebHelper().getString("newPasswd");
		User u = UserManager.getUserByTokenId(mobileId);
		if (u == null) {
			this.setError("204");
			return;
		}
		String code = UserManager.getVerifyCode(mobileId);
		if (!code.equals(verifyCode)) {
			this.setError("216");
			return;
		}
		String exeCode = UserService.updatePasswd(u.getUserId(), MD5.md5(newPasswd));

		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			UserManager.delVerifyCode(mobileId);
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}

	}

	private void searchUser() {
		int page = this.getWebHelper().getInt("page", 1);
		int pageSize = this.getWebHelper().getInt("pageSize", 10);
		String key = this.getWebHelper().getString("key");

		ArrayList<User> users = new ArrayList<User>();
		// 如果是数字，则先检索ID
		if (Validator.isNumber(key)) {
			User u = UserManager.getUser(key);
			if (u != null) {
				u.setChannel(null);
				u.setCreateTime(null);
				u.setExtendData(null);
				u.setPassWord(null);
				u.setSchemaId(null);
				// u.setStatus(null);
				u.setTokenId(null);
				u.setUpdateTime(null);
				u.setUuid(null);
				users.add(u);
				this.setSuccess();
				this.setValue("userList", users);
				return;
			}
		}

		// 再作为名称关键字进行检索
		try {
			List<Map<String, Object>> list = SearchEngines.getIndex(page, pageSize, key, "id");
			for (Map<String, Object> map : list) {
				String id = map.get("id").toString();
				User u = UserManager.getUser(id);
				u.setChannel(null);
				u.setCreateTime(null);
				u.setExtendData(null);
				u.setPassWord(null);
				u.setSchemaId(null);
				// u.setStatus(null);
				u.setTokenId(null);
				u.setUpdateTime(null);
				u.setUuid(null);
				users.add(u);
			}
		} catch (Exception e) {
			this.logger.error(e);
		}
		this.setSuccess();
		this.setValue("userList", users);
	}

	/**
	 * 创建用户
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午11:34:34
	 */
	private void createUser() {
		String mobile = this.getWebHelper().getString("mobile");
		String passwd = this.getWebHelper().getString("passwd");
		String verifyCode = this.getWebHelper().getString("verifyCode");
		String sex = this.getWebHelper().getString("sex", "");
		String signature = this.getWebHelper().getString("signature", "");

		// 是否自动生成
		String isAuto = this.getWebHelper().getString("isAuto");

		User u = UserManager.getUserByTokenId(mobile);
		if (u != null) {
			this.setError("210");
			return;
		}
		String channel = Constant.loginChannel.MOBILE.getValue();
		if (!"1".equals(isAuto)) {
			String code = UserManager.getVerifyCode(mobile);
			if (Validator.isEmpty(code) || !code.equals(verifyCode)) {
				this.setError("216");
				return;
			}
		} else {
			channel = Constant.loginChannel.LOCAL.getValue();
		}

		passwd = MD5.md5(passwd);
		String uid = UIDPoolManager.getUid();
		String nickName = uid;
		this.getLog().info("uuid:" + uid);
		String exeCode = UserService.addUser(uid, mobile, passwd, "", nickName, sex, channel, signature);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			// 创建用户目录
			UserAssistant.makeUserDir(uid);
			UserManager.delVerifyCode(mobile);// 删除验证码
			User user = UserService.getUser(uid);
			this.setSuccess();
			this.setValue("user", user);
		} else {
			this.setError(exeCode);
		}
	}

	private void updateProfile() {
		String userId = this.getWebHelper().getString("userId");
		// String name = this.getWebHelper().getString("profile");
		byte[] data = this.getWebHelper().getBytes("profile");

		String uuid = KeyGenerator.createUniqueId();
		String suffix = ".png";

		// 保存图像
		String path = UserAssistant.getUserAbsoluteDir(userId);
		File img = new File(path + File.separator + uuid + "-profile" + suffix);
		FileOutputStream fis;
		try {
			fis = new FileOutputStream(img);
			fis.write(data);
			fis.flush();
			fis.close();
		} catch (Exception e) {
			logger.error("上传图像失败", e);
			e.printStackTrace();
		}
		String visitUrl = PPConfiguration.getProperties("cfg.properties").getString("img.server.root.url") + UserAssistant.getUserRelativeDir(userId) + "/" + uuid + "-profile" + suffix;
		String exeCode = UserService.updateProfile(userId, visitUrl);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
			this.setValue("visitUrl", visitUrl);
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 修改密码
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月9日下午3:13:49
	 */
	private void updatePasswd() {
		String userId = this.getWebHelper().getString("userId");
		String newPasswd = this.getWebHelper().getString("newPasswd");
		String oldPasswd = this.getWebHelper().getString("oldPasswd");
		User user = UserManager.getUser(userId);
		if (user == null) {
			this.setError("204");
			return;
		}
		if (!user.getPassWord().equals(MD5.md5(oldPasswd))) {
			this.setError("203");
			return;
		}
		String exeCode = UserService.updatePasswd(userId, MD5.md5(newPasswd));
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 修改性别
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月18日下午6:17:46
	 */
	private void updateSex() {
		String userId = this.getWebHelper().getString("userId");
		String sex = this.getWebHelper().getString("sex");
		User user = UserManager.getUser(userId);
		if (user == null) {
			this.setError("204");
			return;
		}

		String exeCode = UserService.updateSex(userId, sex);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

	private void updateNickName() {
		String userId = this.getWebHelper().getString("userId", "abc");
		String nickName = this.getWebHelper().getString("nickName");

		List<String> words = StaticDataManager.getKeyWord(Constant.KeyWordType.NAME.getValue());
		if (Validator.checkKeyWord(nickName, words)) {
			this.setError("218");
			return;
		}

		String exeCode = UserService.updateNickName(userId, nickName);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 修改签名
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月9日下午3:15:15
	 */
	private void updateSignature() {
		String userId = this.getWebHelper().getString("userId");
		String signature = this.getWebHelper().getString("signature");
		List<String> words = StaticDataManager.getKeyWord(Constant.KeyWordType.NAME.getValue());
		if (Validator.checkKeyWord(signature, words)) {
			this.setError("218");
			return;
		}
		String exeCode = UserService.updateSignature(userId, signature);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 
	 * 登录
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月8日上午8:23:03
	 */
	private void login() {
		String channel = this.getWebHelper().getString("channel");
		String tokenId = this.getWebHelper().getString("tokenId");
		String passwd = this.getWebHelper().getString("passwd");
		User u = null;
		if (Constant.loginChannel.LOCAL.getValue().equals(channel)) {// 本地登录
			u = UserManager.getUser(tokenId);
		} else {
			u = UserManager.getUserByTokenId(tokenId);
		}
		// 冻结
		if (u != null && u.getIsActive().equals(userStatus.FREEZE.getValue())) {
			this.setError("208");
			return;
		}

		// 黑名单过滤
		if (u != null && UserManager.isFreeze(u.getUserId())) {
			this.setError("208");
			return;
		}

		// 本地和手机登录需要验证密码
		if (Constant.loginChannel.LOCAL.getValue().equals(channel) || Constant.loginChannel.MOBILE.getValue().equals(channel)) {
			if (u == null) {
				this.setError("202");
				return;
			} else if (!u.getPassWord().equals(MD5.md5(passwd))) {
				this.setError("203");
				return;
			}
			// this.setSuccess();
			// this.setValue("user", u);
		} else {// 第三方
			if (u == null) {
				String pid = UIDPoolManager.getUid();
				// String nickName = this.getWebHelper().getString("nickName");
				// TODO 为了避免昵称中带表情符导致保存失败，暂时将第三方昵称改为id
				String nickName = pid;
				String profile = this.getWebHelper().getString("profile");
				if (Constant.OperaterStatus.SUCESSED.getValue().equals(UserService.addUser(pid, tokenId, "", profile, nickName, "", channel, ""))) {
					// 创建用户目录
					UserAssistant.makeUserDir(pid);
					u = UserService.getUser(pid);
				}
			}
		}

		// 返回推送设备ID
		HashMap<String, String> extData = new HashMap<String, String>();
		String pushTokenId = UserManager.getUserDevice(u.getUserId());
		if (!Validator.isEmpty(pushTokenId)) {
			extData.put("pushDevNo", pushTokenId.substring(0, pushTokenId.lastIndexOf("_")));
		} else {
			extData.put("pushDevNo", "");
		}
		u.setExtendData(extData);
		this.setValue("user", u);
		this.setSuccess();

	}

	/**
	 * 更新用户推送设备
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午5:48:40
	 */
	private void updateUserDevice() {
		String userId = this.getWebHelper().getString("userId");
		String tokenId = this.getWebHelper().getString("tokenId");
		String deviceType = this.getWebHelper().getString("deviceType");
		String provider = this.getWebHelper().getString("provider");

		String res = UserService.updateUserDevice(userId, tokenId, "", deviceType, provider);
		if (OperaterStatus.SUCESSED.getValue().equals(res)) {
			this.setSuccess();
		} else {
			this.setError(res);
		}

	}

}
