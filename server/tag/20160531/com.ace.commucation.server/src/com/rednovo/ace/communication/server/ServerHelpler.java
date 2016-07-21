/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月30日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：ServerHelpler.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server;

import java.util.ArrayList;

import com.rednovo.ace.entity.Server;
import com.rednovo.ace.globalData.ServerRoutManager;
import com.rednovo.tools.PPConfiguration;

/**
 * 服务器负载均衡管理
 * 
 * @author yongchao.Yang/2014年10月30日
 */
public class ServerHelpler {
	public static Server getLocalHost() {
		String ip = PPConfiguration.getProperties("cfg.properties").getString("server.ip");
		String id = PPConfiguration.getProperties("cfg.properties").getString("server.id");
		int port = PPConfiguration.getProperties("cfg.properties").getInt("server.port");
		Server s = new Server();
		s.setId(id);
		s.setIp(ip);
		s.setPort(port);

		return s;
	}

	/**
	 * 获取在线服务器列表
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年2月29日上午8:49:33
	 */
	public static ArrayList<Server> getServerList() {
		ArrayList<Server> list = ServerRoutManager.getOnlineServer();

		return list;
	}

}
