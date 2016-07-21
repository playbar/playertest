/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年11月18日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：ClientHeartBeatRunner.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server.broadcast;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.communication.server.ServerHelpler;
import com.rednovo.ace.communication.server.handler.MessageSender;
import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.tools.KeyGenerator;
import com.rednovo.tools.PPConfiguration;

/**
 * 心跳工作线程
 * 
 * @author yongchao.Yang/2014年11月18日
 */
public class HeartBeatRunner extends Thread {
	private volatile boolean isRun = true;
	private static HeartBeatRunner hbr;
	private int runFrequence = PPConfiguration.getProperties("cfg.properties").getInt("broadCaster.heartBeat.frequence");
	private BroadCasterManager bcm = BroadCasterManager.getInstance();
	private Logger logger = Logger.getLogger(HeartBeatRunner.class);

	/**
	 * 获取心跳线程实例，并启动该线程
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月19日下午7:00:13
	 */
	public static HeartBeatRunner getInstance() {
		if (hbr == null) {
			hbr = new HeartBeatRunner();
		}
		return hbr;
	}

	@Override
	public void run() {
		try {
			// 等待广播服务连接成功
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Message msg = new Message();
		Summary smy = new Summary();
		msg.setSumy(smy);
		smy.setSenderId("");
		smy.setChatMode(ChatMode.PRIVATE.getValue());
		smy.setMsgType(MsgType.TXT_MSG.getValue());
		smy.setInteractMode(InteractMode.REQUEST.getValue());
		smy.setRequestKey("009-003");
		JSONObject obj = new JSONObject();
		obj.put("serverId", ServerHelpler.getLocalHost().getId());
		msg.setBody(obj);
		while (isRun) {
			logger.info("[HeartBeatRunner][广播服务心跳监测 ]");
			try {
				Iterator<String> servers = bcm.getServers();
				while (servers.hasNext()) {
					String sid = servers.next();
					String ssin = bcm.getSessionId(sid);
					String msgId = KeyGenerator.createUniqueId();
					smy.setMsgId(msgId);
					// logger.info("[HeartBeatRunner][广播服务][探测" + sid + "]");
					if (MessageSender.sendBlockMsg(ssin, msg)) {
						logger.info("[HeartBeatRunner][广播服务" + sid + "连接正常]");
					} else {
						logger.info("[HeartBeatRunner][广播服务" + sid + "连接超时。准备中断,尝试重连]");
						bcm.closeBroadCaster(sid);
						bcm.openBroadCaster(sid);
					}
				}

				synchronized (hbr) {
					this.wait(runFrequence);// 等待10s
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		logger.info("[HeartBeatRunner][------>>广播服务心跳结束退出<<--------- ]");
	}

	public void stopRun() {
		this.isRun = false;
	}

	public void startRun() {
		if (!isRun) {
			isRun = true;
			hbr.start();
		}
	}
}
