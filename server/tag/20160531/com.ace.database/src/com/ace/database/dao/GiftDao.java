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
 *                  fileName：GiftDao.java
 *  -------------------------------------------------------------------------------
 */
package com.ace.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.ace.database.ds.DBReleaser;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.Gift;
import com.rednovo.ace.entity.GiftDetail;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.Validator;

/**
 * @author yongchao.Yang/2016年3月5日
 */
public class GiftDao extends BasicDao {

	/**
	 * @param connection
	 */
	public GiftDao(Connection connection) {
		super(connection);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 添加礼物变化明细
	 * 
	 * @param acd
	 * @param changeType
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月4日上午10:46:29
	 */
	public String addGiftChangeDetail(GiftDetail acd, Constant.ChangeType type) {
		String dataBase = "coin_add_detail";
		if (type.getValue().equals(Constant.ChangeType.ADD.getValue())) {// 进账
			dataBase = "receive_detail";
		} else {// 出账
			dataBase = "send_detail";
		}

		PreparedStatement ps = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			String sql = " insert into " + dataBase + " (userId,userName,relatedUserId,relatedUserName,giftId,giftName,giftCnt,price,totalValue,channel,description,createTime) values (?,?,?,?,?,?,?,?,?,?,?,?) ";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, acd.getUserId());
			ps.setString(2, acd.getUserName());
			ps.setString(3, acd.getRelateUserId());
			ps.setString(4, acd.getRelateUserName());
			ps.setString(5, acd.getGiftId());
			ps.setString(6, acd.getGiftName());
			ps.setInt(7, acd.getGiftCnt());
			ps.setBigDecimal(8, acd.getPrice());
			ps.setBigDecimal(9, acd.getTotalValue());
			ps.setString(10, acd.getChannel());
			ps.setString(11, acd.getDescription());
			ps.setString(12, DateUtil.getStringDate());
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[添加礼物变动明细失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return exeRes;
	}

	/**
	 * 获取礼物
	 * 
	 * @param giftId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月5日下午12:46:43
	 */
	public Gift getGift(String giftId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select name,pic ,sendPrice,transformPrice,isCombined,status,createTime,schemaId,type from gift_info where id=?";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, giftId);
			rs = ps.executeQuery();

			if (rs != null && rs.next()) {
				Gift g = new Gift();
				g.setId(giftId);
				g.setCreateTime(rs.getString("createTime"));
				g.setIsCombined(rs.getString("isCombined"));
				g.setName(rs.getString("name"));
				g.setPic(rs.getString("pic"));
				g.setSchemaId(rs.getString("schemaId"));
				g.setSendPrice(rs.getBigDecimal("sendPrice"));
				g.setStatus(rs.getString("status"));
				g.setTransformPrice(rs.getBigDecimal("transformPrice"));
				g.setType(rs.getString("type"));
				return g;
			}

		} catch (Exception e) {
			this.getLogger().error("[获取礼物失败]", e);
		} finally {
			DBReleaser.release(ps, rs);
		}
		return null;

	}

