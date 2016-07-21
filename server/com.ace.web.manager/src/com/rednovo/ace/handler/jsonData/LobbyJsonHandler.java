package com.rednovo.ace.handler.jsonData;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * @author wenhui.Wang/2016年5月18日
 */
public class LobbyJsonHandler extends BasicServiceAdapter{
	Logger logger = Logger.getLogger(OrderJsonHandler.class);

	@Override
	protected void service() {
		String key = this.getKey();
		if("002-040".equals(key)){
			this.getLobbyShow();
		}
	}
	
	/**
	 * 直播监控--大厅直播
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月18日
	 */
	private void getLobbyShow(){
		HashMap<String, String> para = new HashMap<String, String>();
		String page = "";
		int pageInt = this.getWebHelper().getInt("page");
		para.put("page", page.valueOf(pageInt));
		String pageSize = "";
		int pageSizeInt = this.getWebHelper().getInt("rows");
		para.put("pageSize", pageSize.valueOf(pageSizeInt));
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		if (!Validator.isEmpty(jsonData)) {
			JSONObject jsonObject =  JSONObject.parseObject(jsonData);
			JSONArray userArray =jsonObject.getJSONArray("userList");
	        JSONArray showArray =jsonObject.getJSONArray("showList");
	        JSONObject print = new JSONObject();
	        JSONArray data = new JSONArray();
	        print.put("rows", data);
			for (int i = 0; i < userArray.size(); i++) {
				JSONObject showJSONObject = showArray.getJSONObject(i);
				JSONObject userJSONObject = userArray.getJSONObject(i);
			    JSONObject obj = new JSONObject();
				obj.put("sortCnt", showJSONObject.getString("sortCnt"));
				obj.put("title", showJSONObject.getString("title"));
				obj.put("position", showJSONObject.getString("position"));
				obj.put("createTime", showJSONObject.getString("createTime"));
				obj.put("length", showJSONObject.getString("length"));
				obj.put("supportCnt", showJSONObject.getString("supportCnt"));
				obj.put("coinCnt", showJSONObject.getString("coinCnt"));
				obj.put("shareCnt", showJSONObject.getString("shareCnt"));
				obj.put("userId", userJSONObject.getString("userId"));
				obj.put("nickName", userJSONObject.getString("nickName"));
				data.add(obj);
			}
			String json = print.toJSONString();
			StringBuffer sbf = new StringBuffer(json);
			sbf.replace(0, 1, "{\"total\":\"*\",");
			JSONObject jsonObj= JSONObject.parseObject(sbf.toString());
			this.setJsonObj(jsonObj);
		}else{
			this.setError(jsonData);
		}
	}

}
