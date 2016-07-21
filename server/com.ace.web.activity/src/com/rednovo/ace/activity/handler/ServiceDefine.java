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
 *                  prj-name：com.power.handler
 *                  fileName：com.power.handler.ServiceDefine.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.activity.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.rednovo.tools.PPConfiguration;

/**
 * @author Yongchao.Yang
 * 
 */
public class ServiceDefine {
	private static Map<String, Service> map = new HashMap<String, ServiceDefine.Service>();
	static {
		Configuration cf = PPConfiguration.getXML("dispatch.xml");
		List<Object> keyIds = cf.getList("request[@keyId]");
		List<Object> types = cf.getList("request[@responseType]");
		List<Object> isRedirects = cf.getList("request[@isRedirect]");
		List<Object> urls = cf.getList("request[@url]");
		List<Object> services = cf.getList("request");
		for (int i = 0; i < keyIds.size(); i++) {
			String key = keyIds.get(i).toString();
			ServiceDefine.Service tt = new ServiceDefine().new Service();
			tt.setServiceName(services.get(i).toString());
			tt.setKey(key);
			tt.setIsDirect(isRedirects.get(i) == null || "0".equals(isRedirects.get(i)) ? false : true);
			tt.setUrl(urls.get(i) == null ? "" : urls.get(i).toString());
			map.put(key, tt);
		}
	}

	public static String getServiceName(String key) {
		Service ss = map.get(key);
		if (ss == null) {
			return "";
		}
		return ss.getServiceName();
	}

	public static String getUrl(String key) {
		Service ss = map.get(key);
		if (ss == null) {
			return "";
		}
		return ss.getUrl();
	}

	public static boolean getIsDriect(String key) {
		Service ss = map.get(key);
		if (ss == null) {
			return false;
		}
		return ss.getIsDirect();
	}

	private class Service {
		public Service() {}

		private String key;
		private String serviceName;
		private String url;
		private boolean isDirect;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public boolean getIsDirect() {
			return isDirect;
		}

		public void setIsDirect(boolean isDirect) {
			this.isDirect = isDirect;
		}

	}
}
