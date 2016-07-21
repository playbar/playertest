/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2015年8月4日/2015
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.leduo.bb.imserver
 *                  fileName：MessageSender.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.communication.server.BlockMessageListener;
import com.rednovo.ace.communication.server.ServerHelpler;
import com.rednovo.ace.communication.server.ServerSession;
import com.rednovo.ace.communication.server.broadcast.BroadCasterManager;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.globalData.GlobalUserSessionMapping;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;

/**
 * @author yongchao.Yang/2015年8月4日
 */
public class MessageSender {

	private static Logger logger = Logger.getLogger(MessageSender.class);
	private static ServerSession ss = ServerSession.getInstance();
	private static long timeOut = PPConfiguration.getProperties("cfg.properties").getLong("block.message.timeout");

	/**
	 * 发送私聊消息
	 * 
	 * @author Yongchao.Yang
	 * @since 2015年8月4日下午4:00:59
	 */
	public static void sendPrivateMsg(Message msg) {
		String receiverId = msg.getSumy().getReceiverId();
		String sessionId = GlobalUserSessionMapping.getUserSession(receiverId);
		String serverId = GlobalUserSessionMapping.getSessionServer(sessionId);// 接受方所在服务器ID
		msg.getSumy().setReceiveSessionId(sessionId);
		send(serverId, msg);
	}

	/**
	 * 发送本地消息
	 * 
	 * @param sessionId
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2016年2月28日下午1:56:03
	 */
	public static void sendLocalMsg(String sessionId, Message msg) {
		ss.sendLocalMessage(sessionId, msg);
	}

	/**
	 * 发送阻塞消息
	 * 
	 * @param sessionId
	 * @param msg
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月22日上午1:55:22
	 */
	public static boolean sendBlockMsg(String sessionId, Message msg) {
		boolean isOk = false;
		if (Validator.isEmpty(sessionId)) {
			return isOk;
		}
		BlockMessageListener.getInstance().addBlockMsgId(msg.getSumy().getMsgId());
		synchronized (BlockMessageListener.getLocker()) {
			sendLocalMsg(sessionId, msg);
			try {
				BlockMessageListener.getLocker().wait(timeOut);
			} catch (InterruptedException e) {
				logger.error("[同步消息等待回执过程出现异常]", e);
			}
			if (BlockMessageListener.getInstance().ifResponse(msg.getSumy().getMsgId())) {
				isOk = true;
			}
			BlockMessageListener.getInstance().clearBlockMsgId(msg.getSumy().getMsgId());
		}
		return isOk;
	}

	/**
	 * 群信息
	 * 
	 * @param msg
	 * @param goupId
	 * @author Yongchao.Yang
	 * @since 2015年9月2日上午11:15:21
	 */
	public static void sendPublicMessage(Message msg, String liveShowId) {
		// 获取当前直播所有sessionId
		List<String> sessionIds = LiveShowManager.getMemberList(liveShowId, 1, 20000);
		logger.info("[转发观众数:" + sessionIds.size() + "," + sessionIds.toString() + "]");

		// 一次性最多发5000条数据
		HashMap<String, StringBuffer> msgMap = new HashMap<String, StringBuffer>();
		int index = 0;
		for (String sessionId : sessionIds) {
			// 将sessionId按照serverId分组，便于一次性快速发送
			String serverId = GlobalUserSessionMapping.getSessionServer(sessionId);
			StringBuffer sb = msgMap.get(serverId);
			if (sb == null) {
				sb = new StringBuffer();
				msgMap.put(serverId, sb);
			}
			sb.append(sessionId).append("^");
			if (++index >= 5000 || index == sessionIds.size()) {
				index = 0;
				Iterator<String> sids = msgMap.keySet().iterator();
				while (sids.hasNext()) {
					String targetServerId = sids.next();
					String receiveSessionIds = msgMap.get(targetServerId).toString();

					Message newMsg = null;
					try {
						newMsg = (Message) msg.clone();
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
					newMsg.getSumy().setReceiveSessionId(receiveSessionIds);
					MessageSender.send(targetServerId, newMsg);

				}
				msgMap.clear();
			}
		}

	}

	/**
	 * 向相关服务器广播公聊信息
	 * 
	 * @param serverId
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2016年2月28日下午9:07:47
	 */
	private static void send(String serverId, Message msg) {
		String requstKey = msg.getSumy().getRequestKey();
		if (!ServerHelpler.getLocalHost().getId().equals(serverId)) {
			// logger.info("[MessageSender][中转发送消息]");
			msg.getBody().put("proxyKey", requstKey);// 被代理的key
			msg.getSumy().setRequestKey(Constant.SysEvent.BROAD_MSG.getValue());// 如果接收方不在本地，则重置请求KEY，对方服务器接受后按照此标示进行重新处理后发送
			String targetSessoinId = BroadCasterManager.getInstance().getSessionId(serverId);
//			logger.info("[MessageSender][中转消息][serverId:" + serverId + ",sessionId:" + targetSessoinId + "]");
			sendLocalMsg(targetSessoinId, msg);
		} else {
			// logger.info("[MessageSender][本地发送消息]");
			// 本地转发
			String[] ssins = msg.getSumy().getReceiveSessionId().split("\\^");
			String sendSin = msg.getSumy().getSendSessionId();
			for (int i = 0; i < ssins.length; i++) {
				String reqKey = msg.getSumy().getRequestKey();
				// 避免服务器给自己转发自己发出的信息。屏蔽类型:聊天 主播进场
				if (sendSin.equals(ssins[i])) {
					if ("002-009".equals(reqKey)) {
						continue;
					}//
				}
				// 避免发送字节过大，清除无关数据
				msg.getSumy().setReceiveSessionId("");
				logger.info("[MessageSender][" + ssins[i] + "]");
				sendLocalMsg(ssins[i], msg);
				String userId = GlobalUserSessionMapping.getSessionUser(ssins[i]);
				String name = "游客";
				if (!Validator.isEmpty(userId)) {
					name = UserManager.getUser(userId).getNickName();
				}
				logger.info("[MessageSender][给" + name + "发送消息]");
			}
		}
	}
}
