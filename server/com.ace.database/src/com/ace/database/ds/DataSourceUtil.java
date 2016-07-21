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
package com.ace.database.ds;

import java.util.ArrayList;

/**
 * 数据源管理器
 * 
 * @author Administrator
 * 
 */
public class DataSourceUtil {
	private static String DB_DATA_SOURCE_ACE_USER = "ace_user";
	private static String DB_DATA_SOURCE_ACE_ACCOUNT = "ace_account";
	private static String DB_DATA_SOURCE_ACE_SHOW = "ace_show";
	private static String DB_DATA_SOURCE_ACE_ORDER = "ace_order";
	private static String DB_DATA_SOURCE_ACE_GIFT = "ace_gift";
	private static String DB_DATA_SOURCE_ACE_STATISTICS = "ace_statistics";

	private static ArrayList<String> dsList = new ArrayList<String>();

	static {
		dsList.add(DB_DATA_SOURCE_ACE_USER);
		dsList.add(DB_DATA_SOURCE_ACE_ACCOUNT);
		dsList.add(DB_DATA_SOURCE_ACE_SHOW);
		dsList.add(DB_DATA_SOURCE_ACE_ORDER);
		dsList.add(DB_DATA_SOURCE_ACE_GIFT);
		dsList.add(DB_DATA_SOURCE_ACE_STATISTICS);
	}

	public static ArrayList<String> getDataSourceList() {
		return dsList;
	}

	public static String getUserDataSource() {
		return DB_DATA_SOURCE_ACE_USER;
	}

	public static String getAccountDataSource() {
		return DB_DATA_SOURCE_ACE_ACCOUNT;
	}

	public static String getShowDataSource() {
		return DB_DATA_SOURCE_ACE_SHOW;
	}

	public static String getOrderDataSource() {
		return DB_DATA_SOURCE_ACE_ORDER;
	}
	
	public static String getGiftDataSource() {
		return DB_DATA_SOURCE_ACE_GIFT;
	}

	public static String getStatisticsDataSource() {
		return DB_DATA_SOURCE_ACE_STATISTICS;
	}
}
