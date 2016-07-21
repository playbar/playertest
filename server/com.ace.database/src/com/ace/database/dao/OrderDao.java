/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月5日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.web.service
 *                  fileName：OrderDao.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.ace.database.ds.DBReleaser;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.AD;
import com.rednovo.ace.entity.GoodInfo;
import com.rednovo.ace.entity.Order;
import com.rednovo.ace.entity.Server;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.Validator;

/**
 * @author yongchao.Yang/2016年3月5日
 */
public class OrderDao extends BasicDao {

	/**
	 * @param connection
	 */
	public OrderDao(Connection connection) {
		super(connection);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 获取用户资料
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月3日上午9:47:29
	 */
	public GoodInfo getGood(String gid) {
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select id,name,type,rmbPrice,coinPrice,status,description,isCombined,updateTime,createTime,schemaId from good_info where id=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, gid);
			res = ps.executeQuery();
			if (res != null && res.next()) {
				GoodInfo g = new GoodInfo();
				g.setCoinPrice(new BigDecimal(res.getFloat("coinPrice")));
				g.setCreateTime(res.getString("createTime"));
				g.setDescription(res.getString("description"));
				g.setId(res.getString("id"));
				g.setIsCombined(res.getString("isCombined"));
				g.setName(res.getString("name"));
				g.setRmbPrice(new BigDecimal(res.getFloat("rmbPrice")));
				g.setSchemaId(res.getString("schemaId"));
				g.setStatus(res.getString("status"));
				g.setUpdateTime(res.getString("updateTime"));
				g.setType(res.getString("type"));
				return g;
			}

		} catch (Exception e) {
			this.getLogger().error("[获取商品" + gid + "信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}

	/**
	 * 创建订单
	 * 
	 * @param order
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日上午12:59:14
	 */
	public String createOrder(Order order) {
		PreparedStatement ps = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			String sql = " insert into order_detail (orderId,thirdId,payerId,payerName,receiverId,receiverName,goodId,goodCnt,goodName,status,payChannel,orderDes,rmbAmount,coinAmount,updateTime,createTime,orderChannel,channel) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, order.getOrderId());
			ps.setString(2, order.getThirdId());
			ps.setString(3, order.getPayerId());
			ps.setString(4, order.getPayerName());
			ps.setString(5, order.getReceiverId());
			ps.setString(6, order.getReceiveName());
			ps.setString(7, order.getGoodId());
			ps.setInt(8, order.getGoodCnt());
			ps.setString(9, order.getGoodName());
			ps.setString(10, order.getStatus());
			ps.setString(11, order.getPayChannel().getValue());
			ps.setString(12, order.getOrderDes());
			ps.setFloat(13, order.getRmbAmount().floatValue());
			ps.setFloat(14, order.getCoinAmount().floatValue());
			ps.setString(15, DateUtil.getStringDate());
			ps.setString(16, DateUtil.getStringDate());
			ps.setString(17, order.getOrderChannel());
			ps.setString(18, order.getChannel());
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[创建订单失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return exeRes;
	}

	/**
	 * 开通订单
	 * 
	 * @param orderId
	 * @param operatorId
	 * @param operateName
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日上午10:42:59
	 */
	public String openOrder(String orderId, String operatorId, String operateName) {
		PreparedStatement ps = null;
		try {
			String sql = " update order_detail set status=?,operatorId=?,operatorName=?,updateTime=? where orderId=?";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, Constant.OrderStatus.OPENED.getValue());
			ps.setString(2, operatorId);
			ps.setString(3, operateName);
			ps.setString(4, DateUtil.getStringDate());
			ps.setString(5, orderId);
			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[开通" + orderId + "订单失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
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
	 * @since 2016年3月5日下午7:19:50
	 */
	public ArrayList<Order> getOrderList(String userId, String beginTime, String endTime, String status, int page, int pageSize) {
		if (page <= 0) {
			page = 1;
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Order> list = new ArrayList<Order>();
		try {
			StringBuffer sql = new StringBuffer(
					" select orderId,payerId,payerName,receiverId,receiverName,goodId,goodCnt,goodName,status,payChannel,orderDes,rmbAmount,payedAmount,coinAmount,rate,operatorId,operatorName,openTime,updateTime,createTime,orderChannel,channel from order_detail where createTime>=? and createTime<=? and receiverId=? ");
			if (!Validator.isEmpty(status)) {
				sql.append(" and status=?");
			}
			sql.append(" order by createTime desc limit ?,? ");
			ps = this.getConnnection().prepareStatement(sql.toString());
			ps.setString(1, beginTime);
			ps.setString(2, endTime);
			ps.setString(3, userId);
			if (!Validator.isEmpty(status)) {
				ps.setString(4, status);
				ps.setInt(5, (page - 1) * pageSize);
				ps.setInt(6, pageSize);
			} else {
				ps.setInt(4, (page - 1) * pageSize);
				ps.setInt(5, pageSize);

			}
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				Order order = new Order();
				order.setOrderId(rs.getString("orderId"));
				order.setPayerId(rs.getString("payerId"));
				order.setPayerName(rs.getString("payerName"));
				order.setReceiverId(rs.getString("receiverId"));
				order.setReceiveName(rs.getString("receiverName"));
				order.setRmbAmount(rs.getBigDecimal("rmbAmount"));
				order.setCoinAmount(rs.getBigDecimal("coinAmount"));
				order.setGoodId(rs.getString("goodId"));
				order.setGoodName(rs.getString("goodName"));
				order.setGoodCnt(rs.getInt("goodCnt"));
				order.setStatus(rs.getString("status"));
				order.setOpenUserId(rs.getString("operatorId"));
				order.setOpenUserName(rs.getString("operatorName"));
				order.setOrderDes(rs.getString("orderDes"));
				order.setCreateTime(rs.getString("createTime"));
				order.setRate("");
				order.setOrderChannel(rs.getString("orderChannel"));
				order.setChannel(rs.getString("channel"));
				order.setOpenTime(rs.getString("openTime"));
				list.add(order);
			}

		} catch (Exception e) {
			this.getLogger().error("[查询订单列表失败]", e);
		} finally {
			DBReleaser.release(ps, rs);
		}
		return list;
	}

	/**
	 * 获取订单列表 所有用户
	 * 
	 * @param userId
	 * @param beginTime
	 * @param endTime
	 * @param status
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月5日下午7:19:50
	 */
	public ArrayList<Order> getOrderList(String beginTime, String endTime, String status, int page, int pageSize) {
		if (page <= 0) {
			page = 1;
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Order> list = new ArrayList<Order>();
		try {
			StringBuffer sql = new StringBuffer(
					" select orderId,payerId,payerName,receiverId,receiverName,goodId,goodCnt,goodName,status,payChannel,orderDes,rmbAmount,payedAmount,coinAmount,rate,operatorId,operatorName,openTime,updateTime,createTime,orderChannel,channel from order_detail where createTime>=? and createTime<=? ");
			if (!Validator.isEmpty(status)) {
				sql.append(" and status=?");
			}
			sql.append(" order by createTime desc limit ?,? ");
			ps = this.getConnnection().prepareStatement(sql.toString());
			ps.setString(1, beginTime);
			ps.setString(2, endTime);
			if (!Validator.isEmpty(status)) {
				ps.setString(3, status);
				ps.setInt(4, (page - 1) * pageSize);
				ps.setInt(5, pageSize);
			} else {
				ps.setInt(3, (page - 1) * pageSize);
				ps.setInt(4, pageSize);
			}
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				Order order = new Order();
				order.setOrderId(rs.getString("orderId"));
				order.setPayerId(rs.getString("payerId"));
				order.setPayerName(rs.getString("payerName"));
				order.setReceiverId(rs.getString("receiverId"));
				order.setReceiveName(rs.getString("receiverName"));
				order.setRmbAmount(rs.getBigDecimal("rmbAmount"));
				order.setCoinAmount(rs.getBigDecimal("coinAmount"));
				order.setGoodId(rs.getString("goodId"));
				order.setGoodName(rs.getString("goodName"));
				order.setGoodCnt(rs.getInt("goodCnt"));
				order.setStatus(rs.getString("status"));
				order.setOpenUserId(rs.getString("operatorId"));
				order.setOpenUserName(rs.getString("operatorName"));
				order.setOrderDes(rs.getString("orderDes"));
				order.setCreateTime(rs.getString("createTime"));
				order.setOrderChannel(rs.getString("orderChannel"));
				order.setChannel(rs.getString("channel"));
				order.setRate("");
				order.setOpenTime(rs.getString("openTime"));
				list.add(order);
			}

		} catch (Exception e) {
			this.getLogger().error("[查询订单列表失败]", e);
		} finally {
			DBReleaser.release(ps, rs);
		}
		return list;

	}

	/**
	 * 获取订单
	 * 
	 * @param orderId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月5日下午7:14:35
	 */
	public Order getOrder(String orderId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select orderId,payerId,payerName,receiverId,receiverName,goodId,goodCnt,goodName,status,payChannel,orderDes,rmbAmount,payedAmount,coinAmount,rate,operatorId,operatorName,orderChannel,channel,openTime,updateTime,createTime from order_detail where orderId=?";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, orderId);
			rs = ps.executeQuery();
			if (rs != null && rs.next()) {
				Order order = new Order();
				order.setOrderId(rs.getString("orderId"));
				order.setPayerId(rs.getString("payerId"));
				order.setPayerName(rs.getString("payerName"));
				order.setReceiverId(rs.getString("receiverId"));
				order.setReceiveName(rs.getString("receiverName"));
				order.setRmbAmount(rs.getBigDecimal("rmbAmount"));
				order.setCoinAmount(rs.getBigDecimal("coinAmount"));
				order.setGoodId(rs.getString("goodId"));
				order.setGoodName(rs.getString("goodName"));
				order.setGoodCnt(rs.getInt("goodCnt"));
				order.setStatus(rs.getString("status"));
				order.setOpenUserId(rs.getString("operatorId"));
				order.setOpenUserName(rs.getString("operatorName"));
				order.setOrderDes(rs.getString("orderDes"));
				order.setCreateTime(rs.getString("createTime"));
				order.setOrderChannel(rs.getString("orderChannel"));
				order.setChannel(rs.getString("channel"));
				order.setRate("");
				order.setOpenTime(rs.getString("openTime"));
				return order;
			}

		} catch (Exception e) {
			this.getLogger().error("[获取订单" + orderId + "失败]", e);
		} finally {
			DBReleaser.release(ps, rs);
		}
		return null;
	}

	public Order getOrderWithThirdId(String thirdId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select orderId,thirdId,payerId,payerName,orderChannel,channel,receiverId,receiverName,goodId,goodCnt,goodName,status,payChannel,orderDes,rmbAmount,payedAmount,coinAmount,rate,operatorId,operatorName,openTime,updateTime,createTime from order_detail where thirdId=?";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, thirdId);
			rs = ps.executeQuery();
			if (rs != null && rs.next()) {
				Order order = new Order();
				order.setOrderId(rs.getString("orderId"));
				order.setThirdId(rs.getString("thirdId"));
				order.setPayerId(rs.getString("payerId"));
				order.setPayerName(rs.getString("payerName"));
				order.setReceiverId(rs.getString("receiverId"));
				order.setReceiveName(rs.getString("receiverName"));
				order.setRmbAmount(rs.getBigDecimal("rmbAmount"));
				order.setCoinAmount(rs.getBigDecimal("coinAmount"));
				order.setGoodId(rs.getString("goodId"));
				order.setGoodName(rs.getString("goodName"));
				order.setGoodCnt(rs.getInt("goodCnt"));
				order.setStatus(rs.getString("status"));
				order.setOpenUserId(rs.getString("operatorId"));
				order.setOpenUserName(rs.getString("operatorName"));
				order.setOrderDes(rs.getString("orderDes"));
				order.setCreateTime(rs.getString("createTime"));
				order.setOrderChannel(rs.getString("orderChannel"));
				order.setChannel(rs.getString("channel"));
				order.setRate("");
				order.setOpenTime(rs.getString("openTime"));
				return order;
			}

		} catch (Exception e) {
			this.getLogger().error("[通过第三方订单ID" + thirdId + "获取订单失败]", e);
		} finally {
			DBReleaser.release(ps, rs);
		}
		return null;
	}

	/**
	 * 获取全局参数
	 * 
	 * @param key
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午6:40:17
	 */
	public String getGlobalVal(String key) {
		String val = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select value1 from global_setting where key1=? ";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, key);
			rs = ps.executeQuery();
			if (rs != null && rs.next()) {
				val = rs.getString("value1");
			}

		} catch (Exception e) {
			this.getLogger().error("[获取全局配置" + key + "值失败]", e);
		} finally {
			DBReleaser.release(ps, rs);
		}
		return val;
	}

	/**
	 * 获取系统配置
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月17日上午11:57:32
	 */
	public HashMap<String, String> getSysConfig() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String sql = " select param,value from sys_config ";
			ps = this.getConnnection().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				map.put(rs.getString("param"), rs.getString("value"));
			}

		} catch (Exception e) {
			this.getLogger().error("[获取系统配置参数失败]", e);
		} finally {
			DBReleaser.release(ps, rs);
		}
		return map;
	}

	/**
	 * 更新全局参数
	 * 
	 * @param key
	 * @param val
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午6:45:30
	 */
	public String updateGlobalVal(String key, String val) {
		PreparedStatement ps = null;
		try {
			ps = this.getConnnection().prepareStatement("update global_setting set value1=? where key1=?");
			ps.setString(1, val);
			ps.setString(2, key);
			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[更新全局配置" + key + "值失败]", e);

		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	/**
	 * 获取待同步商品信息
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日上午12:01:04
	 */
	public ArrayList<GoodInfo> getGood() {
		ArrayList<GoodInfo> list = new ArrayList<GoodInfo>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select id,name,type,rmbPrice,coinPrice,status,sortId,description,isCombined,updateTime,createTime,schemaId from good_info where status='1' order by sortId";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				GoodInfo gi = new GoodInfo();
				gi.setId(res.getString("id"));
				gi.setName(res.getString("name"));
				gi.setRmbPrice(res.getBigDecimal("rmbPrice"));
				gi.setCoinPrice(res.getBigDecimal("coinPrice"));
				gi.setStatus(res.getString("status"));
				gi.setSortId(res.getInt("sortId"));
				gi.setIsCombined(res.getString("isCombined"));
				gi.setDescription(res.getString("description"));
				gi.setUpdateTime(res.getString("updateTime"));
				gi.setCreateTime(res.getString("createTime"));
				gi.setSchemaId(res.getString("schemaId"));
				gi.setType(res.getString("type"));
				list.add(gi);
			}
			return list;
		} catch (Exception e) {
			this.getLogger().error("[获取待同步商品信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;

	}

	/**
	 * 获取待同步AD信息
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日上午12:01:04
	 */
	public ArrayList<AD> getADList(String status) {
		ArrayList<AD> list = new ArrayList<AD>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select id, title,imgUrl,addres,visitType, status from ad_info where status=? order by id ";

		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, status);

			res = ps.executeQuery();
			while (res != null && res.next()) {
				AD ad = new AD();
				ad.setTitle(res.getString("title"));
				ad.setImgUrl(res.getString("imgUrl"));
				ad.setAddres(res.getString("addres"));
				ad.setVisitType(res.getString("visitType"));
				ad.setId(String.valueOf(res.getInt("id")));
				ad.setStatus(res.getString("status"));
				list.add(ad);
			}
			return list;
		} catch (Exception e) {
			this.getLogger().error("[获取待同步AD信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}

	/**
	 * 获取线上所有服务器
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月26日下午3:51:12
	 */
	public ArrayList<Server> getServers() {
		ArrayList<Server> list = new ArrayList<Server>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select serverId,serverIp,serverPort,status from server_settings order serverId";

		try {
			ps = this.getConnnection().prepareStatement(sql);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				Server server = new Server();
				server.setId(res.getString("serverId"));
				server.setIp(res.getString("serverIp"));
				server.setPort(res.getInt("serverPort"));
				server.setStatus(res.getString("status"));
				list.add(server);
			}
			return list;
		} catch (Exception e) {
			this.getLogger().error("[获取服务器配置失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return list;

	}

	/**
	 * @param id
	 * @param ip
	 * @param port
	 * @param status
	 * @param description
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月8日下午12:14:47
	 */
	public String updateServerInfo(String id, String ip, String port, String status, String description) {

		PreparedStatement ps = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			String sql = "update server_info set ip=?,port=?,description=?,status=?,schemaId=? where id=?";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, ip);
			ps.setString(2, port);
			ps.setString(3, description);
			ps.setString(4, status);
			ps.setString(5, DateUtil.getTimeInMillis());
			ps.setString(6, id);
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[创建server信息失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return exeRes;

	}

	/**
	 * @param ip
	 * @param port
	 * @param status
	 * @param description
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月8日下午12:14:51
	 */
	public String addServerInfo(String ip, String port, String description) {
		PreparedStatement ps = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			String sql = " insert into server_info (ip, port ,description,schemaId) values(?,?,?,?)";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, ip);
			ps.setString(2, port);
			ps.setString(3, description);
			ps.setString(4, DateUtil.getTimeInMillis());
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[创建server信息失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return exeRes;

	}

	/**
	 * @param id
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月8日下午3:08:01
	 */
	public String delServerInfo(String id) {
		PreparedStatement ps = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			String sql = " delete from server_info where id = ?";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, id);
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[删除serverinfo信息失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return exeRes;

	}

	/**
	 * @param batchNo
	 * @param batchNum
	 * @param batchFee
	 * @param detailData
	 * @param stringDate
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年6月23日下午3:37:49
	 */
	public String createBatchInfo(String batchNo, String batchNum, String batchFee) {
		PreparedStatement ps = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			String sql = " insert into batch_info (batchNo,batchNum,batchFee,createTime) values(?,?,?,?)";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, batchNo);
			ps.setString(2, batchNum);
			ps.setString(3, batchFee);
			ps.setString(4, DateUtil.getStringDate());
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[创建订单失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return exeRes;

	}

	/**
	 * @param batchNo
	 * @return
	 * @author ZuKang.Song
	 * @since  2016年6月24日下午5:34:56
	 */
	public boolean getBatchInfo(String batchNo) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select batchNo from batch_info where batchNo=?";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, batchNo);
			rs = ps.executeQuery();
			if (rs != null && rs.next()) {
				return true;
			}

		} catch (Exception e) {
			this.getLogger().error("[获取订单" + batchNo + "失败]", e);
		} finally {
			DBReleaser.release(ps, rs);
		}
		return false;
	}
}
