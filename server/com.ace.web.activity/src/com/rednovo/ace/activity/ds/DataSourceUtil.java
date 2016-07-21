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
package com.rednovo.ace.activity.ds;

import java.util.ArrayList;

/**
 * 数据源管理器
 * 
 * @author Administrator
 * 
 */
public class DataSourceUtil {
	private static String DB_DATA_SOURCE_ACE_ACTIVITY = "ace_activity";
	private static String DB_DATA_SOURCE_ACE_USER = "ace_user";


	private static ArrayList<String> dsList = new ArrayList<String>();

	static {
		dsList.add(DB_DATA_SOURCE_ACE_ACTIVITY);
		dsList.add(DB_DATA_SOURCE_ACE_USER);
	}

	public static ArrayList<String> getDataSourceList() {
		return dsList;
	}

	public static String getActivityUserDataSource() {
		return DB_DATA_SOURCE_ACE_ACTIVITY;
	}

	public static String getServiceUserDataSource() {
		return DB_DATA_SOURCE_ACE_USER;
	}


}
