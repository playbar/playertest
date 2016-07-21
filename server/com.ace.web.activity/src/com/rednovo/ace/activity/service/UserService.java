/*  ------------------------------------------------------------------------------ 
 *                  软件名称:
 *                  公司名称:
 *                  开发作者:ZuKang.Song
 *       			开发时间:2016年5月8日/2016
 *    				All Rights Reserved 2016-2016
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.web.activity
 *                  fileName：UserService.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.activity.service;

import java.util.List;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.rednovo.ace.activity.ds.UserTransactionManager;
import com.rednovo.ace.activity.entity.User;
import com.rednovo.ace.activity.module.UserModule;
import com.rednovo.ace.constant.Constant;


/**
 * @author ZuKang.Song/2016年5月8日
 */
public class UserService {
	private static Logger logger = Logger.getLogger(UserService.class);
	

	/**
	 * @param uid
	 * @return
	 * @author ZuKang.Song
	 * @since  2016年5月8日下午11:41:49
	 */
	public static User getUser(String uid) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * @param user
	 * @return
	 * @author ZuKang.Song
	 * @since  2016年5月8日下午11:46:58
	 */
	public static String addUser(User user) {
		
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule uf = new UserModule();
		try {
			ut.begin();
			exeRes = uf.addUser(user);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
			} else {
				ut.commit();
			}
		} catch (Exception e) {
			try {
				logger.error("[添加用户 " + user.getName() + " 失败]", e);
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
	 * 检测是否能注册
	 * @param userId
	 * @param activityId
	 * @return
	 * @author ZuKang.Song
	 * @since  2016年5月10日上午10:27:38
	 */
	public static String checkUser(String userId, String activityId) {
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		UserModule uf = new UserModule();
		try {
			ut.begin();
			User user = new User();
			user.setUserId(userId);
			user.setActivityId(activityId);
			exeRes = uf.checkUser(user);
			
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
			} else {
				ut.commit();
			}
		} catch (Exception e) {
			try {
				logger.error("[用户 " + userId + " 报名失败]", e);
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
	 * 获取金榜的数据
	 * @return
	 * @author Yongchao.Yang
	 * @since  2016年5月13日下午2:05:23
	 */
	public static List<User> getCatonlist(){
		UserModule uf = new UserModule();
		List<User> catonlist = uf.getCatonlist();	
		for (User user : catonlist) {
			String sickName = uf.getServiceUser(user.getUserId());
			user.setName(sickName);
		}	
		if(catonlist!=null){
			return catonlist;			
		}
		return null;
	}

}
