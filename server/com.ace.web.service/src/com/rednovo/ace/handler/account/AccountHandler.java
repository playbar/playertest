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
package com.rednovo.ace.handler.account;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ace.database.service.AccountService;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.AccountDetail;
import com.rednovo.ace.entity.IncomeDetail;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.ace.handler.BasicServiceAdapter;

/**
 * @author sg.z / 2014年7月29日 下午4:04:10 用户管理控制器类
 */
public class AccountHandler extends BasicServiceAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if (StringUtils.equals("001-002", key)) {// 读取用户资料
			this.getBlance();
		} else if ("001-006".equals(key)) {
			this.getAccountDetailList();
		} else if ("001-011".equals(key)) {
			this.getIncomeDetailList();
		} else if ("001-032".equals(key)) {// 更新金币同时维护消费记录
			this.upadateBlance();
		}
		this.setSuccess();
	}

	/**
	 * 更新金币同时维护消费记录
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月8日下午6:05:58
	 */
	private void upadateBlance() {
		String userId = this.getWebHelper().getString("userId");
		//系统用户
		String relateUserId = Constant.SysUser.SERVER.getValue();
		String amount = this.getWebHelper().getString("amount");
		String exeCode = AccountService.updateBalance(userId, relateUserId, amount);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 获取用户资料
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月3日上午10:34:46
	 */
	private void getBlance() {
		String userId = this.getWebHelper().getString("userId");
		String balance = UserManager.getBalance(userId);
		if (balance == null) {
			this.setValue("blance", "0");
		} else {
			this.setSuccess();
			this.setValue("blance", balance);
		}
	}

	/**
	 * 消费明细
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月3日下午10:34:45
	 */
	private void getAccountDetailList() {
		String userId = this.getWebHelper().getString("userId");
		String beginTime = this.getWebHelper().getString("startTime");
		String endTime = this.getWebHelper().getString("endTime");
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");
		String channel = this.getWebHelper().getString("channel");
		String logicType = this.getWebHelper().getString("logicType");
		HashMap<String, ArrayList<AccountDetail>> map = AccountService.getAccountDetail(userId, Constant.ChangeType.ADD.getValue().equals(channel) ? Constant.ChangeType.ADD : Constant.ChangeType.REDUCE, beginTime, endTime, page, pageSize, logicType);

		String exeCode = map.keySet().iterator().next();
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
			this.setValue("accountList", map.values().iterator().next());
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 获取兑点明细
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午3:29:05
	 */
	private void getIncomeDetailList() {
		String userId = this.getWebHelper().getString("userId");
		String beginTime = this.getWebHelper().getString("startTime");
		String endTime = this.getWebHelper().getString("endTime");
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");
		String channel = this.getWebHelper().getString("channel");

		HashMap<String, ArrayList<IncomeDetail>> map = AccountService.getIncomeDetail(userId, Constant.ChangeType.ADD.getValue().equals(channel) ? Constant.ChangeType.ADD : Constant.ChangeType.REDUCE, beginTime, endTime, page, pageSize);

		String exeCode = map.keySet().iterator().next();
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
			this.setValue("incomeDetailList", map.values().iterator().next());
		} else {
			this.setError(exeCode);
		}
	}

}
