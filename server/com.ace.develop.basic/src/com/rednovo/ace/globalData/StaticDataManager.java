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
 *                  fileName：UserManager.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.globalData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.AD;
import com.rednovo.ace.entity.Gift;
import com.rednovo.ace.entity.GoodInfo;
import com.rednovo.tools.RedisConfig.CacheName;
import com.rednovo.tools.RedisService;

/**
 * 商品，礼物缓存数据
 * 
 * @author yongchao.Yang/2015年4月16日
 */
public class StaticDataManager {
	private static Logger logger = Logger.getLogger(StaticDataManager.class);
	/**
	 * 用户数据
	 */

	private static String GOOD_INFO_MAP = "good_info_map";

	private static String GIFT_INFO_MAP = "gift_info_map";

	private static String SYS_CONFIG_MAP = "sys_config_map";

	private static String NAME_KEY_WORD_SET = "name_key_word_set";// 昵称敏感词

	private static String CHAT_KEY_WORD_SET = "chat_key_word_set";// 聊天敏感词

	private static String SHOW_BANNA_LIST = "show_banna_list";

	/**
	 * 添加缓存用户
	 * 
	 * @param user
	 * @author Yongchao.Yang
	 * @since 2014年11月6日下午3:30:07
	 */
	public static void updateGoodInfo(ArrayList<GoodInfo> goods) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (GoodInfo good : goods) {
			map.put(good.getId(), JSON.toJSONString(good));
		}
		RedisService.getServer(CacheName.STATIC_DATA.getNode()).removeKey(GOOD_INFO_MAP);
		RedisService.getServer(CacheName.STATIC_DATA.getNode()).addMap(GOOD_INFO_MAP, map);
	}

	public static void updateGift(ArrayList<Gift> giftList) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (Gift gift : giftList) {
			map.put(gift.getId(), JSON.toJSONString(gift));
		}
		RedisService.getServer(CacheName.STATIC_DATA.getNode()).removeKey(GIFT_INFO_MAP);
		RedisService.getServer(CacheName.STATIC_DATA.getNode()).addMap(GIFT_INFO_MAP, map);
	}

	/**
	 * 添加缓存banna
	 * 
	 * @param bannaList
	 * @author Yongchao.Yang
	 * @since 2014年11月6日下午3:30:07
	 */
	public static void updateShowBannerList(List<AD> bannaList) {
		if (bannaList != null && bannaList.size() > 0) {
			RedisService rs = RedisService.getServer(CacheName.STATIC_DATA.getNode());
			String[] strs = new String[bannaList.size()];
			for (int i = 0; i < bannaList.size(); i++) {
				strs[i] = JSON.toJSONString(bannaList.get(i));
			}
			rs.removeKey(SHOW_BANNA_LIST);
			rs.addList(SHOW_BANNA_LIST, strs);
		}
	}

	/**
	 * 获取缓存banner列表
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年11月6日下午3:30:07
	 */
	public static List<AD> getBannerList() {
		List<String> str = RedisService.getServer(CacheName.STATIC_DATA.getNode()).getList(SHOW_BANNA_LIST);
		List<AD> bannaList = new ArrayList<AD>();
		for (String jsonStr : str) {
			bannaList.add(JSON.parseObject(jsonStr, AD.class));
		}
		return bannaList;
	}

	/**
	 * 添加系统配置参数
	 * 
	 * @param params
	 * @author Yongchao.Yang
	 * @since 2016年3月17日上午11:45:30
	 */
	public static void addSysConfig(HashMap<String, String> params) {
		RedisService.getServer(CacheName.STATIC_DATA.getNode()).addMap(SYS_CONFIG_MAP, params);
	}

	public static String getSysConfig(String key) {
		return RedisService.getServer(CacheName.STATIC_DATA.getNode()).getMapValue(SYS_CONFIG_MAP, key);
	}

	public static Map<String, String> getSysConfig() {
		return RedisService.getServer(CacheName.STATIC_DATA.getNode()).getMap(SYS_CONFIG_MAP);
	}

	/**
	 * 获取商品信息
	 * 
	 * @param userId
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月6日下午3:30:13
	 */
	public static GoodInfo getGoodInfo(String gid) {
		RedisService rs = RedisService.getServer(CacheName.STATIC_DATA.getNode());
		String goodStr = rs.getMapValue(GOOD_INFO_MAP, gid);
		if (goodStr != null && !goodStr.equals("")) {
			return JSON.parseObject(goodStr, GoodInfo.class);
		}
		return null;
	}

	public static Gift getGift(String giftId) {
		RedisService rs = RedisService.getServer(CacheName.STATIC_DATA.getNode());
		String giftStr = rs.getMapValue(GIFT_INFO_MAP, giftId);
		if (giftStr != null && !giftStr.equals("")) {
			return JSON.parseObject(giftStr, Gift.class);
		}
		return null;
	}

	public static List<GoodInfo> getGoodList() {
		RedisService rs = RedisService.getServer(CacheName.STATIC_DATA.getNode());
		List<String> list = rs.getMapValues(GOOD_INFO_MAP);
		ArrayList<GoodInfo> goods = new ArrayList<GoodInfo>();
		for (String good : list) {
			goods.add(JSON.parseObject(good, GoodInfo.class));
		}
		return goods;
	}

	public static List<Gift> getGiftList() {
		RedisService rs = RedisService.getServer(CacheName.STATIC_DATA.getNode());
		List<String> list = rs.getMapValues(GIFT_INFO_MAP);
		ArrayList<Gift> gifts = new ArrayList<Gift>();
		for (String gift : list) {
			gifts.add(JSON.parseObject(gift, Gift.class));
		}
		return gifts;
	}

	/***
	 * * 添加敏感词 type = name为昵称敏感词 chat为聊天敏感词
	 * 
	 * @param key
	 * @author ZuKang.Song
	 * @since 2016年6月6日下午4:15:47
	 */
	public static void addKeyWord(String txt, String type) {
		if (Constant.KeyWordType.NAME.getValue().equals(type)) {
			RedisService.getServer(CacheName.STATIC_DATA.getNode()).addSet(NAME_KEY_WORD_SET, txt);
		} else if (Constant.KeyWordType.CHAT.getValue().equals(type)) {
			RedisService.getServer(CacheName.STATIC_DATA.getNode()).addSet(CHAT_KEY_WORD_SET, txt);
		}
	}

	/**
	 * 获取敏感词 type = name为昵称敏感词 chat为聊天敏感词
	 * 
	 * @param keys
	 * @param type
	 * @author ZuKang.Song
	 * @since 2016年6月6日下午4:38:46
	 */
	public static List<String> getKeyWord(String type) {
		Set<String> set = new HashSet<String>();
		if (Constant.KeyWordType.NAME.getValue().equals(type)) {
			set = RedisService.getServer(CacheName.STATIC_DATA.getNode()).getSet(NAME_KEY_WORD_SET);
		} else if (Constant.KeyWordType.CHAT.getValue().equals(type)) {
			set = RedisService.getServer(CacheName.STATIC_DATA.getNode()).getSet(CHAT_KEY_WORD_SET);
		}
		List<String> kwlist = new ArrayList<String>(set);
		return kwlist;
	}

	/**
	 * 移除敏感词 type = name为昵称敏感词 chat为聊天敏感词
	 * 
	 * @param keys
	 * @param type
	 * @author ZuKang.Song
	 * @since 2016年6月6日下午4:38:46
	 */
	public static void delKeyWord(String[] keys, String type) {
		if (Constant.KeyWordType.NAME.getValue().equals(type)) {
			RedisService.getServer(CacheName.STATIC_DATA.getNode()).removeSet(NAME_KEY_WORD_SET, keys);
		} else if (Constant.KeyWordType.CHAT.getValue().equals(type)) {
			RedisService.getServer(CacheName.STATIC_DATA.getNode()).removeSet(CHAT_KEY_WORD_SET, keys);
		}
	}
}
