/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年11月27日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：LogicOperator.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.launch;

import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.communication.client.ClientSession;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.constant.Constant.SysUser;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.tools.KeyGenerator;

/**
 * @author yongchao.Yang/2014年11月27日
 */
public class LogicOperator {

	/**
	 * 
	 */
	public LogicOperator() {}

	/**
	 * 设置性别
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年12月18日上午10:48:07
	 */
	public static void updateSex() {
		Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("001-011");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setReceiverId(SysUser.SERVER.getValue());
		sumy.setSenderId("1417076806787");
		msg.setSumy(sumy);

		JSONObject json = new JSONObject();
		json.put("userId", "1417066439399");
		json.put("sex", "1");
		msg.setBody(json);
		ClientSession.getInstance().sendMessage(msg);
	}

	/**
	 * 进入直播间
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年2月28日下午8:39:07
	 */
	public static void enter(String userId, String showId) {
		Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("002-001");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setReceiverId(SysUser.SERVER.getValue());
		sumy.setSenderId("1417076806787");
		msg.setSumy(sumy);

		JSONObject json = new JSONObject();
		json.put("userId", userId);
		json.put("showId", showId);
		msg.setBody(json);
		ClientSession.getInstance().sendMessage(msg);
	}
	
	/**
	 * 踢人
	 * @param userId
	 * @param showId
	 * @author Yongchao.Yang
	 * @since  2016年4月24日下午11:05:56
	 */
	public static void kickMan(String userId, String showId) {
		Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("002-007");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setReceiverId(userId);
		sumy.setSenderId(Constant.SysUser.SERVER.getValue());
		msg.setSumy(sumy);

		JSONObject json = new JSONObject();
		json.put("userId", userId);
		json.put("showId", showId);
		msg.setBody(json);
		ClientSession.getInstance().sendMessage(msg);
	}

	/**
	 * 剔除房间
	 * 
	 * @param userId
	 * @param showId
	 * @author Yongchao.Yang
	 * @since 2016年4月22日上午12:44:26
	 */
	public static void endShow(String showId) {
		Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("002-006");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setReceiverId(showId);
		sumy.setSenderId(Constant.SysUser.SERVER.getValue());
		msg.setSumy(sumy);

		JSONObject json = new JSONObject();
		json.put("showId", showId);
		json.put("isForce", true);
		msg.setBody(json);
		ClientSession.getInstance().sendMessage(msg);
	}

	/**
	 * 群发消息测试
	 * 
	 * @param txtMSG
	 * @author Yongchao.Yang
	 * @since 2014年12月1日下午12:16:01
	 */
	public static void chat(String txtMSG) {

		Message msg = new Message();
		Summary sumy = new Summary();

		sumy.setRequestKey("002-009");
		sumy.setChatMode(ChatMode.GROUP.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setSenderId("4535");
		sumy.setShowId("4266");
		sumy.setSenderName("仰永潮");
		sumy.setSenderSex("1");
		sumy.setReceiverId("4266");
		sumy.setReceiverName("孙卫红");

		msg.setSumy(sumy);

		JSONObject obj = new JSONObject();
		obj.put("txt", txtMSG);
		msg.setBody(obj);
		ClientSession.getInstance().sendMessage(msg);
	}

	public static void getSysToy() {

		Message msg = new Message();
		Summary sumy = new Summary();

		sumy.setRequestKey("004-005");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setSenderId("200000");
		sumy.setShowId("001");
		sumy.setSenderName("仰永潮");
		sumy.setSenderSex("1");
		sumy.setReceiverId("200001");
		sumy.setReceiverName("孙卫红");

		JSONObject obj = new JSONObject();
		msg.setBody(obj);

		msg.setSumy(sumy);
		ClientSession.getInstance().sendMessage(msg);
	}

	public static void chatMediaMsg() {

		Message msg = new Message();
		Summary sumy = new Summary();

		sumy.setRequestKey("004-001");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.MEDIA_MSG_PIC.getValue());
		sumy.setFileName("F:/install/eclipse-jee-juno-SR2-win32-x86_64.zip");
		sumy.setSenderId("200000");
		sumy.setShowId("001");
		sumy.setSenderName("仰永潮");
		sumy.setSenderSex("1");
		sumy.setReceiverId("200001");
		sumy.setReceiverName("孙卫红");
		msg.setSumy(sumy);

		JSONObject obj = new JSONObject();
		msg.setBody(obj);
		ClientSession.getInstance().sendMessage(msg);
	}

