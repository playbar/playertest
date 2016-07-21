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
package com.rednovo.ace.handler.relation;

import java.util.ArrayList;
import java.util.List;

import com.ace.database.service.UserService;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.constant.Constant.InteractMode;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.ace.entity.User;
import com.rednovo.ace.globalData.LiveShowManager;
import com.rednovo.ace.globalData.OutMessageManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.ace.globalData.UserRelationManager;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.KeyGenerator;

/**
 * 关系管理
 * 
 * @author yongchao.Yang/2016年3月3日
 */
public class RelationHanlder extends BasicServiceAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if ("003-001".equals(key)) {// 订阅
			this.subscribe();
		} else if ("003-002".equals(key)) {// 举报
			this.report();
		} else if ("003-003".equals(key)) {// 建议
			this.suggest();
		} else if ("003-004".equals(key)) {// 粉丝列表
			this.getFansList();
		} else if ("003-005".equals(key)) {// 订阅列表
			this.getSubscribeList();
		} else if ("003-006".equals(key)) {// 订阅列表
			this.removeSubscribe();
		}
	}

	/**
	 * 订阅
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月3日上午10:34:46
	 */
	private void subscribe() {
		String userId = this.getWebHelper().getString("userId");
		String starId = this.getWebHelper().getString("starId");
		// 判断是否已经订阅
		if (UserRelationManager.getSubscribe(userId, 1, 10000).contains(starId)) {// 已经关注
			this.setError("206");
			return;

		}
		String exeCode = UserService.subscribe(userId, starId);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			// 如果对方正在直播，则累加粉丝，便于直播结束统计
			List<String> list = LiveShowManager.getSortList(1, 10000);
			if (list.contains(starId)) {
				LiveShowManager.addShowExtData(starId, "FANS", "1");
			}
			Message msg = new Message();
			Summary sumy = new Summary();
			sumy.setRequestKey("002-018");
			sumy.setChatMode(ChatMode.GROUP.getValue());
			sumy.setInteractMode(InteractMode.REQUEST.getValue());
			sumy.setMsgId(KeyGenerator.createUniqueId());
			sumy.setMsgType(MsgType.TXT_MSG.getValue());
			sumy.setSenderId(userId);
			sumy.setShowId(starId);
			sumy.setReceiverId(starId);
			msg.setSumy(sumy);

			User u = UserManager.getUser(userId);
			JSONObject obj = new JSONObject();
			obj.put("type", "5");
			obj.put("userId", userId);
			obj.put("nickName", u.getNickName());
			obj.put("profile", u.getProfile());
			msg.setBody(obj);

			OutMessageManager.addMessage(msg);

			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 取消关注
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月10日下午5:29:43
	 */
	private void removeSubscribe() {
		String userId = this.getWebHelper().getString("userId");
		String starId = this.getWebHelper().getString("starId");

		// 判断是否已经订阅
		if (!UserRelationManager.getSubscribe(userId, 1, 10000).contains(starId)) {// 还未关注
			this.setError("207");
			return;
		}
		String exeCode = UserService.removeSubscribe(userId, starId);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			// 取消缓存映射
			UserRelationManager.removeSubscribe(userId, starId);
			UserRelationManager.removeFans(starId, userId);
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 举报
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月3日下午10:40:43
	 */
	private void report() {
		String userId = this.getWebHelper().getString("userId");
		String starId = this.getWebHelper().getString("starId");
		this.setSuccess();
	}

	/**
	 * 建议
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月3日下午10:41:56
	 */
	private void suggest() {
		String userId = this.getWebHelper().getString("userId");
		String starId = this.getWebHelper().getString("starId");
		String suggest = this.getWebHelper().getString("suggest");
		String contactInfo = this.getWebHelper().getString("contactInfo");
		this.setSuccess();
	}

	/**
	 * 粉丝列表
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月3日下午10:44:26
	 */
	private void getFansList() {
		String userId = this.getWebHelper().getString("userId");
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");
		List<String> list = UserRelationManager.getFans(userId, page, pageSize);
		ArrayList<User> fans = new ArrayList<User>();
		if (list != null && list.size() > 0) {
			for (String uid : list) {
				fans.add(UserManager.getUser(uid));
			}
		}
		this.setSuccess();
		this.setValue("fansList", fans);
	}

	/**
	 * 获取订阅列表
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月10日下午1:48:12
	 */
	private void getSubscribeList() {
		String userId = this.getWebHelper().getString("userId");
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");

		List<String> list = UserRelationManager.getSubscribe(userId, page, pageSize);
		ArrayList<User> subScribe = new ArrayList<User>();
		if (list != null && list.size() > 0) {
			for (String uid : list) {
				subScribe.add(UserManager.getUser(uid));
			}
		}

		this.setSuccess();
		this.setValue("subscribeList", subScribe);
	}

}
