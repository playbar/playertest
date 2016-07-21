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
 *                  fileName：Writer.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.tools.AndroidLogger;

/**
 * 消息发送线程
 * 
 * @author yongchao.Yang/2014年10月14日
 */
public class ClientWriter extends Thread {
	private volatile boolean isRun = false;
	/**
	 * 消息缓存
	 */
	private ClientMessageCache cache = ClientMessageCache.getInstance();

	private static int CACHE_SIZE = 102400;
	private ByteBuffer buffer = ByteBuffer.allocate(CACHE_SIZE);// 数据接收缓冲区
	// private Logger logger = Logger.getLogger(ClientWriter.class);

	private SelectionKey sessionKey;
	// private String sessionId;
	private SocketChannel channel;

	// private int count = 0;

	/**
	 * @param name
	 */
	public ClientWriter(SelectionKey key) {
		super("[ClientWriter]");
		this.sessionKey = key;
		this.channel = (SocketChannel) this.sessionKey.channel();
	}

	@Override
	public void run() {
		System.out.println("[ClientWriter][Writer线程初始化完毕]");
		while (isRun) {
			try {
				synchronized (this) {
					this.wait();
				}
				if (!isRun) {// 回收会话，结束线程，直接退出
					break;
				}

				// 有消息需要发送，则停止心跳，避免因网卡堵塞导致心跳回执超时，造成通讯中断假象
				// add 2015/04/27@yongchao.Yang
				// ClientHeartBeatRunner.getInstance().busy();

				while (cache.getSendMsgCnt() > 0) {
					LinkedList<Message> msgList = cache.getSendMsg();
					while (!msgList.isEmpty()) {
						Message msg = msgList.removeFirst();
						this.sendMsg(msg);
					}
				}

				// 消息发送完毕，开始心跳
				// add 2015/04/27@yongchao.Yang
				// ClientHeartBeatRunner.getInstance().idle();

			} catch (Throwable e) {
				System.out.println("------------ClientWriter异常--------------");
				// 消息发送完毕，开始心跳
				// add 2015/04/27@yongchao.Yang
				// ClientHeartBeatRunner.getInstance().idle();
				AndroidLogger.printLog(e);
				break;
			}
		}
		try {
			ClientSession.getInstance().closeSession();
		} catch (Throwable t) {
			AndroidLogger.printLog(t);
		}
		System.out.println("[ClientWriter][结束运行]");
	}

	/**
	 * 唤醒线程让其工作
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月14日下午4:14:25
	 */
	public void wakeUp() {
		synchronized (this) {
			this.notifyAll();
		}
	}

	/**
	 * 开始监听
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月18日下午12:33:03
	 */
	public void startRun() {
		System.out.println("[ClientWriter][startRun][运行状态:" + isRun + "]");
		if (!isRun) {
			isRun = true;
		}
		this.start();
	}

	/**
	 * 结束线程
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月14日下午4:15:37
	 */
	public void stopRun() {
		System.out.println("[ClientWriter][stopRun][运行状态:" + isRun + "]");
		if (!isRun) {
			return;
		}
		// 先关闭通道
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 再取消所有已经注册的事件
		try {
			this.sessionKey.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.isRun = false;
		synchronized (this) {
			this.notifyAll();
		}
	}

	/**
	 * 发送文本消息
	 * 
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月14日上午9:48:55
	 */
	private void sendMsg(Message msg) throws Exception {
		FileInputStream fis = null;
		int msgSize = 0;
		if (MsgType.TXT_MSG.getValue().equals(msg.getSumy().getMsgType()) || MsgType.GROUP_SHARE.getValue().equals(msg.getSumy().getMsgType())) {// 文本消息
			msgSize = msg.getBody().toJSONString().getBytes().length;
		} else {// 文件消息
			File file = new File(msg.getSumy().getFileName());
			if (!file.exists()) {
				throw new FileNotFoundException("消息文件 " + file.getPath() + " 不存在");
			}
			fis = new FileInputStream(file);
			msgSize = fis.available();
		}

		// 发送包头
		this.warpHeader(msg.getSumy(), msgSize);
		this.sendBytes();
		// 发送包体
		if (MsgType.TXT_MSG.getValue().equals(msg.getSumy().getMsgType()) || MsgType.GROUP_SHARE.getValue().equals(msg.getSumy().getMsgType())) {// 文本消息
			this.sendBigTxt(msg.getBody().toJSONString());
		} else {// 文件消息
			FileChannel fc = fis.getChannel();
			this.buffer.clear();
			int readBytes = 0;
			while ((readBytes = fc.read(buffer)) != -1) {
				System.out.println("[clientWriter][读取字节][" + readBytes + "]");
				buffer.flip();
				this.sendBytes();
				buffer.clear();
			}
			fc.close();
			fis.close();
		}

		// 发送结束标示
		this.sendBigTxt("ok");
	}

	/**
	 * 发送文本消息。如果文本超过当前缓存大小，则拆包发送
	 * 
	 * @param txt
	 * @param sc
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月16日下午3:54:51
	 */
	private void sendBigTxt(String txt) throws Exception {
		// 如果消息太长，超过了当前设置的缓存大小，则需要分包发送
		byte[] bytes = txt.getBytes();
		int size = bytes.length;
		int index = 0;
		while (index < size) {
			int needSendSize = size - index;
			int length = needSendSize > CACHE_SIZE ? CACHE_SIZE : needSendSize;

			this.buffer.clear();
			this.buffer.put(bytes, index, length);
			this.buffer.flip();

			this.sendBytes();
			index = index + length;
		}
	}

	/**
	 * 将缓存压入网卡进行发送，考虑半包情况
	 * 
	 * @param buffer
	 * @param sc
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月15日下午9:44:31
	 */
	private void sendBytes() throws Exception {
		// 当网卡拥堵情况，数据无法发送，则尝试发送10次，地5次以后，每次等待时长渐增
		while (buffer.hasRemaining()) {
			this.channel.write(buffer);
		}
	}

	/**
	 * 构建包头。数据包协议</br> 总数据长度(4byte)+摘要长度(4byte)+摘要+数据+ok(2byte)
	 * 
	 * @param dstBuf {@link ByteBuffer} 包头压入目标缓存
	 * @param id String 消息ID
	 * @param msgType String 数据类型 (1、 文本 2、文件 )
	 * @param chatMode String 消息类型 (1、群聊 2 私聊)
	 * @param senderId String 发送者ID
	 * @param receiverId String 接受者ID
	 * @param msgLength int 消息正文字节数
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年10月4日下午5:16:04
	 */
	private void warpHeader(Summary sumy, int msgLength) {
		// 构建消息摘要
		String summary = JSONObject.toJSONString(sumy);
		int packSize = 8 + summary.getBytes().length + msgLength + 2;

		this.buffer.clear();
		this.buffer.putInt(packSize);
		this.buffer.putInt(summary.getBytes().length);
		this.buffer.put(summary.getBytes());
		this.buffer.flip();

		// System.out.println("[Writer][packSize]" + packSize);
		// System.out.println("[Writer][msgSize]" + msgLength);
		// System.out.println("[Writer][summary]" + summary);
		// System.out.println("[Writer][summarSize]" + summary.getBytes().length);
	}

}
