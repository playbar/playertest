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
 *                  fileName：WorkerPool.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication.server;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 工作线程池
 * 
 * @author yongchao.Yang/2014年10月14日
 */
public class ServerWorkerPool {

	private ArrayList<String> channelList = new ArrayList<String>();

	private HashMap<String, ServerReader> readerPool = new HashMap<String, ServerReader>();
	private HashMap<String, ServerWriter> writerPool = new HashMap<String, ServerWriter>();

	// private static Logger logger = Logger.getLogger(ServerWorkerPool.class);

	private static ServerWorkerPool pool = null;

	public static ServerWorkerPool getInstance() {
		if (pool == null) {
			pool = new ServerWorkerPool();
		}
		return pool;
	}

	/**
	 * 
	 */
	private ServerWorkerPool() {}

	/**
	 * 注册新的会话。
	 * 
	 * @param key
	 * @param mgr
	 * @author Yongchao.Yang
	 * @since 2014年10月22日下午12:30:56
	 */
	public void registChannel(SelectionKey key) {
		String id = key.attachment().toString();
		if (!this.channelList.contains(id)) {
			// logger.debug("[ServerWorkerPool][sessionId:" + id + ",注册读写线程]");
			this.channelList.add(id);
			this.addReader(key);
			this.addWriter(key);
		}
	}

	/**
	 * 获取空闲线程
	 * 
	 * @author Yongchao.Yang
	 * @throws Exception
	 * @since 2014年10月14日下午2:53:00
	 */
	public ServerWriter getWriter(String id) {
		return this.writerPool.get(id);
	}

	/**
	 * 获取可用的读取线程.如果忙碌，会重试5次，每次等待时长为waitTime
	 * 
	 * @return
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月14日下午3:03:47
	 */
	public ServerReader getReader(String id) {
		return this.readerPool.get(id);
	}

	/**
	 * 停止运行reader和 writer
	 * 
	 * @param id
	 * @author Yongchao.Yang
	 * @since 2014年10月21日下午1:00:14
	 */
	public void stopWorker(String id) {
		if (this.channelList.remove(id)) {
			ServerReader r = this.getReader(id);
			r.stopWork();
			ServerWriter w = this.getWriter(id);
			w.stopRun();
			this.readerPool.remove(id);
			this.writerPool.remove(id);
		}

	}

	private synchronized void addReader(SelectionKey key) {
		String id = key.attachment().toString();
		if (!this.readerPool.containsKey(id)) {
			ServerReader reader = new ServerReader(key);
			this.readerPool.put(id, reader);
			reader.start();
		}
	}

	private synchronized void addWriter(SelectionKey key) {
		String id = key.attachment().toString();
		if (!this.writerPool.containsKey(id)) {
			ServerWriter writer = new ServerWriter(key);
			this.writerPool.put(id, writer);
			writer.start();
		}
	}

}
