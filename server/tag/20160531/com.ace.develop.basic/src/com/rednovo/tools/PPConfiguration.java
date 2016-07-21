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
 *                  prj-name：com.popo.user
 *                  fileName：com.power.tools.config.PropertiesConfig.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * @author Yongchao.Yang
 * 
 */
public class PPConfiguration {
	private static Map<String, Configuration> maps = new HashMap<String, Configuration>();
	private static Logger logger = Logger.getLogger(PPConfiguration.class);

	/**
	 * 获取Properties配置文件
	 * 
	 * @param fileName
	 * @return
	 * @author YongChao.Yang/2012-9-11/2012
	 */
	public static Configuration getProperties(String fileName) {
		if (maps.get(fileName) == null) {
			try {
				maps.put(fileName, new PropertiesConfiguration(fileName));
			} catch (ConfigurationException e) {
				logger.error("[初始化" + fileName + "文件失败]", e);
				return null;
			}
		}
		return maps.get(fileName);
	}

	/**
	 * 获取XML配置文件
	 * 
	 * @param fileName
	 * @return
	 * @author YongChao.Yang/2012-9-12/2012
	 */
	public static Configuration getXML(String fileName) {
		if (maps.get(fileName) == null) {
			try {
				maps.put(fileName, new XMLConfiguration(fileName));
			} catch (ConfigurationException e) {
				logger.error("[初始化" + fileName + "文件失败]", e);
				return null;
			}
		}
		return maps.get(fileName);
	}

	public static void test() {
		Configuration cf = PPConfiguration.getXML("dispatch.xml");
		List<Object> uris = cf.getList("request");
		List<Object> keyIds = cf.getList("request[@keyId]");
		List<Object> types = cf.getList("request[@responseType]");
		System.out.println("size::" + uris.size());
		for (int i = 0; i < uris.size(); i++) {
			System.out.println("uri:" + uris.get(i).toString());
			System.out.println("key:" + keyIds.get(i).toString());
			System.out.println("type:" + types.get(i).toString());
		}

	}

	public static void main(String[] args) {
		PPConfiguration.test();
	}
}
