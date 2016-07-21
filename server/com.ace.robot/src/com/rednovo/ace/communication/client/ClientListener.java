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
package com.rednovo.ace.communication.client;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;

import com.rednovo.ace.communication.EventInvokerManager;
import com.rednovo.tools.AndroidLogger;

/**
 * 会话客户端
 * 
 * @author yongchao.Yang/2014年11月9日
 */
public class ClientListener {
	private Selector selector;// 通道选择器
	CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();

	private volatile boolean isRun = true;

	private ClientWorkerPool workerPool = ClientWorkerPool.getInstance();
	private static ClientListener clientListener;
	private EventInvokerManager eventMgr = EventInvokerManager.getInstance();

	/**
	 * 获取客户端单例
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月9日上午1:39:41
	 */
	public synchronized static ClientListener getInstance() {
		if (clientListener == null) {
			try {
				clientListener = new ClientListener();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return clientListener;
	}

	private ClientListener() {
		System.out.println("[ClientListener][-----------初始化-----------------]");
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
		} catch (Throwable e) {
			System.out.println("[ClientListener][closeListener][释放客户端会话资源异常]");
			AndroidLogger.printLog(e);
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
	protected void listen(String ip, int port) throws Exception {
		System.out.println("[ClientLisntener][开始连接服务器:" + ip + ",端口:" + port + "]");
		this.open(ip, port);
		isRun = true;
		try {
			// int index = 0;
			while (isRun) {
				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				while (it.hasNext()) {
					SelectionKey selectionKey = (SelectionKey) it.next();
					// it.remove();
					if (selectionKey.isConnectable()) {
						SocketChannel channel = (SocketChannel) selectionKey.channel();
						if (channel.isConnectionPending()) {
							try {
								if (!channel.finishConnect()) {
									eventMgr.fireOpenFailedEvent();
									this.isRun = false;
									break;
								}
							} catch (Exception ex) {
								this.isRun = false;
								eventMgr.fireOpenFailedEvent();
								break;
							}
						}
						System.out.println("[ClientLisntener][客户端连接成功]");

						SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
						ClientSession.getInstance().setStauts(true);// 设置会话为链接状态
						workerPool.registChannel(key);
						eventMgr.fireOpenSuccessEvent(null);// 链接成功通知

						try {
							// 线程睡眠，等待reader和writer初始化完毕
							Thread.sleep(500);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 启动心跳，只启动一次
						// System.out.println("[ClientLisntener][启动心跳线程]");
						ClientHeartBeatRunner.getInstance().startRun();
						// 启动新消息监听线程
						System.out.println("[ClientLisntener][启动消息监听线程]");
						ClientMessageListener.getInstance().startRun();

					} else if (selectionKey.isReadable()) {
						// System.out.println("[ClientLisntener][捕获读事件" + (++index) + "]");
						selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_READ);
						this.selector.wakeup();
						Thread.sleep(50);
						this.workerPool.getReader().wakeUp();
					} else if (selectionKey.isWritable()) { // 不需要监听写事件
						// System.out.println("[ClientLisntener][捕获写事件]");
						selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
					}
				}
				keys.clear();

			}
		} catch (Throwable e) {
			AndroidLogger.printLog(e);
		}

		ClientSession.getInstance().closeSession();
		System.out.println("[ClientLisntener][客户端会话结束退出]");
	}
}
