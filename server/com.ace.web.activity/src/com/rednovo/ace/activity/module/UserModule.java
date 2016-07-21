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
 *                  fileName：UserFunImpl.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.activity.module;

import java.util.List;

import com.rednovo.ace.activity.entity.User;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class UserModule extends BasicModule {

	/**
	 * 
	 */
	public UserModule() {
		// TODO Auto-generated constructor stub
	}

	public String addUser(User user) {
		return this.getUserDao().addUser(user);
	}

	
	public User getActivityUser(String userId) {
		return this.getUserDao().getUser(userId);
	}
	
	public String getServiceUser(String userId) {
		return this.getserviceUserDao().getServiceUser(userId);
	}
	/**
	 * @param user
	 * @return
	 * @author ZuKang.Song
	 * @since  2016年5月10日上午10:41:51
	 */
	public String checkUser(User user) {
		// TODO Auto-generated method stub
		return this.getUserDao().checkUser(user);
	}

	public List<User> getCatonlist(){
		return this.getUserDao().getCatonlist();
	}

}
