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
import com.rednovo.ace.constant.Constant.OperaterStatus;
import com.rednovo.ace.entity.User;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.Validator;

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
		PreparedStatement ps = null, ps2 = null;
		try {
			ps = this.getConnnection().prepareStatement("insert into user_info(userId,passwd,sex,nickName,channel,rank,basicScore,signature,profile,showImg,status,updateTime,createTime,schemaId) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, user.getUserId());
			ps.setString(2, user.getPassWord());
			ps.setString(3, user.getSex());
			ps.setString(4, user.getNickName());
			ps.setString(5, user.getChannel());
			ps.setString(6, user.getRank());
			ps.setInt(7, user.getBasicScore());
			ps.setString(8, user.getSignature());
			ps.setString(9, user.getProfile());
			ps.setString(10, user.getShowImg());
			ps.setString(11, user.getStatus());// 默认都是普通的用户
			ps.setString(12, DateUtil.getTimeInMillis());
			ps.setString(13, DateUtil.getTimeInMillis());
			ps.setString(14, DateUtil.getTimeInMillis());
			if (ps.executeUpdate() <= 0) {
				return Constant.OperaterStatus.FAILED.getValue();
			}

			ps2 = this.getConnnection().prepareStatement("insert into user_tokenId_mapping (tokenId,userId,channel) values(?,?,?)");
			ps2.setString(1, user.getTokenId());
			ps2.setString(2, user.getUserId());
			ps2.setString(3, user.getChannel());
			if (ps2.executeUpdate() <= 0) {
				return Constant.OperaterStatus.FAILED.getValue();
			}

			return Constant.OperaterStatus.SUCESSED.getValue();
		} catch (Exception e) {
			this.getLogger().error("[添加 " + user.getNickName() + " 用户失败]", e);
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
				user.setUuid(res.getString("uuid"));
				user.setUserId(res.getString("userId"));
				user.setSex(res.getString("sex"));
				user.setNickName(res.getString("nickName"));
				user.setChannel(res.getString("channel"));
				user.setRank(res.getString("rank"));
				user.setBasicScore(res.getInt("basicScore"));
				user.setSignature(res.getString("signature"));
				user.setProfile(res.getString("profile"));
				user.setShowImg(res.getString("showImg"));
				user.setStatus(res.getString("status"));
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
		String sql = "select uuid,info.userId,mapping.tokenId,passwd,sex,nickName,info.channel,rank,basicScore,signature,profile,showImg,status,updateTime,createTime,schemaId,certify from user_info info inner join user_tokenId_mapping mapping on info.userId=mapping.userId  where schemaId>? order by schemaId  limit ?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, synId);
			ps.setInt(2, maxCnt);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				User user = new User();
				user.setUuid(res.getString("uuid"));
				user.setUserId(res.getString("info.userId"));
				user.setTokenId(res.getString("mapping.tokenId"));
				user.setPassWord(res.getString("passwd"));
				user.setSex(res.getString("sex"));
				user.setNickName(res.getString("nickName"));
				user.setChannel(res.getString("info.channel"));
				user.setRank(res.getString("rank"));
				user.setBasicScore(res.getInt("basicScore"));
				user.setSignature(res.getString("signature"));
				user.setProfile(res.getString("profile"));
				user.setShowImg(res.getString("showImg"));
				user.setStatus(res.getString("status"));
				user.setUpdateTime(res.getString("updateTime"));
				user.setCreateTime(res.getString("createTime"));
				user.setSchemaId(res.getString("schemaId"));
				user.setCertify(res.getString("certify"));
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
	 * 修改用户图像
	 * 
	 * @param userId
	 * @param url
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月9日下午1:26:05
	 */
	public String updateProfile(String userId, String url) {
		PreparedStatement ps = null;
		String sql = "update user_info set profile=?, schemaId=? where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, url);
			ps.setString(2, DateUtil.getTimeInMillis());
			ps.setString(3, userId);

			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[修改用户图像失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	public String updateShowImg(String userId, String url) {
		PreparedStatement ps = null;
		String sql = "update user_info set showImg=?, schemaId=? where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, url);
			ps.setString(2, DateUtil.getTimeInMillis());
			ps.setString(3, userId);

			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[修改用户直播封面失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	/**
	 * 修改密码
	 * 
	 * @param userId
	 * @param newPasswd
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月9日下午2:47:28
	 */
	public String updatePasswd(String userId, String newPasswd) {
		PreparedStatement ps = null;
		String sql = "update user_info set passwd=?,schemaId=? where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, newPasswd);
			ps.setString(2, DateUtil.getTimeInMillis());
			ps.setString(3, userId);

			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}
		} catch (Exception e) {
			this.getLogger().error("[修改用户密码失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	/**
	 * 修改性别
	 * 
	 * @param userId
	 * @param sex
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月18日下午6:15:48
	 */
	public String updateSex(String userId, String sex) {
		PreparedStatement ps = null;
		String sql = "update user_info set sex=?,schemaId=? where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, sex);
			ps.setString(2, DateUtil.getTimeInMillis());
			ps.setString(3, userId);

			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}
		} catch (Exception e) {
			this.getLogger().error("[修改用户性别失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	/**
	 * 修改用户昵称
	 * 
	 * @param userId
	 * @param nickName
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月9日下午2:49:12
	 */
	public String updateNickName(String userId, String nickName) {
		PreparedStatement ps = null;
		String sql = "update user_info set nickName=?,schemaId=? where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, nickName);
			ps.setString(2, DateUtil.getTimeInMillis());
			ps.setString(3, userId);

			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}
		} catch (Exception e) {
			this.getLogger().error("[修改用户昵称失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	/**
	 * 修改签名
	 * 
	 * @param userId
	 * @param signature
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月9日下午2:50:11
	 */
	public String updateSignature(String userId, String signature) {
		PreparedStatement ps = null;
		String sql = "update user_info set signature=?,schemaId=? where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, signature);
			ps.setString(2, DateUtil.getTimeInMillis());
			ps.setString(3, userId);

			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}
		} catch (Exception e) {
			this.getLogger().error("[修改用户签名失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	/**
	 * 更新用户位置
	 * 
	 * @param userId
	 * @param signature
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月10日下午1:02:15
	 */
	public String updatePositon(String userId, String position) {
		PreparedStatement ps = null;
		String sql = "update user_info set lastPosition=?,schemaId=? where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, position);
			ps.setString(2, DateUtil.getTimeInMillis());
			ps.setString(3, userId);

			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}
		} catch (Exception e) {
			this.getLogger().error("[修改用户位置失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	/**
	 * 获取待开放号池
	 * 
	 * @param status
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日下午11:27:27
	 */
	public ArrayList<String> getNextPid(String status) {
		ArrayList<String> pid = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select id, beginPid ,endPid from user_pid where status=? order by id";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, status);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				StringBuffer sb = new StringBuffer();
				sb.append(res.getString("id")).append("^").append(res.getString("beginPid")).append("^").append(res.getString("endPid"));
				pid.add(sb.toString());
			}

		} catch (Exception e) {
			this.getLogger().error("[获取号池号段失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return pid;
	}

	/**
	 * 更新号段状态
	 * 
	 * @param id
	 * @param status
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月8日上午12:01:56
	 */
	public String updatePIDStatus(int id, String status) {
		PreparedStatement ps = null;
		String sql = "update user_pid set status=? where id=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, status);
			ps.setInt(2, id);
			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[更新号段状态失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

	/**
	 * 关注
	 * 
	 * @param userId
	 * @param starId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月9日上午12:15:30
	 */
	public String subscribe(String userId, String starId) {
		PreparedStatement ps_fans = null, ps_sub = null;
		try {
			ps_sub = this.getConnnection().prepareStatement("insert into user_subscribe (userId,starId,createTime,schemaId) values(?,?,?,?)");
			ps_sub.setString(1, userId);
			ps_sub.setString(2, starId);
			ps_sub.setString(3, DateUtil.getStringDate());
			ps_sub.setString(4, DateUtil.getTimeInMillis());
			if (ps_sub.executeUpdate() <= 0) {
				return Constant.OperaterStatus.FAILED.getValue();
			}
			ps_fans = this.getConnnection().prepareStatement("insert into user_fans (userId,fansId,createTime,schemaId) values(?,?,?,?)");
			ps_fans.setString(1, starId);
			ps_fans.setString(2, userId);
			ps_fans.setString(3, DateUtil.getStringDate());
			ps_fans.setString(4, DateUtil.getTimeInMillis());

			if (ps_fans.executeUpdate() <= 0) {
				return Constant.OperaterStatus.FAILED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[添加关注失败]", e);
			return Constant.OperaterStatus.FAILED.getValue();
		} finally {
			DBReleaser.release(ps_sub);
			DBReleaser.release(ps_fans);
		}
		return Constant.OperaterStatus.SUCESSED.getValue();
	}

	/**
	 * 取消关注
	 * 
	 * @param userId
	 * @param starId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月10日下午5:15:08
	 */
	public String removeSubscribe(String userId, String starId) {
		PreparedStatement ps_fans = null, ps_sub = null;
		try {
			ps_sub = this.getConnnection().prepareStatement("delete from user_subscribe where  userId=? and starId=?");
			ps_sub.setString(1, userId);
			ps_sub.setString(2, starId);
			if (ps_sub.executeUpdate() <= 0) {
				return Constant.OperaterStatus.FAILED.getValue();
			}
			ps_fans = this.getConnnection().prepareStatement("delete from  user_fans where userId=? and fansId=?");
			ps_fans.setString(1, starId);
			ps_fans.setString(2, userId);

			if (ps_fans.executeUpdate() <= 0) {
				return Constant.OperaterStatus.FAILED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[取消关注失败]", e);
			return Constant.OperaterStatus.FAILED.getValue();
		} finally {
			DBReleaser.release(ps_sub);
			DBReleaser.release(ps_fans);
		}
		return Constant.OperaterStatus.SUCESSED.getValue();
	}

	public ArrayList<String> getSynFans(String synId, int maxCnt) {
		ArrayList<String> list = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select userId,fansId,schemaId from user_fans where schemaId>? order by schemaId  limit ?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, synId);
			ps.setInt(2, maxCnt);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				list.add(res.getString("userId") + "^" + res.getString("fansId") + "^" + res.getString("schemaId"));
			}
			return list;

		} catch (Exception e) {
			this.getLogger().error("[获取粉丝同步数据失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}

	/**
	 * 获取待同步订阅数据
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月9日上午12:22:08
	 */
	public ArrayList<String> getSynScribe(String synId, int maxCnt) {
		ArrayList<String> list = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select userId,starId,schemaId from user_subscribe where schemaId>? order by schemaId  limit ?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, synId);
			ps.setInt(2, maxCnt);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				list.add(res.getString("userId") + "^" + res.getString("starId") + "^" + res.getString("schemaId"));
			}
			return list;

		} catch (Exception e) {
			this.getLogger().error("[获取订阅同步数据失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}

	/**
	 * 获取需要同步的用户及设备数据
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author lxg
	 * @since 2016年5月11日上午12:22:08
	 */
	public ArrayList<String> getSynUserDevice(String synId, int maxCnt) {
		ArrayList<String> list = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet res = null;

		String sql = "select userId, tokenId,deviceNo,deviceType,provider, schemaId from user_device_mapping where schemaId>? order by schemaId limit ?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, synId);
			ps.setInt(2, maxCnt);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				list.add(res.getString("userId") + "^" + res.getString("tokenId") + "^" + res.getString("deviceType") + "^" + res.getString("schemaId"));
			}
		} catch (Exception e) {
			this.getLogger().error("[获取用户及设备信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return list;
	}

	/**
	 * 
	 * @param String
	 * @param status
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午6:55:26
	 */
	public String updateStatus(String userId, String status) {
		PreparedStatement ps = null;
		String sql = "update user_info set status=?,schemaId=? where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, status);
			ps.setString(2, DateUtil.getTimeInMillis());
			ps.setString(3, userId);
			if (ps.executeUpdate() > 0) {
				return OperaterStatus.SUCESSED.getValue();
			}
		} catch (Exception e) {
			this.getLogger().error("[修改用户状态失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return OperaterStatus.FAILED.getValue();
	}

	/**
	 * 添加用户推送设备ID
	 * 
	 * @param upm
	 * @author lxg
	 * @since 2016年5月11日上午12:22:08
	 * @return
	 */
	public String updateUserDevice(String userId, String tokenId, String deviceNo, String deviceType, String provider) {
		PreparedStatement ps = null;
		String updateSql = "update user_device_mapping set tokenId=? ,deviceNo=?, deviceType=?,provider=?, schemaId=? where userId=?";
		String addSql = "insert into user_device_mapping (tokenId ,deviceNo, deviceType,provider ,schemaId,userId) values(?, ?, ?, ?, ?, ?)";
		try {
			if (Validator.isEmpty(UserManager.getUserDevice(userId))) {
				ps = this.getConnnection().prepareStatement(addSql);
			} else {
				ps = this.getConnnection().prepareStatement(updateSql);
			}
			ps.setString(1, tokenId);
			ps.setString(2, deviceNo);
			ps.setString(3, deviceType);
			ps.setString(4, provider);
			ps.setString(5, DateUtil.getTimeInMillis());
			ps.setString(6, userId);
			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[更新用户推送设备信息失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return Constant.OperaterStatus.FAILED.getValue();
	}

}
