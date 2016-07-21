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
 *                  fileName：UserService.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.ace.database.ds.UserTransactionManager;
import com.ace.database.module.UserModule;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.User;
import com.rednovo.ace.entity.UserAuth;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.tools.Validator;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class UserService {
	private static Logger logger = Logger.getLogger(UserService.class);

	public UserService() {}

	public static String addUser(String userId, String tokenId, String passwd, String profile, String nickName, String sex, String channel, String signature) {
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule uf = new UserModule();
		try {
			ut.begin();
			User user = new User();
			user.setUserId(userId);
			user.setTokenId(tokenId);
			user.setPassWord(passwd);
			user.setSex(sex);
			user.setNickName(nickName);
			user.setChannel(channel);
			user.setRank("0");
			user.setBasicScore(0);
			user.setSignature(signature);
			user.setProfile(profile);
			user.setShowImg(profile);
			// user.setStatus("1");
			exeRes = uf.addUser(user);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
			} else {
				ut.commit();
			}
		} catch (Exception e) {
			try {
				logger.error("[添加用户 " + nickName + " 失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			uf.release();
		}
		return exeRes;

	}

	public static User getUser(String userId) {
		User user = null;
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule userModule = new UserModule();
		try {
			ut.begin();
			user = userModule.getUser(userId);
			ut.commit();
		} catch (Exception e) {
			logger.error("[获取用户" + userId + "失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("[获取用户" + userId + "信息回滚事务错误]", e1);
			}
		} finally {
			userModule.release();
		}
		return user;
	}

	public static List<User> getSynUser(String synId, int maxCnt) {
		List<User> list = null;
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule userModule = new UserModule();
		try {
			ut.begin();
			list = userModule.getSynUser(synId, maxCnt);
			ut.commit();
		} catch (Exception e) {
			logger.error("[获取待同步用户数据失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("[获取待同步用户数据回滚事务错误]", e1);
			}
		} finally {
			userModule.release();
		}
		return list;
	}

	public static ArrayList<String> getPid(String status) {
		ArrayList<String> list = new ArrayList<String>();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		try {
			ut.begin();
			list = um.getPid(status);
			ut.commit();
		} catch (Exception e) {
			logger.error("[获取号池号段失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("[获取号池号段数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return list;

	}

	/**
	 * 修改号段状态
	 * 
	 * @param id
	 * @param status
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月8日上午12:06:01
	 */
	public static String updatePIDStatus(int id, String status) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		try {
			ut.begin();
			String exeCode = um.updatePIDStatus(id, status);
			ut.commit();
			return exeCode;
		} catch (Exception e) {
			logger.error("[修改号段状态失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改号段状态数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return Constant.OperaterStatus.FAILED.getValue();

	}

	/**
	 * 关注用户
	 * 
	 * @param userId
	 * @param starId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月9日上午12:24:39
	 */
	public static String subscribe(String userId, String starId) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		try {
			ut.begin();
			String exeCode = um.subscribe(userId, starId);
			ut.commit();
			return exeCode;
		} catch (Exception e) {
			logger.error("[关注用户失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("关注用户数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	/**
	 * 取消关注
	 * 
	 * @param userId
	 * @param starId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月10日下午5:17:46
	 */
	public static String removeSubscribe(String userId, String starId) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		try {
			ut.begin();
			String exeCode = um.removeSubscribe(userId, starId);
			ut.commit();
			return exeCode;
		} catch (Exception e) {
			logger.error("[取消关注用户失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("取消关注数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	public static ArrayList<String> getSynScribe(String synId, int maxCnt) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		ArrayList<String> list = new ArrayList<String>();
		try {
			ut.begin();
			list = um.getSynScribe(synId, maxCnt);
			ut.commit();
			return list;
		} catch (Exception e) {
			logger.error("[获取订阅同步数据失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("获取订阅同步数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return null;
	}

	public static ArrayList<String> getSynFans(String synId, int maxCnt) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		ArrayList<String> list = new ArrayList<String>();
		try {
			ut.begin();
			list = um.getSynFans(synId, maxCnt);
			ut.commit();
			return list;
		} catch (Exception e) {
			logger.error("[获取粉丝同步数据失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("获取粉丝同步数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return null;
	}

	/**
	 * 修改用户图像
	 * 
	 * @param userId
	 * @param url
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月9日下午1:28:36
	 */
	public static String updateProfile(String userId, String url) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		String exeCode = "";
		try {
			ut.begin();
			exeCode = um.updateProfile(userId, url);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
				ut.rollback();
				return exeCode;
			}
			User u = UserManager.getUser(userId);
			// 如果直播封面为空，则默认为用户图像
			if (Validator.isEmpty(u.getShowImg())) {
				exeCode = um.updateShowImg(userId, url);
				if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
					ut.rollback();
					return exeCode;
				}
			}
			ut.commit();
		} catch (Exception e) {
			logger.error("[修改用户图像数据失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改用户图像数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;

	}

	public static String updateShowImg(String userId, String url) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		String exeCode = "";
		try {
			ut.begin();
			exeCode = um.updateShowImg(userId, url);
			ut.commit();
		} catch (Exception e) {
			logger.error("[修改直播封面失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改直播封面回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;

	}

	/**
	 * 修改密码
	 * 
	 * @param userId
	 * @param newPasswd
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月9日下午2:56:36
	 */
	public static String updatePasswd(String userId, String newPasswd) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		String exeCode = "";
		try {
			ut.begin();
			exeCode = um.updatePasswd(userId, newPasswd);
			ut.commit();
		} catch (Exception e) {
			logger.error("[修改密码失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改密码数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;
	}

	/**
	 * 修改性别
	 * 
	 * @param userId
	 * @param sex
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月18日下午6:17:15
	 */
	public static String updateSex(String userId, String sex) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		String exeCode = "";
		try {
			ut.begin();
			exeCode = um.updateSex(userId, sex);
			ut.commit();
		} catch (Exception e) {
			logger.error("[修改性别失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改性别数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;
	}

	public static String updateNickName(String userId, String nickName) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		String exeCode = "";
		try {
			ut.begin();
			exeCode = um.updateNickName(userId, nickName);
			ut.commit();
		} catch (Exception e) {
			logger.error("[修改昵称失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改昵称数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;
	}

	/**
	 * 修改签名
	 * 
	 * @param userId
	 * @param signature
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月9日下午2:59:38
	 */
	public static String updateSignature(String userId, String signature) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		String exeCode = "";
		try {
			ut.begin();
			exeCode = um.updateSignature(userId, signature);
			ut.commit();
		} catch (Exception e) {
			logger.error("[修改签名失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改签名数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;
	}

	public String updatePositon(String userId, String position) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		String exeCode = "";
		try {
			ut.begin();
			exeCode = um.updatePositon(userId, position);
			ut.commit();
		} catch (Exception e) {
			logger.error("[修改个人位置失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改位置回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;

	}

	/**
	 * 获取需要同步的用户推送设备
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午3:14:15
	 */
	public static ArrayList<String> getSynUserDevice(String synId, int maxCnt) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		ArrayList<String> list = new ArrayList<String>();
		try {
			ut.begin();
			list = um.getSynUserIdDevice(synId, maxCnt);
			ut.commit();
		} catch (Exception e) {
			logger.error("[获取订阅同步数据失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("获取订阅同步数据回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return list;
	}

	/**
	 * 修改用户推送设备信息
	 * 
	 * @param userId
	 * @param tokenId
	 * @param deviceNo
	 * @param deviceType
	 * @param provider
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午3:07:20
	 */
	public static String updateUserDevice(String userId, String tokenId, String deviceNo, String deviceType, String provider) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		String exeCode = "";
		try {
			ut.begin();
			exeCode = um.updateUserDevice(userId, tokenId, deviceNo, deviceType, provider);
			ut.commit();
		} catch (Exception e) {
			logger.error("[修改用户推送设备失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改用户推送设备回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;

	}

	/**
	 * 修改用户状态
	 * 
	 * @param userId
	 * @param isActive 0 为冻结 1为正常
	 * @param isAuthen 1为认证
	 * @param isForbidShow 1为禁播
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午3:07:20
	 */
	public static String updateStatus(String userId, String isActive, String isAuthen, String isForbidShow) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		String exeCode = "";
		try {
			ut.begin();
			User u = UserManager.getUser(userId);
			if (Validator.isEmpty(isActive)) {
				isActive = u.getIsActive();
			}
			if (Validator.isEmpty(isAuthen)) {
				isAuthen = u.getIsAuthen();
			}
			if (Validator.isEmpty(isForbidShow)) {
				isForbidShow = u.getIsForbidShow();
			}
			exeCode = um.updateStatus(userId, isActive, isAuthen, isForbidShow);
			ut.commit();
		} catch (Exception e) {
			logger.error("[修改用户状态失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改用户状态回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;
	}

	/**
	 * 添加用户认证信息
	 * 
	 * @param userId
	 * @param name
	 * @param phone
	 * @param cardType
	 * @param cardId
	 * @param bankId
	 * @param bankName
	 * @param bankProvince
	 * @param bankCity
	 * @param bankAddress
	 * @param frontUrl
	 * @param backUrl
	 * @param holdUrl
	 * @param value
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月1日下午7:59:32
	 */
	public static String addUserAuth(String userId, String name, String phone, String cardType, String cardId, String bankId, String bankName, String bankProvince, String bankCity, String bankAddress, String frontUrl, String backUrl, String holdUrl) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		String exeCode = Constant.OperaterStatus.FAILED.getValue();
		try {
			ut.begin();
			List<UserAuth> userAuthlist = um.getUserAuthList(userId, null);
			UserAuth userAuth = new UserAuth();
			userAuth.setUserId(userId);
			userAuth.setName(name);
			userAuth.setPhone(phone);
			userAuth.setCardType(cardType);
			userAuth.setCardId(cardId);
			userAuth.setBankId(bankId);
			userAuth.setBankName(bankName);
			userAuth.setBankProvince(bankProvince);
			userAuth.setBankCity(bankCity);
			userAuth.setBankAddress(bankAddress);
			userAuth.setFrontImg(frontUrl);
			userAuth.setBackImg(backUrl);
			userAuth.setHoldImg(holdUrl);
			userAuth.setStatus(Constant.CertifyProcess.PROCESS.getValue());
			if (!Validator.isEmpty(userAuthlist)) {
				exeCode = um.updateUserAuth(userAuth);
			} else {
				exeCode = um.addUserAuth(userAuth);
			}
			if (!exeCode.equals(Constant.OperaterStatus.SUCESSED.getValue())) {
				return exeCode;
			}
			exeCode = um.updateUserAuthStatus(userId, Constant.CertifyProcess.PROCESS.getValue());
			if (!exeCode.equals(Constant.OperaterStatus.SUCESSED.getValue())) {
				ut.rollback();
				return exeCode;
			}
			ut.commit();
		} catch (Exception e) {
			logger.error("[添加用户审核失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("添加用户审核错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;
	}

}
