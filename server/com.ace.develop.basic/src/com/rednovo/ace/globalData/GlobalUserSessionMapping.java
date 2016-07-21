/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年2月26日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：server
 *                  fileName：GlobalUserSessionMapping.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.globalData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.RedisConfig.CacheName;
import com.rednovo.tools.RedisService;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * 服务器会话管理
 * 
 * @author yongchao.Yang/2016年2月26日
 */
public class GlobalUserSessionMapping {

	private static String SESSION_SERVER_MAP = "session_server_map";// 会话同服务器的映射关系,便于快速定位会话所在服务器
	private static String SERVER_SESSION_LIST = "server_session_list";// 每台服务器的会话明细
	private static String USER_SESSION_MAP = "user_session_map";// 用户痛会话映射关系,便于私聊
	private static String SESSION_USER_MAP = "session_user_map";// 会话同用户的映射，便于获取直播的在线人员信息

	private static Logger logger = Logger.getLogger(GlobalUserSessionMapping.class);

	/**
	 * 新建会话-服务映射
	 * 
	 * @param sessionId
	 * @param serverId
	 * @author Yongchao.Yang
	 * @since 2016年2月26日上午9:34:03
	 */
	public static void addSessionServer(String sessionId, String serverId) {
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).addMap(SESSION_SERVER_MAP, sessionId, serverId);
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).addList(SERVER_SESSION_LIST + "_" + serverId, sessionId);
	}

	public static List<String> getServerSession(String severId) {
		return RedisService.getServer(CacheName.SERVER_DATA.getNode()).getList(SERVER_SESSION_LIST + "_" + severId);
	}

	public static void removeServerSession(String serverId, String sessionId) {
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).removeList(SERVER_SESSION_LIST + "_" + serverId, sessionId, true);
	}

	/**
	 * 新建用户-会话映射
	 * 
	 * @param sessionId
	 * @param userId
	 * @author Yongchao.Yang
	 * @since 2016年2月27日下午12:09:46
	 */
	public static void addUserSession(String userId, String sessionId) {
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).addMap(USER_SESSION_MAP, userId, sessionId);
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).addMap(SESSION_USER_MAP, sessionId, userId);
	}

	/**
	 * 对过期session的处理
	 * 
	 * @param sessionId
	 * @author Yongchao.Yang
	 * @since 2016年3月1日下午9:01:05
	 */
	public static void expireSession(String sessionId) {

		String showId = LiveShowManager.getSessionShow(sessionId);
		String userId = getSessionUser(sessionId);
		String serverId = getSessionServer(sessionId);
		logger.info("[GlobalUserSessionMapping][回收 session-server 映射关系 sessionId:"+sessionId+",serverId:"+serverId+"]");
		// session-server
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).delMapKey(SESSION_SERVER_MAP, sessionId);
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).removeList(SERVER_SESSION_LIST + "_" + serverId, sessionId, true);

		logger.info("[GlobalUserSessionMapping][回收 session-show 映射关系]");
		// session-show
		LiveShowManager.delSessionShow(sessionId);
		LiveShowManager.delMember(showId, sessionId);

		logger.info("[GlobalUserSessionMapping][回收 session-user 映射关系]");
		// session-userId
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).delMapKey(SESSION_USER_MAP, sessionId);

		// 如果userId绑定的sessionId过期，则同时删除sessoin和user的映射
		if (!Validator.isEmpty(userId) && sessionId.equals(getUserSession(userId))) {
			RedisService.getServer(CacheName.SERVER_DATA.getNode()).delMapKey(USER_SESSION_MAP, userId);
		}

		// logger.info("[GlobalUserSessionMapping][回收 会话-用户 映射关系]");

	}

	/**
	 * 结束此会话可能对应的直播信息
	 * 
	 * @param sessionId
	 * @author Yongchao.Yang
	 * @since 2016年3月20日上午10:06:31
	 */
	public static void finishPossibleShow(String sessionId) {

		String userId = GlobalUserSessionMapping.getSessionUser(sessionId);
		if (Validator.isEmpty(userId)) {
			return;
		}

		String realSessionId = GlobalUserSessionMapping.getUserSession(userId);
		// 当前会话和用户为唯一映射,则可以删除直播数据
		if (!sessionId.equals(realSessionId)) {
			return;
		}

		String showId = LiveShowManager.getSessionShow(sessionId);
		if (!Validator.isEmpty(showId)) {
			LiveShowManager.delMember(userId, sessionId);
			// 如果该用户正在直播，则需要清除直播间的数据
			if (showId.equals(userId)) {
				logger.info("[GlobalUserSessionMapping][回收  " + UserManager.getUser(userId).getNickName() + " 直播数据]");
				// 保存直播数据到数据库
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("showId", showId);
				String res = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/002-014", params);

				JSONObject response = JSON.parseObject(res);
				if (Constant.OperaterStatus.SUCESSED.getValue().equals(response.getString("exeStatus"))) {
					// 从缓存中删除直播列表
					LiveShowManager.removeSortShow(showId);
					LiveShowManager.clearRobotCnt(showId);
				}
			}

		}

	}

	/**
	 * 获取会话所在服务ID
	 * 
	 * @param sessionId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年2月26日上午9:33:49
	 */
	public static String getSessionServer(String sessionId) {
		return RedisService.getServer(CacheName.SERVER_DATA.getNode()).getMapValue(SESSION_SERVER_MAP, sessionId);
	}

	/**
	 * 获取所有会话信息
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月19日上午12:56:55
	 */
	public static Iterator<String> getAllSession() {
		return RedisService.getServer(CacheName.SERVER_DATA.getNode()).getMapKeys(SESSION_SERVER_MAP).iterator();
	}

	/**
	 * 通过userId获取sessionId
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年2月27日下午12:23:54
	 */
	public static String getUserSession(String userId) {
		return RedisService.getServer(CacheName.SERVER_DATA.getNode()).getMapValue(USER_SESSION_MAP, userId);
	}

	/**
	 * 通过sessionId获取userId
	 * 
	 * @param sessionId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年2月27日下午5:32:19
	 */
	public static String getSessionUser(String sessionId) {
		return RedisService.getServer(CacheName.SERVER_DATA.getNode()).getMapValue(SESSION_USER_MAP, sessionId);
	}

}
