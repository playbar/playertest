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
package com.rednovo.ace.handler.statisics;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ace.database.service.StatisticsGiftService;
import com.rednovo.ace.entity.StatisticsGift;
import com.rednovo.ace.handler.BasicServiceAdapter;

/**
 * @author WenHui.Wang/2016年6月23日
 */
public class StatisticsHandler extends BasicServiceAdapter {
	Logger logger = Logger.getLogger(StatisticsHandler.class);
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if (StringUtils.equals("001-048", key)) {
			this.getStatistics();
		} 
	}
	private void getStatistics() {
		String senderId = this.getWebHelper().getString("senderId");
		String receiverId = this.getWebHelper().getString("receiverId");
		String starttime = this.getWebHelper().getString("startTime");
		String endTime = this.getWebHelper().getString("endTime");
		ArrayList<StatisticsGift> statisticsList =  StatisticsGiftService.getUserStatisticsGiftInfo(starttime,endTime,senderId,receiverId);
		this.setSuccess();
		this.setValue("giftDetailList",statisticsList);
	}

}
