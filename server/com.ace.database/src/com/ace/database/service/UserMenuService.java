/*  ------------------------------------------------------------------------------ 
 *                  软件名称:他秀手机版
 *                  公司名称:多宝科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年7月15日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自多宝科技研发部，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.duobao.video.logic
 *                  fileName：UserService.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.service;

import java.util.ArrayList;

import javax.transaction.UserTransaction;
import org.apache.log4j.Logger;
import com.ace.database.ds.UserTransactionManager;
import com.ace.database.module.UserModule;
import com.rednovo.ace.constant.Constant;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class UserMenuService {
	private static Logger logger = Logger.getLogger(UserMenuService.class);

	public UserMenuService() {}

	/**
	 * @param userId
	 * @param menus
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月13日下午3:59:47
	 */
	public static String updateUserMenu(String userId, String menus) {
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule uf = new UserModule();
		try {
			ut.begin();
			exeRes = uf.updateUserMenu(userId, menus);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
			} else {
				ut.commit();
			}
		} catch (Exception e) {
			try {
				logger.error("[修改用户权限 " + userId + " 失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			uf.release();
		}
		return exeRes;

	}

	/**
	 * @param userId
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月13日下午4:43:37
	 */
	public static ArrayList<String> getUserMenu(String userId) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		ArrayList<String> list = new ArrayList<String>();
		try {
			ut.begin();
			list = um.getUserMenu(userId);
			ut.commit();
		} catch (Exception e) {
			logger.error("[查询manager用户menu失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改用户状态回滚事务错误]", e1);
			}
		} finally {
			um.release();
		}
		return list;

	}

	/**
	 * 查询manager用户
	 * 
	 * @param userId
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月13日下午1:57:45
	 */
	public static boolean isManager(String userId) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule um = new UserModule();
		boolean exeCode = false;
		try {
			ut.begin();
			exeCode = um.isManager(userId);
			ut.commit();
		} catch (Exception e) {
			logger.error("[查询manager用户失败]", e);
		} finally {
			um.release();
		}
		return exeCode;

	}
}
