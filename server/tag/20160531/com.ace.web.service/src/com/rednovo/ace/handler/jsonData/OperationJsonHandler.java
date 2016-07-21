package com.rednovo.ace.handler.jsonData;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * @author wenhui.Wang/2016年5月18日
 */
public class OperationJsonHandler extends BasicServiceAdapter{
	
	Logger logger = Logger.getLogger(OrderJsonHandler.class);
	private static String url = PPConfiguration.getProperties("cfg.properties").getString("server.dispatch.path");

	private String getJsonData(String path, HashMap<String, String> para){
		return HttpSender.httpClientGet(url + path, para, para);
	}

	@Override
	protected void service() {
		
		String key = this.getKey();
		
		if("009-070".equals(key)){
			this.getLobbyBanner();
		} else if ("009-050".equals(key)){//运营管理--修改banner状态
			this.updateLobbyBannerStatus();
		} else if ("009-060".equals(key)){//运营管理--添加,修改banner
			this.addUpdateLobbyBanner();
		} else if ("009-080".equals(key)){//运营管理--根据id查询banner
			this.selectLobbyBannerID();
		} else if ("001-350".equals(key)) {//运营管理--礼物管理
		 	this.getGiftManage();
		} else if ("001-320".equals(key)) {//运营管理--修改礼物状态
		 	this.updateGiftStatus();
		} else if ("001-330".equals(key)) {//运营管理--根据id查询礼物
		 	this.selectGiftID();
		} else if ("001-310".equals(key)) {//运营管理--添加,修改礼物
		 	this.addUpdateGift();
		}
	}
	
	/**
	 * 运营管理--大厅banner
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月18日
	 */
	private void getLobbyBanner() {
		HashMap<String, String> para = new HashMap<String, String>();
		
		String status = this.getWebHelper().getString("status");
		para.put("status", status);
		
		String page = "";
		int pageInt = this.getWebHelper().getInt("page");
		para.put("page", page.valueOf(pageInt));
		
		String pageSize = "";
		int pageSizeInt = this.getWebHelper().getInt("rows");
		para.put("pageSize", pageSize.valueOf(pageSizeInt));
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		
		StringBuffer sbf = new StringBuffer(jsonData);
		sbf.replace(2, 8, "rows");
		sbf.replace(0, 1, "{\"total\":\"*\",");
		JSONObject jsonObject = JSONObject.parseObject(sbf.toString());
		this.setJsonObj(jsonObject);
	}
	/**
	 * 运营管理--修改banner状态
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月18日
	 */
	private void updateLobbyBannerStatus(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String id = this.getWebHelper().getString("id");
		para.put("id", id);
		
		String status = this.getWebHelper().getString("status");
		para.put("status", status);
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		this.setSuccess();
		
	}
	
	/**
	 * 运营管理--添加,修改banner
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月18日
	 */
	private void addUpdateLobbyBanner(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String id = this.getWebHelper().getString("id");
		para.put("id", id);
		
		String title = this.getWebHelper().getString("title");
		para.put("title", title);
		
		String addres = this.getWebHelper().getString("addres");
		para.put("addres", addres);
		
		String visitType = this.getWebHelper().getString("visitType");
		para.put("visitType", visitType);
		
		String status = this.getWebHelper().getString("status");
		para.put("status", status);
		
		String imgUrl = this.getWebHelper().getString("imgUrl");
		para.put("imgUrl", imgUrl);	
		
		String path = this.getWebHelper().getString("path");
		
		if(Validator.isEmpty(addres) || Validator.isEmpty(status)  ||
				Validator.isEmpty(title) || Validator.isEmpty(visitType) ||
				Validator.isEmpty(imgUrl) || Validator.isEmpty(path)){
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
	
	/**
	 * 运营管理--根据id查询banner
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月18日
	 */
	private void selectLobbyBannerID(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String id = this.getWebHelper().getString("id");
		para.put("id", id);
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		JSONObject jsonObject = JSONObject.parseObject(jsonData.toString());
		
		this.setJsonObj(jsonObject);
	}
	
	/**
	 * 运营管理--礼物管理
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月18日
	 */
	private void getGiftManage() {
		HashMap<String, String> para = new HashMap<String, String>();
		
		String status = this.getWebHelper().getString("status");
		para.put("status", status);
		
		String page = "";
		int pageInt = this.getWebHelper().getInt("page");
		para.put("page", page.valueOf(pageInt));
		
		String pageSize = "";
		int pageSizeInt = this.getWebHelper().getInt("rows");
		para.put("pageSize", pageSize.valueOf(pageSizeInt));
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		
		StringBuffer sbf = new StringBuffer(jsonData);
		sbf.replace(2, 10, "rows");
		sbf.replace(0, 1, "{\"total\":\"*\",");
		JSONObject obj = JSONObject.parseObject(sbf.toString());
		this.setJsonObj(obj);
	}
	
	/**
	 * 运营管理--修改礼物状态
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月18日
	 */
	private void updateGiftStatus() {
		HashMap<String, String> para = new HashMap<String, String>();
		
		String id = this.getWebHelper().getString("id");
		para.put("id", id);
		
		String status = this.getWebHelper().getString("status");
		para.put("status", status);
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		this.setSuccess();
	}
	
	/**
	 * 运营管理--根据id查询礼物
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月18日
	 */
	private void selectGiftID(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String id = this.getWebHelper().getString("id");
		para.put("id", id);
		
		String path = this.getWebHelper().getString("path");
		String jsonData = getJsonData(path, para);
		JSONObject jsonObject = JSONObject.parseObject(jsonData.toString());
		
		this.setJsonObj(jsonObject);
		
	}
	
	/**
	 * 运营管理--添加,修改礼物
	 * 
	 * @author wenhui.Wang
	 * @since 2016年5月18日
	 */
	private void addUpdateGift(){
		HashMap<String, String> para = new HashMap<String, String>();
		
		String id = this.getWebHelper().getString("id");
		para.put("id", id);
		
		String name = this.getWebHelper().getString("name");
		para.put("name", name);
		//赠送价格
		String sendPrice = this.getWebHelper().getString("sendPrice");
		para.put("sendPrice", sendPrice);
		//转换价格
		String transformPrice = this.getWebHelper().getString("transformPrice");
		para.put("transformPrice", transformPrice);
		//是否组合礼物(预留)
		String isCombined = this.getWebHelper().getString("isCombined");
		para.put("isCombined", isCombined);
		//礼物状态 0 下架 1上架 2待处理
		String status = this.getWebHelper().getString("status");
		para.put("status", status);
		//礼物类型 0 普通礼物  1 超级礼物
		String sortId = this.getWebHelper().getString("sortId");
		para.put("sortId", sortId);
		//排序id
		String type = this.getWebHelper().getString("type");
		para.put("type", type);
		//礼物图片
		String pic = this.getWebHelper().getString("pic");
		para.put("pic", pic);
		
		String path = this.getWebHelper().getString("path");
		if(!Validator.isFloat(sendPrice)  || !Validator.isFloat(transformPrice) |
				Validator.isEmpty(status) ||  Validator.isEmpty(sortId)		   || 
				Validator.isEmpty(name)   ||  Validator.isEmpty(pic)           || 
				Validator.isEmpty(path)   ||  Validator.isEmpty(type)){
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
