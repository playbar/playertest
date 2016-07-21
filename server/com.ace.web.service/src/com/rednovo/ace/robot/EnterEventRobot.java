/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年5月23日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.commucation.server
 *                  fileName：RobotController.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.robot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.ace.entity.User;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.ace.globalData.OutMessageManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;

/**
 * 机器人控制线程
 * 
 * @author yongchao.Yang/2016年5月23日
 */
public class EnterEventRobot extends Thread {
	private volatile boolean isRun = true;
	private Logger logger = Logger.getLogger(EnterEventRobot.class);
	private static Random r = new Random();

	/**
	 * @param name
	 */
	public EnterEventRobot(String name) {
		super(name);
	}

	private static ArrayList<String> freedomUser = new ArrayList<String>();

	static {
		XMLConfiguration cf = (XMLConfiguration) PPConfiguration.getXML("robot.xml");
		List<ConfigurationNode> list = cf.getRoot().getChildren();
		for (ConfigurationNode node2 : list) {
			freedomUser.add(String.valueOf(node2.getChild(1).getValue()));
		}
	}

	@Override
	public void run() {
		logger.info("机器人线程启动完毕");
		while (isRun) {
			try {
				Collections.shuffle(freedomUser);
				List<String> liveShows = LiveShowManager.getSortList(1, 1000);
				Collections.shuffle(liveShows);
				// 进入房间
				int index = 0;
				for (String sid : liveShows) {
					// 如果房间为新开房间，则补满50人
					long cnt = LiveShowManager.getRobotCnt(sid);
					if (cnt == 0) {
						logger.info("[EnterEventRobot][发现新房间" + sid + "]");
						new NewLiveShowRobot(sid).start();
						continue;
					}

					logger.info("[EnterEventRobot][普通机器人" + sid + "]");
					Message enterInfo = createEnterMessage("1", freedomUser.get(index++), sid);
					OutMessageManager.addMessage(enterInfo);
					LiveShowManager.updateRobotCnt(sid, 1);
					synchronized (this) {
						this.wait(500);
					}

				}
				synchronized (this) {
					this.wait((r.nextInt(20) + 5) * 1000);
				}

			} catch (Exception e) {
				logger.error("机器人控制线程出现异常", e);
			}
		}
	}

	/**
	 * 构建消息类型
	 * 
	 * @param type
	 * @param showId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月23日下午3:23:47
	 */
	private Message createEnterMessage(String type, String userId, String showId) {
		Message msg = new Message();
		// 进入房间
		User u = UserManager.getUser(userId);
		Summary smy = new Summary();
		smy.setChatMode(ChatMode.GROUP.getValue());
		smy.setInteractMode(InteractMode.REQUEST.getValue());
		smy.setMsgId("9999-enter");
		smy.setMsgType(MsgType.TXT_MSG.getValue());
		smy.setReceiverId("");
		smy.setReceiverName("");
		smy.setRequestKey("002-001");
		smy.setSenderId(userId);
		smy.setSenderName(u.getNickName());
		smy.setShowId(showId);
		JSONObject jo = new JSONObject();

		jo.put("userId", userId);
		jo.put("nickName", u.getNickName());
		jo.put("profile", u.getProfile());
		jo.put("sex", Validator.isEmpty(u.getSex()) ? "0" : u.getSex());
		jo.put("channel", "4");
		msg.setBody(jo);
		msg.setSumy(smy);

		return msg;
	}

}
