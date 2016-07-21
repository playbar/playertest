/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月22日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：LogicHandler.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server.handler;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.communication.server.broadcast.BroadCasterManager;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.constant.Constant.SysUser;
import com.rednovo.ace.entity.BasicData;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.ace.entity.User;
import com.rednovo.ace.globalData.GlobalUserSessionMapping;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.KeyGenerator;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * 数据处理
 * 
 * @author yongchao.Yang/2014年10月22日
 */
public class LogicHandler {
	// 背景 麦选卡
	private String sessionId;
	private Message requestMsg, responseMsg;
	private Summary responseSumy;
	private String requestKey;
	private BasicData bd;
	private JSONObject responseBody = new JSONObject(), requestBody;
	private static LocalSessoinManager usm = LocalSessoinManager.getInstance();
	private static Logger logger = Logger.getLogger(LogicHandler.class);

	/**
	 * 
	 */
	public LogicHandler(String sessionId, Message msg) {
		this.sessionId = sessionId;
		this.requestMsg = msg;
		this.requestBody = msg.getBody();
		// 发送文件，没有body部分
		if (Constant.MsgType.TXT_MSG.getValue().equals(msg.getSumy().getMsgType())) {
			this.bd = JSONObject.parseObject(this.requestBody.getString("basicData"), BasicData.class);
		}
		this.requestKey = msg.getSumy().getRequestKey();

		this.responseMsg = new Message();
		this.responseSumy = new Summary();
		this.responseSumy.setReceiverId(msg.getSumy().getSenderId());
		this.responseSumy.setSenderId(SysUser.SERVER.getValue());
		this.responseSumy.setChatMode(ChatMode.PRIVATE.getValue());
		this.responseSumy.setInteractMode(InteractMode.RESPONSE.getValue());
		this.responseSumy.setMsgId(this.requestMsg.getSumy().getMsgId());
		this.responseSumy.setMsgType(MsgType.TXT_MSG.getValue());
		this.responseSumy.setRequestKey(this.requestMsg.getSumy().getRequestKey());
		this.responseSumy.setSendTime(DateUtil.getTimeInMillis());
		this.responseSumy.setFileName(this.requestMsg.getSumy().getFileName());
		this.responseSumy.setShowId(this.requestMsg.getSumy().getShowId());
		this.responseSumy.setDuration(this.requestMsg.getSumy().getDuration());

		this.responseMsg.setSumy(responseSumy);
		this.responseMsg.setBody(responseBody);
	}

	/**
	 * 进入直播
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年2月27日上午9:19:16
	 */
	public Message enter() {
		if (!this.validateKey("002-001")) {
			return this.responseMsg;
		}
		String userId = this.requestBody.getString("userId");
		String showId = this.requestBody.getString("showId");
		if (Validator.isEmpty(showId)) {
			this.failed("600", "没有选择直播间");
			return this.responseMsg;
		}

		if (!Validator.isEmpty(userId) && UserManager.isFreeze(userId)) {
			this.failed("208", "你已经被冻结，无法进入");
			return this.responseMsg;
		}

		// 非主播用户进入房间，需要检测房间状态
		if (!userId.equals(showId)) {
			List<String> showIds = LiveShowManager.getSortList(1, 100000);
			if (!showIds.contains(showId)) {
				this.failed("301", "直播已经结束");
				return this.responseMsg;
			}
		}

		// 累加用户,便于直播结算时显示
		LiveShowManager.addShowExtData(showId, "MEMBER", "1");

		if (!Validator.isEmpty(userId)) {
			GlobalUserSessionMapping.addUserSession(userId, sessionId);
		}

		// 如果会话已经存在,不再重复发送消息
		if (LiveShowManager.getMemberList(showId, 1, 100000).contains(sessionId)) {
			this.sucessed();
			return this.responseMsg;
		}
		// 添加观众
		LiveShowManager.addMember(showId, sessionId);
		// 广播进场消息
		JSONObject jo = new JSONObject();
		if (Validator.isEmpty(userId)) {
			jo.put("sex", "0");
			jo.put("nickName", "游客");
		} else {
			User u = UserManager.getUser(userId);
			jo.put("userId", userId);
			jo.put("nickName", u.getNickName());
			jo.put("profile", u.getProfile());
			jo.put("sex", Validator.isEmpty(u.getSex()) ? "0" : u.getSex());
		}
		this.requestMsg.setBody(jo);

		// 广播用户进入房间
		requestMsg.getSumy().setRequestKey("002-001");
		// requestMsg.getSumy().setSendSessionId(sessionId);
		MessageSender.sendPublicMessage(this.requestMsg, showId);

		this.sucessed();
		return this.responseMsg;
	}

