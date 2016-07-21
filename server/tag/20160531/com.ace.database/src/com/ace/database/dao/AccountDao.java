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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.ace.database.ds.DBReleaser;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.AccountDetail;
import com.rednovo.ace.entity.IncomeDetail;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.Validator;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class AccountDao extends BasicDao {
	/**
	 * 
	 */
	public AccountDao(Connection connection) {
		super(connection);
	}

	/**
	 * 获取账户余额
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日上午12:51:53
	 */
	public BigDecimal getBalance(String userId) {
		PreparedStatement ps = null;
		ResultSet res = null;
		try {
			ps = this.getConnnection().prepareStatement("select balance from account_balance where userId=? ");
			ps.setString(1, userId);
			res = ps.executeQuery();
			if (res != null && res.next()) {
				BigDecimal money = res.getBigDecimal("balance");
				return money;
			} else {
				this.getLogger().info("[账户" + userId + " 不存在]");
				return null;
			}
		} catch (Exception e) {
			this.getLogger().error("[获取账户" + userId + " 余额失败]", e);

		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}

	/**
	 * 获取礼物总收入
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日下午1:27:14
	 */
	public BigDecimal getIncome(String userId) {
		PreparedStatement ps = null;
		ResultSet res = null;
		try {
			ps = this.getConnnection().prepareStatement("select balance from account_income where userId=? ");
			ps.setString(1, userId);
			res = ps.executeQuery();
			if (res != null && res.next()) {
				float money = res.getFloat("balance");
				return new BigDecimal(money);
			}
		} catch (Exception e) {
			this.getLogger().error("[获取用户" + userId + "收入总额失败]", e);

		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}

	/**
	 * 修改用户收入余额
	 * 
	 * @param userId
	 * @param amount
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日下午1:28:25
	 */
	public String updateIncome(String userId, BigDecimal amount) {
		boolean isExist = true;
		String sql = "update account_income set balance=balance+?,schemaId=? where userId=?";

		BigDecimal balance = this.getIncome(userId);
		if (Validator.isEmpty(balance)) {
			sql = "insert into account_income (userId,balance,schemaId) values (?,?,?)";
			isExist = false;
		}

		PreparedStatement ps = null;
		String res = Constant.OperaterStatus.FAILED.getValue();
		try {
			ps = this.getConnnection().prepareStatement(sql);
			if (isExist) {
				ps.setBigDecimal(1, amount);
				ps.setString(2, DateUtil.getTimeInMillis());
				ps.setString(3, userId);
			} else {
				ps.setString(1, userId);
				ps.setBigDecimal(2, amount);
				ps.setString(3, DateUtil.getTimeInMillis());
			}
			if (ps.executeUpdate() > 0) {
				res = Constant.OperaterStatus.SUCESSED.getValue();
			}
		} catch (Exception e) {
			this.getLogger().error("[修改用户" + userId + " 收入总额失败]", e);

		} finally {
			DBReleaser.release(ps);
		}
		return res;
	}

	/**
	 * 调整账户余额
	 * 
	 * @param userId
	 * @param amount
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日上午11:37:52
	 */
	public String updateBlance(String userId, BigDecimal amount) {
		PreparedStatement ps = null;
		boolean isExist = true;
		String res = Constant.OperaterStatus.FAILED.getValue();
		String sql = "update account_balance set balance=balance+?,schemaId=? where userId=? ";
		if (this.getBalance(userId) == null) {
			isExist = false;
			sql = "insert into account_balance(userId,balance,schemaId) values(?,?,?)";
		}
		try {
			ps = this.getConnnection().prepareStatement(sql);
			if (isExist) {
				ps.setBigDecimal(1, amount);
				ps.setString(2, DateUtil.getTimeInMillis());
				ps.setString(3, userId);
			} else {
				ps.setString(1, userId);
				ps.setBigDecimal(2, amount);
				ps.setString(3, DateUtil.getTimeInMillis());
			}
			if (ps.executeUpdate() > 0) {
				return Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[修改账户" + userId + "充值余额失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return res;
	}

	/**
	 * 查询账户明细
	 * 
	 * @param userId
	 * @param beginTime
	 * @param endTime
	 * @param type
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午2:09:19
	 */
	public HashMap<String, ArrayList<AccountDetail>> getAccountDetailList(String userId, String beginTime, String endTime, Constant.ChangeType type, int page, int pageSize) {
		HashMap<String, ArrayList<AccountDetail>> val = new HashMap<String, ArrayList<AccountDetail>>();
		String exeCode = "";
		ArrayList<AccountDetail> list = new ArrayList<AccountDetail>();
		String dataBase = "coin_add_detail";
		if (Constant.ChangeType.ADD.getValue().equals(type.getValue())) {// 进账
			dataBase = "coin_add_detail";
		} else if (Constant.ChangeType.REDUCE.getValue().equals(type.getValue())) {// 出账
			dataBase = "coin_reduce_detail";
		} else {
			this.getLogger().error("[账户明细变化类型未指定]");
			exeCode = "104";
		}
		if (page <= 0) {
			page = 1;
		}
		if (pageSize <= 0) {
			pageSize = 10;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select id, userId,userName,relatedUserId,relatedUserName,amount,channel,desciption,createTime from   " + dataBase + " where userId=? and createTime>=? and createTime<=? order by createTime desc limit ?,? ";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, beginTime);
			ps.setString(3, endTime);
			ps.setInt(4, (page - 1) * pageSize);
			ps.setInt(5, pageSize);
			
			rs = ps.executeQuery();

			while (rs != null && rs.next()) {
				AccountDetail ad = new AccountDetail();
				ad.setId(rs.getString("id"));
				ad.setUserId(rs.getString("userId"));
				ad.setUserName(rs.getString("userName"));
				ad.setRelateUserId(rs.getString("relatedUserId"));
				ad.setRelateUserName(rs.getString("relatedUserName"));
				ad.setAmount(rs.getBigDecimal("amount"));
				ad.setChannel(rs.getString("channel"));
				ad.setDesciption(rs.getString("desciption"));
				ad.setCreateTime(rs.getString("createTime"));
				list.add(ad);
			}
			exeCode = Constant.OperaterStatus.SUCESSED.getValue();

		} catch (Exception e) {
			exeCode = "300";
			this.getLogger().error("[查询账户明细失败]", e);
		} finally {
			DBReleaser.release(ps, rs);
		}
		val.put(exeCode, list);
		return val;

	}

	/**
	 * 获取账户进出账明细
	 * 
	 * @param userId
	 * @param beginTime
	 * @param endTime
	 * @param type
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午4:00:17
	 */
	public HashMap<String, ArrayList<IncomeDetail>> getIncomeDetailList(String userId, String beginTime, String endTime, Constant.ChangeType type, int page, int pageSize) {
		HashMap<String, ArrayList<IncomeDetail>> val = new HashMap<String, ArrayList<IncomeDetail>>();
		String exeCode = "";
		ArrayList<IncomeDetail> list = new ArrayList<IncomeDetail>();
		String dataBase = "income_add_detail";
		if (Constant.ChangeType.ADD.getValue().equals(type.getValue())) {// 进账
			dataBase = "income_add_detail";
		} else if (Constant.ChangeType.REDUCE.getValue().equals(type.getValue())) {// 出账
			dataBase = "income_reduce_detail";
		} else {
			this.getLogger().error("[账户明细变化类型未指定]");
			exeCode = "104";
		}
		if (page <= 0) {
			page = 1;
		}
		if (pageSize < 0) {
			pageSize = 10;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select id, userId,userName,relatedUserId,relatedUserName,amount,channel,desciption,createTime from   " + dataBase + " where userId=? and createTime>=? and createTime<=? order by createTime desc limit ?,? ";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, beginTime);
			ps.setString(3, endTime);
			ps.setInt(4, (page - 1) * pageSize);
			ps.setInt(5, pageSize);
			rs = ps.executeQuery();

			while (rs != null && rs.next()) {
				IncomeDetail detail = new IncomeDetail();
				detail.setId(rs.getString("id"));
				detail.setUserId(rs.getString("userId"));
				detail.setUserName(rs.getString("userName"));
				detail.setRelatedUserId(rs.getString("relatedUserId"));
				detail.setRelatedUserName(rs.getString("relatedUserName"));
				detail.setAmount(rs.getBigDecimal("amount"));
				detail.setChannel(rs.getString("channel"));
				detail.setDescription(rs.getString("desciption"));
				detail.setCreateTime(rs.getString("createTime"));
				list.add(detail);
			}
			exeCode = Constant.OperaterStatus.SUCESSED.getValue();

		} catch (Exception e) {
			exeCode = "300";
			this.getLogger().error("[查询兑点明细失败]", e);
		} finally {
			DBReleaser.release(ps, rs);
		}
		val.put(exeCode, list);
		return val;

	}

	/**
	 * 添加账户变化明细
	 * 
	 * @param acd
	 * @param changeType
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日上午10:46:29
	 */
	public String addAccountDetail(AccountDetail acd, Constant.ChangeType type) {
		String dataBase = "coin_add_detail";
		if (Constant.ChangeType.ADD.getValue().equals(type.getValue())) {// 进账
			dataBase = "coin_add_detail";
		} else if (Constant.ChangeType.REDUCE.getValue().equals(type.getValue())) {// 出账
			dataBase = "coin_reduce_detail";
		} else {
			this.getLogger().error("[账户明细变化类型未指定]");
			return Constant.OperaterStatus.FAILED.getValue();
		}

		PreparedStatement ps = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			String sql = " insert into " + dataBase + " (userId,userName,relatedUserId,relatedUserName,amount,channel,desciption,createTime) values (?,?,?,?,?,?,?,?) ";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, acd.getUserId());
			ps.setString(2, acd.getUserName());
			ps.setString(3, acd.getRelateUserId());
			ps.setString(4, acd.getRelateUserName());
			ps.setBigDecimal(5, acd.getAmount());
			ps.setString(6, acd.getChannel());
			ps.setString(7, acd.getDesciption());
			ps.setString(8, DateUtil.getStringDate());
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[添加账户变动明细失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return exeRes;

	}

	/**
	 * 添加收入收支明细
	 * 
	 * @param acd
	 * @param type
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月5日下午3:51:41
	 */
	public String addIncomeDetail(IncomeDetail detail, Constant.ChangeType type) {
		String dataBase = "income_add_detail";
		if (type.getValue().equals(Constant.ChangeType.ADD.getValue())) {// 收入进账
			dataBase = "income_add_detail";
		} else if (type.getValue().equals(Constant.ChangeType.REDUCE.getValue())) {// 收入出账
			dataBase = "income_reduce_detail";
		} else {
			this.getLogger().error("[收入变化类型未指定]");
			return Constant.OperaterStatus.FAILED.getValue();
		}

		PreparedStatement ps = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			String sql = " insert into " + dataBase + " (userId,userName,relatedUserId,relatedUserName,amount,channel,desciption,createTime) values (?,?,?,?,?,?,?,?) ";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, detail.getUserId());
			ps.setString(2, detail.getUserName());
			ps.setString(3, detail.getRelatedUserId());
			ps.setString(4, detail.getRelatedUserName());
			ps.setBigDecimal(5, detail.getAmount());
			ps.setString(6, detail.getChannel());
			ps.setString(7, detail.getDescription());
			ps.setString(8, DateUtil.getStringDate());
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[添加收入账户变动明细失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return exeRes;
	}

	/**
	 * 获取待同步余额数据
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日上午12:46:57
	 */
	public ArrayList<HashMap<String, String>> getSynBalance(String synId, int maxCnt) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select userId,balance,schemaId from account_balance where schemaId>? order by schemaId  limit ?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, synId);
			ps.setInt(2, maxCnt);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				String userId = res.getString("userId");
				String balance = res.getString("balance");
				String schemaId = res.getString("schemaId");
				map.put(userId, balance + "^" + schemaId);
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			this.getLogger().error("[获取待同步余额信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;

	}

	public ArrayList<HashMap<String, String>> getSynIncome(String synId, int maxCnt) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select userId,balance,schemaId from account_income where schemaId>? order by schemaId  limit ?";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, synId);
			ps.setInt(2, maxCnt);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				String userId = res.getString("userId");
				String balance = res.getString("balance");
				String schemaId = res.getString("schemaId");
				map.put(userId, balance + "^" + schemaId);
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			this.getLogger().error("[获取待同步兑点余额信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;

	}
}
