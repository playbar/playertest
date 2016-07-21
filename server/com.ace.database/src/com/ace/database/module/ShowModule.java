/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月3日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.web.service
 *                  fileName：ShowFun.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.module;

import java.util.ArrayList;

import com.rednovo.ace.entity.LiveShow;

/**
 * @author yongchao.Yang/2016年3月3日
 */
public class ShowModule extends BasicModule {

	/**
	 * 
	 */
	public ShowModule() {

	}

	public String addShow(String userId, String title, String image, String position) {
		LiveShow ls = new LiveShow();
		ls.setUserId(userId);
		ls.setTitle(title);
		ls.setPosition(position);
		return this.getShowDao().addShow(ls);

	}

	public LiveShow getLiveShow(String showId) {
		return this.getShowDao().getLiveShow(showId);
	}

	public String refreshData(String userId, String length, String memberCnt, String fansCnt, String supportCnt, String coinCnt, String shareCnt) {
		return this.getShowDao().refreshData(userId, length, memberCnt, fansCnt, supportCnt, coinCnt, shareCnt);
	}

	public ArrayList<LiveShow> getSynLiveShow(String showId, int maxCnt) {
		return this.getShowDao().getSynLiveShow(showId, maxCnt);
	}

	public ArrayList<String> updateSort(int page, int pageSize) {
		return this.getShowDao().updateSort(page, pageSize);
	}

	public String moveShowData(String userId) {
		return this.getShowDao().moveShowData(userId);
	}

	public ArrayList<LiveShow> getShow(int page, int pageSize) {
		return this.getShowDao().getShow(page, pageSize);
	}
}
