/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月4日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.database
 *                  fileName：AccountFun.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.module;

import java.util.ArrayList;

import com.rednovo.ace.entity.StatisticsGift;


/**
 * 用户礼物数据统计
 * @author lijiang/2016.6.22
 */
public class StatisticsGiftModule extends BasicModule{
	
	public StatisticsGiftModule() {

	}
	
	/**
	 * 获取客户礼物统计信息
	 * @param day
	 * @param senderId
	 * @param receiverId
	 * @param giftId
	 * @return
	 * @author lijiang/2016.6.22
	 */
	public String getStatisticsGiftInfo(String day, String senderId,String receiverId,String giftId) {
		return this.getStatisticsGiftDao().getStatisticsGiftInfo(day, senderId, receiverId, giftId);
	}
	
	/**
	 * 更新客户礼物统计信息
	 * @param day
	 * @param senderId
	 * @param receiverId
	 * @param giftId
	 * @param cnt
	 * @author lijiang/2016.6.22
	 */
	public String updateStatisticsGiftInfo(String day, String senderId,String receiverId,String giftId,int cnt) {
		return this.getStatisticsGiftDao().updateStatisticsGiftInfo(day, senderId, receiverId, giftId,cnt);
	}
	
	/**
	 * 新增客户礼物统计信息
	 * @param day
	 * @param senderId
	 * @param receiverId
	 * @param giftId
	 * @param cnt
	 * @author lijiang/2016.6.22
	 */
	public String addStatisticsGiftInfo(String day, String senderId,String receiverId,String giftId,int cnt) {
		return this.getStatisticsGiftDao().addStatisticsGiftInfo(day, senderId, receiverId, giftId,cnt);
	}
	
	/**
	 * 根据客户ID 和 时间获取礼物统计信息
	 * 
	 * @param starttime 开始时间
	 * @param endtime 结束时间
	 * @param senderId 送礼用户ID
	 * @param receiverId 收礼用户ID
	 * @return
	 * @author lijiang/2016.6.22
	 */
	public ArrayList<StatisticsGift> getUserStatisticsGiftInfo(String starttime, String endtime, String senderId, String receiverId) {
		return this.getStatisticsGiftDao().getUserStatisticsGiftInfo(starttime, endtime, senderId, receiverId);
	}
}
