/*  ------------------------------------------------------------------------------ 
 * 
 *    				软件名称:泡泡娱乐交友平台(手机版)
 *    				公司名称:北京双迪信息技术有限公司
 *                  开发作者:changqing
 *       			开发时间:2012-9-7/2012
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容仅限于北京双迪信息技术有限公司内部使用 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.power.handler
 *                  fileName：com.power.handler.BasicServiceAdapter.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.handler;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.rednovo.ace.constant.Constant;
import com.rednovo.tools.JsonConverter;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;
import com.rednovo.tools.web.WebHelper;

/**
 * 基础服务适配器，所有Service类需继承该类，用于子类实现具体服务方法。
 * 
 * @author changqing
 * 
 */
public abstract class BasicServiceAdapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private WebHelper helper;
	private String requestKey;
	private String deviceId;
	private String ver;
	private String userId;
	private JSONObject jsonObj = new JSONObject();
	private String htmlResponse;
	private byte[] resBytes;

	public BasicServiceAdapter() {}

	protected Logger getLog() {
		return this.logger;
	}

	public WebHelper getWebHelper() {
		return this.helper;
	}

	/**
	 * 服务抽象方法，由子类去实现
	 * 
	 * @param req
	 * @param resp
	 * @param view
	 * @author changqing/2012-9-7/2012
	 */
	protected abstract void service();

	/**
	 * 
	 * 
	 * @author YongChao.Yang/2012-9-11/2012
	 */
	void handleRequest() {
		this.helper = new WebHelper(request, response);
		wrappedResponse();
		// 屏蔽注册流程验证
		if (this.auth() .equals(Constant.OperaterStatus.SUCESSED.getValue())) {
			this.service();
		}
		this.doResponse();
	}

	/**
	 * 包装Responset类型
	 * 
	 * @author YongChao.Yang/2012-9-12/2012
	 */
	private void wrappedResponse() {
		Configuration cf = PPConfiguration.getXML("dispatch.xml");
		List<Object> keyIds = cf.getList("request[@keyId]");
		List<Object> types = cf.getList("request[@responseType]");
		List<Object> isRedirects = cf.getList("request[@isRedirect]");
		List<Object> urls = cf.getList("request[@url]");
		String uri = this.getWebHelper().getURI();
		for (int i = 0; i < keyIds.size(); i++) {
			if (keyIds.get(i).toString().equals(uri)) {
				if ("2".equals(types.get(i).toString())) {
					// 流请求
					StreamResponse res = new StreamResponse(response);
					this.response = res;
				} else if ("3".equals(types.get(i).toString())) {
					// 中转请求
					DispatchResponse res = new DispatchResponse(response);
					this.response = res;
					res.setDispathView(urls.get(i).toString());
					res.setIsRedirect("1".equals(isRedirects.get(i).toString()) ? true : false);
				} else if ("4".equals(types.get(i).toString())) {
					HtmlResponse res = new HtmlResponse(response);
					this.response = res;
				} else if ("5".equals(types.get(i).toString())) {
					XmlResponse res = new XmlResponse(response);
					this.response = res;
				}
				break;
			}
		}
	}

	private void doResponse() {
		if (this.response instanceof StreamResponse) {
			writeImageToClient();
			return;
		} else if (this.response instanceof DispatchResponse) {
			try {
				fireView(((DispatchResponse) (this.response)).getDispathView(), ((DispatchResponse) (this.response)).getIsRedirect());
			} catch (Exception e) {
				this.getLog().error("\t\t[请求" + this.getWebHelper().getURI() + "失败]", e);
			}
		} else if (this.response instanceof HtmlResponse) {
			writeHtmlToClient();
		} else if (this.response instanceof XmlResponse) {
			writeXMLToClient();
		} else {
			writeJsonToClient();
		}

	}

	/**
	 * 向JSON对象中写入成功内容
	 * 
	 * @param jsonObj
	 * @throws Exception
	 * @author changqing/2012-9-8/2012
	 */
	protected void setSuccess() {
		JsonConverter.setSuccess(jsonObj);
	}

	/**
	 * 向JSON对象中写入失败内容
	 * 
	 * @param jsonObj
	 * @throws Exception
	 * @author changqing/2012-9-8/2012
	 */
	protected void setError(String errCode) {
		JsonConverter.setFailed(jsonObj, errCode);
	}
	
	/**
	 * 向JSON对象中写入失败内容
	 * 
	 * @param jsonObj
	 * @throws Exception
	 * @author lxg
	 */
	protected void setError(String errCode, String errMsg) {
		JsonConverter.setFailed(jsonObj, errCode, errMsg);
	}

	/**
	 * 向JSON对象中写入内容
	 * 
	 * @param jsonObj
	 * @param name
	 * @param value
	 * @throws Exception
	 * @author changqing/2012-9-8/2012
	 */
	protected void setValue(String key, Object value) {
		JsonConverter.setValue(jsonObj, key, value);
	}

	/**
	 * 回写流对象到客户端
	 * 
	 * @param bytes
	 * @author YongChao.Yang/2012-9-12/2012
	 */
	protected void wrteBytes(byte[] bytes) {
		this.resBytes = bytes;
	}

	protected void writeHtml(String html) {
		this.htmlResponse = html;
	}

	/**
	 * 往客户端写入内容
	 * 
	 * @param data
	 * @author changqing/2012-9-7/2012
	 */
	private void writeJsonToClient() {
		this.response.setContentType("text/html;charset=UTF-8");
		String json = null;
		try {
			PrintWriter writer = this.response.getWriter();
			json = JSON.toJSONString(this.jsonObj, SerializerFeature.DisableCircularReferenceDetect);
			writer.write(json);// 来禁止循环引用检测
			writer.flush();
			writer.close();
		} catch (Exception e) {
			this.getLog().error("\t\t[客户端写入内容(json[" + json + "])失败]", e);
		}
	}

	private void writeHtmlToClient() {
		this.response.setContentType("text/html;charset=UTF-8");
		try {
			PrintWriter writer = this.response.getWriter();
			writer.write(this.htmlResponse);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			this.getLog().error("\t\t[客户端写入内容(html)失败]", e);
		}
	}

	private void writeXMLToClient() {
		this.response.setContentType("text/xml;charset=UTF-8");
		try {
			PrintWriter writer = this.response.getWriter();
			writer.write(this.htmlResponse);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			this.getLog().error("\t\t[客户端写入内容(xml)失败]", e);
		}
	}

	/**
	 * 王客户端写入图片流
	 * 
	 * @param bytes
	 * @author YongChao.Yang/2012-9-12/2012
	 */
	private void writeImageToClient() {
		this.response.setContentType("application/octet-stream");
		try {
			OutputStream os = this.response.getOutputStream();
			os.write(resBytes);
			os.flush();
			os.close();
		} catch (Exception e) {
			this.getLog().error("\t\t[客户端写入流内容失败]", e);
		}
	}

	/**
	 * 内部跳转
	 * 
	 * @param req
	 * @param resp
	 * @param view
	 * @param idRedirect
	 * @throws Exception
	 * @author changqing/2012-9-8/2012
	 */
	private void fireView(String view, boolean idRedirect) throws Exception {
		if (view == null) {
			return;
		}
		if (idRedirect) {
			this.response.sendRedirect(view);// 重定向
		} else {
			this.request.getRequestDispatcher(view).forward(request, response);// 请求转发
		}
	}

	void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	void setKey(String key) {
		this.requestKey = key;
	}

	void setVer(String ver) {
		this.ver = ver;
	}

	void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 获取请求key
	 * 
	 * @return
	 * @author YongChao.Yang/2013-1-2/2013
	 */
	public String getKey() {
		return this.requestKey;
	}

	/**
	 * 获取系统版本号
	 * 
	 * @return
	 * @author YongChao.Yang/2013-1-2/2013
	 */
	public String getVer() {
		return this.ver;
	}

	/**
	 * 获取请求用户ID
	 * 
	 * @return
	 * @author YongChao.Yang/2013-1-2/2013
	 */
	public String getUserId() {
		this.userId = this.getWebHelper().getString("userId");
		if (StringUtils.isNotBlank(this.userId)) {
			return this.userId;
		}
		return null;
	}

	/**
	 * 获取请求设备号
	 * 
	 * @return
	 * @author YongChao.Yang/2013-1-2/2013
	 */
	public String getDeviceId() {
		if (!Validator.isEmpty(this.deviceId)) {
			return this.deviceId;
		} else if (!Validator.isEmpty(this.getWebHelper().getString("deviceId"))) {
			return this.getWebHelper().getString("deviceId");
		} else {
			return "00-21-CC-4A-3F-39";
		}
	}

	/**
	 * 请求合法性验证
	 * 
	 * @return
	 * @author YongChao.Yang/2013-1-1/2013
	 */
	String auth() {
		if (this.getKey().equals("001-004")) {
			return Constant.OperaterStatus.SUCESSED.getValue();
		}

		String userId = this.getWebHelper().getString("header_userId");
		String deviceId = this.getWebHelper().getString("deviceID");
		String version = this.getWebHelper().getString("header_version");

		/**
		 * // System.out.println("header_basicData：" + // this.getWebHelper().getString("header_basicData")); // 老版本，跳过验证 if (StringChecker.isNull(version)) { return OperateStatus.OPERATE_STATUS_SUCESSED; } String basicData = ""; try { basicData = SecurityUtil.decrypt(this.getWebHelper().getString(
		 * "header_basicData")); } catch (Exception e) { SystemLogService.addLog(LogType.SYS_LOG_TYPE_ILLEGAL, LogType.SYS_LOG_ITEM_TYPE_ILLEGAL_REQUEST, StringChecker.isNull(userId) ? 0 : Long.valueOf(userId), 0, null, "[非法访问]用户" + userId + "请求头数据解密失败,系统拦截", "[非法访问]用户" + userId + "请求头数据解密失败,系统拦截");
		 * this.getLog().error("[非法访问]:包头解密失败", e); this.setError("602"); return OperateStatus.OPERATE_STATUS_FAILED; } // clientAuthId~Password~deviceId~version~curDate~secretKey String[] fields = basicData.split("\\~"); // String version = fields[3]; String curDate = fields[4];
		 * 
		 * if (StringChecker.isNull(userId) || StringChecker.isNull(deviceId) || StringChecker.isNull(version) || StringChecker.isNull(curDate)) { SystemLogService.addLog(LogType.SYS_LOG_TYPE_ILLEGAL, LogType.SYS_LOG_ITEM_TYPE_ILLEGAL_REQUEST, StringChecker.isNull(userId) ? 0 : Long.valueOf(userId),
		 * 0, null, "[非法访问]用户" + userId + "缺失请求头数据,系统拦截", "[非法访问]用户" + userId + "缺失请求头数据,系统拦截"); this.getLog().error("[非法访问]:包头部分数据缺失", null); this.setError("602"); return OperateStatus.OPERATE_STATUS_FAILED; } // 验证用户和设备 if (!fields[0].equals(userId) || !fields[2].equals(deviceId) ||
		 * !fields[3].equals(version)) { SystemLogService.addLog(LogType.SYS_LOG_TYPE_ILLEGAL, LogType.SYS_LOG_ITEM_TYPE_ILLEGAL_REQUEST, StringChecker.isNull(userId) ? 0 : Long.valueOf(userId), 0, null, "[非法访问]用户" + userId + "请求头数据用户设备号匹配失败,系统拦截", "[非法访问]用户" + userId + "请求头数据用、设备、版本号匹配失败,系统拦截");
		 * this.getLog().error("[非法访问]:包头用户ID或设备号验证失败", null); this.setError("602"); return OperateStatus.OPERATE_STATUS_FAILED; } // 验证密码 User user = UserCacheAdapter.getInstance().getUser(Long.valueOf(userId)); if (user == null || !user.getPassword().equals(fields[1])) {
		 * SystemLogService.addLog(LogType.SYS_LOG_TYPE_ILLEGAL, LogType.SYS_LOG_ITEM_TYPE_ILLEGAL_REQUEST, StringChecker.isNull(userId) ? 0 : Long.valueOf(userId), 0, null, "[非法访问]用户" + userId + "请求头数据中用户密码验证失败,拦截处理", "[非法访问]用户" + userId + "请求头数据中用户密码验证失败,拦截处理");
		 * this.getLog().error("[非法访问]:包头密码验证失败", null); this.setError("203"); return OperateStatus.OPERATE_STATUS_FAILED; } if(Constant.USER_STATUS_FREEZE.equals(user.getStatus())){ SystemLogService.addLog(LogType.SYS_LOG_TYPE_ILLEGAL, LogType.SYS_LOG_ITEM_TYPE_ILLEGAL_REQUEST,
		 * StringChecker.isNull(userId) ? 0 : Long.valueOf(userId), 0, null, "[非法访问]用户" + userId + "请求用户已经冻结,拦截处理", "[非法访问]用户" + userId + "请求用户已经冻结,拦截处理"); this.setError("208"); return OperateStatus.OPERATE_STATUS_FAILED; } // 验证版本
		 * 
		 * // 验证有效时间 if (Math.abs(DateUtil.getMinutes(DateUtil.getStringDate(), curDate)) > 10) { SystemLogService.addLog(LogType.SYS_LOG_TYPE_ILLEGAL, LogType.SYS_LOG_ITEM_TYPE_ILLEGAL_REQUEST, StringChecker.isNull(userId) ? 0 : Long.valueOf(userId), 0, null, "[非法访问]用户" + userId + "请求超时,拦截处理",
		 * "[非法访问]用户" + userId + "请求超时,拦截处理"); this.getLog().error("[非法访问]:请求超时", null); this.setError("602"); return OperateStatus.OPERATE_STATUS_FAILED; }
		 **/

		this.setDeviceId(deviceId);
		this.setUserId(userId);
		this.setVer(version);
		return Constant.OperaterStatus.SUCESSED.getValue();
	}

	/**
	 * 重置响应对象
	 * @param jsonObj
	 */
	public void setJsonObj(JSONObject jsonObj){
		this.jsonObj = jsonObj;
	}
}
