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
import java.util.ArrayList;

import com.ace.database.ds.DBReleaser;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.StatisticsGift;
import com.rednovo.tools.DateUtil;

/**
 * @author lijiang/2016.6.22
 */
public class StatisticsGiftDao extends BasicDao {

	public StatisticsGiftDao(Connection connection) {
		super(connection);
	}

	/**
	 * 获取客户礼物统计信息
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author lijiang/2016.6.22
	 */
	public String getStatisticsGiftInfo(String day, String senderId, String receiverId, String giftId) {
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select day,senderId,receiverId,giftId,cnt,showId,updateTime,createTime from statistics_gift_day where day=? and senderId=? and receiverId=? and giftId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, day);
			ps.setString(2, senderId);
			ps.setString(3, receiverId);
			ps.setString(4, giftId);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				return res.getString("cnt");
			}
		} catch (Exception e) {
			this.getLogger().error("[获取用户礼物信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}

	/**
	 * 更新客户礼物统计信息
	 * 
	 * @param day
	 * @param senderId
	 * @param receiverId
	 * @param giftId
	 * @param cnt
	 * @return
	 * @author lijiang/2016.6.22
	 */
	public String updateStatisticsGiftInfo(String day, String senderId, String receiverId, String giftId, int cnt) {
		PreparedStatement ps = null;
		ResultSet res = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		String sql = "update statistics_gift_day set day=?, senderId=?, receiverId=?, giftId=?, cnt=?, updateTime=? where day=? and senderId=? and receiverId=? and giftId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, day);
			ps.setString(2, senderId);
			ps.setString(3, receiverId);
			ps.setString(4, giftId);
			ps.setInt(5, cnt);
			ps.setString(6, DateUtil.getStringDate());
			ps.setString(7, day);
			ps.setString(8, senderId);
			ps.setString(9, receiverId);
			ps.setString(10, giftId);
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}
		} catch (Exception e) {
			this.getLogger().error("[更新用户礼物統計信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return exeRes;
	}

	/**
	 * 新增客户礼物统计信息
	 * 
	 * @param day
	 * @param senderId
	 * @param receiverId
	 * @param giftId
	 * @param cnt
	 * @return
	 * @author lijiang/2016.6.22
	 */
	public String addStatisticsGiftInfo(String day, String senderId, String receiverId, String giftId, int cnt) {
		PreparedStatement ps = null;
		ResultSet res = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		String sql = "insert into statistics_gift_day (day,senderId,receiverId,giftId,cnt,showId,updateTime,createTime) values (?,?,?,?,?,?,?,?)";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, day);
			ps.setString(2, senderId);
			ps.setString(3, receiverId);
			ps.setString(4, giftId);
			ps.setInt(5, cnt);
			ps.setString(6, receiverId);
			ps.setString(7, DateUtil.getStringDate());
			ps.setString(8, DateUtil.getStringDate());
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}
		} catch (Exception e) {
			this.getLogger().error("[新增用户礼物統計信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return exeRes;
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
		PreparedStatement ps = null;
		ResultSet res = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select day,senderId,receiverId,giftId,cnt,showId,updateTime,createTime from statistics_gift_day where 1=1");
		if (null != starttime && !"".equals(starttime)) {
			sql.append(" and day>='" + starttime + "'");
		}
		if (null != endtime && !"".equals(endtime)) {
			sql.append(" and day<='" + endtime + "'");
		}
		if (null != senderId && !"".equals(senderId)) {
			sql.append(" and senderId='" + senderId + "'");
		}
		if (null != receiverId && !"".equals(receiverId)) {
			sql.append(" and receiverId='" + receiverId + "'");
		}
		sql.append("order by day desc");
		try {
			ps = this.getConnnection().prepareStatement(sql.toString());
			res = ps.executeQuery();
			ArrayList<StatisticsGift> list = new ArrayList<StatisticsGift>();
			while (res != null && res.next()) {
				StatisticsGift sg = new StatisticsGift();
				sg.setDay(res.getString("day"));
				sg.setSenderId(res.getString("senderId"));
				sg.setReceiverId(res.getString("receiverId"));
				sg.setGiftId(res.getString("giftId"));
				sg.setCnt(res.getInt("cnt"));
				sg.setShowId(res.getString("showId"));
				sg.setUpdateTime(res.getString("updateTime"));
				sg.setCreateTime(res.getString("createTime"));
				list.add(sg);
			}
			return list;
		} catch (Exception e) {
			this.getLogger().error("[根据用户ID获取礼物统计信息失败,查询sql:" + sql + "]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}
}
