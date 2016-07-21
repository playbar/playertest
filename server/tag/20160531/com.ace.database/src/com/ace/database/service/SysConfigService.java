/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月6日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.database
 *                  fileName：SysConfigService.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.service;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.ace.database.ds.UserTransactionManager;
import com.ace.database.module.OrderModule;
import com.rednovo.ace.constant.Constant;

/**
 * @author yongchao.Yang/2016年3月6日
 */
public class SysConfigService {
	private static Logger logger = Logger.getLogger(SysConfigService.class);

	/**
	 * 
	 */
	public SysConfigService() {
		// TODO Auto-generated constructor stub
	}

	public static String getGlobalVal(String key) {
		String val = null;
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		OrderModule om = new OrderModule();
		try {
			ut.begin();
			val = om.getGlobalVal(key);
			ut.commit();
			return val;

		} catch (Exception e) {
			try {
				logger.error("[获取全局参数" + key + "失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return val;
	}

	public static String setGlobalVal(String key, String val) {
		String exeCode = Constant.OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		OrderModule om = new OrderModule();
		try {
			ut.begin();
			exeCode = om.updateGlobalVal(key, val);
			ut.commit();

		} catch (Exception e) {
			try {
				logger.error("[获取全局参数" + key + "失败]", e);
				ut.rollback();
				exeCode = Constant.OperaterStatus.FAILED.getValue();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return exeCode;

	}

}
