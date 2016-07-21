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
public class NewLiveShowRobot extends Thread {
	private Logger logger = Logger.getLogger(NewLiveShowRobot.class);
	private String showId;
	private static ArrayList<String> freedomUser = new ArrayList<String>();

	static {
		XMLConfiguration cf = (XMLConfiguration) PPConfiguration.getXML("robot.xml");
		List<ConfigurationNode> list = cf.getRoot().getChildren();
		for (ConfigurationNode node2 : list) {
			freedomUser.add(String.valueOf(node2.getChild(1).getValue()));
		}
	}

	/**
	 * @param name
	 */
	public NewLiveShowRobot(String showId) {
		this.showId = showId;
	}

	@Override
	public void run() {
		Collections.shuffle(freedomUser);
		// 进入房间
		int index = 0;
		// 如果房间为新开房间，则补满50人
		long cnt = LiveShowManager.getRobotCnt(showId);
		if (cnt < 50) {
			for (int i = 0; i < 30 - cnt; i++) {
				logger.info("[NewLiveShowRobot][-------往新房间里压入机器人" + showId + "------------]");
				try {
					Message enterInfo = createEnterMessage("1", freedomUser.get(index++), showId);
					OutMessageManager.addMessage(enterInfo);
					LiveShowManager.updateRobotCnt(showId, 1);
					Thread.sleep(5000);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

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
		msg.setBody(jo);
		msg.setSumy(smy);

		return msg;
	}

}
