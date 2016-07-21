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
package com.rednovo.ace.communication.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.communication.EventInvokerManager;
import com.rednovo.ace.communication.FileHelper;
import com.rednovo.ace.communication.server.handler.LocalSessoinManager;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.tools.KeyGenerator;

/**
 * 服务器端网络字节读取线程
 * 
 * @author yongchao.Yang/2014年10月18日
 */
public class ServerReader extends Thread {
	private volatile boolean isRun = true;
	private Logger logger = Logger.getLogger(getClass());

	private EventInvokerManager eventMgr = EventInvokerManager.getInstance();

	private LocalSessoinManager usm = LocalSessoinManager.getInstance();
	/**
	 * 消息缓存
	 */
	LocalMessageCache msgCache = LocalMessageCache.getInstance();

	private final int BUFFER_SIZE = 1048576;

	private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

	private SelectionKey sessionKey;
	private SocketChannel channel;
	private String sessionId;
	private int packSize, summarySize;

	/**
	 * @param name
	 */
	public ServerReader(SelectionKey key) {
		super(key.attachment().toString());
		this.sessionKey = key;
		this.sessionId = this.sessionKey.attachment().toString();
		this.channel = (SocketChannel) this.sessionKey.channel();
	}

	@Override
	public void run() {
		while (isRun) {
			try {
				synchronized (this) {
					// logger.debug("\t\t[ServerReader] [wait] [" + System.nanoTime() + "," + System.currentTimeMillis() + "]");
					this.wait();
				}
				// // 回收会话，结束线程，直接退出
				if (!isRun) {
					break;
				}
				this.buffer.clear();
				this.buffer.flip();
				this.getCompleteMsg();
			} catch (Exception le) {
				// eventMgr.fireSessionSuspendEvent(sessionId);// 通知通信异常
				logger.error("[ServerReader][可能因为对方中断连接而终止工作]", le);
				break;
			}
		}
		// 回收资源
		usm.releaseSession(sessionId);
		logger.info("[ServerReader][结束运行 (" + sessionId + ") ]");
	}

	/**
	 * 解析数据包
	 * 
	 * @param isNewPack
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @throws InterruptedException
	 * @throws LinkedException
	 * @throws IOException
	 * @since 2014年10月15日下午10:44:55
	 */
	private void getCompleteMsg() throws Exception {
		String summary = "", ok = "";
		Message message = new Message();
		// 处理完毕之后等待
		this.getHeader();
		summary = this.getSummary();
		// logger.debug("\t\t[ServerReader][summary][" + summary + "]");
		Summary sumy = JSONObject.parseObject(summary, Summary.class);
		message.setSumy(sumy);
		String msgType = sumy.getMsgType();
		if (MsgType.TXT_MSG.getValue().equals(msgType)) {// 文本消息
			message.setBody(this.getMsg());
		} else {
			this.createFile(this.packSize - 8 - this.summarySize - 2, sumy);
		}

		ok = this.getOK();
		if ("ok".equalsIgnoreCase(ok)) {
			logger.debug("[ServerReader][新消息][" + JSON.toJSONString(message) + "]");
			// 触发事件
			eventMgr.fireNewMessageEvent(sessionId, message);
			// logger.debug("\t\t[ServerReader][新消息][" + JSON.toJSONString(message) + "]");

			// ---------------------- -----------
			// -------------处理粘包情况-----------
			// ----------------------------------
			if (this.buffer.remaining() > 0) { // 当前数据包中还有未处理完数据(粘包),接着处理
				logger.debug("[ServerReader][读取粘包消息]");
				getCompleteMsg();
			} else {// 打开事件监听，等待获取其他数据
				// logger.info("\t\t[准备打开读监听事件]");
				this.openReadEvent(this.sessionKey);
				// logger.info("\t\t[读监听事件打开完毕]");
			}

		} else {
			logger.error("[ServerReader][数据包解析异常]");
		}
	}

	/**
	 * 获取包长和摘要长度
	 * 
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @throws InterruptedException
	 * @throws LinkedException
	 * @since 2014年10月14日下午11:35:33
	 */
	private void getHeader() throws Exception {
		byte[] bytes = this.getData(8);
		ByteBuffer bf = ByteBuffer.allocate(8);
		bf.put(bytes);
		bf.flip();
		this.packSize = bf.getInt();
		this.summarySize = bf.getInt();
		// logger.debug("\t\t[获取包头数据]packSize:" + packSize + ",sumrySize:" + summarySize + "]");
	}

	/**
	 * 获取摘要
	 * 
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @throws InterruptedException
	 * @throws LinkedException
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
	 * @throws InterruptedException
	 * @throws LinkedException
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
	 * @throws InterruptedException
	 * @throws LinkedException
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
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws LinkedException
	 * @throws Exception
	 * @since 2014年10月15日下午10:04:27
	 */
	private void createFile(int fileSize, Summary smy) throws Exception {
		String filePath = FileHelper.getDir(smy);
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String fileName = smy.getFileName();
		String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
		String newFileName = KeyGenerator.createUniqueId() + fileSuffix;
		smy.setFileName(newFileName);

		// logger.info("\t\t[ServerReader][文件存储路径]" + filePath + File.separator + fileName);
		File msgFile = new File(filePath + File.separator + newFileName);
		if (!msgFile.exists()) {
			logger.info("[ServerReader ]" + msgFile.getPath());
			msgFile.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(msgFile);
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
	 * @throws InterruptedException
	 * @throws LinkedException
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
	public void stopWork() {
		// User user = RedisCache.getUser(usm.getSessionUser(this.sessionId));
		// 先关闭通道
		try {
			// if (this.channel.isOpen()) {
			logger.info("[ServerReader][关闭 channel (" + this.sessionId + " ) ]");

			this.channel.close();
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 再取消所有已经注册的事件
		try {
			// if (this.sessionKey.isValid()) {
			logger.info("[ServerReader][取消sessionKey (" + this.sessionId + ") ]");

			this.sessionKey.cancel();
			// }
		} catch (Exception e) {
			logger.error("[ServerReader][关闭会话异常  (" + sessionId + ") ]", e);
		}
		this.isRun = false;
		synchronized (this) {
			this.notify();
		}
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
	 * @throws InterruptedException
	 * @since 2014年10月18日下午1:31:47
	 */
	private void getBytes(int maxSize) throws Exception {

		this.buffer.clear();

		int byteSize = 0;

		while (byteSize < maxSize) {

			int readSize = this.channel.read(buffer);
			if (readSize == -1) {
				logger.info("[ServerReader][会话 ( " + sessionId + ") 读取数据完毕]");
				throw new Exception("链接异常");
			}

			// TODO 解决cup过高待检测
			if (readSize == 0) {
				synchronized (this) {
					this.wait(100);
					continue;
				}
			}
			// logger.info("\t\t[ServerReader][接受字节:"+readSize+"]");

			byteSize += readSize;
		}

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

	/**
	 * 结束本次会话。释放所有资源并放出通知
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年11月19日下午3:54:20
	 */
	// private void closeSession() {
	// eventMgr.fireSessionSuspendEvent(sessionId);// 通知通信异常
	// // 解除用户各种状态
	// logger.info("\t\t[ServerReader][释放用户资源及状态]");
	// usm.removeExpiredUser(usm.getSessionUser(sessionId));
	// // 解除会话各种状态
	// logger.info("\t\t[ServerReader][释放会话资源及状态]");
	// usm.releaseSession(sessionId);
	// }
}
