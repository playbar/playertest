/*  ------------------------------------------------------------------------------ 
 *                  软件名称:
 *                  公司名称:
 *                  开发作者:ZuKang.Song
 *       			开发时间:2016年5月17日/2016
 *    				All Rights Reserved 2016-2016
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.commucation.server
 *                  fileName：ReceiveMessageManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.daemon;

import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.communication.server.handler.MessageSender;
import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.globalData.OutMessageManager;
import com.rednovo.tools.Validator;

/**
 * 第三方消息发送线程
 * 
 * @author ZuKang.Song/2016年5月17日
 */
public class OutMsgSender extends Thread {
	private volatile boolean isRun = true;
	private static Logger logger = Logger.getLogger(OutMsgSender.class);

	private static OutMsgSender kwu = null;

	public static OutMsgSender getInstance() {
		if (kwu == null) {
			kwu = new OutMsgSender("OutMsgSender");
		}
		return kwu;
	}

	private OutMsgSender(String name) {
		super(name);
	}

	@Override
	public void run() {
		logger.info("第三方消息发送线程启动完毕");
		while (isRun) {
			try {
				synchronized (this) {
					this.wait(1000);
				}

				List<String> msglist = OutMessageManager.removeMessage();
				if (msglist == null || msglist.size() == 0) {
					continue;
				}
				for (String msgstr : msglist) {
					Message msg = JSON.parseObject(msgstr, Message.class);
					if (ChatMode.PRIVATE.getValue().equals(msg.getSumy().getChatMode())) {// 私聊
						MessageSender.sendPrivateMsg(msg);
					} else {// 公聊
						if (Validator.isEmpty(msg.getSumy().getShowId())) {
							logger.info("群发消息失败:找不到房间ID");
							continue;
						}
						logger.info("[OutMsgSender][广播消息][" + msgstr + "]");
						MessageSender.sendPublicMessage(msg, msg.getSumy().getShowId());
					}
					// 分担服务器网卡压力
					synchronized (this) {
						this.wait(200);
					}
				}

			} catch (Exception e) {
				logger.error("OutMsgSender异常", e);
			}
		}

	}
}
