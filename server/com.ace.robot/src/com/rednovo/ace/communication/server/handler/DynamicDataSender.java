/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月12日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.commucation.server
 *                  fileName：DynamicDataSender.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server.handler;

import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.communication.server.ServerHelpler;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.ace.globalData.GlobalUserSessionMapping;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.KeyGenerator;
import com.rednovo.tools.Validator;

/**
 * 所有直播间动态数据推送线程
 * 
 * @author yongchao.Yang/2016年3月12日
 */
public class DynamicDataSender extends Thread {
	private volatile boolean isRun = true;
	public static DynamicDataSender dds;
	private static Logger logger = Logger.getLogger(DynamicDataSender.class);

	public static DynamicDataSender getInstance() {
		if (dds == null) {
			dds = new DynamicDataSender("DynamicDataSender");
		}
		return dds;
	}

	/**
	 * @param name
	 */
	private DynamicDataSender(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			try {
				List<String> list = LiveShowManager.getSortList(1, 10000);
				List<String> localSessions = GlobalUserSessionMapping.getServerSession(ServerHelpler.getLocalHost().getId());
				for (String showId : list) {
					// 只同步本地主播数据
					String showSession = GlobalUserSessionMapping.getUserSession(showId);
					if (!localSessions.contains(showSession)) {
						continue;
					}

					// 获取观众数
					String memberCnt = String.valueOf(LiveShowManager.getMemberCnt(showId));
					String newSuptCnt = LiveShowManager.getNewSupportCnt(showId);
					String totalSupCnt = LiveShowManager.getTotalSupportCnt(showId);
					if (Validator.isEmpty(memberCnt)) {
						memberCnt = "0";
					}
					if (Validator.isEmpty(newSuptCnt)) {
						newSuptCnt = "0";
					}
					if (Validator.isEmpty(totalSupCnt)) {
						totalSupCnt = "0";
					}
					logger.info("[推送房间" + showId + "动态数据][总点赞数:" + totalSupCnt + ",新点赞" + newSuptCnt + ",观众" + memberCnt + "]");
					this.sendMsg(showId, newSuptCnt, totalSupCnt, memberCnt);
				}

				synchronized (this) {
					try {
						this.wait(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} catch (Exception ex) {
				logger.error("[房间广播出现异常]", ex);
			}

		}
	}

	public void stopRun() {
		this.isRun = false;
	}

	private void sendMsg(String showId, String newSuptCnt, String totalSuptCnt, String memberCnt) {

		Message newMsg = new Message();
		Summary newSummary = new Summary();
		newMsg.setSumy(newSummary);
		newSummary.setSenderId(Constant.SysUser.SERVER.getValue());// 消息发送人

		newSummary.setInteractMode(InteractMode.REQUEST.getValue());// 请求-应答模式
		newSummary.setMsgId(KeyGenerator.createUniqueId());// 消息ID
		newSummary.setMsgType(Constant.MsgType.TXT_MSG.getValue());// 消息类型
																	// 文本、文件
		newSummary.setRequestKey("004-003");// 请求KEY
		newSummary.setSendTime(DateUtil.getTimeInMillis());// 发送时间

		newSummary.setChatMode(Constant.ChatMode.GROUP.getValue());

		newMsg.setSumy(newSummary);
		JSONObject jo = new JSONObject();
		jo.put("newSuptCnt", newSuptCnt);
		jo.put("totalSuptCnt", totalSuptCnt);
		jo.put("memberCnt", memberCnt);
		newMsg.setBody(jo);
		MessageSender.sendPublicMessage(newMsg, showId);
	}

}
