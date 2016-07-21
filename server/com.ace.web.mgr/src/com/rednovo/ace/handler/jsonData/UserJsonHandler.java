package com.rednovo.ace.handler.jsonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.configuration.Configuration;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * @author wenhui.Wang/2016年5月31日
 */
public class UserJsonHandler extends BasicServiceAdapter {

	@Override
	protected void service() {
		String key = this.getKey();
		if ("001-001".equals(key)) {
			this.login(); // 登录
		} else if ("001-002".equals(key)) {
			this.freezeUser(); // 用户管理--冻结,解冻
		} else if ("001-003".equals(key)) {
			this.forbidShower(); // --禁播,取消禁播
		} else if ("001-004".equals(key)) {
			this.downShower(); // --下播
		} else if ("001-005".equals(key)) {
			this.forbidSpeak(); // --禁言
		} else if ("001-006".equals(key)) {
			this.cancelForbidSpeak(); // --取消禁言
		} else if ("001-007".equals(key)) {
			this.sendSystemMessage(); // --发送系统消息
		} else if ("001-008".equals(key)) {
			this.sendWarnMessage(); // --发送警告消息
		} else if ("001-009".equals(key)) {
			this.getKeyWorld(); // 敏感词管理--列表
		} else if ("001-010".equals(key)) {
			this.addKeyWorld(); // --添加
		} else if ("001-011".equals(key)) {
			this.delKeyWorld(); // --删除
		} else if ("001-012".equals(key)) {
			this.getServers(); // 服务器管理 --列表
		} else if ("001-013".equals(key)) {
			this.addServers(); // --添加
		} else if ("001-014".equals(key)) {
			this.delServers(); // --删除
		} else if ("001-015".equals(key)) {
			this.getCoinHistory(); // 账户管理--加减币记录
		} else if ("001-016".equals(key)) {
			this.addSubtractCoin(); // --加减币
		} else if ("001-017".equals(key)) {
			this.getMenu(); //菜单列表
		} else if ("001-018".equals(key)) {
			this.getUserMenu(); //用户的菜单
		} else if ("001-019".equals(key)) {
			this.addUserMenu(); //给用户添加菜单
		} else if ("001-020".equals(key)) {
			this.getOrder(); //订单查询
		} else if ("001-021".equals(key)) {
			this.getExchangeList(); //提现记录
		}else if ("001-022".equals(key)) {
			this.getExchangeStepList(); //提现记录
		}
	}

	/**
	 * 登录
	 */
	private void login() {
		HashMap<String, String> para = new HashMap<String, String>();
		String userName = this.getWebHelper().getString("userName");
		para.put("userId", userName);
		String userPwd = this.getWebHelper().getString("userPwd");
		para.put("passwd", userPwd);
		String path = this.getWebHelper().getString("path");
		String res = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject response = JSON.parseObject(res);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(response.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(res);
		}
	}

	/**
	 * 用户管理--冻结,解冻
	 */
	private void freezeUser() {
		HashMap<String, String> para = new HashMap<String, String>();
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		String isActive = this.getWebHelper().getString("isActive");
		para.put("isActive", isActive);
		para.put("key", "001-002");
		String path = this.getWebHelper().getString("path");
		String res = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject response = JSON.parseObject(res);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(response.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(res);
		}
	}

	/**
	 * 用户管理--禁播,取消禁播
	 */
	private void forbidShower() {
		HashMap<String, String> para = new HashMap<String, String>();
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		String isForbidShow = this.getWebHelper().getString("isForbidShow");
		para.put("isForbidShow", isForbidShow);
		para.put("isActive", "");
		para.put("isAuthen", "");
		para.put("type", "0");
		para.put("key", "001-003");
		String path = this.getWebHelper().getString("path");
		String res = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject response = JSON.parseObject(res);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(response.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(res);
		}
	}

