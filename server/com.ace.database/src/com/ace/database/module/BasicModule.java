/*  ------------------------------------------------------------------------------ 
 * 
 *    				软件名称:泡泡娱乐交友平台(手机版)
 *    				公司名称:北京双迪信息技术有限公司
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2012-8-16/2012
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容仅限于北京双迪信息技术有限公司内部使用 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.popo.transaction
 *                  fileName：com.popo.func.BasicFun.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.module;

import java.sql.Connection;

import com.ace.database.dao.AccountDao;
import com.ace.database.dao.ExchangeDao;
import com.ace.database.dao.GiftDao;
import com.ace.database.dao.OrderDao;
import com.ace.database.dao.ShowDao;
import com.ace.database.dao.StatisticsGiftDao;
import com.ace.database.dao.UserDao;
import com.ace.database.ds.DBReleaser;
import com.ace.database.ds.DataSourceManager;
import com.ace.database.ds.DataSourceUtil;

/**
 * FUNCTION层对象基础类.用户管理各个功能对象中Dao的生成和相关资源的释放
 * 
 * @author YongChao.Yang
 * 
 */
public class BasicModule {
	private UserDao userDao;
	private ShowDao showDao;
	private AccountDao accountDao;
	private OrderDao orderDao;
	private GiftDao giftDao;
	private ExchangeDao exchangeDao;
	private StatisticsGiftDao statisticsGiftDao;
	private Connection userPoolConn, showPoolConn, accountPoolConn, orderPoolConn, giftPoolConn, exchangePoolConn ,statisticsGiftPoolConn;

	// 设备号
	private String deviceNo;

	/**
	 * 获取用户持久层操作DAO对象
	 * 
	 * @return
	 * @author YongChao.Yang/2012-9-5/2012
	 */
	public UserDao getUserDao() {
		if (this.userDao == null) {
			this.userDao = new UserDao(this.getUserPoolConn());
		}
		return this.userDao;
	}

	public ExchangeDao getExchangeDao() {
		if (this.exchangeDao == null) {
			this.exchangeDao = new ExchangeDao(this.getExchangePoolConn());
		}
		return this.exchangeDao;
	}

	public ShowDao getShowDao() {
		if (this.showDao == null) {
			this.showDao = new ShowDao(this.getShowPoolConn());
		}
		return this.showDao;
	}

	public AccountDao getAccountDao() {
		if (this.accountDao == null) {
			this.accountDao = new AccountDao(this.getAccountPoolConn());
		}
		return this.accountDao;
	}

	public GiftDao getGiftDao() {
		if (this.giftDao == null) {
			this.giftDao = new GiftDao(this.getGiftPoolConn());
		}
		return this.giftDao;
	}

	public OrderDao getOrderDao() {
		if (this.orderDao == null) {
			this.orderDao = new OrderDao(this.getOrderPoolConn());
		}
		return this.orderDao;
	}
	
	public StatisticsGiftDao getStatisticsGiftDao() {
		if (this.statisticsGiftDao == null) {
			this.statisticsGiftDao = new StatisticsGiftDao(this.getStatisticsGiftPoolConn());
		}
		return this.statisticsGiftDao;
	}

	private Connection getUserPoolConn() {
		if (this.userPoolConn == null) {
			this.userPoolConn = DataSourceManager.getInstance().getConnection(DataSourceUtil.getUserDataSource());
		}
		return this.userPoolConn;
	}

	private Connection getShowPoolConn() {
		if (this.showPoolConn == null) {
			this.showPoolConn = DataSourceManager.getInstance().getConnection(DataSourceUtil.getShowDataSource());
		}
		return showPoolConn;
	}

	private Connection getAccountPoolConn() {
		if (this.accountPoolConn == null) {
			this.accountPoolConn = DataSourceManager.getInstance().getConnection(DataSourceUtil.getAccountDataSource());
		}
		return this.accountPoolConn;
	}

	private Connection getOrderPoolConn() {
		if (this.orderPoolConn == null) {
			this.orderPoolConn = DataSourceManager.getInstance().getConnection(DataSourceUtil.getOrderDataSource());
		}
		return this.orderPoolConn;
	}

	private Connection getGiftPoolConn() {
		if (this.giftPoolConn == null) {
			this.giftPoolConn = DataSourceManager.getInstance().getConnection(DataSourceUtil.getGiftDataSource());
		}
		return this.giftPoolConn;
	}

	private Connection getExchangePoolConn() {
		if (this.exchangePoolConn == null) {
			this.exchangePoolConn = DataSourceManager.getInstance().getConnection(DataSourceUtil.getAccountDataSource());
		}
		return this.exchangePoolConn;
	}
	
	private Connection getStatisticsGiftPoolConn() {
		if (this.statisticsGiftPoolConn == null) {
			this.statisticsGiftPoolConn = DataSourceManager.getInstance().getConnection(DataSourceUtil.getStatisticsDataSource());
		}
		return this.statisticsGiftPoolConn;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	/**
	 * 释放Function层实例所涉及到的所有数据库连接资源
	 * 
	 * @author YongChao.Yang/2012-9-5/2012
	 */
	public void release() {
		if (this.userDao != null) {
			DBReleaser.release(this.userPoolConn);
		}
		if (this.showDao != null) {
			DBReleaser.release(this.showPoolConn);
		}
		if (this.accountDao != null) {
			DBReleaser.release(this.accountPoolConn);
		}
		if (this.orderDao != null) {
			DBReleaser.release(this.orderPoolConn);
		}
		if (this.giftDao != null) {
			DBReleaser.release(this.giftPoolConn);
		}
		if (this.exchangeDao != null) {
			DBReleaser.release(this.exchangePoolConn);
		}
		if (this.statisticsGiftDao != null) {
			DBReleaser.release(this.statisticsGiftPoolConn);
		}
	}
}
