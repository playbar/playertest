/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播手机版
 *                  公司名称:多宝科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月18日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：MessageReader.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.communication.FileHelper;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.constant.Constant.SysEvent;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.tools.AndroidLogger;

/**
 * @author yongchao.Yang/2014年10月18日
 */
public class ClientReader extends Thread {
	private volatile boolean isRun = false;

	private ClientMessageCache cmc = ClientMessageCache.getInstance();
	/**
	 * 消息缓存
	 */
	// LocalMessageCache msgCache = LocalMessageCache.getInstance();

	private final int BUFFER_SIZE = 102400;
	private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

	private SelectionKey sessionKey;
	private SocketChannel channel;
	// private String sessionId;
	private int packSize, summarySize;
//	private static Logger logger = Logger.getLogger(ClientWriter.class);

	/**
	 * @param name
	 */
	public ClientReader(SelectionKey key) {
		super("[ClientReader]");
		this.sessionKey = key;
		this.channel = (SocketChannel) this.sessionKey.channel();
	}

	@Override
	public void run() {
		System.out.println("[ClientReader][Reader线程初始化完毕]");
		while (isRun) {
			try {
				synchronized (this) {
					this.wait();
				}
				// 回收会话，结束线程，直接退出
				if (!isRun) {
					break;
				}
				this.buffer.clear();
				this.buffer.flip();
				this.getCompleteMsg();
			} catch (Throwable ex) {
				System.out.println("[ClientReader]线程异常");
				AndroidLogger.printLog(ex);
				break;
			}
		}

		try {
			ClientSession.getInstance().closeSession();
		} catch (Throwable e) {
			AndroidLogger.printLog(e);
		}
		System.out.println("[ClientReader 结束运行]");
	}

	/**
	 * 解析数据包
	 * 
	 * @param isNewPack
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月15日下午10:44:55
	 */
	private void getCompleteMsg() throws Exception {
		// System.out.println("getCompleteMsg 开始读取数据");
		String summary = "", ok = "";
		Message message = new Message();
		// 处理完毕之后等待
		this.getHeader();
		summary = this.getSummary();
		Summary sumy = JSONObject.parseObject(summary, Summary.class);
		message.setSumy(sumy);

		if (MsgType.TXT_MSG.getValue().equals(sumy.getMsgType()) || MsgType.GROUP_SHARE.getValue().equals(sumy.getMsgType())) {// 文本消息
			message.setBody(this.getMsg());
		} else {// 文件消息
			// message.setBody(sumy.getFileName());
			this.getFile(this.packSize - 8 - this.summarySize - 2, sumy.getFileName());
		}
		ok = this.getOK();
		if ("ok".equalsIgnoreCase(ok)) {
			// ------------------------------------------------------------------------
			//
			// 1、 服务端和客户端共用一套Reader，此处代码特意针对客户端心跳程序
			// 2、 为保持客户端心跳封装性，针对客户端的心跳响应,Reader不做抛出处理。
			// 3、服务器端不做此考虑
			// 4、此处代码破坏了程序的完整性，实在有点垃圾,不得已而为之，这个世界就是这样，容不了完美的东西，憾之
			// --------------------------------------------------------------------------

			long time = System.currentTimeMillis();
			ClientHeartBeatRunner.getInstance().updatResponseTime(time);
			System.out.println("------------------------------");
			System.out.println(JSON.toJSONString(message));
			System.out.println("------------------------------");
			if (!SysEvent.HEART_BEAT.getValue().equals(sumy.getRequestKey())) {
				// 触发事件
				cmc.addReceiveMsg(message);
				ClientMessageListener.getInstance().wakeUp();
				// eventMgr.fireNewMessageEvent(sessionId, message);
			}

			// ---------------------- -----------
			// -------------处理粘包情况-----------
			// ----------------------------------
			if (this.buffer.remaining() > 0) { // 当前数据包中还有未处理完数据(粘包),接着处理
				// System.out.println("\t\t[读取粘包消息]");
				getCompleteMsg();
			} else {// 打开事件监听，等待获取其他数据
				this.openReadEvent(this.sessionKey);
			}

		} else {
			System.out.println("[ClientReader][数据包解析异常]");
		}
	}

	/**
	 * 获取包长和摘要长度
	 * 
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月14日下午11:35:33
	 */
	private void getHeader() throws Exception {
		byte[] bytes = this.getData(8);
		ByteBuffer bf = ByteBuffer.allocate(8);
		bf.put(bytes);
		bf.flip();
		this.packSize = bf.getInt();
		this.summarySize = bf.getInt();
		// System.out.println("[Reader][包总大小][" + packSize + "][摘要大小][" + this.summarySize + "]");
	}

