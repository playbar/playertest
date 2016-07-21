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
package com.rednovo.ace.activity.globalData;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.activity.entity.User;
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
	 * 添加用户
	 * 
	 * @param user
	 * @author Yongchao.Yang
	 * @since 2014年11月6日下午3:37:57
	 */
	public static void addUser(User user) {
		RedisService.getServer(CacheName.USER_INFO_DATA.getNode()).addMap(USER_INFO_MAP, user.getUserId(), JSON.toJSONString(user));
		
	}



}
