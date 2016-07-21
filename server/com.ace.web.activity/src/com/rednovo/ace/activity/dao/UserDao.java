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
package com.rednovo.ace.activity.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.rednovo.ace.activity.ds.DBReleaser;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.activity.entity.User;
import com.rednovo.tools.DateUtil;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class UserDao extends BasicDao {

	/**
	 * 
	 */
	public UserDao(Connection connection) {
		super(connection);
	}

	/**
	 * 添加用户
	 * 
	 * @param user
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月3日上午9:10:07
	 */
	public String addUser(User user) {
		PreparedStatement ps = null;
		try {
			ps = this.getConnnection().prepareStatement("insert into user_info(id,userId,activityId,name,school,weixin,sex,channel,qq,specialty,signature,profile,status,phone,IDCard,createTime,updateTime,schemaId) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, user.getId());
			ps.setString(2, user.getUserId());
			ps.setString(3, user.getActivityId());
			ps.setString(4, user.getName());
			ps.setString(5, user.getSchool());
			ps.setString(6, user.getWeixin());
			ps.setString(7, user.getSex());
			ps.setString(8, user.getChannel());
			ps.setString(9, user.getQq());
			ps.setString(10, user.getSpecialty());
			ps.setString(11, user.getSignature());
			ps.setString(12, user.getProfile());
			ps.setString(13, user.getStatus());// 默认都是普通的用户
			ps.setString(14, user.getPhone());
			ps.setString(15, user.getIDCard());
			ps.setString(16, DateUtil.getTimeInMillis());
			ps.setString(17, DateUtil.getTimeInMillis());
			ps.setString(18, DateUtil.getTimeInMillis());
			if (ps.executeUpdate() <= 0) {
				return Constant.OperaterStatus.FAILED.getValue();
			}

			return Constant.OperaterStatus.SUCESSED.getValue();
		} catch (Exception e) {
			this.getLogger().error("[添加 " + user.getName() + " 用户失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	/**
	 * 获取用户资料
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月3日上午9:47:29
	 */
	public User getUser(String userId) {
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select uuid,userId,passwd,sex,nickName,channel,rank,basicScore,signature,profile,showImg,status,updateTime,createTime,schemaId from user_info where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, userId);
			res = ps.executeQuery();
			if (res != null && res.next()) {
				User user = new User();
				user.setId(res.getString("id"));
				user.setUserId(res.getString("userId"));
				user.setActivityId(res.getString("activityId"));
				user.setName( res.getString("name"));
				user.setSchool(res.getString("school"));
				user.setWeixin(res.getString("weixin"));
				user.setSex(res.getString("sex"));
				user.setChannel(res.getString("channel"));
				user.setQq(res.getString("qq"));
				user.setSpecialty(res.getString("specialty"));
				user.setSignature(res.getString("signature"));
				user.setProfile(res.getString("profile"));
				user.setStatus(res.getString("status"));// 默认都是普通的用户
				user.setPhone(res.getString("phone"));
				user.setIDCard(res.getString("IDCard"));
				user.setUpdateTime(res.getString("updateTime"));
				user.setCreateTime(res.getString("createTime"));
				user.setSchemaId(res.getString("schemaId"));
				return user;
			}

		} catch (Exception e) {
			this.getLogger().error("[根据用户id获取用户资料失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}

	/**
	 * 获取需要同步的用户数据
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午6:59:10
	 */
	public ArrayList<User> getSynUser(String synId, int maxCnt) {
		ArrayList<User> list = new ArrayList<User>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select uuid,info.userId,mapping.tokenId,passwd,sex,nickName,info.channel,rank,basicScore,signature,profile,showImg,status,updateTime,createTime,schemaId from user_info info inner join user_tokenId_mapping mapping on info.userId=mapping.userId  where schemaId>? order by schemaId  limit ?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, synId);
			ps.setInt(2, maxCnt);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				User user = new User();
				user.setId(res.getString("id"));
				user.setUserId(res.getString("userId"));
				user.setActivityId(res.getString("activityId"));
				user.setName( res.getString("name"));
				user.setSchool(res.getString("school"));
				user.setWeixin(res.getString("weixin"));
				user.setSex(res.getString("sex"));
				user.setChannel(res.getString("channel"));
				user.setQq(res.getString("qq"));
				user.setSpecialty(res.getString("specialty"));
				user.setSignature(res.getString("signature"));
				user.setProfile(res.getString("profile"));
				user.setStatus(res.getString("status"));// 默认都是普通的用户
				user.setPhone(res.getString("phone"));
				user.setIDCard(res.getString("IDCard"));
				user.setUpdateTime(res.getString("updateTime"));
				user.setCreateTime(res.getString("createTime"));
				user.setSchemaId(res.getString("schemaId"));
				list.add(user);
			}
			return list;

		} catch (Exception e) {
			this.getLogger().error("[根据用户id获取用户资料失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}

	/**
	 * @param user
	 * @return
	 * @author ZuKang.Song
	 * @since  2016年5月10日上午10:43:30
	 */
	public String checkUser(User user) {
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select id from user_info where userId=? and activityId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, user.getUserId());
			ps.setString(2, user.getActivityId());
			res = ps.executeQuery();
			if (res != null && res.next()) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}
				

		} catch (Exception e) {
			this.getLogger().error("[根据用户id获取用户资料失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}
	
	/**
	 * 
	 * @return
	 * @author Lei.Zhang
	 * @since  2016年5月13日下午1:14:08
	 */
	public List<User> getCatonlist(){
		List<User> list = new ArrayList<User>();
		PreparedStatement ps = null;
		ResultSet res = null;	
		String sql = "SELECT info.id,info.userid,score.score FROM user_info info LEFT JOIN vote_score score on info.id = score.Id ORDER BY score.score DESC";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			res = ps.executeQuery();	
			while (res != null && res.next()) {
				User user = new User();
				user.setId(res.getString("id"));
				user.setUserId(res.getString("userid"));
				user.setScore(res.getInt("score"));
				list.add(user);	
			}
			return list;
		} catch (Exception e) {
			this.getLogger().error("[获取金榜资料信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}



}
