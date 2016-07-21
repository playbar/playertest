/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2015年4月16日/2015
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.leduo.bb.imserver
 *                  fileName：LiveShowManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.globalData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.entity.LiveShow;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.RedisConfig.CacheName;
import com.rednovo.tools.RedisService;
import com.rednovo.tools.Validator;

/**
 * 直播间数据管理
 * 
 * @author yongchao.Yang/2015年4月16日
 */
public class LiveShowManager {

	/**
	 * 直播观众列表
	 */
	private static String LIVESHOW_SESSION_LIST = "live_show_session_list";

	/**
	 * 正在直播列表
	 */
	private static String LIVESHOW_LIST = "live_show_list";

	/**
	 * 直播详情
	 */
	private static String LIVESHOW_INFO_MAP = "live_show_info_map";
	/**
	 * 用户所属直播间
	 */
	private static String SESSION_LIVESHOW_MAP = "session_liveshow_map";
	/**
	 * 房间扩展数据
	 */
	private static String LIVESHOW_EXT_DATA = "liveshow_ext_data_map";
	/**
	 * 连送礼物
	 */
	private static String GIFT_SERIES_STRING = "gift_series_string";
	/**
	 * 禁言
	 */
	private static String FORBID_USER_SHOW_MAP = "fobid_user_show_map";

	/**
	 * 直播间在线人数
	 */
	private static String LIVESHOW_MEMBER_CNT_MAP = "liveshow_member_cnt_map";

	/**
	 * 添加在线观众。</br> 考虑到游客身份，所以对应的是sessionId而非userId。需要用户时，则通过sessionId获取对应的user
	 * 
	 * @param liveShowId
	 * @param sessionIds
	 * @author Yongchao.Yang
	 * @since 2016年2月27日上午11:12:20
	 */
	public static void addMember(String liveShowId, String sessionId) {
		RedisService rs = RedisService.getServer(CacheName.LIVESHOW_MEMBER_DATA.getNode());
		rs.addList(LIVESHOW_SESSION_LIST + "_" + liveShowId, sessionId);
		rs.addMap(SESSION_LIVESHOW_MAP, sessionId, liveShowId);
	}

	public static void delMember(String liveShowId, String sessionId) {
		RedisService.getServer(CacheName.LIVESHOW_MEMBER_DATA.getNode()).removeList(LIVESHOW_SESSION_LIST + "_" + liveShowId, sessionId, true);
	}

	public static void delSessionShow(String sessionId) {
		RedisService.getServer(CacheName.LIVESHOW_MEMBER_DATA.getNode()).delMapKey(SESSION_LIVESHOW_MAP, sessionId);
	}

