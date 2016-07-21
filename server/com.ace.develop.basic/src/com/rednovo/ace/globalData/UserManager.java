/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2015年4月16日/2015
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.leduo.bb.imserver
 *                  fileName：UserManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.globalData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.entity.User;
import com.rednovo.tools.RedisConfig.CacheName;
import com.rednovo.tools.RedisService;
import com.rednovo.tools.Validator;

/**
 * 用户缓存管理
 * 
 * @author yongchao.Yang/2015年4月16日
 */
public class UserManager {

	/**
	 * 用户数据
	 */

	private static String USER_INFO_MAP = "user_info_map";
	private static String USER_INFO_EXT_MAP = "user_info_exe_map";// 用户扩展信息
	private static String USER_TOKENID_MAP = "tokenId_userId_map";
	private static String MOBILE_VERIFY_CODE = "mobile_verify_code";// 手机验证码
	private static String USER_BLACKLIST_SET = "user_blacklist_set";
	private static String PUSH_USER_SET = "push_user_set";// 上播推送用户列表
	private static String USER_PUSH_DEVICE_MAP = "user_push_device_map";// 用户设备映射ID
	private static String USER_LENGTHSHARE_MAP = "user_lengthshare_map";// 有extardata的userID

	/**
	 * 账户余额
	 */
	private static String BALANCE_INFO_MAP = "balance_info_map";
	/**
	 * 兑点余额
	 */
	private static String INCOME_INFO_MAP = "income_info_map";

	/**
	 * 添加缓存用户
	 * 
	 * @param user
	 * @author Yongchao.Yang
	 * @since 2014年11月6日下午3:30:07
	 */
	public static void updateUser(User user) {
		addUser(user);
	}

	public static void updataBalance(String userId, String balance) {
		addBalance(userId, balance);
	}

	public static void updateIncome(String userId, String balance) {
		addIncome(userId, balance);
	}

	/**
	 * 获取用户
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月6日下午3:30:13
	 */
	public static User getUser(String userId) {
		RedisService rs = RedisService.getServer(CacheName.USER_INFO_DATA.getNode());
		String userInfo = rs.getMapValue(USER_INFO_MAP, userId);
		if (userInfo != null && !userInfo.equals("")) {
			return JSON.parseObject(userInfo, User.class);
		}
		return null;
	}

	/**
	 * 添加扩展字段
	 * 
	 * @param userId
	 * @param key
	 * @param Val
	 * @author Yongchao.Yang
	 * @since 2016年3月10日下午1:26:49
	 */
	public static void setExtData(String userId, String key, String Val) {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).addMap(USER_INFO_EXT_MAP + "_" + userId, key, Val);
	}

	public static String getExtData(String userId, String key) {
		return RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).getMapValue(USER_INFO_EXT_MAP + "_" + userId, key);
	}

	public static User getUserByTokenId(String tokenId) {
		String userId = RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).getMapValue(USER_TOKENID_MAP, tokenId);
		if (Validator.isEmpty(userId)) {
			return null;
		}

		return getUser(userId);
	}

	/**
	 * 获取账户余额
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日上午1:05:12
	 */
	public static String getBalance(String userId) {
		RedisService rs = RedisService.getServer(CacheName.USER_INFO_DATA.getNode());
		return rs.getMapValue(BALANCE_INFO_MAP, userId);
	}

	/**
	 * 查询用户银币
	 * 
	 * @param userId
	 * @return
	 */
	public static String getIncome(String userId) {
		RedisService rs = RedisService.getServer(CacheName.USER_INFO_DATA.getNode());
		return rs.getMapValue(INCOME_INFO_MAP, userId);
	}

	/**
	 * 获取验证码
	 * 
	 * @param mobileNo
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月15日下午10:24:44
	 */
	public static String getVerifyCode(String mobileNo) {
		return RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).getString(MOBILE_VERIFY_CODE + "_" + mobileNo);
	}

	public static void delVerifyCode(String mobileNo) {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).removeKey(MOBILE_VERIFY_CODE + "_" + mobileNo);
	}

	/**
	 * 存储验证码
	 * 
	 * @param mobielNo
	 * @param code
	 * @author Yongchao.Yang
	 * @since 2016年3月15日下午10:24:55
	 */
	public static void addVerifyCode(String mobielNo, String code) {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).addString(MOBILE_VERIFY_CODE + "_" + mobielNo, 120, code);
	}

	/**
	 * 添加用户
	 * 
	 * @param user
	 * @author Yongchao.Yang
	 * @since 2014年11月6日下午3:37:57
	 */
	public static void addUser(User user) {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).addMap(USER_INFO_MAP, user.getUserId(), JSON.toJSONString(user));
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).addMap(USER_TOKENID_MAP, user.getTokenId(), user.getUserId());
	}

	public static void addBalance(String userId, String balance) {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).addMap(BALANCE_INFO_MAP, userId, balance);
	}

	public static void addIncome(String userId, String balance) {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).addMap(INCOME_INFO_MAP, userId, balance);
	}

	/**
	 * 添加上播推送ID
	 * 
	 * @param userId
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午12:51:41
	 */
	public static void addPushStarId(String userId) {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).addSet(PUSH_USER_SET, userId);
	}

	/**
	 * 获取需要推送的主播ID，最后删除
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午12:55:53
	 */
	public static Set<String> getPushStar() {
		RedisService rs = RedisService.getServer(CacheName.USER_INFO_DATA.getNode());
		Set<String> userList = rs.getSet(PUSH_USER_SET);
		rs.removeKey(PUSH_USER_SET);
		return userList;
	}

	/**
	 * 更新用户和推送设备信息
	 * 
	 * @param userDeviceMap
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午1:08:57
	 */
	public static void updateUserDevice(HashMap<String, String> userDeviceMap) {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).addMap(USER_PUSH_DEVICE_MAP, userDeviceMap);
	}

	/**
	 * 获取用户的推送设备
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午1:09:11
	 */
	public static String getUserDevice(String userId) {
		return RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).getMapValue(USER_PUSH_DEVICE_MAP, userId);
	}

	public static void freezeUser(String userId) {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).addSet(USER_BLACKLIST_SET, userId);
	}

	public static boolean isFreeze(String userId) {

		return RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).containsSetValue(USER_BLACKLIST_SET, userId);
	}

	/**
	 * 用户每天累计的观看时长和分享数
	 * 
	 * @param userId
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月30日上午10:39:11
	 */
	public static String getTimeAndShare(String userId) {
		return RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).getMapValue(USER_LENGTHSHARE_MAP, userId);
	}

	/**
	 * @param userId
	 * @param exeData
	 * @author ZuKang.Song
	 * @since 2016年6月30日上午10:39:54
	 */
	public static void addTimeAndShare(String userId, String exeData) {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).addMap(USER_LENGTHSHARE_MAP, userId, exeData);
	}

	/**
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月30日上午11:32:39
	 */
	public static Map<String, String> getAllTimeAndShare() {
		return RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).getMap(USER_LENGTHSHARE_MAP);
	}

	/**
	 * 每天00:00清除用户的观看时长和分享数线程
	 * @author ZuKang.Song
	 * @since 2016年6月30日上午11:45:41
	 */
	public static void removeLengthShare() {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).removeKey(USER_LENGTHSHARE_MAP);
	}

}
