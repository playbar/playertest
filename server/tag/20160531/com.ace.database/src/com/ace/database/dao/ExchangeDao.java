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

import com.ace.database.ds.DBReleaser;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.OperaterStatus;
import com.rednovo.ace.entity.ExchangeBindInfo;
import com.rednovo.ace.entity.ExchangeDetail;
import com.rednovo.tools.DateUtil;

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

	public ExchangeBindInfo getBindInfo(String userId) {
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select userId,weChatId,mobileId,updateTime,createTime from exchange_bind_info  where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, userId);
			res = ps.executeQuery();
			if (res != null && res.next()) {
				ExchangeBindInfo bind = new ExchangeBindInfo();
				bind.setUserId(userId);
				bind.setWeChatId(res.getString("weChatId"));;
				bind.setMobileId(res.getString("mobileId"));
				bind.setUpdateTime(res.getString("updateTime"));
				bind.setCreateTime(res.getString("createTime"));
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
		String sql = "update exchange_bind_info set weChatId=?,mobileId=?,schemaId=? where userId=? ";
		if (this.getBindInfo(bindInfo.getUserId()) == null) {
			isExist = false;
			sql = "insert into exchange_bind_info (userId,weChatId,mobileId,updateTime,createTime,schemaId) values (?,?,?,?,?,?)";
		}
		try {
			ps = this.getConnnection().prepareStatement(sql);
			if (isExist) {
				ps.setString(1, bindInfo.getWeChatId());
				ps.setString(2, bindInfo.getMobileId());
				ps.setString(3, DateUtil.getTimeInMillis());
				ps.setString(4, bindInfo.getUserId());
			} else {
				ps.setString(1, bindInfo.getUserId());
				ps.setString(2, bindInfo.getWeChatId());
				ps.setString(3, bindInfo.getMobileId());
				ps.setString(4, DateUtil.getStringDate());
				ps.setString(5, DateUtil.getStringDate());
				ps.setString(6, DateUtil.getTimeInMillis());
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
		String sql = "insert into exchange_detail (userId,userName,weChatId,mobileId,coinAmount,rmbAmount,status,payerId,payerName,updateTime,createTime) values (?,?,?,?,?,?,?,?,?,?,?)";
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

}
