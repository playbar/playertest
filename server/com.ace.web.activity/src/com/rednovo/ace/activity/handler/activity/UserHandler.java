/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播移动
 *                  公司名称:美播娱乐
 *                  开发作者:sg.z
 *       			开发时间:2014年7月29日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自美播娱乐研发部，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：meibo-admin
 *                  fileName：UserHandler.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.activity.handler.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.rednovo.ace.activity.entity.User;
import com.rednovo.ace.activity.handler.BasicServiceAdapter;
import com.rednovo.ace.activity.service.UserService;
import com.rednovo.ace.constant.Constant;
import com.rednovo.tools.KeyGenerator;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.UserAssistant;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class UserHandler extends BasicServiceAdapter {
	Logger logger = Logger.getLogger(UserHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if ("001-001".equals(key)) {// 报名
			this.createUser();
		} else if ("001-002".equals(key)) {// 投票
			this.doVote();
		} else if ("001-003".equals(key)) {// 投票
			this.checkUser();
		} else if ("001-004".equals(key)) {// 金榜数据
			this.getCatonlist();
		}
	}

	/**
	 * 
	 * @author ZuKang.Song
	 * @since 2016年5月10日下午4:02:54
	 */
	private void checkUser() {
		String activityId = this.getWebHelper().getString("activityId");
		String userId = this.getWebHelper().getString("userId");
		// 根据userId, activityId查询是否有用户记录，flag为1时表示已经注册
		String flag = UserService.checkUser(userId, activityId);
		if (!"0".equals(flag)) {
			// 已注册
			// this.setError("210");
			this.setSuccess();
			this.setValue("flag", "false");
			return;
		}
		this.setSuccess();
		this.setValue("flag", "true");
		return;

	}

	/**
	 * 投票
	 * 
	 * @author ZuKang.Song
	 * @since 2016年5月7日下午5:36:57
	 */
	private void doVote() {

	}

	/**
	 * 创建用户
	 * 
	 * @author ZuKang.Song
	 * @since 2016年3月6日下午11:34:34
	 */
	private void createUser() {

		String name = this.getWebHelper().getString("name");
		String school = this.getWebHelper().getString("school");
		String weixin = this.getWebHelper().getString("weixin");
		String qq = this.getWebHelper().getString("qq", "");
		String specialty = this.getWebHelper().getString("specialty", "");
		String activityId = this.getWebHelper().getString("activityId");
		String userId = this.getWebHelper().getString("userId");
		String channel = this.getWebHelper().getString("channel");
		byte[] data = this.getWebHelper().getBytes("file");
		String suffix = ".png";

		String id = KeyGenerator.createUniqueId();
		String visitUrl = "";
		if (data != null) {

			// 保存图像
			String path = UserAssistant.getUserAbsoluteDir(userId);
			UserAssistant.makeUserDir(userId);
			File img = new File(path + File.separator + userId + "-profile" + suffix);
			if (!img.exists()) {
				try {
					img.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileOutputStream fis;
			try {
				fis = new FileOutputStream(img);
				fis.write(data);
				fis.flush();
				fis.close();
			} catch (Exception e) {
				logger.error("上传图像失败", e);
				e.printStackTrace();
			} 
			visitUrl = PPConfiguration.getProperties("cfg.properties").getString("img.server.root.url") + UserAssistant.getUserRelativeDir(userId) + "/" + userId + "-profile" + suffix;
		}
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setSchool(school);
		user.setWeixin(weixin);
		user.setQq(qq);
		user.setSpecialty(specialty);
		user.setActivityId(activityId);
		user.setUserId(userId);
		user.setChannel(channel);
		user.setProfile(visitUrl);
		String exeCode = UserService.addUser(user);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			// 创建用户目录
			// UserAssistant.makeUserDir(id);
			this.setSuccess();
			this.setValue("user", "123");
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 金榜的数据
	 * @return
	 * @author Lei.Zang
	 * @since  2016年5月13日下午12:01:29
	 */
	private String getCatonlist(){
		List catonlist = UserService.getCatonlist();
		if(catonlist!=null){
	
			this.setValue("list", catonlist);
		}
		return "";
	}

	
}
