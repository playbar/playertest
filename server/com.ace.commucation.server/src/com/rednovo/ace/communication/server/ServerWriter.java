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
package com.rednovo.ace.communication.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.communication.FileHelper;
import com.rednovo.ace.communication.server.handler.LocalSessoinManager;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;

/**
 * 消息发送线程
 * 
 * @author yongchao.Yang/2014年10月14日
 */
public class ServerWriter extends Thread {
	private volatile boolean isRun = true;
	/**
	 * 消息缓存
	 */
	private LocalMessageCache cache = LocalMessageCache.getInstance();
	private LocalSessoinManager usm = LocalSessoinManager.getInstance();

	private static int CACHE_SIZE = 1048576;

	private ByteBuffer buffer = ByteBuffer.allocate(CACHE_SIZE);// 数据接收缓冲区
	private Logger logger = Logger.getLogger(getClass());

	private SelectionKey sessionKey;
	private String sessionId;
	private SocketChannel channel;

	/**
	 * @param name
	 */
	public ServerWriter(SelectionKey key) {
		super(key.attachment().toString());
		this.sessionKey = key;
		this.sessionId = this.sessionKey.attachment().toString();
		this.channel = (SocketChannel) this.sessionKey.channel();
	}

	public String getSessionId() {
		return this.sessionId;
	}

	@Override
	public void run() {
		while (isRun) {
			try {
				synchronized (this) {
					this.wait();
				}
				if (!isRun) {// 回收会话，结束线程，直接退出
					break;
				}
				while (cache.getSendMsgCnt(sessionId) > 0) {
					LinkedList<Message> msgList = cache.getSendMsg(sessionId);
					while (!msgList.isEmpty()) {
						Message msg = msgList.removeFirst();
						this.sendMsg(msg);
						logger.debug("[ServerWriter][发送消息完毕][" + sessionId + "][" + JSON.toJSONString(msg) + "]");
					}
				}

			} catch (Exception e) {
				logger.error("[ServerWriter][可能因为对方中断连接而终止工作]", e);
				break;
			}
		}
		// 回收资源
		usm.releaseSession(sessionId);
		logger.info("[ServerWriter][结束运行  (" + sessionId + ") ]");
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
	 * 结束线程
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月14日下午4:15:37
	 */
	public void stopRun() {

		// 先关闭通道
		try {
			// if (this.channel.isOpen()) {
			logger.info("[ServerWriter][关闭channel (" + this.sessionId + ") ]");
			this.channel.close();
			// }

		} catch (IOException e) {
			e.printStackTrace();
		}
		// 再取消所有已经注册的事件
		try {
			// if (this.sessionKey.isValid()) {
			logger.info("[ServerWriter][取消sessionKey (" + this.sessionId + ") ]");
			this.sessionKey.cancel();
			// }
		} catch (Exception e) {
			logger.error("[ServerWriter][关闭会话异常 (" + sessionId + ") ]", e);
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

		// logger.info("\t\t[SeverWriter][发送消息][" + msg.getSumy().getRequestKey() + "]");
		FileInputStream fis = null;
		int msgSize = 0;
		if (MsgType.TXT_MSG.getValue().equals(msg.getSumy().getMsgType())) {// 文本消息
			msgSize = msg.getBody().toJSONString().getBytes().length;
		} else {// 文件消息

			String basePath = FileHelper.getDir(msg.getSumy());
			File file = new File(basePath + File.separator + msg.getSumy().getFileName());
			if (!file.exists()) {
				throw new FileNotFoundException("[ServerWriter][消息文件 " + file.getPath() + " 不存在]");
			}
			fis = new FileInputStream(file);
			msgSize = fis.available();
		}

		// 发送包头
		this.warpHeader(msg.getSumy(), msgSize);
		this.sendBytes();
		// 发送包体
		if (MsgType.TXT_MSG.getValue().equals(msg.getSumy().getMsgType())) {// 文本消息
			this.sendBigTxt(msg.getBody().toJSONString());
		} else {// 文件消息
			FileChannel fc = fis.getChannel();
			this.buffer.clear();
			int totalSize = 0, byteCnt = 0;
			while ((byteCnt = fc.read(buffer)) != -1) {
				// logger.info("\t\t[Writer][发送字节][" + byteCnt + "]");
				buffer.flip();
				this.sendBytes();
				totalSize = totalSize + byteCnt;

				buffer.clear();
			}
			// logger.info("\t\t[ServerWrite][读取文件大小][" + totalSize + "]");
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
		// logger.debug("\t\t[Writer][data]" + txt);
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
			int byteCnt = this.channel.write(buffer);
			// TODO 解决cup过高待检测

			if (byteCnt == 0) {
				synchronized (this) {
					this.wait(100);
					continue;
				}
			}

			// logger.info("\t\t[Writer][发送字节] [" + byteCnt + "]");
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

		// logger.debug("\t\t[Writer][" + sumy.getRequestKey() + "][packSize]" + packSize);
		// logger.debug("\t\t[Writer][" + sumy.getRequestKey() + "][msgSize]" + msgLength);
		// logger.debug("\t\t[Writer][" + sumy.getRequestKey() + "][summary]" + summary);
		// logger.debug("\t\t[Writer][" + sumy.getRequestKey() + "][summarSize]" + summary.getBytes().length);
	}
}
