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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.Gift;
import com.rednovo.ace.entity.GiftDetail;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.tools.DateUtil;

/**
 * @author yongchao.Yang/2016年3月3日
 */
public class GiftModule extends BasicModule {

	public GiftModule() {

	}

	public Gift getGift(String id) {
		return this.getGiftDao().getGift(id);
	}

	/**
	 * 赠送礼物明细
	 * 
	 * @param detail
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日下午1:29:17
	 */
	public String addGiftDetail(String senderId, String receiverId, String giftId, int giftCnt) {
		Gift g = this.getGiftDao().getGift(giftId);
		if (g == null) {
			return "500";
		}

		BigDecimal consumeVal = g.getSendPrice().multiply(new BigDecimal(giftCnt));

		GiftDetail gcd = new GiftDetail();
		gcd.setUserId(senderId);
		gcd.setChannel(Constant.logicType.SEND_GIFT.getValue());
		gcd.setCreateTime(DateUtil.getStringDate());
		gcd.setDescription(senderId + "消费" + consumeVal.floatValue() + "金币,送给用户(" + receiverId + ")" + giftCnt + "个" + g.getName());
		gcd.setGiftCnt(giftCnt);
		gcd.setGiftId(giftId);
		gcd.setGiftName(g.getName());
		gcd.setPrice(g.getSendPrice());
		gcd.setRelateUserId(receiverId);
		gcd.setRelateUserName(UserManager.getUser(receiverId).getNickName());
		gcd.setTotalValue(consumeVal);
		gcd.setUserName("");

		String exeRes = this.getGiftDao().addGiftChangeDetail(gcd, Constant.ChangeType.REDUCE);
		if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {// 添加礼物送出记录
			return exeRes;
		}

		// 添加接收明细
		gcd.setUserId(receiverId);
		gcd.setUserName("");
		gcd.setRelateUserId(senderId);
		gcd.setRelateUserName("");
		gcd.setDescription(receiverId + "收到" + senderId + "礼物(" + g.getName() + ")" + giftCnt + "个,消费金币" + consumeVal.floatValue());
		return this.getGiftDao().addGiftChangeDetail(gcd, Constant.ChangeType.ADD);
	}

	/**
	 * 获取礼物进出账明细
	 * 
	 * @param userId
	 * @param beginTime
	 * @param endTime
	 * @param type
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午4:23:18
	 */
	public HashMap<String, ArrayList<GiftDetail>> getGiftDetailList(String userId, String beginTime, String endTime, Constant.ChangeType type, int page, int pageSize) {
		return this.getGiftDao().getGiftDetailList(userId, beginTime, endTime, type, page, pageSize);
	}

	public ArrayList<Gift> getSynGift() {
		return this.getGiftDao().getGift();
	}
	
	/**
	 * 添加或者修改礼物
	 * @param gift
	 * @return
	 */
	public String addOrUpdateGift(Gift gift){
		return this.getGiftDao().addOrUpdateGift(gift);
	}
	
	/**
	 * 查询上架，下架，待上架数据
	 * @param status
	 * @return
	 */
	public List<Gift> getGiftByStatus(String status){
		return this.getGiftDao().getGiftByStatus(status);
	}
	
	/**
	 * 获取需要同步的收礼信息
	 * @param synId
	 * @param maxCnt
	 * @return
	 */
	public ArrayList<GiftDetail> getSynReceiveGiftDetailList(String synId, int maxCnt){
		return this.getGiftDao().getSynReceiveGiftDetailList(synId, maxCnt);
	}
}
