/*  ------------------------------------------------------------------------------ 
 *                  软件名称:
 *                  公司名称:
 *                  开发作者:ZuKang.Song
 *       			开发时间:2016年5月31日/2016
 *    				All Rights Reserved 2016-2016
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.cache.mgr
 *                  fileName：CacheHandle.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.handler.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.ace.entity.User;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.ace.globalData.OutMessageManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.ace.syn.UserViewTime;
import com.rednovo.tools.Validator;

/**
 * @author ZuKang.Song/2016年5月31日
 */
public class CacheHandle extends BasicServiceAdapter {
	Logger logger = Logger.getLogger(CacheHandle.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rednovo.ace.handler.BasicServiceAdapter#service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if ("001-001".equals(key)) {// 发送各种公屏消息
			this.messageSender();
		} else if ("001-002".equals(key)) {// 冻结用户
			logger.info("[002-021冻结用户]");
			this.freezeUser();
		} else if ("001-003".equals(key)) {// 禁播下播主播
			logger.info("[002-020禁播主播]");
			this.forbidShower();
		} else if ("001-004".equals(key)) {// 禁播下播主播
			logger.info("[获取每天在线时长（毫秒）和分享数]");
			this.getTimeAndShare();
		}
	}

	/**
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月29日下午5:15:04
	 */
	private void getTimeAndShare() {
		Map<String, String> map = UserManager.getAllTimeAndShare();
		this.setValue("TimeAndShareMap", map);
	}

	/**
	 * 公屏消息type 1.警告 2.分享 3.点赞 4.系统公告 5.关注
	 * 
	 * @author ZuKang.Song
	 * @since 2016年5月31日下午5:20:55
	 */
	private void messageSender() {
		Message message = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("002-018");
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setChatMode(ChatMode.GROUP.getValue());
		sumy.setMsgId("9999-messageSender");
		sumy.setSenderId(Constant.SysUser.SERVER.getValue());
		String type = this.getWebHelper().getString("type");
		logger.info("[CacheHandle_messageSender]_type" + type);
		JSONObject obj = new JSONObject();
		obj.put("type", type);
		if ("1".equals(type)) {
			logger.info("[CacheHandle_messageSender_发送警告通知]");
			String userId = this.getWebHelper().getString("userId");
			String msg = this.getWebHelper().getString("msg");
			sumy.setChatMode(ChatMode.PRIVATE.getValue());
			obj.put("msg", msg);

			sumy.setReceiverId(userId);

		} else if ("2".equals(type)) {
			logger.info("[CacheHandle_messageSender_发送分享通知]");
			String userId = this.getWebHelper().getString("userId");
			String showId = this.getWebHelper().getString("showId");
			// String channel = this.getWebHelper().getString("channel");
			User u = UserManager.getUser(userId);
			// obj.put("channel", channel);
			obj.put("userId", userId);
			obj.put("nickName", u.getNickName());
			obj.put("profile", u.getProfile());
			sumy.setSenderId(userId);
			sumy.setShowId(showId);
			sumy.setReceiverId(showId);
			// 分享一次30经验，每天上限三次
			String exeData = UserManager.getTimeAndShare(userId);
			if (Validator.isEmpty(exeData)) {
				exeData = "0_1";
			} else {
				String length = exeData.split("_")[0];
				int share = Integer.parseInt(exeData.split("_")[1]);
				share = share + 1;
				exeData = length + "_" + share;
			}
			UserManager.addTimeAndShare(userId, exeData);
			System.out.println(userId + ":" + exeData.split("_")[1]);

		} else if ("4".equals(type)) {
			logger.info("[CacheHandle_messageSender_发送系统通知]");
			// String userId = this.getWebHelper().getString("userId");
			String msg = this.getWebHelper().getString("msg");
			List<String> showidlist = LiveShowManager.getSortList(1, 10000);
			for (String showid : showidlist) {
				sumy.setShowId(showid);
				obj.put("msg", msg);
				obj.put("userId", showid);
				message.setSumy(sumy);
				message.setBody(obj);
				// 将消息压入缓存中
				OutMessageManager.addMessage(message);
			}

		} else if ("5".equals(type)) {
			logger.info("[CacheHandle_messageSender_发送关注通知]");
			String userId = this.getWebHelper().getString("userId");
			String showId = this.getWebHelper().getString("showId");
			sumy.setSenderId(userId);
			sumy.setShowId(showId);
			sumy.setReceiverId(showId);
			User u = UserManager.getUser(userId);
			obj.put("type", "5");
			obj.put("userId", userId);
			obj.put("nickName", u.getNickName());
			obj.put("profile", u.getProfile());
		} else if ("6".equals(type)) {
			logger.info("[CacheHandle_messageSender_发送升级通知]");
			String userId = this.getWebHelper().getString("userId");
			String showId = this.getWebHelper().getString("showId");
			String grade = this.getWebHelper().getString("grade");
			sumy.setSenderId(userId);
			sumy.setShowId(showId);
			sumy.setReceiverId(showId);
			User u = UserManager.getUser(userId);
			obj.put("type", "5");
			obj.put("userId", userId);
			obj.put("grade", grade);
			obj.put("nickName", u.getNickName());
			obj.put("profile", u.getProfile());
		}
		message.setSumy(sumy);
		message.setBody(obj);
		logger.info("[CacheHandle_messageSender_发送通知]" + JSON.toJSONString(message));
		// 将消息压入缓存中
		if (!"4".equals(type)) {
			OutMessageManager.addMessage(message);
		}
		this.setSuccess();
	}

	/**
	 * type 0.禁播 1.下播
	 * 
	 * @author ZuKang.Song
	 * @since 2016年5月31日下午3:38:47
	 */
	private void forbidShower() {
		String userId = this.getWebHelper().getString("userId");
		String type = this.getWebHelper().getString("type");
		Message msg = new Message();
		Summary smy = new Summary();
		smy.setChatMode(ChatMode.PRIVATE.getValue());
		smy.setInteractMode(InteractMode.REQUEST.getValue());
		smy.setMsgId("9999-forbidShower");
		smy.setMsgType(MsgType.TXT_MSG.getValue());
		smy.setReceiverId(userId);
		smy.setReceiverName(userId);
		smy.setRequestKey("002-020");
		smy.setShowId(userId);
		smy.setSenderId(userId);
		JSONObject jo = new JSONObject();
		jo.put("userId", userId);
		jo.put("type", type);
		msg.setBody(jo);
		msg.setSumy(smy);
		// 将消息压入缓存中
		OutMessageManager.addMessage(msg);
		this.setSuccess();
	}

	/**
	 * 冻结用户
	 * 
	 * @author ZuKang.Song
	 * @since 2016年5月31日下午3:34:25
	 */
	private void freezeUser() {
		String userId = this.getWebHelper().getString("userId");
		Message msg = new Message();
		Summary smy = new Summary();
		smy.setChatMode(ChatMode.PRIVATE.getValue());
		smy.setInteractMode(InteractMode.REQUEST.getValue());
		smy.setMsgId("9999-freezeUser");
		smy.setMsgType(MsgType.TXT_MSG.getValue());
		smy.setReceiverId(userId);
		smy.setReceiverName("");
		smy.setRequestKey("002-021");
		smy.setSenderId(userId);
		smy.setShowId(userId);
		JSONObject jo = new JSONObject();
		jo.put("userId", userId);
		msg.setBody(jo);
		msg.setSumy(smy);
		// 将消息压入缓存中
		OutMessageManager.addMessage(msg);
		this.setSuccess();
	}

}