	/**
	 * 测试
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年11月27日下午7:39:16
	 */
	public static void ping(String hello) {
		Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("009-009");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setFileName("e:/3.mp4");
		sumy.setReceiverId(SysUser.SERVER.getValue());
		sumy.setSenderId("1417076806787");
		msg.setSumy(sumy);

		JSONObject obj = new JSONObject();
		obj.put("body", hello);
		msg.setBody(obj);

		ClientSession.getInstance().sendMessage(msg);

	}

	/**
	 * 获取他人信息
	 * 
	 * @param userId
	 * @param groupId
	 * @author Yongchao.Yang
	 * @since 2014年12月18日下午6:05:40
	 */
	public static void getUserInfo(String userId, String friendId) {
		Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("002-004");

		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setReceiverId(SysUser.SERVER.getValue());

		msg.setSumy(sumy);

		JSONObject obj = new JSONObject();
		obj.put("userId", userId);
		obj.put("friendId", friendId);
		msg.setBody(obj);
		ClientSession.getInstance().sendMessage(msg);

	}

	/**
	 * 注册
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年12月18日上午10:49:13
	 */
	public static void regist(String phoneId, String passwd, String verifyCode) {
		Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("001-002");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setReceiverId(SysUser.SERVER.getValue());
		msg.setSumy(sumy);

		JSONObject obj = new JSONObject();
		obj.put("phoneId", phoneId);
		obj.put("passwd", passwd);
		obj.put("verifyCode", verifyCode);
		msg.setBody(obj);
		ClientSession.getInstance().sendMessage(msg);

	}

	/**
	 * 登录
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年12月18日上午10:56:23
	 */
	public static void login(String tokenId, String passwd, String way) {
		Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("001-001");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setReceiverId(SysUser.SERVER.getValue());
		msg.setSumy(sumy);

		JSONObject obj = new JSONObject();
		obj.put("tokenId", tokenId);
		obj.put("passwd", passwd);
		obj.put("way", way);
		msg.setBody(obj);
		ClientSession.getInstance().sendMessage(msg);
	}

	/**
	 * 完善用户资料
	 * 
	 * @param userId
	 * @param nickName
	 * @param sex
	 * @author Yongchao.Yang
	 * @since 2015年4月17日上午8:22:26
	 */
	public static void finishUserInfo(String userId, String nickName, String sex) {
		Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("001-003");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setReceiverId(SysUser.SERVER.getValue());
		msg.setSumy(sumy);

		JSONObject obj = new JSONObject();
		obj.put("userId", userId);
		obj.put("nickName", nickName);
		obj.put("sex", sex);
		msg.setBody(obj);
		ClientSession.getInstance().sendMessage(msg);
	}

	/**
	 * 修改昵称
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年12月18日上午10:56:53
	 */
	public static void sendGift(String senderId, String receiverId, String showId, String giftId, int cnt) {
		Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("002-010");
		sumy.setChatMode(ChatMode.PRIVATE.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId(KeyGenerator.createUniqueId());
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setReceiverId(SysUser.SERVER.getValue());
		msg.setSumy(sumy);

		JSONObject obj = new JSONObject();
		obj.put("senderId", senderId);
		obj.put("receiverId", receiverId);
		obj.put("showId", showId);
		obj.put("giftId", giftId);
		obj.put("giftCnt", String.valueOf(cnt));
		msg.setBody(obj);
		ClientSession.getInstance().sendMessage(msg);
	}

}
