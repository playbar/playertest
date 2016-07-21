/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2015年4月13日/2015
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.leduo.bb.imserver
 *                  fileName：UIDPoolManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.globalData;

import java.util.ArrayList;

import com.rednovo.tools.RedisConfig.CacheName;
import com.rednovo.tools.RedisService;

/**
 * @author yongchao.Yang/2015年4月13日
 */
public class UIDPoolManager {
	/**
	 * 用户号池
	 */
	private static String UID_POOL_LIST = "uid_pool_list";

	/**
	 * 向号池中加入uid
	 * 
	 * @param uid
	 * @author Yongchao.Yang
	 * @since 2015年4月13日上午11:36:35
	 */
	public static void fillPidPool(ArrayList<String> uid) {
		String[] uids = new String[uid.size()];
		uid.toArray(uids);
		RedisService.getServer(CacheName.UID_POOL.getNode()).addList(UID_POOL_LIST, uids);
	}

	/**
	 * 从号池中获取一个可用的uid
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2015年4月13日上午11:37:22
	 */
	public static String getUid() {
		return RedisService.getServer(CacheName.UID_POOL.getNode()).popListNextValue(UID_POOL_LIST);
	}

	/**
	 * 获取号池大小
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日下午11:41:48
	 */
	public static long getPoolSize() {
		return RedisService.getServer(CacheName.UID_POOL.getNode()).getListSize(UID_POOL_LIST);
	}

}
