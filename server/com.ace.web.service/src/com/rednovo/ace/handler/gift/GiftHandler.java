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
package com.rednovo.ace.handler.gift;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ace.database.service.AccountService;
import com.ace.database.service.GiftService;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.Gift;
import com.rednovo.ace.entity.GiftDetail;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.Validator;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class GiftHandler extends BasicServiceAdapter {
	Logger logger = Logger.getLogger(GiftHandler.class);
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if (StringUtils.equals("001-010", key)) {// 赠送礼物
			this.senGift();
		} else if ("001-012".equals(key)) {// 礼物进出账明细
			this.getGiftDetailList();
		} else if ("001-014".equals(key)) {// 获取礼物列表
			this.getGiftList();
		} else if("001-031".equals(key)){ //添加或者修改礼物
			this.addOrUpdateGift();
		} else if("001-032".equals(key)){ //上架，下架礼物
			this.changeGiftStatus();
		} else if("001-033".equals(key)){ //根据id，查询礼物
			this.getGift();
		} else if("001-035".equals(key)){ //根据状态，查询礼物
			this.getGiftsByStatus();
		}
	}

	/**
	 * 赠送礼物
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午1:03:38
	 */
	private void senGift() {
		String senderId = this.getWebHelper().getString("senderId");
		String showId = this.getWebHelper().getString("showId");
		String receiverId = this.getWebHelper().getString("receiverId");
		String giftId = this.getWebHelper().getString("giftId");
		int giftCnt = this.getWebHelper().getInt("giftCnt");
		if (UserManager.getUser(senderId) == null || UserManager.getUser(receiverId) == null) {
			this.setError("204");
			return;
		}

		String rex = GiftService.sendGift(senderId, receiverId, showId, giftId, giftCnt);

		if (!Constant.OperaterStatus.SUCESSED.getValue().equals(rex)) {
			this.setError(rex);
		} else {
			BigDecimal balance = AccountService.getAccountBalance(senderId);
			System.out.println("balance" + balance.toBigInteger());
			this.setValue("balance", balance.toString());
			this.setSuccess();

		}

	}

	/**
	 * 查询礼物进出账明细
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午4:44:59
	 */
	private void getGiftDetailList() {
		String userId = this.getWebHelper().getString("userId");
		String beginTime = this.getWebHelper().getString("startTime");
		String endTime = this.getWebHelper().getString("endTime");
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");
		String channel = this.getWebHelper().getString("channel");

		HashMap<String, ArrayList<GiftDetail>> map = GiftService.getGiftDetailList(userId, beginTime, endTime, Constant.ChangeType.ADD.getValue().equals(channel) ? Constant.ChangeType.ADD : Constant.ChangeType.REDUCE, page, pageSize);

		String exeCode = map.keySet().iterator().next();
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
			this.setValue("giftDetailList", map.values().iterator().next());
		} else {
			this.setError(exeCode);
		}

	}

	/**
	 * 获取礼物列表
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月9日上午1:26:41
	 */
	private void getGiftList() {
		List<Gift> list = StaticDataManager.getGiftList();
		Collections.sort(list);
		this.setValue("giftList", list);
		this.setSuccess();
	}
	
	/**
	 * 上架，下架礼物
	 * 
	 * @author lxg
	 * @since 2016年5月6日上午1:26:41
	 */
	private void changeGiftStatus(){
		String status = this.getWebHelper().getString("status");
		String giftId = this.getWebHelper().getString("id");
		if(Validator.isEmpty(status) || !Validator.isInt(status) ||
				Validator.isEmpty(giftId) || !Validator.isInt(giftId)){
			
			this.setError("218");
			return;
		}
		
		final Gift gift = GiftService.getGiftById(giftId);
		if(gift == null){
			this.setError("218");
			return;
		}
		
		gift.setStatus(status);
		String exeCode = GiftService.addOrUpdateGift(gift);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}
	
	/**
	 * 添加，修改礼物
	 * 
	 * @author lxg
	 * @since 2016年5月6日上午1:26:41
	 */
	private void addOrUpdateGift(){
		String name = this.getWebHelper().getString("name");
		String id = this.getWebHelper().getString("id");
		String sendPrice = this.getWebHelper().getString("sendPrice");
		String transformPrice = this.getWebHelper().getString("transformPrice");
		String isCombined = this.getWebHelper().getString("isCombined");
		String status = this.getWebHelper().getString("status");
		String sortId = this.getWebHelper().getString("sortId");
		//byte[] data = this.getWebHelper().getBytes("pic");
		String visitUrl = this.getWebHelper().getString("pic");
		String type = this.getWebHelper().getString("type");
		
		if(!Validator.isFloat(sendPrice) || !Validator.isFloat(transformPrice) |
				Validator.isEmpty(status) || Validator.isEmpty(sortId) || 
				Validator.isEmpty(name) ||  Validator.isEmpty(visitUrl)||
				Validator.isEmpty(type)){
			
			this.setError("218");
			return;
		}
		
//		String uuid = KeyGenerator.createUniqueId();
//		String giftDir = PropertyUtils.getGiftDir();
//		String visitUrl = PropertyUtils.getVisitUrl(giftDir, uuid);

//		String path = UserAssistant.getUserAbsoluteDir(giftDir);
//		Constant.OperaterStatus operaterStatus = CommonUtils.writeFile(path, uuid, data);
//		if(operaterStatus == Constant.OperaterStatus.FAILED){
//			this.setError(operaterStatus.getValue());
//			return;
//		}
		
	
		final Gift gift = new Gift();
		gift.setId(id);
		gift.setIsCombined(isCombined);
		gift.setName(name);
		gift.setPic(visitUrl);
		gift.setSendPrice(new BigDecimal(sendPrice));
		gift.setSortId(Integer.parseInt(sortId));
		gift.setStatus(status);
		gift.setTransformPrice(new BigDecimal(transformPrice));
		gift.setType(type);
		if(Validator.isEmpty(id)){
			gift.setCreateTime(DateUtil.getTimeInMillis());
		}
		String exeCode = GiftService.addOrUpdateGift(gift);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}
	
	/**
	 * 根据礼物状态查询礼物列表
	 */
	private void getGiftsByStatus(){
		String status = this.getWebHelper().getString("status");
		if(Validator.isEmpty(status) || !Validator.isInt(status)){
			this.setError("218");
			return;
		}
		
		final List<Gift> gifts = GiftService.getGiftByStatus(status);
		this.setValue("giftList", gifts);
	}
	
	/**
	 * 根据礼物id，查询礼物信息
	 */
	private void getGift(){
		String id = this.getWebHelper().getString("id");
		if(Validator.isEmpty(id) || !Validator.isInt(id)){
			this.setError("218");
			return;
		}
		
		Gift gift = GiftService.getGiftById(id);
		this.setValue("gift", gift);
	}
}
