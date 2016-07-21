package com.rednovo.tools;

import org.apache.commons.configuration.Configuration;

/**
 * 读取配置文件字段
 * @author lxg
 * @date 2016.5.10
 *
 */
public class PropertyUtils {

	private static Configuration getCfg(){
		return PPConfiguration.getProperties("cfg.properties");
	}
	
	/**
	 * 获取服务器路径
	 * @return
	 */
	public static String getServerImgUrl(){
		return getCfg().getString("img.server.root.url");
	}
	
	/**
	 * 获取礼物目录
	 * @return
	 */
	public static String getGiftDir(){
		return getCfg().getString("img.server.gift.dir.name");
	}
	
	/**
	 * 获取礼物目录
	 * @return
	 */
	public static String getAdDir(){
		return getCfg().getString("img.server.ad.dir.name");
	}
	
	/**
	 * 获取用户校验目录
	 * @return
	 */
	public static String getCercifyDir(){
		return getCfg().getString("img.server.cercify.name");
	}
	
	
	/**
	 * 获取访问路径地址
	 * @param dir 图片目录
	 * @param picName 图片名称
	 * @return
	 */
	public static String getVisitUrl(String dir, String picName, String sign, String suffix){
		final StringBuffer append = new StringBuffer(PropertyUtils.getServerImgUrl());
			append.append(dir);
			append.append("/");
			append.append(picName);
			append.append(sign);
			append.append(suffix);
		return append.toString();
	}
}
