package com.rednovo.ace.launch;

import org.apache.log4j.Logger;

import com.rednovo.ace.communication.EventInvokerManager;
import com.rednovo.ace.communication.daemon.DynamicDataSender;
import com.rednovo.ace.communication.daemon.LocalSessionCleaner;
import com.rednovo.ace.communication.daemon.OutMsgSender;
import com.rednovo.ace.communication.server.DataParseWorker;
import com.rednovo.ace.communication.server.ServerEventInvoker;
import com.rednovo.ace.communication.server.ServerSession;
import com.rednovo.ace.communication.server.broadcast.BroadCasterManager;
import com.rednovo.ace.communication.server.broadcast.HeartBeatRunner;
import com.rednovo.ace.communication.server.handler.UserSessionScaner;
import com.rednovo.tools.PPConfiguration;

public class ServerLauncher {

	Logger logger = Logger.getLogger(getClass());

	public ServerLauncher() {}

	public void listen() {
		// 实例化会话事件回调对象。当事件发生时，触发抛出，便于外部程序处理
		EventInvokerManager eim = EventInvokerManager.getInstance();
		ServerEventInvoker sei = new ServerEventInvoker();
		eim.registInvoker(sei);

		// 启动服务器会话线程
		ServerSession ss = ServerSession.getInstance();
		logger.info("[ServerLauncher][" + ss.getServerId() + "] 服务器启动");
		ss.listen();

		// 启动新数据解析线程
		logger.info("[ServerLauncher][启动本地消息处理线程]");
		DataParseWorker.getInstance().start();

		// 启动会话状态监听线程
		logger.info("[ServerLauncher][启动会话状态监听线程]");
		new UserSessionScaner().start();

		// 启动房间动态数据推送线程
		logger.info("[ServerLauncher][启动房间动态数据推送线程]");
		DynamicDataSender.getInstance().start();

		// 创建同其他服务器的广播服务
		logger.info("[ServerLauncher][探测其他SessionServer，并连接]");
		BroadCasterManager.getInstance().createBroadCaster();

		// 开启广播服务心跳监测线程
		logger.info("[ServerLaunch][启动广播服务心跳线程]");
		HeartBeatRunner.getInstance().start();

		// 开启敏感词更新线程
		logger.info("[ServerLaunch][启动敏感词更新线程线程]");
		KeyWordUpdater.getInstance().start();

		// 本地会话监控线程
		logger.info("[ServerLaunch][启动本地会话监控线程]");
		LocalSessionCleaner.getInstance().start();

		// 开启第三方消息发送线程
		if (PPConfiguration.getProperties("cfg.properties").getBoolean("out.messsage.sender.run")) {
			logger.info("[ServerLaunch][开启第三方消息发送线程]");
			OutMsgSender.getInstance().start();
		}

	}

	// public static void clearSession() {
	// GlobalUserSessionMapping.closeServer(ServerHelpler.getLocalHost().getId());
	// }

	public static void main(String[] args) {
		// clearSession();
		ServerLauncher serverLauncher = new ServerLauncher();
		serverLauncher.listen();
	}

}
