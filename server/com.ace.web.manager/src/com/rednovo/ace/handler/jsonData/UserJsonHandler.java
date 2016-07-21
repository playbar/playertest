package com.rednovo.ace.handler.jsonData;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * @author wenhui.Wang/2016年5月18日
 */
public class UserJsonHandler extends BasicServiceAdapter{
	Logger logger = Logger.getLogger(OrderJsonHandler.class);

	@Override
	protected void service() {
		String key = this.getKey();
		if("001-210".equals(key)){			//-用户管理--用户管理
			this.getUserManage();
		} else if ("001-420".equals(key)){	//-用户管理--实名认证
			this.getCertify();
		} else if ("001-410".equals(key)){	//-用户管理--修改实名认证状态
			this.updateCertifyStatus();
		} else if("".equals(key)){
			
		} else if("".equals(key)){
			
		}
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
	
	/**
	 * 用户管理
	 */
	private void getUserManage(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String key = this.getWebHelper().getString("key");
		para.put("key", key);
		
		String page = "";
		int pageInt = this.getWebHelper().getInt("page");
		para.put("page", page.valueOf(pageInt));
		
		String pageSize = "";
		int pageSizeInt = this.getWebHelper().getInt("rows");
		para.put("pageSize", pageSize.valueOf(pageSizeInt));
		
		String status = this.getWebHelper().getString("status");
		para.put("status", status);
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		
		JSONObject obj = JSONObject.parseObject(jsonData);
		JSONArray jsonArray =obj.getJSONArray("userList");
		String json = page(jsonArray,pageInt,pageSizeInt);
		JSONObject jsonObject = JSONObject.parseObject(json);
		
		this.setJsonObj(jsonObject);
	}
	
	/**
	 * 实名认证
	 */
	private void getCertify(){
		HashMap<String, String> para = new HashMap<String, String>();
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		
		String page = "";
		int pageInt = this.getWebHelper().getInt("page");
		para.put("page", page.valueOf(pageInt));
		
		String pageSize = "";
		int pageSizeInt = this.getWebHelper().getInt("rows");
		para.put("pageSize", pageSize.valueOf(pageSizeInt));
		
		String status = this.getWebHelper().getString("status");
		para.put("status", status);
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		
		JSONObject obj = JSONObject.parseObject(jsonData);
		JSONArray jsonArray =obj.getJSONArray("certifyList");
		
		String json = page(jsonArray,pageInt,pageSizeInt);
		
		JSONObject jsonObject = JSONObject.parseObject(json);
		this.setJsonObj(jsonObject);
	}
	
	
	
	
	/**
	 * 用户管理--修改实名认证状态
	 */
	private void updateCertifyStatus(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String userId = this.getWebHelper().getString("userId");
		para.put("userId", userId);
		
		String status = this.getWebHelper().getString("status");
		para.put("status", status);
		
		String path = this.getWebHelper().getString("path");
		if(Validator.isEmpty(userId) || Validator.isEmpty(status)){
			this.setError("218");
			return;
		}
		String jsonData = getJsonData(path, para);
		JSONObject obj = JSONObject.parseObject(jsonData);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(obj.getString("exeStatus"))) {
			this.setSuccess();
		} else {
			this.setError(jsonData);
		}
	}

}
