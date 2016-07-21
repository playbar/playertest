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
package com.rednovo.ace.communication.client;

import java.nio.channels.SelectionKey;

/**
 * 工作线程池
 * 
 * @author yongchao.Yang/2014年10月14日
 */
public class ClientWorkerPool {

	private ClientReader reader = null;
	private ClientWriter writer = null;

	private static ClientWorkerPool pool = null;

	public synchronized static ClientWorkerPool getInstance() {
		if (pool == null) {
			pool = new ClientWorkerPool();
		}
		return pool;
	}

	/**
	 * 
	 */
	private ClientWorkerPool() {}

	/**
	 * 注册新的会话。
	 * 
	 * @param key
	 * @param mgr
	 * @author Yongchao.Yang
	 * @since 2014年10月22日下午12:30:56
	 */
	public void registChannel(SelectionKey key) {
		this.addReader(key);
		this.addWriter(key);
	}

	/**
	 * 获取空闲线程
	 * 
	 * @author Yongchao.Yang
	 * @throws Exception
	 * @since 2014年10月14日下午2:53:00
	 */
	public ClientWriter getWriter() {
		return this.writer;
	}

	/**
	 * 获取可用的读取线程.如果忙碌，会重试5次，每次等待时长为waitTime
	 * 
	 * @return
	 * @throws Exception
	 * @author Yongchao.Yang
	 * @since 2014年10月14日下午3:03:47
	 */
	public ClientReader getReader() {
		return this.reader;
	}

	/**
	 * 停止运行reader和 writer
	 * 
	 * @param id
	 * @author Yongchao.Yang
	 * @since 2014年10月21日下午1:00:14
	 */
	public void stopWorker() {
		this.reader.stopRun();
		this.writer.stopRun();

	}

	private void addReader(SelectionKey key) {
		this.reader = new ClientReader(key);
		this.reader.startRun();
	}

	private void addWriter(SelectionKey key) {
		this.writer = new ClientWriter(key);
		this.writer.startRun();
	}

}