	public static String getSessionShow(String sessionId) {
		return RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).getMapValue(SESSION_LIVESHOW_MAP, sessionId);
	}

	/**
	 * 累加直播数据
	 * 
	 * @param userId
	 * @param key
	 * @param value
	 * @author Yongchao.Yang
	 * @since 2016年3月25日下午2:19:05
	 */
	public static void addShowExtData(String userId, String key, String value) {
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).sumMapValue(LIVESHOW_EXT_DATA + "_" + userId, key, Long.valueOf(value));
	}

	/**
	 * 获取直播结算数据
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月25日下午3:09:41
	 */
	public static HashMap<String, String> getShowExtData(String userId) {
		HashMap<String, String> extData = new HashMap<String, String>();
		extData.put("SHARE", "0");
		extData.put("SUPPORT", "0");
		extData.put("COIN", "0");
		extData.put("FANS", "0");
		extData.put("MEMBER", "0");
		extData.put("LENGTH", "0");
		LiveShow s = LiveShowManager.getShow(userId);
		if (s == null) {
			return extData;
		}
		// 直播时长
		String length = String.valueOf((Long.valueOf(DateUtil.getTimeInMillis()) - Long.valueOf(s.getStartTime())) / 1000);

		Map<String, String> data = RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).getMap(LIVESHOW_EXT_DATA + "_" + userId);
		String supportCnt = Validator.isEmpty(data.get("SUPPORT")) ? "0" : data.get("SUPPORT");
		String coinCnt = Validator.isEmpty(data.get("COIN")) ? "0" : data.get("COIN");
		String fansCnt = Validator.isEmpty(data.get("FANS")) ? "0" : data.get("FANS");
		String memberCnt = Validator.isEmpty(data.get("MEMBER")) ? "0" : data.get("MEMBER");
		String shareCnt = Validator.isEmpty(data.get("SHARE")) ? "0" : data.get("SHARE");

		extData.put("SHARE", shareCnt);
		extData.put("SUPPORT", supportCnt);
		extData.put("COIN", coinCnt);
		extData.put("FANS", fansCnt);
		extData.put("MEMBER", memberCnt);
		extData.put("LENGTH", length);

		return extData;
	}

	/**
	 * 累加房间点赞个数
	 * 
	 * @param showId
	 * @param cnt
	 * @author Yongchao.Yang
	 * @since 2016年3月12日下午9:19:40
	 */
	public static void addSupportCnt(String showId, int cnt) {
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).sumMapValue(LIVESHOW_EXT_DATA + "_" + showId, "SUPPORT", cnt);
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).sumMapValue(LIVESHOW_EXT_DATA + "_" + showId, "NEW_SUPPORT", cnt);
	}

	public static String getTotalSupportCnt(String showId) {
		return RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).getMapValue(LIVESHOW_EXT_DATA + "_" + showId, "SUPPORT");
	}

	public static String getNewSupportCnt(String showId) {
		RedisService rs = RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode());
		String cnt = rs.getMapValue(LIVESHOW_EXT_DATA + "_" + showId, "NEW_SUPPORT");
		rs.delMapKey(LIVESHOW_EXT_DATA + "_" + showId, "NEW_SUPPORT");
		return cnt;
	}

	/**
	 * 获取观众列表
	 * 
	 * @param groupId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月5日下午10:15:30
	 */
	public static List<String> getMemberList(String liveShowId, int page, int pageSize) {
		return RedisService.getServer(CacheName.LIVESHOW_MEMBER_DATA.getNode()).getList((LIVESHOW_SESSION_LIST + "_" + liveShowId), (page - 1) * pageSize, (page - 1) * pageSize + pageSize - 1);
	}

	/**
	 * 获取群成员数量
	 * 
	 * @param groupId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2015年4月14日上午12:29:14
	 */
	public static long getMemberCnt(String liveShowId) {
		return RedisService.getServer(CacheName.LIVESHOW_MEMBER_DATA.getNode()).getListSize(LIVESHOW_SESSION_LIST + "_" + liveShowId);
	}

	/**
	 * 更新直播排序列表
	 * 
	 * @param liveShowIds
	 * @author Yongchao.Yang
	 * @since 2016年2月27日上午11:37:33
	 */
	public static void updateShowSort(List<String> list) {
		RedisService rs = RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode());
		// 先删除老的排序，然后添加新排序
		rs.removeKey(LIVESHOW_LIST);
		if (list == null || list.size() == 0) {
			return;
		}
		String[] shows = new String[list.size()];
		list.toArray(shows);
		// 先删除老的排序，然后添加新排序
		rs.addList(LIVESHOW_LIST, shows);

	}

	/**
	 * 获取直播列表
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年2月27日上午11:45:28
	 */
	public static List<String> getSortList(int page, int pageSize) {
		if (page <= 0) {
			page = 1;
		}
		int beginIndex = (page - 1) * pageSize;
		return RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).getList(LIVESHOW_LIST, beginIndex, beginIndex + pageSize - 1);
	}

	/**
	 * 删除直播数据
	 * 
	 * @param showId
	 * @author Yongchao.Yang
	 * @since 2016年3月12日下午10:46:41
	 */
	public static void removeSortShow(String showId) {
		// 删除首页数据
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).removeList(LIVESHOW_LIST, showId, true);
		// 删除直播详情
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).delMapKey(LIVESHOW_INFO_MAP, showId);
		// 删除观众列表
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).removeKey(LIVESHOW_SESSION_LIST + "_" + showId);
		// 删除点赞
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).removeKey(LIVESHOW_EXT_DATA + "_" + showId);
	}

	/**
	 * 新增直播
	 * 
	 * @param show
	 * @author Yongchao.Yang
	 * @since 2016年2月27日上午11:49:25
	 */
	public static void addShow(LiveShow show) {
		String showStr = JSON.toJSONString(show);
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).addMap(LIVESHOW_INFO_MAP, show.getShowId(), showStr);
	}

	public static LiveShow getShow(String id) {
		String showStr = RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).getMapValue(LIVESHOW_INFO_MAP, id);
		if (Validator.isEmpty(showStr)) {
			return null;
		} else {
			return JSON.parseObject(showStr, LiveShow.class);
		}
	}

	/**
	 * 添加连送礼物
	 * 
	 * @param userId
	 * @param showId
	 * @param giftId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月23日下午6:26:20
	 */
	public static String addSeriesGift(String userId, String showId, String giftId) {
		RedisService rs = RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode());
		String cntStr = rs.getString(GIFT_SERIES_STRING + "_" + userId + "_" + showId + "_" + giftId);
		if (!Validator.isEmpty(cntStr)) {
			cntStr = String.valueOf(Integer.parseInt(cntStr) + 1);
		} else {
			cntStr = "1";
		}
		rs.addString(GIFT_SERIES_STRING + "_" + userId + "_" + showId + "_" + giftId, 6, cntStr);
		return cntStr;
	}

	/**
	 * 添加禁言用户，时限30分总
	 * 
	 * @param userId
	 * @param showId
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午7:22:43
	 */
	public static void addForbidUser(String userId, String showId) {
		long expireTime = Long.valueOf(DateUtil.getTimeInMillis()) + 1800 * 1000;
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).addMap(FORBID_USER_SHOW_MAP, showId + "_" + userId, String.valueOf(expireTime));
	}

	/**
	 * 取消禁言用户
	 * 
	 * @param userId
	 * @param showId
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午7:22:43
	 */
	public static void delForbidUser(String userId, String showId) {
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).delMapKey(FORBID_USER_SHOW_MAP, showId + "_" + userId);
	}

	/**
	 * 判断用户在指定房间是否禁言
	 * 
	 * @param userId
	 * @param showId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午7:29:48
	 */
	public static boolean isUserForbidden(String userId, String showId) {
		return RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).getMapKeys(FORBID_USER_SHOW_MAP).contains(showId + "_" + userId);
	}

	/**
	 * 获取所有被禁言的用户
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午7:31:02
	 */
	public static Map<String, String> getAllForbiddenUser() {
		return RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).getMap(FORBID_USER_SHOW_MAP);
	}

	/**
	 * 清除被禁言的用户
	 * 
	 * @param key
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午7:39:22
	 */
	public static void removeForbiddenUser(ArrayList<String> key) {
		String[] keys = new String[key.size()];
		key.toArray(keys);
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).delMapKey(FORBID_USER_SHOW_MAP, keys);
	}

	/**
	 * 增加直播间在线人数
	 * 
	 * @param showId
	 * @param cnt
	 * @author Yongchao.Yang
	 * @since 2016年5月26日上午11:16:16
	 */
	public static void updateRobotCnt(String showId, long cnt) {
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).sumMapValue(LIVESHOW_MEMBER_CNT_MAP, showId, cnt);
	}

	/**
	 * 获取直播间人数
	 * 
	 * @param showId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年5月26日上午11:17:37
	 */
	public static long getRobotCnt(String showId) {
		String cnt = RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).getMapValue(LIVESHOW_MEMBER_CNT_MAP, showId);
		if (Validator.isEmpty(cnt)) {
			return 0;
		}
		return Long.parseLong(cnt);
	}

	/**
	 * 清除直播间人数
	 * 
	 * @param showId
	 * @author Yongchao.Yang
	 * @since 2016年5月26日上午11:25:31
	 */
	public static void clearRobotCnt(String showId) {
		RedisService.getServer(CacheName.LIVESHOW_INFO_DATA.getNode()).delMapKey(LIVESHOW_MEMBER_CNT_MAP, showId);
	}
}