	/**
	 * 获取摘要
	 * 
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月14日下午11:38:37
	 */
	private String getSummary() throws Exception {
		byte[] bytes = this.getData(this.summarySize);
		return new String(bytes);
	}

	/**
	 * 获取文本消息
	 * 
	 * @return
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月14日下午11:43:48
	 */
	private JSONObject getMsg() throws Exception {
		byte[] bytes = this.getData(this.packSize - 8 - this.summarySize - 2);
		return JSONObject.parseObject(new String(bytes));
	}

	/**
	 * 获取结束标识符
	 * 
	 * @return
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月14日下午11:44:26
	 */
	private String getOK() throws Exception {
		byte[] bytes = this.getData(2);
		return new String(bytes);
	}

	/**
	 * 获取文件
	 * 
	 * @param buffer
	 * @param sc
	 * @param length
	 * @author Yongchao.Yang
	 * @throws Exception
	 * @since 2014年10月15日下午10:04:27
	 */
	private void getFile(int fileSize, String fileName) throws Exception {
		String filePath = FileHelper.getMsgFileSavePath();
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(filePath + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		FileChannel fc = fos.getChannel();
		int dataIndex = 0;
		while (true) {
			if (dataIndex < fileSize) {// 如果没有获取足够数据，则获取知道完整，然后退出
				int remain = this.buffer.remaining();
				int byteRemain = fileSize - dataIndex;
				if (remain > 0) {
					if (byteRemain > remain) {// 文件长度大于缓存长度，直接读取整个缓存
						fc.write(this.buffer);
						dataIndex = dataIndex + remain;
					} else {
						byte[] bytes = new byte[byteRemain];
						this.buffer.get(bytes, 0, byteRemain);
						ByteBuffer bb = ByteBuffer.allocate(byteRemain);
						bb.put(bytes);
						bb.flip();
						fc.write(bb);
						dataIndex = dataIndex + remain;
					}
				} else {
					this.getBytes(byteRemain > BUFFER_SIZE ? BUFFER_SIZE : byteRemain);
				}
			} else {
				break;
			}
		}

		fos.flush();
		fos.close();
		fc.close();

	}

	/**
	 * 从byteBuffer缓存中读取指定长度的字节
	 * 
	 * @param buffer
	 * @param sc
	 * @param length
	 * @return
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月14日下午11:18:19
	 */
	private byte[] getData(int length) throws Exception {
		byte[] bytes = new byte[length];
		int dataIndex = 0;
		while (true) {
			if (dataIndex < length) {// 如果没有获取足够数据，则获取知道完整，然后退出
				int remain = this.buffer.remaining();
				int byteRemain = length - dataIndex;
				if (remain > 0) {
					int readCnt = byteRemain > remain ? remain : byteRemain;
					buffer.get(bytes, dataIndex, readCnt);
					dataIndex = dataIndex + readCnt;
				} else {
					this.getBytes(byteRemain > BUFFER_SIZE ? BUFFER_SIZE : byteRemain);
				}
			} else {
				break;
			}
		}
		return bytes;
	}

	/**
	 * 结束线程
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月18日下午1:21:04
	 */
	public void stopRun() {
		System.out.println("[ClientReader][stopRun][运行状态:" + isRun + "]");
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
			this.notify();
		}
	}

	/**
	 * 开始监听
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月18日下午12:33:03
	 */
	public void startRun() {
		System.out.println("[ClientReader][startRun][运行状态:" + isRun + "]");
		if (!isRun) {
			isRun = true;
		}
		this.start();
	}

	/**
	 * 促使线程开始工作
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月18日下午2:23:14
	 */
	public void wakeUp() {
		synchronized (this) {
			this.notifyAll();
		}
	}

	/**
	 * 读取网路字节
	 * 
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月18日下午1:31:47
	 */
	private void getBytes(int maxSize) throws Exception {
		this.buffer.clear();
		int byteSize = this.channel.read(buffer);
		if (byteSize == -1) {
			System.out.println("[[ClientReader][读取字节-1]");
			return;
		}
		while (byteSize < maxSize) {
			int readSize = this.channel.read(buffer);
			if (readSize == -1) {
				System.out.println("[ClientReader][读取字节-1]");
				return;
			}
			// 解决cpu消耗过高的问题
			if (byteSize == 0) {
				synchronized (this) {
					this.wait(50);
				}
			}
			byteSize = byteSize + readSize;
		}

		// System.out.println("\t\t[Reader][读取字节][" + byteSize + "]");
		this.buffer.flip();
	}

	/**
	 * 打开读事件监听功能
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年10月17日上午11:32:17
	 */
	private void openReadEvent(SelectionKey key) {
		key.interestOps(key.interestOps() | SelectionKey.OP_READ);
		key.selector().wakeup();
	}
}
