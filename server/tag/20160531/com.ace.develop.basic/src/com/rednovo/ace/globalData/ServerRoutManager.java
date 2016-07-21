/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月24日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.develop.basic
 *                  fileName：GlobalServerMapping.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.globalData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.entity.Server;
import com.rednovo.tools.RedisConfig.CacheName;
import com.rednovo.tools.RedisService;

/**
 * @author yongchao.Yang/2016年3月24日
 */
public class ServerRoutManager {

	private static String SERVER_MAP = "server_map";// 在线服务器
	/**
	 * 本机广播服务客户端列表
	 */
	private static String BROAD_CLIENT_SET = "broad_client_set";
	/**
	 * 本机服务服务器端列表
	 */
	private static String BROAD_SERVER_SET = "broad_server_set";

	/**
	 * 
	 */
	public ServerRoutManager() {}

	public static void addBroadClient(String localId, String clientId) {
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).addSet(BROAD_CLIENT_SET + "_" + localId, clientId);
	}

	public static void addBroadServer(String localId, String serverId) {
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).addSet(BROAD_SERVER_SET + "_" + localId, serverId);
	}

	public static Set<String> getBroadClient(String localId) {
		return RedisService.getServer(CacheName.SERVER_DATA.getNode()).getSet(BROAD_CLIENT_SET + "_" + localId);
	}

	public static Set<String> getBroadServer(String localId) {
		return RedisService.getServer(CacheName.SERVER_DATA.getNode()).getSet(BROAD_SERVER_SET + "_" + localId);
	}

	/**
	 * 添加在线服务器
	 * 
	 * @param server
	 * @author Yongchao.Yang
	 * @since 2016年2月29日上午10:51:15
	 */
	public static void registServer(Server server) {
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).addMap(SERVER_MAP, server.getId(), JSON.toJSONString(server));
	}

	/**
	 * 下线服务器
	 * 
	 * @param serverId
	 * @author Yongchao.Yang
	 * @since 2016年5月26日下午3:59:11
	 */
	public static void offServer(String serverId) {
		RedisService.getServer(CacheName.SERVER_DATA.getNode()).delMapKey(SERVER_MAP, serverId);
	}

	/**
	 * 获取在线服务器
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年2月29日上午10:53:17
	 */
	public static ArrayList<Server> getOnlineServer() {
		ArrayList<Server> serverList = new ArrayList<Server>();
		List<String> list = RedisService.getServer(CacheName.SERVER_DATA.getNode()).getMapValues(SERVER_MAP);
		for (String str : list) {
			serverList.add(JSON.parseObject(str, Server.class));
		}
		return serverList;
	}

	public static Server getServer(String serverId) {
		String str = RedisService.getServer(CacheName.SERVER_DATA.getNode()).getMapValue(SERVER_MAP, serverId);
		return JSON.parseObject(str, Server.class);
	}

}
