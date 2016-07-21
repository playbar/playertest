/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月8日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.develop.basic
 *                  fileName：UserAssistant.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.tools;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * @author yongchao.Yang/2016年3月8日
 */
public class UserAssistant {
	private static Logger logger = Logger.getLogger(UserAssistant.class);

	/**
	 * 创建用户目录
	 * 
	 * @param userId
	 * @author Yongchao.Yang
	 * @since 2016年3月8日上午9:51:41
	 */
	public static void makeUserDir(String userId) {
		String userDir = UserAssistant.getUserAbsoluteDir(userId);
		File dir = new File(userDir);
		if (!dir.exists()) {
			logger.info("[创建用户父目录]");
			dir.mkdirs();
		} else {
			logger.info("[用户父目录已经存在]");

		}
	}

	/**
	 * 获取用户目录
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月8日上午9:52:52
	 */
	public static String getUserAbsoluteDir(String userId) {
		StringBuffer userDir = new StringBuffer(userId.substring(0, userId.length() - 3));
		int len = userDir.length();
		for (int i = 1; i < len; i++) {
			userDir.insert((i * 2) - 1, File.separator);
		}
		StringBuffer baseDir = new StringBuffer(PPConfiguration.getProperties("cfg.properties").getString("user.photo.path"));
		baseDir.append(File.separator).append(userDir);
		return baseDir.toString();
	}
	/**
	 * 获取用户相对目录
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since  2016年3月8日下午2:41:23
	 */
	public static String getUserRelativeDir(String userId) {
		StringBuffer userDir = new StringBuffer(userId.substring(0, userId.length() - 3));
		int len = userDir.length();
		for (int i = 1; i < len; i++) {
			userDir.insert((i * 2) - 1, "/");
		}
		return userDir.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(UserAssistant.getUserRelativeDir("2125333"));
	}
}


