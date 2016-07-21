/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播移动
 *                  公司名称:美播娱乐
 *                  开发作者:sg.z
 *       			开发时间:2014年7月29日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自美播娱乐研发部，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：meibo-admin
 *                  fileName：UserHandler.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.handler.show;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ace.database.service.ShowService;
import com.ace.database.service.UserService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.constant.Constant.OperaterStatus;
import com.rednovo.ace.constant.Constant.userStatus;
import com.rednovo.ace.entity.LiveShow;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.ace.entity.User;
import com.rednovo.ace.globalData.GlobalUserSessionMapping;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.ace.globalData.OutMessageManager;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.ace.globalData.UserRelationManager;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.KeyGenerator;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.UserAssistant;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class ShowHandler extends BasicServiceAdapter {
	private Logger logger = Logger.getLogger(ShowHandler.class);
	private static Random rdm = new Random();

	public static ArrayList<User> freedomUser = new ArrayList<User>();
	private static Random r = new Random();

	static {

		XMLConfiguration cf = (XMLConfiguration) PPConfiguration.getXML("robot.xml");
		List<ConfigurationNode> list = cf.getRoot().getChildren();
		for (ConfigurationNode node2 : list) {
			User u = new User();
			u.setUserId(String.valueOf(node2.getChild(1).getValue()));
			u.setNickName(String.valueOf(node2.getChild(4).getValue()));
			u.setProfile(String.valueOf(node2.getChild(9).getValue()));
			freedomUser.add(u);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if (StringUtils.equals("002-002", key)) {// 读取用户资料
			this.newShow();
		} else if ("002-004".equals(key)) {// 获取直播列表
			this.getLiveShowList();
		} else if ("002-005".equals(key)) {// 获取观众列表
			this.getShowInitData();
		} else if ("002-011".equals(key)) {// 分享直播
			this.share();
		} else if ("002-014".equals(key)) {// 结束直播(删除数据库)
			this.updateShowData();
		} else if ("002-015".equals(key)) {// 结束直
			this.clearShowData();
		} else if ("002-016".equals(key)) {// 直播清算
			this.getShowSettleData();
		} else if ("001-029".equals(key)) {// 获取我的订阅直列表
			this.getSubscribeShow();
		} else if ("002-017".equals(key)) {// 获取应用宝直播列表
			this.getTencentList();
		} else if ("002-018".equals(key)) {// 禁言
			this.forbidUser();
		} else if ("002-019".equals(key)) {// 拉去分享模板
			this.getShareData();
		}
	}

	/**
	 * 开播
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月8日下午2:49:04
	 */
	private void newShow() {
		String userId = this.getWebHelper().getString("userId");
		String title = this.getWebHelper().getString("title");
		String position = this.getWebHelper().getString("position");
		String name = this.getWebHelper().getString("showImg");
		byte[] data = this.getWebHelper().getBytes("showImg");

		User u = UserService.getUser(userId);
		// 判断用户状态
		if (u.getIsActive().equals(userStatus.FREEZE.getValue())) {
			// 用户已经被冻结
			this.setError("208");
			return;
		}

		// 主播开播
		if (userStatus.FORBIDSHOW.getValue().equals(u.getIsForbidShow())) {
			this.setError("222", "用户被禁播");
			return;
		}

		if (Validator.isEmpty(title)) {
			title = UserManager.getUser(userId).getNickName() + "的直播";
		} else {
			List<String> words = StaticDataManager.getKeyWord(Constant.KeyWordType.NAME.getValue());
			if (Validator.checkKeyWord(title, words)) {
				this.setError("218");
				return;
			}
		}
		if (Validator.isEmpty(position)) {
			position = "地球的背面";
		}

		String exeCode = "";
		if (!Validator.isEmpty(name)) {
			String imgName = KeyGenerator.createUniqueId() + "-show.png";
			// 保存封面
			String path = UserAssistant.getUserAbsoluteDir(userId);
			File img = new File(path + File.separator + imgName);

			FileOutputStream fis;
			try {
				if (!img.exists()) {
					img.createNewFile();
				}
				fis = new FileOutputStream(img);
				fis.write(data);
				fis.flush();
				fis.close();

			} catch (Exception e) {
				logger.error("保存封面失败", e);
				e.printStackTrace();
			}
			String visitUrl = PPConfiguration.getProperties("cfg.properties").getString("img.server.root.url") + UserAssistant.getUserRelativeDir(userId) + "/" + imgName;
			exeCode = UserService.updateShowImg(userId, visitUrl);
			if (!Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
				this.setError(exeCode);
				return;
			}
		}

		exeCode = ShowService.addShow(userId, u.getShowImg(), title, position);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			// 更新用户的位置信息
			UserManager.setExtData(userId, "position", position);
			this.setValue("upStream", PPConfiguration.getProperties("cfg.properties").getString("cdn.upstream.url") + userId);
			this.setValue("showId", userId);
			this.setSuccess();

			// 添加推送队列
			UserManager.addPushStarId(userId);

		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 直播结算
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月15日下午7:20:39
	 */
	private void getShowSettleData() {
		String showId = this.getWebHelper().getString("showId");

		LiveShow ls = ShowService.getLiveShow(showId);

		if (ls != null) {
			this.setValue("support", ls.getSupportCnt());
			this.setValue("coins", ls.getCoinCnt());
			this.setValue("fans", ls.getFansCnt());
			this.setValue("length", ls.getLength());
			this.setValue("memberCnt", ls.getMemberCnt());
			this.setSuccess();
		} else {
			this.setError("300");
		}

	}

	/**
	 * 观众列表
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月8日下午8:52:11
	 */
	private void getShowInitData() {
		String showId = this.getWebHelper().getString("showId");
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");
		ArrayList<User> memberList = new ArrayList<User>();
		List<String> sessionIds = LiveShowManager.getMemberList(showId, 1, 1000);

		for (String sid : sessionIds) {
			String uid = GlobalUserSessionMapping.getSessionUser(sid);
			if (Validator.isEmpty(uid)) {
				User u = new User();
				u.setUserId("-1");
				u.setNickName("游客");
				memberList.add(u);
			} else {
				User u = UserManager.getUser(uid);
				u.setChannel(null);
				u.setCreateTime(null);
				u.setPassWord(null);
				u.setUpdateTime(null);
				u.setShowImg(null);
				u.setTokenId(null);
				u.setSchemaId(null);
				u.setUuid(null);
				u.setSubscribeCnt(0);

				memberList.add(u);
			}

			Collections.shuffle(freedomUser);
			for (int i = 0; i < 30; i++) {
				memberList.add(freedomUser.get(i));
			}
		}

		String supportCnt = LiveShowManager.getTotalSupportCnt(showId);
		String memberSize = String.valueOf((LiveShowManager.getMemberCnt(showId) - 1) * 14 + LiveShowManager.getRobotCnt(showId));

		this.setSuccess();
		this.setValue("memberList", memberList);
		this.setValue("supportCnt", Validator.isEmpty(supportCnt) ? "0" : supportCnt);
		this.setValue("memberSize", memberSize);
	}

	/**
	 * 获取直播列表
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月3日下午9:54:45
	 */

	private void getLiveShowList() {
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");
		List<String> list = LiveShowManager.getSortList(page, pageSize);
		ArrayList<User> users = new ArrayList<User>();
		ArrayList<LiveShow> shows = new ArrayList<LiveShow>();
		HashMap<String, User> userMap = new HashMap<String, User>();
		for (String id : list) {
			LiveShow s = LiveShowManager.getShow(id);
			User u = UserManager.getUser(s.getUserId());
			userMap.put(id, u);
			if (s != null) {
				// 考虑机器人数
				long memberCnt = (LiveShowManager.getMemberCnt(id) - 1) * 14 + LiveShowManager.getRobotCnt(id);
				s.setMemberCnt(String.valueOf(memberCnt));

				long scroe = getSort(memberCnt, 0l, Long.valueOf(s.getStartTime()), Long.valueOf(u.getBasicScore()));
				s.setSortCnt(scroe);
				shows.add(s);
			}
		}

		Collections.sort(shows);

		for (LiveShow liveShow : shows) {
			users.add(userMap.get(liveShow.getShowId()));
		}

		this.setSuccess();
		this.setValue("showList", shows);
		this.setValue("userList", users);
	}

	/**
	 * 分享成功
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年5月25日下午3:34:48
	 */
	private void share() {
		String userId = this.getWebHelper().getString("userId");
		String showId = this.getWebHelper().getString("showId");
		String channel = this.getWebHelper().getString("channel");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("showId", showId);
		params.put("type", "2");
		String res = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("redis.server.url") + "/001-001", params);
		/*Message msg = new Message();
		Summary sumy = new Summary();
		sumy.setRequestKey("002-018");
		sumy.setChatMode(ChatMode.GROUP.getValue());
		sumy.setInteractMode(InteractMode.REQUEST.getValue());
		sumy.setMsgId("9999-share");
		sumy.setMsgType(MsgType.TXT_MSG.getValue());
		sumy.setSenderId(userId);
		sumy.setShowId(showId);
		sumy.setReceiverId(showId);
		msg.setSumy(sumy);

		JSONObject obj = new JSONObject();
		User u = UserManager.getUser(userId);

		obj.put("type", "2");
		obj.put("channel", channel);
		obj.put("userId", userId);
		obj.put("nickName", u.getNickName());
		obj.put("profile", u.getProfile());
		msg.setBody(obj);
		// 将消息压入缓存中
		OutMessageManager.addMessage(msg);
*/
		this.setSuccess();

	}

	private void getSubscribeShow() {
		String userId = this.getWebHelper().getString("userId");
		ArrayList<User> users = new ArrayList<User>();
		ArrayList<LiveShow> subscribeList = new ArrayList<LiveShow>();

		// 获取我订阅的主播
		List<String> stars = UserRelationManager.getSubscribe(userId, 1, 10000);
		if (!Validator.isEmpty(stars)) {
			List<String> showIds = LiveShowManager.getSortList(1, 10000);
			for (String uid : stars) {
				for (String showId : showIds) {
					if (showId.equals(uid)) {
						users.add(UserManager.getUser(uid));
						LiveShow s = LiveShowManager.getShow(showId);
						s.setMemberCnt(String.valueOf((LiveShowManager.getMemberCnt(showId) - 1) * 14 + LiveShowManager.getRobotCnt(showId)));
						subscribeList.add(s);
						break;
					}
				}
			}

		} else {
			this.setValue("ifSubscribe", "0");
		}

		if (!subscribeList.isEmpty()) {
			this.setValue("showList", subscribeList);
		} else {
			// 获取最新的10条直播
			List<String> newShowIds = LiveShowManager.getSortList(1, 10);
			for (String sid : newShowIds) {
				users.add(UserManager.getUser(sid));
				LiveShow s = LiveShowManager.getShow(sid);
				s.setMemberCnt(String.valueOf((LiveShowManager.getMemberCnt(sid) - 1) * 14 + LiveShowManager.getRobotCnt(sid)));
				subscribeList.add(s);

			}
			this.setValue("recommandList", subscribeList);
		}
		this.setValue("userList", users);
		this.setSuccess();

	}

	/**
	 * 结束直播
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月8日下午8:58:42
	 */
	private void updateShowData() {
		String showId = this.getWebHelper().getString("showId");
		if (OperaterStatus.SUCESSED.getValue().equals(ShowService.finishShow(showId))) {
			this.setSuccess();
		} else {
			this.setError("300");
		}
	}

	/**
	 * 结束直播所有数据
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月14日下午8:58:10
	 */
	private void clearShowData() {
		String showId = this.getWebHelper().getString("showId");
		ShowService.finishShow(showId);
		LiveShowManager.removeSortShow(showId);
		this.setSuccess();
	}

	private void getTencentList() {
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");

		List<String> list = LiveShowManager.getSortList(page, 1000);

		HashMap<String, User> userMap = new HashMap<String, User>();
		ArrayList<User> users = new ArrayList<User>();
		ArrayList<LiveShow> shows = new ArrayList<LiveShow>();
		for (String id : list) {
			LiveShow s = LiveShowManager.getShow(id);
			if (s != null) {
				shows.add(s);

				long memCnt = LiveShowManager.getMemberCnt(id);
				s.setMemberCnt(String.valueOf(memCnt));
				// 获取各项统计值
				HashMap<String, String> data = LiveShowManager.getShowExtData(id);
				// 获取用户
				User u = UserManager.getUser(s.getUserId());
				userMap.put(u.getUserId(), u);

				// 计算用户当前拍排序值
				long score = this.getSort(memCnt, Long.valueOf(data.get("SUPPORT")), Long.valueOf(s.getStartTime()), (long) u.getBasicScore());
				s.setSortCnt(score);
			}
		}

		// 排序
		Collections.sort(shows);
		JSONArray ja = new JSONArray();

		for (LiveShow liveShow : shows) {
			User u = userMap.get(liveShow.getUserId());
			int radio_id = Integer.parseInt(liveShow.getUserId());
			String radio_name = u.getNickName();
			String radio_subname = "";
			String radio_city = liveShow.getPosition();
			long radio_fan_number = UserRelationManager.getFansCnt(liveShow.getUserId()) + rdm.nextInt(1000);
			int radio_show_number = Integer.parseInt(liveShow.getMemberCnt()) + rdm.nextInt(1000);
			String radio_label = "";
			String radio_pic_url = u.getShowImg();

			String radio_action_url = "http://api.17ace.cn/share/index2.html?showId=" + liveShow.getShowId();
			int is_showing_now = 1;

			JSONObject jo = new JSONObject();
			jo.put("radio_id", radio_id);
			jo.put("radio_name", radio_name);
			jo.put("radio_subname", radio_subname);
			jo.put("radio_city", radio_city);
			jo.put("radio_fan_number", radio_fan_number);
			jo.put("radio_show_number", radio_show_number);
			jo.put("radio_label", radio_label);
			jo.put("radio_pic_url", radio_pic_url);
			jo.put("radio_action_url", radio_action_url);
			jo.put("is_showing_now", is_showing_now);

			ja.add(jo);

		}
		this.setValue("radio_list", ja);
		this.setValue("ret", 0);
		this.setValue("msg", "OK");
		this.setSuccess();
	}

	private void forbidUser() {
		String showId = this.getWebHelper().getString("showId");
		String userId = this.getWebHelper().getString("userId");
		String type = this.getWebHelper().getString("type");
		if ("1".equals(type)) {
			LiveShowManager.delForbidUser(userId, showId);
		} else {
			LiveShowManager.addForbidUser(userId, showId);
		}
		this.setSuccess();
	}

	/**
	 * 拉取分享模板数据
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年5月24日下午8:39:12
	 */
	private void getShareData() {
		String showId = this.getWebHelper().getString("showId");
		String userId = this.getWebHelper().getString("userId");
		String type = this.getWebHelper().getString("type");

		if (Validator.isEmpty(userId) || Validator.isEmpty(type) || Validator.isEmpty(showId)) {
			this.setError("218");
			return;
		}
		// type:1 主播自己分享 0普通用户

		String title = StaticDataManager.getSysConfig("shareTitle"), imgSrc = "", sumy = StaticDataManager.getSysConfig("shareSummary"), url = StaticDataManager.getSysConfig("shareURL") + "?showId=" + showId;
		User u = UserManager.getUser(showId);
		if ("1".equals(type)) {
			title = title.replaceAll("\\$", u.getNickName());
		} else {
			LiveShow show = LiveShowManager.getShow(showId);
			title = show.getTitle();
		}

		imgSrc = u.getShowImg();
		this.setValue("title", title);
		this.setValue("imgSrc", Validator.isEmpty(imgSrc) ? "http://cache.17ace.cn/share/default.png" : imgSrc);
		this.setValue("sumy", sumy.replaceAll("\\$", u.getNickName()));
		this.setValue("url", url);

		this.setSuccess();

	}

	/**
	 * 计算直播的排序
	 * 
	 * @param memberCnt int 每个用户2个权重
	 * @param supportCnt int 每个100个赞1个权重
	 * @param remainTime int 每剩余10秒2个权重
	 * @param basicCnt int 基本权重
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年4月23日下午6:33:25
	 */
	private long getSort(long memberCnt, long supportCnt, long beginTime, long basicCnt) {
		long score = memberCnt * 2;
		// score = score + (supportCnt / 500);

//		long remainTime = (System.currentTimeMillis() - beginTime) / 1000;// 播放时长
//		// 开播前5分钟给于加权
//		if (remainTime < 600) {
//			score = score + (600 - remainTime) / 60;
//		}
		score = score + basicCnt;

		return score;
	}
}
