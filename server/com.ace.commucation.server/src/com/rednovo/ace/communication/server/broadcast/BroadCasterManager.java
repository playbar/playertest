/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年2月25日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：server
 *                  fileName：BroadCasterManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server.broadcast;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.communication.server.BlockMessageListener;
import com.rednovo.ace.communication.server.ServerHelpler;
import com.rednovo.ace.communication.server.ServerWorkerPool;
import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Server;
import com.rednovo.ace.entity.Summary;
import com.rednovo.ace.globalData.ServerRoutManager;
import com.rednovo.tools.KeyGenerator;
import com.rednovo.tools.Validator;

/**
 * @author yongchao.Yang/2016年2月25日
 */
public class BroadCasterManager {
	private static BroadCasterManager cm;
	private HashMap<String, BroadCaster> serverIdAndBroadCaster = new HashMap<String, BroadCaster>();
	private HashMap<String, String> severIdAndSessonId = new HashMap<String, String>();// serverId和sessionId的映射关系
	private HashMap<String, String> clientIdAndSessonId = new HashMap<String, String>();// serverId和sessionId的映射关系
	private ServerWorkerPool swp = ServerWorkerPool.getInstance();
	private Logger logger = Logger.getLogger(BroadCasterManager.class);

	public static synchronized BroadCasterManager getInstance() {
		if (cm == null) {
			cm = new BroadCasterManager();
		}
		return cm;
	}

	/**
	 * 
	 */
	private BroadCasterManager() {

		// 启动同步消息应答监听
		BlockMessageListener.getInstance().start();

	}

	public void addBroadCaster(String serverId, BroadCaster broadCaster) {
		serverIdAndBroadCaster.put(serverId, broadCaster);
		try {
			broadCaster.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BroadCaster getBroadCaster(String serverId) {
		return serverIdAndBroadCaster.get(serverId);
	}

	/**
	 * 验证指定ID服务器是否已经注册
	 * 
	 * @param serverId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年2月29日上午11:35:10
	 */
	public boolean isServerExist(String serverId) {
		return ServerRoutManager.getBroadServer(ServerHelpler.getLocalHost().getId()).contains(serverId);
	}

	/**
	 * 设置广播服务各项参数。
	 * 
	 * @param key
	 * @author Yongchao.Yang
	 * @since 2016年3月1日上午9:36:30
	 */
	public void setBroadCaster(Server server, SelectionKey key) {
		// 创建读写线程
		swp.registChannel(key);
		// sessionId和serverId的绑定
		bindServer(server.getId(), key.attachment().toString());
		// 给BroadCaster设置sessionId
		this.getBroadCaster(server.getId()).setSessionId(key.attachment().toString());
		// 设置BroadCaster的运行状态
		this.getBroadCaster(server.getId()).setStauts(true);// 设置会话为链接状态
	}

	public void bindServer(String serverId, String sessionId) {
		logger.info("[广播][绑定服务端][" + serverId + "," + sessionId + "]");
		ServerRoutManager.addBroadServer(ServerHelpler.getLocalHost().getId(), serverId);
		this.severIdAndSessonId.put(serverId, sessionId);
	}

	public void registClient(String clientId, String sessionId) {
		logger.info("[广播][注册客户端][" + clientId + "," + sessionId + "]");
		ServerRoutManager.addBroadClient(ServerHelpler.getLocalHost().getId(), clientId);
		this.clientIdAndSessonId.put(clientId, sessionId);
	}

	/**
	 * 关闭指定对指定服务器的广播服务
	 * 
	 * @param serverId
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2016年3月1日下午8:22:13
	 */
	public void closeBroadCaster(String serverId) throws Exception {
		logger.info("[BroadCasterManager][关闭 " + serverId + "广播服务]");
		if (this.serverIdAndBroadCaster.get(serverId) != null) {
			this.serverIdAndBroadCaster.get(serverId).close();
		}
		this.serverIdAndBroadCaster.remove(serverId);
	}

	/**
	 * 获取指定服务器的sessionId
	 * 
	 * @param serverId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月1日下午4:50:19
	 */
	public String getSessionId(String serverId) {
		logger.info("[BroadCasterManager][转发服务器ID:" + serverId + "]");
		if (!Validator.isEmpty(this.severIdAndSessonId.get(serverId))) {
			return this.severIdAndSessonId.get(serverId);
		} else if (!Validator.isEmpty(this.clientIdAndSessonId.get(serverId))) {
			return this.clientIdAndSessonId.get(serverId);
		} else {
			logger.error("中转消息异常.找不到服务" + serverId + "会话信息");
			return null;
		}

	}

	/**
	 * 打开指定server广播服务
	 * 
	 * @param serverId
	 * @author Yongchao.Yang
	 * @since 2016年3月1日下午8:29:07
	 */
	public void openBroadCaster(String serverId) {
		logger.info("[BroadCasterManager][打开 " + serverId + "广播服务]");
		Server s = ServerRoutManager.getServer(serverId);
		BroadCaster bc = new BroadCaster(s);
		this.addBroadCaster(serverId, bc);
		bc.broadCast(sayHello());
	}

	/**
	 * 
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月1日上午8:19:10
	 */
	public void createBroadCaster() {
		Set<String> sid = ServerRoutManager.getBroadServer(ServerHelpler.getLocalHost().getId());
		Set<String> cid = ServerRoutManager.getBroadClient(ServerHelpler.getLocalHost().getId());
		// 只要有建立了链接关系，就按照老链接关系运行，避免服务器建立不不要的联基金
		if (!Validator.isEmpty(sid) || !Validator.isEmpty(cid)) {
			return;
		}
		ArrayList<Server> servers = ServerHelpler.getServerList();
		for (Server server : servers) {
			// 忽略本机
			if (this.isServerExist(server.getId()) || ServerHelpler.getLocalHost().getId().equals(server.getId())) {
				continue;
			}
			logger.info("[BroadCasterManager][连接 SessionServer ][" + server.getId() + "," + server.getIp() + "]");
			BroadCaster bc = new BroadCaster(server);
			this.addBroadCaster(server.getId(), bc);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("[BroadCasterManager][Say Hello]");
			bc.broadCast(sayHello());

		}
	}

	/**
	 * 获取本机所有关联的serverId
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月1日下午6:27:31
	 */
	public Iterator<String> getServers() {
		Set<String> servers = ServerRoutManager.getBroadServer(ServerHelpler.getLocalHost().getId());
		Set<String> clients = ServerRoutManager.getBroadClient(ServerHelpler.getLocalHost().getId());

		logger.info("---------------------DEBUG-------------------");
		logger.info("[BroadCasterManager][本机ID:" + ServerHelpler.getLocalHost().getId() + ",本机IP:" + ServerHelpler.getLocalHost().getIp() + "]");
		logger.info("[BroadCasterManager][广播服务端:" + servers.toString() + "]");
		logger.info("[BroadCasterManager][广播客户端端:" + clients.toString() + "]");
		logger.info("---------------------DEBUG-------------------");
		return servers.iterator();
	}

	private Message sayHello() {
		Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("009-003");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		msg.setSumy(sumy);

		JSONObject obj = new JSONObject();
		obj.put("serverId", ServerHelpler.getLocalHost().getId());
		msg.setBody(obj);

		return msg;

	}
}
