/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播手机版
 *                  公司名称:多宝科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月14日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：ServerListener.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server.broadcast;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.rednovo.ace.communication.server.ServerWorkerPool;
import com.rednovo.ace.entity.Server;

/**
 * 广播-消息监听
 * 
 * @author yongchao.Yang/2014年11月9日
 */
public class BroadCastListener {
	private Selector selector;// 通道选择器
	CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
	private static Logger logger = Logger.getLogger(BroadCastListener.class);

	private volatile boolean isRun = true;
	private Server server;

	public BroadCastListener(Server ser) {
		this.server = ser;
	}

	/**
	 * 开始连接服务器
	 * 
	 * @param ServerIp
	 * @param port
	 * @author Yongchao.Yang
	 * @throws Exception
	 * @since 2014年10月24日下午12:13:30
	 */
	private void open(String host, int port) throws Exception {
		InetSocketAddress address = new InetSocketAddress(host, port);
		SocketChannel client = SocketChannel.open();

		client.configureBlocking(false);
		selector = Selector.open();
		client.register(selector, SelectionKey.OP_CONNECT);
		client.connect(address);
	}

	/**
	 * 管理客户端会话
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月24日上午9:47:49
	 */
	protected void closeListener() {
		try {
			this.isRun = false;
			Thread.sleep(500);
			this.selector.close();
			this.selector.wakeup();
			BroadCasterManager.getInstance().getBroadCaster(server.getId()).setStauts(false);
		} catch (Exception e) {
			logger.error("\t\t[释放广播服务资源异常:" + e.getMessage() + "]", e);
		}
	}

	/**
	 * 
	 * @param ip
	 * @param port
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年11月9日上午1:15:54
	 */
	protected void listen() throws Exception {
		logger.info("[BroadCastListener][开始连接服务器:" + server.getIp() + ",端口:" + server.getPort() + "]");
		this.open(server.getIp(), server.getPort());
		isRun = true;
		try {
			// int index = 0;
			while (isRun) {
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey selectionKey = (SelectionKey) it.next();
					it.remove();
					if (selectionKey.isConnectable()) {
						SocketChannel channel = (SocketChannel) selectionKey.channel();
						if (channel.isConnectionPending()) {
							try {
								if (!channel.finishConnect()) {
									// eventMgr.fireOpenFailedEvent();
									logger.info("[BroadCastListener][连接服务器失败 ip:" + server.getIp() + ",port:" + server.getPort() + "]");
									this.isRun = false;
									break;
								}
							} catch (Exception ex) {
								this.isRun = false;
								// eventMgr.fireOpenFailedEvent();
								logger.error("[BroadCastListener][连接服务器失败 ip:" + server.getIp() + ",port:" + server.getPort() + "]", ex);
								break;
							}
						}
						SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
						String sessionId = String.valueOf(System.currentTimeMillis());
						key.attach(sessionId);

						BroadCasterManager.getInstance().setBroadCaster(server, key);
						logger.info("[BroadCastListener][连接服务成功]");

					} else if (selectionKey.isReadable()) {// 不需要捕获读事件
						// System.out.println("[ClientLauncher][ClientLisntener][捕获读事件" + (++index) + "]");
						selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_READ);
						ServerWorkerPool.getInstance().getReader(selectionKey.attachment().toString()).wakeUp();
					} else if (selectionKey.isWritable()) { // 不需要监听写事件
						// System.out.println("[ClientLauncher][ClientLisntener][捕获写事件]");
						selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
					}

				}
			}
		} catch (Exception e) {
			logger.error("[BroadCastListener][会话异常 ip:" + server.getIp() + ",port:" + server.getPort() + "]", e);
		}

		BroadCasterManager.getInstance().getBroadCaster(server.getId()).setStauts(false);
		if (this.selector.isOpen()) {
			this.selector.close();
		}

		logger.error("[BroadCastListener][会话结束]");

	}
}