	/**
	 * 送礼物
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月12日下午1:00:13
	 */
	public Message sendGift() {
		if (!this.validateKey("002-010")) {
			return this.responseMsg;
		}
		String senderId = this.requestBody.getString("senderId");
		String showId = this.requestBody.getString("showId");
		String receiverId = this.requestBody.getString("receiverId");
		String giftId = this.requestBody.getString("giftId");
		String giftCnt = this.requestBody.getString("cnt");

		if (Validator.isEmpty(showId)) {
			this.failed("600", "没有选择直播间");
			return this.responseMsg;
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("senderId", senderId);
		params.put("showId", showId);
		params.put("receiverId", receiverId);
		params.put("giftId", giftId);
		params.put("giftCnt", giftCnt);

		String res = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/001-010", params);
		JSONObject response = JSON.parseObject(res);
		// 处理成功,广播礼物消息
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(response.getString("exeStatus"))) {
			// 记录本次直播的加币数量，便于直播结束统计
			BigDecimal coinAmount = StaticDataManager.getGift(giftId).getTransformPrice().multiply(new BigDecimal(giftCnt));
			LiveShowManager.addShowExtData(showId, "COIN", coinAmount.toString());

			requestBody.put("senderName", UserManager.getUser(senderId).getNickName());
			requestBody.put("receiverName", UserManager.getUser(receiverId).getNickName());
			MessageSender.sendPublicMessage(this.requestMsg, showId);

			this.setValue("balance", response.getString("balance"));
			this.sucessed();
		} else {
			this.failed(response.getString("errCode"), "赠送礼物失败");
		}
		return this.responseMsg;
	}

	/**
	 * 踢人
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月12日下午6:10:55
	 */
	public Message kickMan() {
		if (!this.validateKey("002-007")) {
			return this.responseMsg;
		}
		String showId = this.requestBody.getString("showId");
		String userId = this.requestBody.getString("userId");

		if (Validator.isEmpty(showId)) {
			this.failed("600", "没有选择直播间");
			return this.responseMsg;
		}

		// 广播消息
		this.requestBody.put("userName", UserManager.getUser(userId).getNickName());
		this.requestBody.put("userId", userId);
		MessageSender.sendPublicMessage(this.requestMsg, showId);
		// 等待消息发出
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 释放该用户资源
		LocalSessoinManager.getInstance().releaseSession(GlobalUserSessionMapping.getUserSession(userId));

		this.sucessed();
		return this.responseMsg;

	}

	/**
	 * 退出房间
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月12日下午8:17:55
	 */
	public Message exit() {
		if (!this.validateKey("002-006")) {
			return this.responseMsg;
		}
		String showId = this.requestBody.getString("showId");
		boolean isForce = this.requestBody.getBooleanValue("isForce");// 强制退出

		if (Validator.isEmpty(showId)) {
			this.failed("600", "没有选择直播间");
			return this.responseMsg;
		}
		// 判断是否房主退出房间,广播消息
		String UserId = GlobalUserSessionMapping.getSessionUser(sessionId);
		if (showId.equals(UserId) || isForce) {
			Message newMsg = new Message();
			Summary newSummary = new Summary();
			newMsg.setSumy(newSummary);
			newSummary.setSenderId(SysUser.SERVER.getValue());// 消息发送人
			newSummary.setInteractMode(InteractMode.REQUEST.getValue());// 请求-应答模式
			newSummary.setMsgId(KeyGenerator.createUniqueId());// 消息ID
			newSummary.setMsgType(MsgType.TXT_MSG.getValue());// 消息类型
			newSummary.setRequestKey("004-004");// 请求KEY
			newSummary.setChatMode(ChatMode.GROUP.getValue());
			newMsg.setSumy(newSummary);
			JSONObject jo = new JSONObject();
			newMsg.setBody(jo);
			MessageSender.sendPublicMessage(newMsg, showId);
			// 返回结算数据
			HashMap<String, String> data = LiveShowManager.getShowExtData(showId);
			this.setValue("support", data.get("SUPPORT"));
			this.setValue("coins", data.get("COIN"));
			this.setValue("fans", data.get("FANS"));
			this.setValue("length", data.get("LENGTH"));
			this.setValue("memberCnt", data.get("MEMBER"));
			logger.info("[LogicHandler][主播" + UserManager.getUser(UserId).getNickName() + "结束直播][" + sessionId + "]");
			// 结束直播资源
			GlobalUserSessionMapping.finishPossibleShow(sessionId);
		}

		// 释放该用户资源
		GlobalUserSessionMapping.expireSession(sessionId);

		this.sucessed();
		return this.responseMsg;
	}

