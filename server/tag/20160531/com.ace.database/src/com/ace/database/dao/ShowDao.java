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
 *                  fileName：ShowDao.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.ace.database.ds.DBReleaser;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.LiveShow;
import com.rednovo.tools.DateUtil;

/**
 * @author yongchao.Yang/2016年3月3日
 */
public class ShowDao extends BasicDao {

	/**
	 * @param connection
	 */
	public ShowDao(Connection connection) {
		super(connection);
	}

	/**
	 * 开播
	 * 
	 * @param show
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月3日下午3:37:25
	 */
	public String addShow(LiveShow show) {
		PreparedStatement ps = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			ps = this.getConnnection().prepareStatement("insert into show_list_live(userId,sortCnt,title,position,startTime,createTime,schemaId) values(?,?,?,?,?,?,?)");

			String time = DateUtil.getTimeInMillis();
			ps.setString(1, show.getUserId());
			ps.setString(2, "0");
			ps.setString(3, show.getTitle());
			ps.setString(4, show.getPosition());
			ps.setString(5, time);
			ps.setString(6, DateUtil.getStringDate());
			ps.setString(7, time);
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}
		} catch (Exception e) {
			this.getLogger().error("[用户" + show.getUserId() + "开播数据保存失败]", e);
			return Constant.OperaterStatus.FAILED.getValue();
		} finally {
			DBReleaser.release(ps);
		}
		return exeRes;
	}

	/**
	 * 获取所有的在线直播
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年4月21日下午10:37:35
	 */
	public ArrayList<LiveShow> getShow(int page, int pageSize) {
		PreparedStatement ps = null;
		ArrayList<LiveShow> list = new ArrayList<LiveShow>();
		ResultSet res = null;
		try {
			ps = this.getConnnection().prepareStatement("select userId,sortCnt,title,position,startTime,createTime,schemaId from show_list_live order by id desc limit ?,?");
			ps.setInt(1, (page - 1) * pageSize);
			ps.setInt(2, pageSize);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				LiveShow li = new LiveShow();
				li.setUserId(res.getString("userId"));
				li.setSortCnt(res.getLong("sortCnt"));
				li.setTitle(res.getString("title"));
				li.setPosition(res.getString("position"));
				li.setStartTime(res.getString("startTime"));
				li.setLength(res.getString("length"));
				li.setSupportCnt(res.getString("supportCnt"));
				li.setCoinCnt(res.getString("coinCnt"));
				li.setShareCnt(res.getString("shareCnt"));
				li.setCreateTime(res.getString("createTime"));
				li.setSchemaId(res.getString("schemaId"));
				list.add(li);
			}

		} catch (Exception e) {
			this.getLogger().error("[获取所有直播数据失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return list;

	}

	/**
	 * 获取所有待同步直播数据
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月8日下午8:15:21
	 */
	public ArrayList<String> updateSort(int page, int pageSize) {
		if (page <= 0) {
			page = 1;
		}
		ArrayList<String> list = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select userId from show_list_live order by sortCnt DESC limit ?,?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setInt(1, (page - 1) * pageSize);
			ps.setInt(2, pageSize);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				list.add(res.getString("userId"));
			}
			return list;
		} catch (Exception e) {
			this.getLogger().error("[获取直播列表信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;

	}
	

	/**
	 * 刷新直播间实时统计数据
	 * 
	 * @param id
	 * @param supportCnt
	 * @param coinCnt
	 * @param shareCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月8日下午8:14:10
	 */
	public String refreshData(String userId, String length, String memberCnt, String fansCnt, String supportCnt, String coinCnt, String shareCnt) {
		PreparedStatement ps = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			ps = this.getConnnection().prepareStatement("update show_list_live set length=?,memberCnt=?,fansCnt=?, supportCnt=?,coinCnt=?,shareCnt=? where userId=?");
			ps.setString(1, length);
			ps.setString(2, memberCnt);
			ps.setString(3, fansCnt);
			ps.setString(4, supportCnt);
			ps.setString(5, coinCnt);
			ps.setString(6, shareCnt);
			ps.setString(7, userId);
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}
		} catch (Exception e) {
			this.getLogger().error("[更新" + userId + "直播实时数据]", e);
			return Constant.OperaterStatus.FAILED.getValue();
		} finally {
			DBReleaser.release(ps);
		}
		return exeRes;
	}

	/**
	 * 获取新增直播数据，并同步到缓存中
	 * 
	 * @param showId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月8日下午8:09:04
	 */
	public ArrayList<LiveShow> getSynLiveShow(String synId, int maxCnt) {
		ArrayList<LiveShow> list = new ArrayList<LiveShow>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select userId,sortCnt,title,position,startTime,length,supportCnt,coinCnt,shareCnt,createTime,schemaId from show_list_live where schemaId>? limit ?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, synId);
			ps.setInt(2, maxCnt);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				LiveShow li = new LiveShow();
				li.setUserId(res.getString("userId"));
				li.setSortCnt(res.getLong("sortCnt"));
				li.setTitle(res.getString("title"));
				li.setPosition(res.getString("position"));
				li.setStartTime(res.getString("startTime"));
				li.setLength(res.getString("length"));
				li.setSupportCnt(res.getString("supportCnt"));
				li.setCoinCnt(res.getString("coinCnt"));
				li.setShareCnt(res.getString("shareCnt"));
				li.setCreateTime(res.getString("createTime"));
				li.setSchemaId(res.getString("schemaId"));

				list.add(li);
			}
			return list;
		} catch (Exception e) {
			this.getLogger().error("[获取待同步直播信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}

	public LiveShow getLiveShow(String userId) {
		LiveShow li = new LiveShow();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select userId,title,position,startTime,length,memberCnt,fansCnt,supportCnt,coinCnt,shareCnt,createTime,schemaId from show_list_live where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, userId);
			res = ps.executeQuery();
			if (res != null && res.next()) {
				li.setUserId(res.getString("userId"));
				li.setTitle(res.getString("title"));
				li.setPosition(res.getString("position"));
				li.setStartTime(res.getString("startTime"));
				li.setLength(res.getString("length"));
				li.setMemberCnt(res.getString("memberCnt"));
				li.setFansCnt(res.getString("fansCnt"));
				li.setSupportCnt(res.getString("supportCnt"));
				li.setCoinCnt(res.getString("coinCnt"));
				li.setShareCnt(res.getString("shareCnt"));
				li.setCreateTime(res.getString("createTime"));
			}
		} catch (Exception e) {
			this.getLogger().error("[获取直播" + userId + "信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return li;
	}

	/**
	 * 结束本次直播
	 * 
	 * @param showId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月8日下午9:23:38
	 */
	public String moveShowData(String userId) {
		PreparedStatement last_ps = null, his_ps = null, live_remove_ps = null;
		String live_remove_sql = "delete from show_list_live where userId=?";
		String last_add_sql = "insert into show_list_last(userId,title,position,startTime,length,supportCnt,coinCnt,shareCnt,createTime) SELECT userId,title, position,startTime, length,supportCnt,coinCnt,shareCnt,createTime FROM show_list_live WHERE userId=?";
		String his_add_sql = "insert into show_list_history(userId,title,position,startTime,length,supportCnt,coinCnt,shareCnt,createTime) SELECT userId,title, position,startTime, length,supportCnt,coinCnt,shareCnt,createTime FROM show_list_live WHERE userId=?";

		try {
			// 添加上次记录
			last_ps = this.getConnnection().prepareStatement(last_add_sql);
			last_ps.setString(1, userId);
			if (last_ps.executeUpdate() <= 0) {
				return Constant.OperaterStatus.FAILED.getValue();
			}

			// 添加历史记录
			his_ps = this.getConnnection().prepareStatement(his_add_sql);
			his_ps.setString(1, userId);
			if (his_ps.executeUpdate() <= 0) {
				return Constant.OperaterStatus.FAILED.getValue();
			}

			// 删除本次直播
			live_remove_ps = this.getConnnection().prepareStatement(live_remove_sql);
			live_remove_ps.setString(1, userId);
			if (live_remove_ps.executeUpdate() <= 0) {
				return Constant.OperaterStatus.FAILED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[将直播数据移入历史操作失败]", e);
			return Constant.OperaterStatus.FAILED.getValue();
		} finally {
			DBReleaser.release(last_ps);
			DBReleaser.release(live_remove_ps);
			DBReleaser.release(his_ps);
		}
		return Constant.OperaterStatus.SUCESSED.getValue();
	}
}
