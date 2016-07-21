/*  ------------------------------------------------------------------------------ 
 *                  软件名称:他秀手机版
 *                  公司名称:多宝科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年7月15日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自多宝科技研发部，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.duobao.video.logic
 *                  fileName：UserService.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.service;

import java.util.ArrayList;
import java.util.HashMap;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.ace.database.ds.UserTransactionManager;
import com.ace.database.module.AccountModule;
import com.ace.database.module.OrderModule;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.AD;
import com.rednovo.ace.entity.GoodInfo;
import com.rednovo.ace.entity.Order;
import com.rednovo.ace.entity.Server;
import com.rednovo.ace.globalData.ServerRoutManager;
import com.rednovo.tools.Validator;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class OrderService {
	private static Logger logger = Logger.getLogger(OrderService.class);

	public OrderService() {}

	/**
	 * 创建订单
	 * 
	 * @param payerId
	 * @param receiverId
	 * @param goodId
	 * @param goodCnt
	 * @return
	 * @author Yongchao.Yang
	 * @param orderChannel
	 * @param channel
	 * @since 2016年3月4日下午3:12:27
	 */
	public static String createOrder(String payerId, String thirdId, String receiverId, String goodId, int goodCnt, Constant.payChannel channel, String orderChannel, String appchannel) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		OrderModule om = new OrderModule();
		try {
			ut.begin();
			String orderId = om.createOrder(payerId, thirdId, receiverId, goodId, goodCnt, channel, orderChannel, appchannel);
			if (Validator.isEmpty(orderId)) {
				ut.rollback();
			} else {
				ut.commit();
				return orderId;
			}
		} catch (Exception e) {
			try {
				logger.error("[用户 " + payerId + "创建订单失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return "";
	}

	/**
	 * 开通订单
	 * 
	 * @param orderId
	 * @param openUserId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日下午3:16:46
	 */
	public static String openOrder(String orderId, String openUserId) {
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		OrderModule om = new OrderModule();
		AccountModule am = new AccountModule();

		UserTransaction ut = UserTransactionManager.getUserTransaction();
		try {
			ut.begin();
			exeRes = om.openOrder(orderId, openUserId, "");
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
				return exeRes;
			}
			Order order = om.getOrder(orderId);
			// 加币
			exeRes = am.addMoney(order.getReceiverId(), order.getPayerId(), order.getCoinAmount(), Constant.logicType.ORDER);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
				ut.rollback();
				return exeRes;
			}
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[" + openUserId + "开通订单" + orderId + "失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
			am.release();
		}
		return exeRes;
	}

	/**
	 * 获取订单列表
	 * 
	 * @param orderId
	 * @param openUserId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日下午3:16:46
	 */
	public static ArrayList<Order> getOrderList(String userId, String beginTime, String endTime, Constant.OrderStatus status, int page, int pageSize) {
		OrderModule om = new OrderModule();
		ArrayList<Order> list = null;
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		try {
			ut.begin();
			list = om.getOrderList(userId, beginTime, endTime, status, page, pageSize);
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取订单列表失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return list;
	}

	/**
	 * 获取系统配置参数
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月17日下午12:21:42
	 */
	public static HashMap<String, String> getSysConfig() {
		OrderModule om = new OrderModule();
		HashMap<String, String> map = new HashMap<String, String>();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		try {
			ut.begin();
			map = om.getSysConfig();
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取系统配置参数失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return map;
	}

	public static Order getOrder(String orderId) {
		Order order = null;
		OrderModule om = new OrderModule();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		try {
			ut.begin();
			order = om.getOrder(orderId);
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取订单" + orderId + "失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return order;
	}

	public static Order getOrderWithThirdId(String thirdId) {
		Order order = null;
		OrderModule om = new OrderModule();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		try {
			ut.begin();
			order = om.getOrderWithThirdId(thirdId);
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[根据第三方ID" + thirdId + "获取订单失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return order;
	}

	/**
	 * 获取待同步商品信息
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日上午12:03:37
	 */
	public static ArrayList<GoodInfo> getAllGood() {
		ArrayList<GoodInfo> list = null;
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		OrderModule om = new OrderModule();
		try {
			ut.begin();
			list = om.getAllGood();
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取待同步商品数据失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return list;
	}

	public static ArrayList<AD> getADList(String status) {
		ArrayList<AD> list = new ArrayList<AD>();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		OrderModule om = new OrderModule();
		try {
			ut.begin();
			list = om.getADList(status);
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取待同步广告数据失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return list;
	}

	/**
	 * 获取服务器列表
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月26日下午3:52:41
	 */
	public static ArrayList<Server> getServerList() {
		ArrayList<Server> list = new ArrayList<Server>();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		OrderModule om = new OrderModule();
		try {
			ut.begin();
			list = om.getServers();
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取服务器配置数据失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return list;
	}

	/**
	 * @param id
	 * @param ip
	 * @param port
	 * @param status
	 * @param description
	 * @param string
	 * @author ZuKang.Song
	 * @since 2016年6月8日上午11:54:32
	 */
	public static String updateServerInfo(String id, String ip, String port, String status, String description) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		OrderModule um = new OrderModule();
		String exeCode = "";
		try {
			ut.begin();
			if (Validator.isEmpty(ServerRoutManager.getServer(id))) {
				exeCode = um.addServerInfo(ip, port, description);
			} else {
				exeCode = um.updateServerInfo(id, ip, port, status, description);
			}
			ut.commit();
		} catch (Exception e) {
			logger.error("[修改seversetting失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改seversetting失败回滚事务时错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;
	}

	/**
	 * @param id
	 * @author ZuKang.Song
	 * @return
	 * @since 2016年6月8日上午11:55:16
	 */
	public static String delServerInfo(String id) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		OrderModule um = new OrderModule();
		String exeCode = "";
		try {
			ut.begin();
			exeCode = um.delServerInfo(id);
			ut.commit();
		} catch (Exception e) {
			logger.error("[修改severInfo失败]", e);
			try {
				ut.rollback();
			} catch (Exception e1) {
				logger.error("修改severInfo失败回滚事务时错误]", e1);
			}
		} finally {
			um.release();
		}
		return exeCode;
	}

	/**
	 * 获取订单列表
	 * 
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @param status
	 * @param page
	 * @param pageSize
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月13日下午5:09:02
	 */
	public static ArrayList<Order> getOrderList(String userId, String startTime, String endTime, String status, int page, int pageSize) {
		OrderModule om = new OrderModule();
		ArrayList<Order> list = null;
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		try {
			ut.begin();
			if (Validator.isEmpty(userId)) {
				list = om.getOrderList(startTime, endTime, status, page, pageSize);
			} else {
				list = om.getOrderList(userId, startTime, endTime, status, page, pageSize);
			}
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取订单列表失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return list;
	}

	
	/**
	 * 创建批量付款批次号
	 * 
	 * @param batchFee 批量付款总金额
	 * @param batchNum 几笔
	 * @param detailData 0315006^testture0002@126.com^常炜买家^20.00^hello
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月23日下午3:19:36
	 */
	public static String createBatchInfo(String batchFee, String batchNum) {
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		OrderModule om = new OrderModule();
		try {
			ut.begin();
			String batchNo = om.createBatchInfo(batchFee, batchNum);
			if (Validator.isEmpty(batchNo)) {
				ut.rollback();
			} else {
				ut.commit();
				return batchNo;
			}
		} catch (Exception e) {
			try {
				logger.error("[创建批量付款失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return "";
	}
	
	/**
	 * 获取批量打款号
	 * @param batchNo
	 * @return
	 * @author ZuKang.Song
	 * @since  2016年6月24日下午5:12:23
	 */
	public static boolean getBatchInfo(String batchNo) {
		boolean istrue = false;
		OrderModule om = new OrderModule();
		UserTransaction ut = UserTransactionManager.getUserTransaction();
		try {
			ut.begin();
			istrue = om.getBatchInfo(batchNo);
			ut.commit();
		} catch (Exception e) {
			try {
				logger.error("[获取批量打款号" + batchNo + "失败]", e);
				ut.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			om.release();
		}
		return istrue;
	}
}
