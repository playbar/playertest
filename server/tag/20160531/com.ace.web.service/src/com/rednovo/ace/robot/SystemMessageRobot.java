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
import com.rednovo.ace.constant.Constant.SysUser;
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
public class SystemMessageRobot extends Thread {
	private volatile boolean isRun = true;
	private Logger logger = Logger.getLogger(SystemMessageRobot.class);

	/**
	 * @param name
	 */
	public SystemMessageRobot(String name) {
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
				for (String sid : liveShows) {

					// 消息提醒
					logger.info("[RobotController][压入警告消息]");
					Message privateMessage = createWarnMessage(sid, "禁止直播喝酒，这段话只有主播能看到");
					OutMessageManager.addMessage(privateMessage);

					Message publicMesage = createPublicMessage("4", "999999", sid, "", "房间内杜绝脏话和违禁词语，请大家自觉遵守");
					OutMessageManager.addMessage(publicMesage);

				}
				synchronized (this) {
					this.wait(new Random().nextInt(100) * 1000);
				}

			} catch (Exception e) {
				logger.error("机器人控制线程出现异常", e);
			}
		}
	}

	private Message createPublicMessage(String type, String userId, String showId, String cnt, String tips) {
		Message msg = new Message();
		// 进入房间
		User u = UserManager.getUser(userId);
		Summary smy = new Summary();
		smy.setChatMode(ChatMode.GROUP.getValue());
		smy.setInteractMode(InteractMode.REQUEST.getValue());
		smy.setMsgId("9999-other");
		smy.setMsgType(MsgType.TXT_MSG.getValue());
		smy.setReceiverId("");
		smy.setReceiverName("");
		smy.setRequestKey("002-018");
		smy.setSenderId(userId);
		smy.setSenderName(u.getNickName());
		smy.setShowId(showId);
		JSONObject jo = new JSONObject();

		jo.put("type", type);
		if (!Validator.isEmpty(cnt)) {
			jo.put("cnt", cnt);
		}

		if (!Validator.isEmpty(tips)) {
			jo.put("msg", tips);
		}

		jo.put("userId", userId);
		jo.put("nickName", u.getNickName());
		jo.put("profile", u.getProfile());
		jo.put("sex", Validator.isEmpty(u.getSex()) ? "0" : u.getSex());
		msg.setBody(jo);
		msg.setSumy(smy);

		return msg;
	}

	private Message createWarnMessage(String showId, String txt) {
		Message msg = new Message();
		// 进入房间
		Summary smy = new Summary();
		smy.setChatMode(ChatMode.PRIVATE.getValue());
		smy.setInteractMode(InteractMode.REQUEST.getValue());
		smy.setMsgId("9999-warning");
		smy.setMsgType(MsgType.TXT_MSG.getValue());
		smy.setReceiverId(showId);
		smy.setReceiverName("");
		smy.setRequestKey("002-018");
		smy.setSenderId(SysUser.SERVER.getValue());
		smy.setSenderName("服务器");
		smy.setShowId(showId);
		JSONObject jo = new JSONObject();

		jo.put("msg", txt);
		jo.put("type", "1");

		msg.setBody(jo);
		msg.setSumy(smy);

		return msg;
	}
}
