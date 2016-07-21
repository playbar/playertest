/*  ------------------------------------------------------------------------------ 
 * 
 *    				软件名称:泡泡娱乐交友平台(手机版)
 *    				公司名称:北京双迪信息技术有限公司
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2012-8-16/2012
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容仅限于北京双迪信息技术有限公司内部使用 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.popo.distribute
 *                  fileName：com.popo.distribute.commuite.client.ConvertTools.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.tools;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;

/**
 * JSON文本转换工具.主要提供将文本转换为对象的功能
 * 
 * @author Yongchao.Yang
 * 
 */
public class JsonConverter {
	private static String JSON_EXE_STATUS = "exeStatus";
	private static String JSON_EXE_ERROE_CODE = "errCode";
	private static String JSON_EXE_ERROR_MSG  = "errMsg";
	private static Logger logger = Logger.getLogger(JsonConverter.class);

	/**
	 * 获取执行结果的错误代码
	 * 
	 * @param json
	 * @return
	 * @author YongChao.Yang/2012-9-16/2012
	 */
	public static String getErrorCode(String json) {
		JSONObject jo = JSON.parseObject(json);
		if (Constant.OperaterStatus.FAILED.getValue().equals(JsonConverter.JSON_EXE_STATUS)) {
			return jo.getString("errCode");
		}
		return "";
	}

	/**
	 * 获取json执行结果值
	 * 
	 * @param json
	 * @return
	 * @author YongChao.Yang/2012-9-16/2012
	 */
	public static int getExeStatus(String json) {
		JSONObject jo = JSON.parseObject(json);
		return jo.getIntValue(JsonConverter.JSON_EXE_STATUS);
	}

	/**
	 * 设置执行结果为正确
	 * 
	 * @param jsonObj
	 * @return
	 * @author YongChao.Yang/2012-9-16/2012
	 */
	public static JSONObject setSuccess(JSONObject jsonObj) {
		jsonObj.put(JsonConverter.JSON_EXE_STATUS, Constant.OperaterStatus.SUCESSED.getValue());
		return (JSONObject) jsonObj;

	}

	/**
	 * 设置执行结果为错误
	 * 
	 * @param jsonObj
	 * @return
	 * @author YongChao.Yang/2012-9-16/2012
	 */
	public static JSONObject setFailed(JSONObject jsonObj, String errCode) {
		jsonObj.put(JsonConverter.JSON_EXE_STATUS, Constant.OperaterStatus.FAILED.getValue());
		jsonObj.put(JSON_EXE_ERROE_CODE, errCode);
		return jsonObj;
	}

	/**
	 * 设置执行结果为错误
	 * 
	 * @param jsonObj
	 * @return
	 * @author lxg
	 */
	public static JSONObject setFailed(JSONObject jsonObj, String errCode, String errMsg){
		jsonObj.put(JsonConverter.JSON_EXE_STATUS, Constant.OperaterStatus.FAILED.getValue());
		jsonObj.put(JSON_EXE_ERROE_CODE, errCode);
		jsonObj.put(JSON_EXE_ERROR_MSG, errMsg);
		return jsonObj;
	}
	
	/**
	 * 向JSON对象中注入键值对
	 * 
	 * @param jsonObj
	 * @param key
	 * @param value
	 * @return
	 * @author YongChao.Yang/2012-9-16/2012
	 */
	public static JSONObject setValue(JSONObject jsonObj, String key, Object value) {
		if (!Validator.isEmpty(key)) {
			value = value == null ? "" : value;
			jsonObj.put(key, value);
		} else {
			logger.error("[json设置键值对异常,KEY值为空]");
		}
		return jsonObj;
	}
}