	/**
	 * 点赞
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月12日下午9:08:16
	 */
	public Message support() {
		if (!this.validateKey("002-013")) {
			return this.responseMsg;
		}
		String showId = this.requestBody.getString("showId");
		String userId = this.requestBody.getString("userId");
		String cnt = this.requestBody.getString("cnt");

		if (Validator.isEmpty(showId)) {
			this.failed("600", "没有选择直播间");
			return this.responseMsg;
		}
		LiveShowManager.addSupportCnt(showId, Integer.valueOf(cnt));

		this.sucessed();
		return this.responseMsg;
	}

	/**
	 * 聊天
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月28日下午2:54:47
	 */
	public Message chat() {
		if (!this.validateKey("002-009")) {
			return this.responseMsg;
		}
		String chatMode = this.requestMsg.getSumy().getChatMode();
		//
		Message newMsg = new Message();
		Summary newSummary = new Summary();
		newMsg.setSumy(newSummary);
		//
		// // 消息发送者
		User sender = UserManager.getUser(this.requestMsg.getSumy().getSenderId());
		newSummary.setSenderId(sender.getUserId());// 消息发送人
		newSummary.setSenderName(sender.getNickName());// 消息发送人昵称
		newSummary.setSenderSex(sender.getSex());// 消息发送者性别

		newSummary.setInteractMode(InteractMode.REQUEST.getValue());// 请求-应答模式
		newSummary.setMsgId(this.requestMsg.getSumy().getMsgId());// 消息ID
		newSummary.setMsgType(this.requestMsg.getSumy().getMsgType());// 消息类型
																		// 文本、文件
		newSummary.setRequestKey(this.requestMsg.getSumy().getRequestKey());// 请求KEY
		newSummary.setSendTime(DateUtil.getTimeInMillis());// 发送时间

		newSummary.setDuration(this.requestMsg.getSumy().getDuration());
		//
		newSummary.setFileName(this.requestMsg.getSumy().getFileName());
		newSummary.setChatMode(this.requestMsg.getSumy().getChatMode());

		newSummary.setSendSessionId(sessionId);// 设置发送者的会话id，可以满足日后匿名用户也可聊天的需求

		newMsg.setSumy(newSummary);

		newMsg.setBody(this.requestBody);

		String txt = this.requestBody.getString("txt");
		txt = Validator.replaceKeyWord(txt, Constant.getKeyWord());

		this.requestBody.put("txt", txt);

		if (ChatMode.PRIVATE.getValue().equals(chatMode)) {// 私聊
			String receiverId = this.requestMsg.getSumy().getReceiverId();
			// // 参数验证
			if (Validator.isEmpty(receiverId)) {
				this.failed("600", "参数异常");
				return this.responseMsg;
			}
			newSummary.setReceiverId(receiverId);
			MessageSender.sendPrivateMsg(newMsg);

		} else {// 公聊
			logger.debug("\t\t[获取到群聊消息]");
			MessageSender.sendPublicMessage(newMsg, this.requestMsg.getSumy().getShowId());
		}
		this.sucessed();
		return this.responseMsg;
	}

