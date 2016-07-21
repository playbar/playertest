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
 *                  prj-name：com.popo.toolkit
 *                  fileName：com.power.tools.web.ParameterHelper.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.tools.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rednovo.tools.Validator;

/**
 * 用户请求助手.如果request为复合数据类型,则自动对request进行包装
 * 
 * @author Yongchao.Yang
 * 
 */
public class WebHelper {
	private static String classPath = WebHelper.class.getResource("/").getPath();
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HashMap<String, FileItem> multiReqKeyValues = null;

	private static Logger logger = Logger.getLogger(WebHelper.class);

	public WebHelper(HttpServletRequest req, HttpServletResponse res) {
		this.response = res;
		this.request = req;
		try {
			this.request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String content_type = this.request.getContentType();
		if (content_type != null && content_type.contains("multipart/form-data")) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024 * 1024);
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setFileSizeMax(1024 * 1024 * 50);
			upload.setHeaderEncoding("UTF-8");
			List<?> list = null;
			try {
				list = upload.parseRequest(request);
			} catch (FileUploadException e) {
				logger.error("[包装机与附件类型request出错]", e);
			}
			multiReqKeyValues = new HashMap<String, FileItem>();
			for (int i = 0; i < list.size(); i++) {
				FileItem fileItem = (FileItem) list.get(i);
				multiReqKeyValues.put(fileItem.getFieldName(), fileItem);
			}
		}
	}

	/**
	 * 获取String类型值
	 * 
	 * @param paraName
	 * @return
	 * @author YongChao.Yang/2012-9-11/2012
	 */
	public String getString(String paraName) {
		if (multiReqKeyValues != null) {
			try {
				FileItem fi = multiReqKeyValues.get(paraName);
				if (fi == null) {
					return "";
				} else {
					return fi.isFormField() ? fi.getString("UTF-8") : fi.getName();
				}
			} catch (Exception e) {
				logger.error("[获取String类型值错误]", e);
			}
		}
		return Validator.isEmpty(request.getParameter(paraName)) ? "" : request.getParameter(paraName);
	}

	/**
	 * 获取String类型值
	 * 
	 * @param paraName
	 * @param defaultVal
	 * @return
	 * @author YongChao.Yang/2012-12-12/2012
	 */
	public String getString(String paraName, String defaultVal) {
		String value = this.getString(paraName);
		if (StringUtils.isBlank(value)) {
			return defaultVal;
		} else {
			return value;
		}
	}

	/**
	 * 获取int类型参数值
	 * 
	 * @param paraName
	 * @return
	 * @author YongChao.Yang/2012-9-11/2012
	 */
	public int getInt(String paraName) {
		if (Validator.isEmpty(this.getString(paraName))) {
			return 0;
		}
		return Integer.parseInt(this.getString(paraName));
	}

	/**
	 * 获取int类型参数值
	 * 
	 * @param paraName
	 * @param defaultVal
	 * @return
	 * @author YongChao.Yang/2012-12-12/2012
	 */
	public int getInt(String paraName, int defaultVal) {
		int value = this.getInt(paraName);
		if (value == 0) {
			return defaultVal;
		} else {
			return value;
		}
	}

	/**
	 * 获取long类型参数值
	 * 
	 * @param paraName
	 * @return
	 * @author YongChao.Yang/2012-9-11/2012
	 */
	public long getLong(String paraName) {
		if (Validator.isEmpty(this.getString(paraName))) {
			return 0;
		}
		return Long.parseLong(this.getString(paraName));
	}

	/**
	 * 获取long类型参数值
	 * 
	 * @param paraName
	 * @param defaultVal
	 * @return
	 * @author YongChao.Yang/2012-12-12/2012
	 */
	public long getLong(String paraName, long defaultVal) {
		long value = this.getLong(paraName);
		if (value == 0) {
			return defaultVal;
		} else {
			return value;
		}
	}

	/**
	 * 获取double类型参数值
	 * 
	 * @param paraName
	 * @return
	 * @author YongChao.Yang/2012-9-11/2012
	 */
	public double getDouble(String paraName) {
		if (Validator.isEmpty(this.getString(paraName))) {
			return 0;
		}
		return Double.parseDouble(this.getString(paraName));
	}

	/**
	 * 获取double类型参数值
	 * 
	 * @param paraName
	 * @param defaultVal
	 * @return
	 * @author YongChao.Yang/2012-12-12/2012
	 */
	public double getDouble(String paraName, long defaultVal) {
		double value = this.getDouble(paraName);
		if (value == 0) {
			return defaultVal;
		} else {
			return value;
		}
	}

	/**
	 * 
	 * @param paraName
	 * @return
	 * @author YongChao.Yang/2012-9-20/2012
	 */
	public byte[] getBytes(String paraName) {
		if (multiReqKeyValues != null && multiReqKeyValues.get(paraName) != null) {
			return multiReqKeyValues.get(paraName).get();
		}
		return null;
	}

	public HttpServletRequest getRequest() {
		return this.request;
	}

	public HttpServletResponse getResponse() {
		return this.response;
	}

	public String getURI() {
		String uri = this.request.getRequestURI();
		int index = uri.lastIndexOf("service") + 8;
		uri = uri.substring(index, uri.length());
		if (uri.endsWith("/")) {
			uri = uri.substring(0, uri.length() - 1);
		}
		return uri;
	}

	/**
	 * 获取当前WEB应用的根绝对路径，返回形式如:D:/webapp/appname/
	 * 
	 * @return
	 */
	public static String getWebRootPath() {
		StringBuffer sb = new StringBuffer(classPath);
		int startIndex = sb.indexOf("WEB-INF");
		sb.delete(startIndex, sb.length());
		return sb.toString();
	}

	/**
	 * 获取当前WEB的classes绝对路径,返回形式如:D:/root/WEB-INF/classes/
	 * 
	 * @return
	 */
	public static String getWebClassesPath() {
		return WebHelper.classPath;
	}

}
