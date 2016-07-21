package com.rednovo.ace.handler.jsonData;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.web.HttpSender;

/**
 * @author wenhui.Wang/2016年5月18日
 */
public class AccountJsonHandler extends BasicServiceAdapter{

	Logger logger = Logger.getLogger(OrderJsonHandler.class);
	private static String url = PPConfiguration.getProperties("cfg.properties").getString("server.dispatch.path");

	private String getJsonData(String path, HashMap<String, String> para){
		return HttpSender.httpClientGet(url + path, para, para);
	}
	
	private String page(JSONArray jsonArray, int pageInt, int pageSizeInt){
		int size = jsonArray.size();
		int startIndex = (pageInt - 1)*pageSizeInt;
		int endIndex = startIndex+pageSizeInt;
		
		JSONObject print = new JSONObject();
        JSONArray data = new JSONArray();
        print.put("rows", data);
		for (int i = startIndex; i < (endIndex>size?size:endIndex); i++) {
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			data.add(jsonObj);
		}
		String json = print.toJSONString();
		
		StringBuffer sbf = new StringBuffer(json);
		sbf.replace(0, 1, "{\"total\":\""+jsonArray.size()+"\",");
		
		return sbf.toString();
	}
	
	@Override
	protected void service() {
		String key = this.getKey();
		if("001-300".equals(key)){
			this.getSumManage();
		} else if ("001-050".equals(key)){//账户管理--订单查询
			this.getRechargeOrder();
		} else if ("001-120".equals(key)) {//账号管理--礼物查询
			this.getGiftHandler();
		} else if ("001-060".equals(key)){//账户管理--加减币记录
			this.getCoinHistory();
		} else if ("001-340".equals(key)){//账户管理--加减币
			this.addSubtractCoin();
		}
	}
	
	/**
	 * 账号管理--余额查询
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月9日
	 */
	private void getSumManage(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		
		String page = "";
		int pageInt = this.getWebHelper().getInt("page");
		para.put("page", page.valueOf(pageInt));
		
		String pageSize = "";
		int pageSizeInt = this.getWebHelper().getInt("rows");
		para.put("pageSize", pageSize.valueOf(pageSizeInt));
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		
		JSONObject obj = JSONObject.parseObject(jsonData);
		JSONArray jsonArray =obj.getJSONArray("userList");
		String json = page(jsonArray,pageInt,pageSizeInt);
		JSONObject jsonObject = JSONObject.parseObject(json);
		this.setJsonObj(jsonObject);
		
	}
	
	/**
	 * 账号管理--礼物查询
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月7日
	 */
	private void getGiftHandler() {
		HashMap<String, String> para = new HashMap<String, String>();
		
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		
		String startTime = this.getWebHelper().getString("startTime");
		para.put("startTime", startTime);
		
		String endTime = this.getWebHelper().getString("endTime");
		para.put("endTime", endTime);
		
		String page = "";
		int pageInt = this.getWebHelper().getInt("page");
		para.put("page", page.valueOf(pageInt));
		
		String pageSize = "";
		int pageSizeInt = this.getWebHelper().getInt("rows");
		para.put("pageSize", pageSize.valueOf(pageSizeInt));
		
		String channel = this.getWebHelper().getString("channel");
		para.put("channel", channel);
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		
		JSONObject obj = JSONObject.parseObject(jsonData);
		JSONArray jsonArray =obj.getJSONArray("giftDetailList");
		String json = page(jsonArray,pageInt,pageSizeInt);
		JSONObject jsonObject = JSONObject.parseObject(json);
		this.setJsonObj(jsonObject);
		
	}
	
	/**
	 * 账号管理--订单查询
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月9日
	 */
	private void getRechargeOrder(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		
		String startTime = this.getWebHelper().getString("startTime");
		para.put("startTime", startTime);
		
		String endTime = this.getWebHelper().getString("endTime");
		para.put("endTime", endTime);
		
		String channel = this.getWebHelper().getString("channel");
		para.put("channel", channel);
		
		String page = "";
		int pageInt = this.getWebHelper().getInt("page");
		para.put("page", page.valueOf(pageInt));
		
		String pageSize = "";
		int pageSizeInt = this.getWebHelper().getInt("rows");
		para.put("pageSize", pageSize.valueOf(pageSizeInt));
		
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
	
		JSONObject obj = JSONObject.parseObject(jsonData);
		JSONArray jsonArray =obj.getJSONArray("payList");
		String json = page(jsonArray,pageInt,pageSizeInt);
		JSONObject jsonObject = JSONObject.parseObject(json);
		this.setJsonObj(jsonObject);
	}
	
	/**
	 * 账号管理--加减币记录
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月11日
	 */
	private void getCoinHistory(){
		HashMap<String, String> para = new HashMap<String, String>();
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		
		String startTime = this.getWebHelper().getString("startTime");
		para.put("startTime", startTime);
		
		String endTime = this.getWebHelper().getString("endTime");
		para.put("endTime", endTime);
		
		String channel = this.getWebHelper().getString("channel");
		para.put("channel", channel);
		
		String page = "";
		int pageInt = this.getWebHelper().getInt("page");
		para.put("page", page.valueOf(pageInt));
		
		String pageSize = "";
		int pageSizeInt = this.getWebHelper().getInt("rows");
		para.put("pageSize", pageSize.valueOf(pageSizeInt));
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		
		JSONObject obj = JSONObject.parseObject(jsonData);
		JSONArray jsonArray =obj.getJSONArray("accountList");
		String json = page(jsonArray,pageInt,pageSizeInt);
		JSONObject jsonObject = JSONObject.parseObject(json);
		this.setJsonObj(jsonObject);
		
	}
	/**
	 * 账号管理--加减币
	 * @author wenhui.Wang
	 * @since 2016年5月11日
	 */
	private void addSubtractCoin(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		
		String relateUserId = this.getWebHelper().getString("relateUserId");
		para.put("relateUserId", relateUserId);
		//赠送价格
		String num = this.getWebHelper().getString("num");
		para.put("num", num);
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		this.setSuccess();
	}

}