	// 处理广播信息
	public Message parseBroadMessage() {
		if (!this.validateKey(Constant.SysEvent.BROAD_MSG.getValue())) {
			return this.responseMsg;
		}

		String proxyKey = this.requestBody.remove("proxyKey").toString();

		Message newMsg = new Message();
		Summary newSummary = new Summary();
		newMsg.setSumy(newSummary);
		// 消息发送者
		User sender = UserManager.getUser(this.requestMsg.getSumy().getSenderId());
		// System.out.println("-----------------------------------------------------");
		// System.out.println("senderId=" + this.requestMsg.getSumy().getSenderId());

		newSummary.setSenderId(sender.getUserId());// 消息发送人
		newSummary.setSenderName(sender.getNickName());// 消息发送人昵称
		newSummary.setSenderSex(sender.getSex());// 消息发送者性别

		newSummary.setInteractMode(InteractMode.REQUEST.getValue());// 请求-应答模式
		newSummary.setMsgId(this.requestMsg.getSumy().getMsgId());// 消息ID
		newSummary.setMsgType(this.requestMsg.getSumy().getMsgType());// 消息类型
		newSummary.setRequestKey(proxyKey);// 恢复被代理的key
		newSummary.setSendTime(DateUtil.getTimeInMillis());// 发送时间
		newSummary.setDuration(this.requestMsg.getSumy().getDuration());
		newSummary.setFileName(this.requestMsg.getSumy().getFileName());
		newSummary.setChatMode(this.requestMsg.getSumy().getChatMode());
		newMsg.setSumy(newSummary);

		newMsg.setBody(this.requestBody);
		String[] sins = this.requestMsg.getSumy().getReceiveSessionId().split("\\^");
		for (int i = 0; i < sins.length; i++) {
			MessageSender.sendLocalMsg(sins[i], newMsg);
		}

		this.sucessed();
		return this.responseMsg;
	}

	/**
	 * 接受其他服务的连接示好
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月24日下午2:46:18
	 */
	public Message registClientBroadCaster() {
		if (!this.validateKey("009-003")) {
			return this.responseMsg;
		}
		String serverId = this.requestBody.getString("serverId");
		BroadCasterManager.getInstance().registClient(serverId, sessionId);
		usm.updateSessionTime(sessionId);
		logger.info("[LogicHandler][接受" + serverId + "示好，注册其信息]");

		this.setValue("body", "response");
		this.sucessed();
		return this.responseMsg;

	}

	/**
	 * 心跳
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月9日上午1:01:21
	 */
	public Message heartBeat() {
		if (!this.validateKey("009-004")) {
			return this.responseMsg;
		}
		// 更新用户会话映射
		// logger.info("[LogicHandler][更新会话" + sessionId + "时间]");
		usm.updateSessionTime(sessionId);
		this.setValue("body", "ping");
		this.sucessed();
		return this.responseMsg;

	}

	/**
	 * 通信测试
	 * 
	 * @param sessionId
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2014年10月22日上午10:35:06
	 */
	public Message ping() {
		String key = this.requestMsg.getSumy().getRequestKey();
		if (!"009-009".equals(key)) {
			this.failed("600", "请求key错误");
		} else {
			String msg = this.requestBody.getString("body") + "(" + this.sessionId + ")";
			this.setValue("body", msg);
		}
		return responseMsg;
	}

	/**
	 * 返回数据错误
	 * 
	 * @param errCode
	 * @param errMsg
	 * @author Yongchao.Yang
	 * @since 2014年10月22日上午10:43:20
	 */
	private void failed(String errCode, String errMsg) {
		responseBody.put("errCode", errCode);
		responseBody.put("exeStatus", "0");
		responseBody.put("errMsg", errMsg);

	}

	private void sucessed() {
		responseBody.put("exeStatus", "1");
	}

	/**
	 * 设置值
	 * 
	 * @param key
	 * @param value
	 * @author Yongchao.Yang
	 * @since 2014年10月25日下午3:19:34
	 */
	private void setValue(String key, Object value) {
		responseBody.put(key, value);
	}

	/**
	 * 验证请求key和逻辑操作key是否一致
	 * 
	 * @param key
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月25日上午11:05:00
	 */
	private boolean validateKey(String key) {
		if (!key.equals(requestKey)) {
			this.failed("600", "请求key错误");
			return false;
		}
		return true;
	}
}
