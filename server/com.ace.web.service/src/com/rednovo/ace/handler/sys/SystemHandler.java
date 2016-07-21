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
package com.rednovo.ace.handler.sys;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.rednovo.ace.entity.AD;
import com.rednovo.ace.entity.Server;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.KeyGenerator;
import com.rednovo.tools.Validator;
import com.rednovo.tools.sms.SendSMS;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class SystemHandler extends BasicServiceAdapter {
	private Logger logger = Logger.getLogger(SystemHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if ("009-001".equals(key)) {// 读取用户资料
			this.getSysConfig();
		} else if ("009-002".equals(key)) {
			this.getAD();
		} else if ("009-003".equals(key)) {
			this.sendSMS();
		} else if ("008-001".equals(key)) {
			this.getSessionServer();
		}
	}

	/**
	 * 开播
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月8日下午2:49:04
	 */
	private void getAD() {
		List<AD> list = StaticDataManager.getBannerList();
		this.setValue("ad", list);
		this.setSuccess();
	}

	/**
	 * 系统总控参数
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月13日下午2:18:23
	 */
	private void getSysConfig() {

		Map<String, String> config = StaticDataManager.getSysConfig();
		Iterator<String> keys = config.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			String val = config.get(key);
			this.setValue(key, val);
		}

		this.setSuccess();
	}

	private void sendSMS() {
		String mobileNo = this.getWebHelper().getString("mobileNo");
		String code = UserManager.getVerifyCode(mobileNo);

		if (!Validator.isEmpty(code)) {
			this.setError("214");
			return;
		}

		String verifyCode = KeyGenerator.getSixRandom();
		boolean isOk = SendSMS.sendSms(mobileNo, verifyCode);
		if (isOk) {
			UserManager.addVerifyCode(mobileNo, verifyCode);
			this.setSuccess();
		} else {
			this.setError("220");
		}
	}

	/**
	 * 获取会话服务器列表
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月21日上午11:00:36
	 */
	private void getSessionServer() {

		Server s1 = new Server();
		s1.setId("246");
		s1.setIp("119.29.68.246");
		s1.setPort(9999);

		Server s2 = new Server();
		s2.setId("28");
		s2.setIp("119.29.89.28");
		s2.setPort(9999);

		Server s3 = new Server();
		s3.setId("053");
		s3.setIp("119.29.96.53");
		s3.setPort(9999);

		Random r = new Random();
		while (true) {
			int next = r.nextInt(100);
			if (next < 3) {
				continue;
			}
			int val = next % 3;
			if (val == 0) {
				this.setValue("server", s1);
			} else if (val == 1) {
				this.setValue("server", s2);
			} else if (val == 2) {
				this.setValue("server", s3);
			}
			break;
		}

	}

}