	/**
	 * 获取礼物进出账明细
	 * 
	 * @param userId
	 * @param beginTime
	 * @param endTime
	 * @param type
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月6日下午4:02:10
	 */
	public HashMap<String, ArrayList<GiftDetail>> getGiftDetailList(String userId, String beginTime, String endTime, Constant.ChangeType type, int page, int pageSize) {
		HashMap<String, ArrayList<GiftDetail>> val = new HashMap<String, ArrayList<GiftDetail>>();
		String exeCode = "";
		ArrayList<GiftDetail> list = new ArrayList<GiftDetail>();
		String dataBase = "receive_detail";
		if (Constant.ChangeType.ADD.getValue().equals(type.getValue())) {// 进账
			dataBase = "receive_detail";
		} else if (Constant.ChangeType.REDUCE.getValue().equals(type.getValue())) {// 出账
			dataBase = "send_detail";
		} else {
			this.getLogger().error("[礼物进出账类型未指定]");
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
			String sql = " select id, userId,userName,relatedUserId,relatedUserName,giftId,giftName,giftCnt,price,totalValue,channel,description,createTime from   " + dataBase + " where userId=? and createTime>=? and createTime<=? order by createTime desc limit ?,? ";
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, beginTime);
			ps.setString(3, endTime);
			ps.setInt(4, (page - 1) * pageSize);
			ps.setInt(5, pageSize);
			rs = ps.executeQuery();

			while (rs != null && rs.next()) {
				GiftDetail gd = new GiftDetail();
				gd.setId(rs.getInt("id"));
				gd.setUserId(rs.getString("userId"));
				gd.setUserName(rs.getString("userName"));
				gd.setRelateUserId(rs.getString("relatedUserId"));
				gd.setRelateUserName(rs.getString("relatedUserName"));
				gd.setPrice(rs.getBigDecimal("price"));
				gd.setChannel(rs.getString("channel"));
				gd.setGiftCnt(rs.getInt("giftCnt"));
				gd.setGiftId(rs.getString("giftId"));
				gd.setGiftName(rs.getString("giftName"));
				gd.setTotalValue(rs.getBigDecimal("totalValue"));
				gd.setDescription(rs.getString("description"));
				gd.setCreateTime(rs.getString("createTime"));

				list.add(gd);
			}
			exeCode = Constant.OperaterStatus.SUCESSED.getValue();

		} catch (Exception e) {
			exeCode = "300";
			this.getLogger().error("[查询礼物明细失败]", e);
		} finally {
			DBReleaser.release(ps, rs);
		}
		val.put(exeCode, list);
		return val;

	}

	/**
	 * 获取待同步礼物数据
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日上午12:26:48
	 */
	public ArrayList<Gift> getGift() {
		ArrayList<Gift> list = new ArrayList<Gift>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select id,name,pic,sendPrice,transformPrice,isCombined,status,sortId,createTime,schemaId,type from gift_info where status='1' order by sortId";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				Gift gi = new Gift();
				gi.setId(res.getString("id"));
				gi.setName(res.getString("name"));
				gi.setPic(res.getString("pic"));
				gi.setSendPrice(res.getBigDecimal("sendPrice"));
				gi.setTransformPrice(res.getBigDecimal("transformPrice"));
				gi.setIsCombined(res.getString("isCombined"));
				gi.setStatus(res.getString("status"));
				gi.setCreateTime(res.getString("createTime"));
				gi.setSchemaId(res.getString("schemaId"));
				gi.setSortId(res.getInt("sortId"));
				gi.setType(res.getString("type"));
				list.add(gi);
			}
			return list;
		} catch (Exception e) {
			this.getLogger().error("[获取待同步礼物信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;

	}
	
	/**
	 * 添加或者修改礼物
	 * 
	 * @param acd
	 * @param changeType
	 * @return
	 * @author lxg
	 * @since 2016年3月4日上午10:46:29
	 */
	public String addOrUpdateGift(Gift gift) {
		String dataBase = "gift_info";
		
		PreparedStatement ps = null;
		String exeRes = Constant.OperaterStatus.FAILED.getValue();
		try {
			String sql = null;
			boolean isUpdate = Validator.isEmpty(gift.getId()) ? false : Long.parseLong(gift.getId()) > 0 ;
			if(isUpdate){
				sql = " update " + dataBase + " set name=?, pic=?, sendPrice=?, transformPrice=?, isCombined=?, status=?, sortId=?, createTime=?, schemaId=?, type=? where id=?";
			}else{
				sql = " insert into " + dataBase + " (name, pic, sendPrice, transformPrice, isCombined, status, sortId, createTime, schemaId, type) values (?,?,?,?,?,?,?,?,?,?) ";
			}
			
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, gift.getName());
			ps.setString(2, gift.getPic());
			ps.setBigDecimal(3, gift.getSendPrice());
			ps.setBigDecimal(4, gift.getTransformPrice());
			ps.setString(5, gift.getIsCombined());
			ps.setString(6, gift.getStatus());
			ps.setInt(7, gift.getSortId());
			ps.setString(8, DateUtil.getTimeInMillis());
			ps.setString(9, DateUtil.getTimeInMillis());
			ps.setString(10, gift.getType());
			if(isUpdate){
				ps.setString(11, gift.getId());
			}
			if (ps.executeUpdate() > 0) {
				exeRes = Constant.OperaterStatus.SUCESSED.getValue();
			}

		} catch (Exception e) {
			this.getLogger().error("[添加或者修改礼物失败]", e);
		} finally {
			DBReleaser.release(ps);
		}
		return exeRes;
	}
	
	/**
	 * 获取下架，上架，待上架礼物数据
	 * 
	 * @param synId
	 * @param maxCnt
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月7日上午12:26:48
	 */
	public ArrayList<Gift> getGiftByStatus(String status) {
		ArrayList<Gift> list = new ArrayList<Gift>();
		PreparedStatement ps = null;
		ResultSet res = null;
		String sql = "select id,name,pic,sendPrice,transformPrice,isCombined,status,sortId,createTime,schemaId,type from gift_info where status=? order by sortId";
		try {
			ps = this.getConnnection().prepareStatement(sql);
			ps.setString(1, status);
			res = ps.executeQuery();
			while (res != null && res.next()) {
				Gift gi = new Gift();
				gi.setId(res.getString("id"));
				gi.setName(res.getString("name"));
				gi.setPic(res.getString("pic"));
				gi.setSendPrice(res.getBigDecimal("sendPrice"));
				gi.setTransformPrice(res.getBigDecimal("transformPrice"));
				gi.setIsCombined(res.getString("isCombined"));
				gi.setStatus(res.getString("status"));
				gi.setCreateTime(res.getString("createTime"));
				gi.setSchemaId(res.getString("schemaId"));
				gi.setSortId(res.getInt("sortId"));
				gi.setType(res.getString("type"));
				list.add(gi);
			}
			return list;
		} catch (Exception e) {
			this.getLogger().error("[获取待同步礼物信息失败]", e);
		} finally {
			DBReleaser.release(ps, res);
		}
		return null;
	}
}
