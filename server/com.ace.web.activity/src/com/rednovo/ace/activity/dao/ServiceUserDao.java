/*  ------------------------------------------------------------------------------ 
 *                  软件名称:
 *                  公司名称:
 *                  开发作者:ZuKang.Song
 *       			开发时间:2016年5月13日/2016
 *    				All Rights Reserved 2016-2016
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.web.activity
 *                  fileName：ServiceUserDao.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.activity.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.rednovo.ace.activity.ds.DBReleaser;
import com.rednovo.ace.activity.entity.User;

/**
 * @author ZuKang.Song/2016年5月13日
 */
public class ServiceUserDao extends BasicDao {
	/**
	 * @param connection
	 */
	public ServiceUserDao(Connection connection) {
		super(connection);
	}

	private static Logger logger = Logger.getLogger(ServiceUserDao.class);
	
	/**
	 * 获取用户资料
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月3日上午9:47:29
	 */
	public String getServiceUser(String userId) {
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select nickName  FROM user_info where userId=?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, userId);
			res = ps.executeQuery();
			if (res != null && res.next()) {
				return res.getString("nickName");
			}

		} catch (Exception e) {
			this.getLogger().error("[根据用户id获取用户昵称失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}
}
