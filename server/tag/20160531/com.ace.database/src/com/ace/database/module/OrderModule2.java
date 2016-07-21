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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.AD;
import com.rednovo.ace.entity.GoodInfo;
import com.rednovo.ace.entity.Order;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.KeyGenerator;

/**
 * 账户操作
 * 
 * @author yongchao.Yang/2016年3月4日
 */
public class OrderModule2 extends BasicModule {

	/**
	 * 
	 */
	public OrderModule2() {

	}

	public Order getOrder(String orderId) {
		return this.getOrderDao().getOrder(orderId);
	}

	public Order getOrderWithThirdId(String thirdId) {
		return this.getOrderDao().getOrderWithThirdId(thirdId);
	}

	/**
	 * 创建新订单
	 * 
	 * @param order
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日下午12:04:09
	 */
	public String createOrder(String payerId, String thirdId, String receiverId, String goodId, int goodCnt, Constant.payChannel channel) {
		String orderId = KeyGenerator.createUniqueId();

		GoodInfo good = this.getOrderDao().getGood(goodId);
		BigDecimal rmbAmount = good.getRmbPrice().multiply(new BigDecimal(goodCnt));
		BigDecimal coinAmount = good.getCoinPrice().multiply(new BigDecimal(goodCnt));

		Order newOrder = new Order();
		newOrder.setOrderId(orderId);// 订单ID
		newOrder.setThirdId(thirdId);
		newOrder.setPayerId(payerId);// 支付人ID
		newOrder.setPayerName("");// 支付人姓名
		newOrder.setReceiveName("");// 接收人姓名
		newOrder.setReceiverId(receiverId);// 接收人ID
		newOrder.setRmbAmount(rmbAmount);// 需要支付的RMB数量
		newOrder.setCoinAmount(coinAmount);// 购买的金币数
		newOrder.setPayedAmount(null);// 实际支付的RMB数量
		newOrder.setPayChannel(channel);// 支付方式

		newOrder.setGoodId(goodId);// 购买的商品ID
		newOrder.setGoodName(good.getName());// 购买商品名称
		newOrder.setGoodCnt(goodCnt);// 购买商品数量
		newOrder.setStatus(Constant.OrderStatus.UNPAYED.getValue());// 订单状态
		newOrder.setOpenTime("");// 订单实际开通时间
		newOrder.setOpenUserId("");// 开通人ID
		newOrder.setOpenUserName("");// 开通人姓名
		newOrder.setRate("");// 当前汇率
		newOrder.setOrderDes(payerId + "话费人民币" + rmbAmount.floatValue() + "元给,用户" + receiverId + "购买" + goodCnt + "商品(" + goodId + ")");// 订单描述
		newOrder.setCreateTime(DateUtil.getStringDate());// 创建时间

		if (Constant.OperaterStatus.SUCESSED.getValue().equals(this.getOrderDao().createOrder(newOrder))) {
			return orderId;
		}

		return "";
	}

	/**
	 * 开通订单
	 * 
	 * @param orderId
	 * @param operatorId
	 * @param operatorName
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日下午1:23:19
	 */
	public String openOrder(String orderId, String operatorId, String operatorName) {
		Order order = this.getOrderDao().getOrder(orderId);
		if (order == null) {
			return "700";// 订单不存在
		}
		if (Constant.OrderStatus.OPENED.getValue().equals(order.getStatus())) {
			return "702";// 订单已经开通
		}
		return this.getOrderDao().openOrder(orderId, operatorId, operatorName);
	}

	/**
	 * 获取订单列表
	 * 
	 * @param userId
	 * @param beginTime
	 * @param endTime
	 * @param status
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月5日下午7:32:24
	 */
	public ArrayList<Order> getOrderList(String userId, String beginTime, String endTime, Constant.OrderStatus status, int page, int pageSize) {
		return this.getOrderDao().getOrderList(userId, beginTime, endTime, status.getValue(), page, pageSize);
	}

	/**
	 * 获取全局参数
	 * 
	 * @param key
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午6:41:08
	 */
	public String getGlobalVal(String key) {
		return this.getOrderDao().getGlobalVal(key);
	}

	public HashMap<String, String> getSysConfig() {
		return this.getOrderDao().getSysConfig();
	}

	/**
	 * 更新全局参数
	 * 
	 * @param key
	 * @param val
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午6:46:19
	 */
	public String updateGlobalVal(String key, String val) {
		return this.getOrderDao().updateGlobalVal(key, val);
	}

	public ArrayList<GoodInfo> getAllGood() {
		return this.getOrderDao().getGood();
	}

	public List<AD> getADList() {
		return this.getOrderDao().getADList("");
	}
}
