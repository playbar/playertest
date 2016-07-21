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
 *                  fileName：UserRelationManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.globalData;

import java.util.List;

import com.rednovo.tools.RedisConfig.CacheName;
import com.rednovo.tools.RedisService;

/**
 * 用户好友管理
 * 
 * @author yongchao.Yang/2015年4月16日
 */
public class UserRelationManager {
	/**
	 * 粉丝列表
	 */
	private static String USER_FANS_LIST = "user_fans_list";
	/**
	 * 订阅列表
	 */
	private static String USER_SUBSCRIBE_LIST = "user_subscribe_list";

	/**
	 * 添加粉丝
	 * 
	 * @param userId
	 * @param friendId
	 * @author Yongchao.Yang
	 * @since 2014年12月15日下午4:19:24
	 */
	public static void addFans(String userId, String... fansIds) {
		RedisService.getServer(CacheName.USER_FANS_DATA.getNode()).addList(USER_FANS_LIST + "_" + userId, true, fansIds);
	}

	/**
	 * 获取粉丝数
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月10日下午12:59:52
	 */
	public static long getFansCnt(String userId) {
		return RedisService.getServer(CacheName.USER_FANS_DATA.getNode()).getListSize(USER_FANS_LIST + "_" + userId);
	}

	/**
	 * 我的订阅总数
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月18日下午9:45:29
	 */
	public static long getSubscribeCnt(String userId) {
		return RedisService.getServer(CacheName.USER_FANS_DATA.getNode()).getListSize(USER_SUBSCRIBE_LIST + "_" + userId);
	}

	/**
	 * 添加订阅
	 * 
	 * @param userId
	 * @param starIds
	 * @author Yongchao.Yang
	 * @since 2016年2月27日下午1:16:58
	 */
	public static void addSubscribe(String userId, String... starIds) {
		RedisService.getServer(CacheName.USER_SUBCRIBE_DATA.getNode()).addList(USER_SUBSCRIBE_LIST + "_" + userId, true, starIds);
	}

	/**
	 * 获取粉丝列表
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年2月27日下午1:23:58
	 */
	public static List<String> getFans(String userId, int page, int pageSize) {
		if (page <= 0) {
			page = 1;
		}
		return RedisService.getServer(CacheName.USER_FANS_DATA.getNode()).getList(USER_FANS_LIST + "_" + userId, (page - 1) * pageSize, (page - 1) * pageSize + pageSize - 1);
	}

	/**
	 * 获取用户订阅列表
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年2月27日下午4:33:25
	 */
	public static List<String> getSubscribe(String userId, int page, int pageSize) {
		return RedisService.getServer(CacheName.USER_SUBCRIBE_DATA.getNode()).getList(USER_SUBSCRIBE_LIST + "_" + userId, (page - 1) * pageSize, (page - 1) * pageSize + pageSize - 1);
	}

	/**
	 * 取消粉丝
	 * 
	 * @param starId
	 * @param userId
	 * @author Yongchao.Yang
	 * @since 2016年2月27日下午4:36:19
	 */
	public static void removeFans(String starId, String userId) {
		RedisService.getServer(CacheName.USER_FANS_DATA.getNode()).removeList(USER_FANS_LIST + "_" + starId, userId, true);
	}

	/**
	 * 取消订阅
	 * 
	 * @param userId
	 * @param starId
	 * @author Yongchao.Yang
	 * @since 2016年2月27日下午4:36:00
	 */
	public static void removeSubscribe(String userId, String starId) {
		RedisService.getServer(CacheName.USER_SUBCRIBE_DATA.getNode()).removeList(USER_SUBSCRIBE_LIST + "_" + userId, starId, true);
	}
}