	/**
	 * 下播
	 */
	private void downShower() {
		String userId = this.getWebHelper().getString("userId");
		HashMap<String, String> para = new HashMap<String, String>();
		para.put("userId", userId);
		para.put("type", "1");
		String path = this.getWebHelper().getString("path");

		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("redis.server.url") + "/" + path + "", para);

		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(jsonData);
		}
	}

	/**
	 * 禁言
	 */
	private void forbidSpeak() {
		String userId = this.getWebHelper().getString("userId");
		String showId = this.getWebHelper().getString("showId");
		HashMap<String, String> para = new HashMap<String, String>();
		para.put("userId", userId);
		para.put("showId", showId);
		para.put("type", "0");
		String path = this.getWebHelper().getString("path");
		String res = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject response = JSON.parseObject(res);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(response.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(res);
		}
	}

	/**
	 * 取消禁言
	 */
	private void cancelForbidSpeak() {
		String userId = this.getWebHelper().getString("userId");
		String showId = this.getWebHelper().getString("showId");
		HashMap<String, String> para = new HashMap<String, String>();
		para.put("userId", userId);
		para.put("showId", showId);
		para.put("type", "1");
		String path = this.getWebHelper().getString("path");
		String res = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject response = JSON.parseObject(res);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(response.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(res);
		}
	}

	/**
	 * 用户管理--发送警告消息
	 */
	private void sendWarnMessage() {
		HashMap<String, String> para = new HashMap<String, String>();
		String warnMessage = this.getWebHelper().getString("warnMessage");
		para.put("msg", warnMessage);
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		para.put("type", "1");
		String path = this.getWebHelper().getString("path");
		if (Validator.isEmpty(warnMessage) || Validator.isEmpty(warnMessage) || Validator.isEmpty(path)) {
			this.setError("218");
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("redis.server.url") + "/" + path + "", para);

		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(jsonData);
		}
	}

	/**
	 * 用户管理--发送系统消息
	 */
	private void sendSystemMessage() {
		HashMap<String, String> para = new HashMap<String, String>();
		String systemMessage = this.getWebHelper().getString("systemMessage");
		para.put("msg", systemMessage);
		para.put("type", "4");
		String path = this.getWebHelper().getString("path");
		if (Validator.isEmpty(systemMessage) || Validator.isEmpty(path)) {
			this.setError("218");
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("redis.server.url") + "/" + path + "", para);

		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(jsonData);
		}
	}

	/**
	 * 获取关键字(敏感词)
	 */
	private void getKeyWorld() {
		HashMap<String, String> para = new HashMap<String, String>();
		String path = this.getWebHelper().getString("path");
		String type = this.getWebHelper().getString("type");
		para.put("type", type);
		if (Validator.isEmpty(path)) {
			this.setError("218");
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("redis.server.url") + "/" + path + "", para);
		JSONObject jsonObject = JSONObject.parseObject(jsonData);
		JSONArray jsonArray = jsonObject.getJSONArray("keywordlist");
		this.setValue("keywordlist", jsonArray);
	}

	/**
	 * 添加关键字(敏感词)
	 */
	private void addKeyWorld() {
		HashMap<String, String> para = new HashMap<String, String>();
		String txt = this.getWebHelper().getString("txt");
		para.put("txt", txt);
		String type = this.getWebHelper().getString("type");
		para.put("type", type);
		String methed = this.getWebHelper().getString("methed");
		para.put("methed", methed);
		String path = this.getWebHelper().getString("path");
		if (Validator.isEmpty(txt) || Validator.isEmpty(path)) {
			this.setError("218");
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("redis.server.url") + "/" + path + "", para);

		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(jsonData);
		}
	}

	/**
	 * 删除关键字(敏感词)
	 */
	private void delKeyWorld() {
		HashMap<String, String> para = new HashMap<String, String>();
		String keys = this.getWebHelper().getString("keys");
		para.put("txt", keys);
		String type = this.getWebHelper().getString("type");
		para.put("type", type);
		String methed = this.getWebHelper().getString("methed");
		para.put("methed", methed);
		String path = this.getWebHelper().getString("path");
		if (Validator.isEmpty(keys) || Validator.isEmpty(path)) {
			this.setError("218");
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("redis.server.url") + "/" + path + "", para);

		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(jsonData);
		}
	}

	/**
	 * 服务器管理--列表
	 */
	private void getServers() {
		HashMap<String, String> para = new HashMap<String, String>();
		String path = this.getWebHelper().getString("path");
		if (Validator.isEmpty(path)) {
			this.setError("218");
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject jsonObject = JSONObject.parseObject(jsonData);
		JSONArray jsonArray = jsonObject.getJSONArray("");
		this.setValue("keywordlist", jsonArray);
	}

	/**
	 * 服务器管理--添加
	 */
	private void addServers() {
		HashMap<String, String> para = new HashMap<String, String>();
		String ip = this.getWebHelper().getString("ip");
		para.put("ip", ip);
		String port = this.getWebHelper().getString("port");
		para.put("port", port);
		String status = this.getWebHelper().getString("status");
		para.put("status", status);
		String description = this.getWebHelper().getString("description");
		para.put("description", description);
		para.put("methed", "add");
		String path = this.getWebHelper().getString("path");
		if (Validator.isEmpty(ip) || Validator.isEmpty(port) || Validator.isEmpty(status) || Validator.isEmpty(description) || Validator.isEmpty(path)) {
			this.setError("600");
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(jsonData);
		}
	}

	/**
	 * 服务器管理--删除
	 */
	private void delServers() {
		HashMap<String, String> para = new HashMap<String, String>();
		String id = this.getWebHelper().getString("id");
		para.put("id", id);
		para.put("methed", "del");
		String path = this.getWebHelper().getString("path");
		if (Validator.isEmpty(path)) {
			this.setError("600");
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(jsonData);
		}
	}

	/**
	 * 账号管理--加减币记录
	 */
	private void getCoinHistory() {
		HashMap<String, String> para = new HashMap<String, String>();
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);

		String startTime = this.getWebHelper().getString("startTime");
		para.put("startTime", startTime);

		String endTime = this.getWebHelper().getString("endTime");
		para.put("endTime", endTime);

		String channel = this.getWebHelper().getString("channel");
		para.put("channel", channel);

		String logicType = this.getWebHelper().getString("logicType");
		para.put("logicType", logicType);

		para.put("page", "1");
		para.put("pageSize", "10000");
		String path = this.getWebHelper().getString("path");
		if (Validator.isEmpty(userId) || Validator.isEmpty(startTime) || Validator.isEmpty(endTime) || Validator.isEmpty(channel) || Validator.isEmpty(path)) {
			this.setError("600");
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			JSONArray jsonArray = obj.getJSONArray("accountList");
			this.setValue("rows", jsonArray);
		} else {
			this.setError(jsonData);
		}
	}

	/**
	 * 账号管理--加减币
	 */
	private void addSubtractCoin() {
		HashMap<String, String> para = new HashMap<String, String>();

		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		// String relateUserId = this.getWebHelper().getString("relateUserId");
		// para.put("relateUserId", relateUserId);
		String num = this.getWebHelper().getString("num");
		para.put("amount", num);

		String path = this.getWebHelper().getString("path");

		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(jsonData);
		}
	}

	/**
	 * 获取菜单列表
	 */
	private void getMenu() {
		StringBuffer sbf = new StringBuffer();
		Configuration cf = PPConfiguration.getXML("tree.xml");
		List<Object> node = cf.getList("node");
		for (int n = 0; n < node.size(); n++) {
			String nodeid = cf.getString("node(" + n + ")[@id]");
			String nodeText = cf.getString("node(" + n + ")[@text]");
			sbf.append("{");
			sbf.append("\"id\":\"" + nodeid + "\",");
			sbf.append("\"text\":\"" + nodeText + "\"");
			sbf.append("},");
			List<Object> menuId = cf.getList("node(" + n + ").menu[@id]");
			List<Object> menuText = cf.getList("node(" + n + ").menu[@text]");
			for (int i = 0; i < menuId.size(); i++) {
				sbf.append("{");
				sbf.append("\"id\":\"" + menuId.get(i) + "\",");
				sbf.append("\"text\":\"" + menuText.get(i) + "\"");
				sbf.append("},");
			}
		}
		sbf.setLength(sbf.length() - 1);
		this.setValue("menuList", "[" + sbf.toString() + "]");
	}

	/**
	 * 获取用户拥有菜单
	 */
	private void getUserMenu() {
		HashMap<String, String> para = new HashMap<String, String>();
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		String path = this.getWebHelper().getString("path");
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			JSONArray jsonArray = obj.getJSONArray("menuList");
			Configuration cf = PPConfiguration.getXML("tree.xml");
			List<Object> nodeId = cf.getList("node[@id]");
			List<Object> menuId = cf.getList("node.menu[@id]");
			menuId.addAll(nodeId);
			List<Object> idList = new ArrayList<Object>();
			for (Object object : menuId) {
				if (jsonArray.contains(object)) {
					idList.add(object);
				}
			}
			System.out.println(idList);
			this.setValue("idList", idList);
		} else {
			this.setError(jsonData);
		}
	}

	/**
	 * 给用户添加菜单
	 */
	private void addUserMenu() {
		HashMap<String, String> para = new HashMap<String, String>();
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		String menus = this.getWebHelper().getString("menus");
		para.put("menus", menus);
		String path = this.getWebHelper().getString("path");
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(jsonData);
		}
	}
	
	private void getOrder(){
		HashMap<String, String> para = new HashMap<String, String>();
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);		
		String startTime = this.getWebHelper().getString("startTime");
		para.put("startTime", startTime);			
		String endTime = this.getWebHelper().getString("endTime");
		para.put("endTime", endTime);			
		String status = this.getWebHelper().getString("status");
		para.put("status", status);		
		para.put("page", "1");
		para.put("pageSize", "100000");
		String path = this.getWebHelper().getString("path");
		if (Validator.isEmpty(startTime) || Validator.isEmpty(endTime) || Validator.isEmpty(path)) {
			this.setError("600");
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			JSONArray jsonArray = obj.getJSONArray("orderLost");
			this.setValue("rows", jsonArray);
		} else {
			this.setError(jsonData);
		}
	}
	
	/**
	 * 提现记录
	 */
	private void getExchangeList(){
		HashMap<String, String> para = new HashMap<String, String>();
		String startTime = this.getWebHelper().getString("startTime");
		para.put("startTime", startTime);			
		String endTime = this.getWebHelper().getString("endTime");
		para.put("endTime", endTime);			
		String status = this.getWebHelper().getString("status");
		para.put("status", status);		
		para.put("page", "1");
		para.put("pageSize", "100000");
		String path = this.getWebHelper().getString("path");
		if (Validator.isEmpty(path)) {
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			JSONArray jsonArray = obj.getJSONArray("exchangeList");
			this.setValue("rows", jsonArray);
		} else {
			this.setError(jsonData);
		}
	}
	
	
	private void getExchangeStepList(){
		HashMap<String, String> para = new HashMap<String, String>();
		String step = this.getWebHelper().getString("step");
		para.put("step", step);		
		para.put("page", "1");
		para.put("pageSize", "100000");
		String path = this.getWebHelper().getString("path");
		if (Validator.isEmpty(path)) {
			return;
		}
		String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/" + path + "", para);
		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			JSONArray jsonArray = obj.getJSONArray("exchangeList");
			this.setValue("rows", jsonArray);
		} else {
			this.setError(jsonData);
		}
	}
}
