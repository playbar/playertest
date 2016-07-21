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

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.entity.Message;
import com.rednovo.tools.RedisConfig.CacheName;
import com.rednovo.tools.RedisService;

/**
 * 外部消息缓存管理器
 * 
 * @author yongchao.Yang/2015年4月16日
 */
public class OutMessageManager {
	/**
	 * 指令列表
	 */
	private static String OUT_MESSAGE_LIST = "out_message_list";

	/**
	 * 添加外部消息
	 * 
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2016年5月23日下午12:27:51
	 */
	public static void addMessage(Message msg) {
		RedisService.getServer(CacheName.STATIC_DATA.getNode()).addList(OUT_MESSAGE_LIST, JSON.toJSONString(msg));
	}

	/**
	 * 获取所有待发消息
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月23日下午12:28:52
	 */
	public static List<String> removeMessage() {
		return RedisService.getServer(CacheName.STATIC_DATA.getNode()).popList(OUT_MESSAGE_LIST);
	}

}
