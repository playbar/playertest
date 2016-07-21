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
package com.rednovo.ace.communication.server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.rednovo.ace.communication.EventInvokerManager;
import com.rednovo.ace.communication.server.handler.LocalSessoinManager;
import com.rednovo.ace.globalData.GlobalUserSessionMapping;

/**
 * @author yongchao.Yang/2014年10月14日
 */
public class ServerListener {
	private Logger loger = Logger.getLogger(getClass());
	private ServerWorkerPool serverWorkerPool = ServerWorkerPool.getInstance();
	private EventInvokerManager eventMgr = EventInvokerManager.getInstance();
	private LocalSessoinManager usm = LocalSessoinManager.getInstance();

	private static ServerListener sl;

	private Selector selector;// 通道选择器
	private int port;

	private volatile boolean isRun = true;

	public static ServerListener getInstance() {
		if (sl == null) {
			try {
				sl = new ServerListener(9999);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sl;
	}

	/**
	 * * @throws IOException
	 * 
	 */
	private ServerListener(int port) throws Exception {

		this.port = port;
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress(this.port));
		selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * 关闭服务
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月21日下午3:25:22
	 */
	protected void closeServer() {
		this.isRun = false;
		try {
			Thread.sleep(10);
			this.selector.wakeup();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void listen() throws Exception {
		eventMgr.fireReadyEvent();// 注册服务，便于其他节点进行广播通信
		loger.info("[ServerListener][开始监听]");
		// int index = 0;
		while (isRun) {
			selector.select();
			SocketChannel client = null;
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

			while (iterator.hasNext()) {
				SelectionKey selectionKey = iterator.next();
				iterator.remove();

				try {
					if (selectionKey.isAcceptable()) {
						loger.info("[ServerListener][------>捕获来自客户端请求<-----]");
						ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
						client = server.accept();
						client.configureBlocking(false);
						SelectionKey key = client.register(selector, SelectionKey.OP_READ);

						String sessionId = String.valueOf(System.currentTimeMillis());
						// TODO 暂时注释掉
						// 将会话同服务器进行映射
						GlobalUserSessionMapping.addSessionServer(sessionId, ServerHelpler.getLocalHost().getId());
						// TODO

						// 回调事件，通知连接成功
						eventMgr.fireOpenSuccessEvent(sessionId);
						key.attach(sessionId);
						usm.registChannel(key);

						// loger.info("\t\t[ServerListener 注册读写线程]");

					} else if (selectionKey.isReadable()) {
						// loger.info("\t\t[ServerListener 读事件(" + System.nanoTime() + "," + System.currentTimeMillis() + ")" + (++index) + "]");
						selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_READ);
						this.selector.wakeup();
						// loger.info("\t\t[ServerListener 关闭读事件" + (index) + "]");
						Thread.sleep(10);
						this.serverWorkerPool.getReader(selectionKey.attachment().toString()).wakeUp();
					} else if (selectionKey.isWritable()) {
						// loger.info("\t\t[ServerListener:" + "获取到写事件]");
						selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
						this.selector.wakeup();
					}

				} catch (Exception ex) {
					loger.error("[ServerListener]--------->>>[sessionId(" + String.valueOf(selectionKey.attachment()) + "已经取消]<<<------------)", ex);
				}

			}

		}

	}

	/**
	 * @param args
	 * @author Yongchao.Yang
	 * @since 2014年10月2日下午9:55:27
	 */
	public static void main(String[] args) {

		try {
			Thread.sleep(100);
			ServerListener server = new ServerListener(9999);
			server.listen();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
