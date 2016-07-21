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
 *                  fileName：UserDaoImpl.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ace.database.ds.DBReleaser;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.OperaterStatus;
import com.rednovo.ace.entity.ExchangeBindInfo;
import com.rednovo.ace.entity.ExchangeDetail;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.Validator;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class ExchangeDao extends BasicDao {
	/**
	 * 
	 */
	public ExchangeDao(Connection connection) {
		super(connection);
	}

	public ExchangeBindInfo getBindInfo(String userId, String channel) {
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select userId,weChatId,mobileId,updateTime,createTime,userName from exchange_bind_info  where userId=? and channel=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, channel);
			res = ps.executeQuery();
			if (res != null && res.next()) {
				ExchangeBindInfo bind = new ExchangeBindInfo();
				bind.setUserId(userId);
				bind.setWeChatId(res.getString("weChatId"));;
				bind.setMobileId(res.getString("mobileId"));
				bind.setUpdateTime(res.getString("updateTime"));
				bind.setCreateTime(res.getString("createTime"));
				bind.setUserName(res.getString("userName"));
				return bind;
			}

		} catch (Exception e) {
			this.getLogger().error("[根据用户(" + userId + ")获取用户兑点绑定信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}

	/**
	 * 获取账户余额
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日上午12:51:53
	 */
	public String bind(ExchangeBindInfo bindInfo) {

		PreparedStatement ps = null;
		boolean isExist = true;
		String sql = "update exchange_bind_info set weChatId=?,mobileId=?,schemaId=?, updateTime=?, userName=?,channel=? where userId=? ";
		if (this.getBindInfo(bindInfo.getUserId(), bindInfo.getChannel()) == null) {
			isExist = false;
			sql = "insert into exchange_bind_info (userId,weChatId,mobileId,updateTime,createTime,schemaId,userName,channel) values (?,?,?,?,?,?,?,?)";
		}
		try {
			ps = this.getConnnection().prepareStatement(sql);
			if (isExist) {
				ps.setString(1, bindInfo.getWeChatId());
				ps.setString(2, bindInfo.getMobileId());
				ps.setString(3, DateUtil.getTimeInMillis());
				ps.setString(4, DateUtil.getStringDate());
				ps.setString(5, bindInfo.getUserName());
				ps.setString(6, bindInfo.getChannel());
				ps.setString(7, bindInfo.getUserId());
			} else {
				ps.setString(1, bindInfo.getUserId());
				ps.setString(2, bindInfo.getWeChatId());
				ps.setString(3, bindInfo.getMobileId());
				ps.setString(4, DateUtil.getStringDate());
				ps.setString(5, DateUtil.getStringDate());
				ps.setString(6, DateUtil.getTimeInMillis());
				ps.setString(7, bindInfo.getUserName());
				ps.setString(8, bindInfo.getChannel());
			}
			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[修改用户(" + bindInfo.getUserId() + ")兑点捆绑信息失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return OperaterStatus.FAILED.getValue();
	}

	public String addRequest(ExchangeDetail detail) {
		PreparedStatement ps = null;
		String sql = "insert into exchange_detail (userId,userName,weChatId,mobileId,coinAmount,rmbAmount,status,payerId,payerName,updateTime,createTime,realName,step) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, detail.getUserId());
			ps.setString(2, detail.getUserName());
			ps.setString(3, detail.getWeChatId());
			ps.setString(4, detail.getMobileId());
			ps.setBigDecimal(5, detail.getCoinAmount());
			ps.setBigDecimal(6, detail.getRmbAmount());
			ps.setString(7, detail.getStatus());
			ps.setString(8, detail.getPayerId());
			ps.setString(9, detail.getPayerName());
			ps.setString(10, DateUtil.getStringDate());
			ps.setString(11, DateUtil.getStringDate());
			ps.setString(12, detail.getRealName());
			ps.setString(13, "1");

			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[用户(" + detail.getUserId() + ")添加兑点申请失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return OperaterStatus.FAILED.getValue();
	}

	public String applyRequest(String requestId, String status, String payerId, String payerName) {
		PreparedStatement ps = null;
		String sql = "update exchange_detail set payerId=?,payerName=?, status=?,updateTime=? where id=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, payerId);
			ps.setString(2, payerName);
			ps.setString(3, status);
			ps.setString(4, DateUtil.getStringDate());
			ps.setString(5, requestId);
			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[修改兑点申请(" + requestId + ")失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return OperaterStatus.FAILED.getValue();

	}

	/**
	 * 根据userId查询提现记录
	 * 
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @return
	 * @author WenHui.Wang
	 * @since 2016年6月21日下午4:14:58
	 */
	public List<ExchangeDetail> getExchangeDetailList(String userId, String startTime, String endTime, int page, int pageSize) {
		List<ExchangeDetail> exchangeList = new ArrayList<ExchangeDetail>();
		String dataBase = "exchange_detail";
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select id, userId, userName, realName, weChatId, mobileId, coinAmount, rmbAmount, status, step,  payerId, payerName, updateTime, createTime from " + dataBase + " where createTime>=? and createTime<=?");
		if (!Validator.isEmpty(userId) && !"".equals(userId)) {
			sql.append(" and userId='" + userId + "'");
		}
		sql.append(" order by createTime desc limit ?,? ");
		try {
			ps = this.getConnnection().prepareStatement(sql.toString());
			ps.setString(1, startTime);
			ps.setString(2, endTime);
			ps.setInt(3, (page - 1) * pageSize);
			ps.setInt(4, pageSize);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				ExchangeDetail ed = new ExchangeDetail();
				ed.setId(rs.getInt("id"));
				ed.setUserId(rs.getString("userId"));
				ed.setUserName(rs.getString("userName"));
				ed.setRealName(rs.getString("realName"));
				ed.setWeChatId(rs.getString("weChatId"));
				ed.setMobileId(rs.getString("mobileId"));
				ed.setCoinAmount(rs.getBigDecimal("coinAmount"));
				ed.setRmbAmount(rs.getBigDecimal("rmbAmount"));
				ed.setStatus(rs.getString("status"));
				ed.setStep(rs.getString("step"));
				ed.setPayerId(rs.getString("payerId"));
				ed.setPayerName(rs.getString("payerName"));
				ed.setUpdateTime(rs.getString("updateTime"));
				ed.setCreateTime(rs.getString("createTime"));
				exchangeList.add(ed);
			}
		} catch (Exception e) {
			this.getLogger().error("[查询ExchangeDetail表失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return exchangeList;
	}

	/**
	 * 根据审核状态查询提现记录
	 * 
	 * @param step
	 * @param page
	 * @param pageSize
	 * @return
	 * @author WenHui.Wang
	 * @param status
	 * @since 2016年6月22日下午4:14:58
	 */
	public List<ExchangeDetail> getExchangeStepList(String step, String status, int page, int pageSize) {
		List<ExchangeDetail> exchangeList = new ArrayList<ExchangeDetail>();
		String dataBase = "exchange_detail";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select id, userId, userName,realName, weChatId, mobileId, coinAmount, rmbAmount, status,  payerId, payerName, step, updateTime, createTime from " + dataBase + " where step=? and status = ? order by createTime desc limit ?,? ";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, step);
			ps.setString(2, status);
			ps.setInt(3, (page - 1) * pageSize);
			ps.setInt(4, pageSize);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				ExchangeDetail ed = new ExchangeDetail();
				ed.setId(rs.getInt("id"));
				ed.setUserId(rs.getString("userId"));
				ed.setUserName(rs.getString("userName"));
				ed.setRealName(rs.getString("realName"));
				ed.setWeChatId(rs.getString("weChatId"));
				ed.setMobileId(rs.getString("mobileId"));
				ed.setCoinAmount(rs.getBigDecimal("coinAmount"));
				ed.setRmbAmount(rs.getBigDecimal("rmbAmount"));
				ed.setStep(rs.getString("step"));
				ed.setStatus(rs.getString("status"));
				ed.setPayerId(rs.getString("payerId"));
				ed.setPayerName(rs.getString("payerName"));
				ed.setUpdateTime(rs.getString("updateTime"));
				ed.setCreateTime(rs.getString("createTime"));
				exchangeList.add(ed);
			}
		} catch (Exception e) {
			this.getLogger().error("[查询ExchangeDetail表失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return exchangeList;
	}

	/**
	 * 批量修改用户审核状态以及插入审核记录
	 * 
	 * @param userId
	 * @param step
	 * @param status
	 * @return
	 * @author WenHui.Wang
	 * @param auditId
	 * @param applyId
	 * @param des
	 * @since 2016年6月22日下午4:14:58
	 */
	public String updateStepStatus(String id, String step, String status, String applyId, String auditId, String des) {
		PreparedStatement ps = null;
		try {
			int executeUpdate = 0;
			if (!Validator.isEmpty(id)) {
				String[] ids = id.split("\\^");
				// this.getConnnection().setAutoCommit(false);
				String sql = "update exchange_detail set step=?,status=?,updateTime=?  where id=?";
				ps = this.getConnnection().prepareStatement(sql);
				for (int i = 0; i < ids.length; i++) {
					ps.setString(1, step);
					ps.setString(2, status);
					ps.setString(3, DateUtil.getStringDate());
					ps.setString(4, ids[i]);
					executeUpdate = ps.executeUpdate();
				}
			}
			if (executeUpdate > 0) {
				if (!Validator.isEmpty(applyId)) {
					String[] applyIds = applyId.split("\\^");
					String[] dess = null;
					if (des.contains("^")) {
						dess = des.split("\\^");
					}
					String sql = "insert into apply_cash_audit (applyId,step,status,auditId,auditName,des,updateTime,createTime) values (?,?,?,?,?,?,?,?)";
					this.getConnnection().setAutoCommit(false);
					ps = this.getConnnection().prepareStatement(sql);
					for (int i = 0; i < applyIds.length; i++) {
						ps.setString(1, applyIds[i]);
						ps.setString(2, step);
						ps.setString(3, status);
						ps.setString(4, auditId);
						ps.setString(5, UserManager.getUser(auditId).getNickName());
						if (!Validator.isEmpty(dess)) {
							ps.setString(6, dess[i]);
						} else {
							ps.setString(6, des);
						}
						ps.setString(7, DateUtil.getStringDate());
						ps.setString(8, DateUtil.getStringDate());
						ps.addBatch();
					}
					ps.executeBatch();
					this.getConnnection().commit();
				}
				return Constant.OperaterStatus.SUCESSED.getValue();
			}
		} catch (SQLException e) {
			this.getLogger().error("[更新userId[" + id.split("\\^") + "];step[" + step + "]用户的审核状态];status[" + status + "]失败", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}
}
