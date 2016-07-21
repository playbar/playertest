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
 *                  fileName：RequestDispatch.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.communication.server.handler.LogicHandler;
import com.rednovo.ace.communication.server.handler.MessageSender;
import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.tools.DateUtil;

/**
 * 请求分发处理器
 * 
 * @author yongchao.Yang/2014年10月22日
 */
public class RequestDispatch {
	private static Logger logger = Logger.getLogger(RequestDispatch.class);

	public static void dispatch(String sessionId, Message msg) {
		// logger.info("\t\t[RequestDispatch][处理请求,sessionId:" + sessionId + "]");
		String requestKey = msg.getSumy().getRequestKey();
		LogicHandler handler = new LogicHandler(sessionId, msg);
		Message response = null;
		// logger.info("[RequestDispatch][监测请求:" + requestKey + "]");
		if ("002-001".equals(requestKey)) {// 进入直播间
			logger.info("[RequestDispatch][进入直播间][" + sessionId + "]");
			response = handler.enter();
		} else if ("002-006".equals(requestKey)) {// 退出房间
			logger.info("[RequestDispatch][退出房间][" + sessionId + "]");
			response = handler.exit();
		} else if ("002-007".equals(requestKey)) {// 踢人
			logger.info("[RequestDispatch][踢人][" + sessionId + "]");
			response = handler.kickMan();
		} else if ("009-004".equals(requestKey)) {// 心跳
			// logger.debug("[RequestDispatch][心跳][" + sessionId + "]");
			response = handler.heartBeat();
		} else if ("002-009".equals(requestKey)) {// 聊天
			logger.info("[RequestDispatch][聊天][" + sessionId + "]");
			response = handler.chat();
		} else if ("002-010".equals(requestKey)) {// 送礼物
			logger.info("[RequestDispatch][送礼物][" + sessionId + "]");
			response = handler.sendGift();
		} else if ("002-013".equals(requestKey)) {// 送礼物
			logger.info("[RequestDispatch][点赞][" + sessionId + "]");
			response = handler.support();
		} else if ("009-002".equals(requestKey)) {// 广播
			logger.info("[RequestDispatch][解析转发消息][" + sessionId + "]");
			response = handler.parseBroadMessage();
		} else if ("009-003".equals(requestKey)) {// 广播发起段证明自己
			logger.info("[RequestDispatch][证明自己][" + sessionId + "]");
			response = handler.registClientBroadCaster();
		} else if ("009-009".equals(requestKey)) {// ping
			logger.info("[RequestDispatch][ping][" + sessionId + "]");
			response = handler.ping();
		} else {
			logger.error("[错误的请求号:" + requestKey + "]");
			Summary smy = new Summary();
			smy.setChatMode(ChatMode.PRIVATE.getValue());
			smy.setInteractMode(InteractMode.RESPONSE.getValue());
			smy.setMsgId(msg.getSumy().getMsgId());
			smy.setMsgType(MsgType.TXT_MSG.getValue());
			smy.setReceiverId(msg.getSumy().getSenderId());
			smy.setSendTime(DateUtil.getStringDate());

			JSONObject jo = new JSONObject();
			jo.put("exeStatus", "0");
			jo.put("exeCode", "301");
			jo.put("exeMsg", "请求编号[" + requestKey + "]不存在");

			response = new Message();
			response.setBody(jo);
			response.setSumy(smy);

		}
		doResponse(sessionId, response);
	}

	/**
	 * 响应请求
	 * 
	 * @param sessionId
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2014年10月22日上午11:22:59
	 */
	private static void doResponse(String sessionId, Message msg) {
		// logger.info("[RequestDispatch][响应][" + sessionId + "][" + JSON.toJSONString(msg) + "]");
		MessageSender.sendLocalMsg(sessionId, msg);
	}

}
