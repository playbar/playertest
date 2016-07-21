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
 *                  prj-name：com.popo.logic
 *                  fileName：com.popo.util.StringChecker.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证函数
 * 
 * @author YongChao.Yang
 * 
 */
public class Validator {
	/**
	 * 参数是否为空
	 * 
	 * @param param
	 * @param errCode
	 * @param errMsg
	 * @return
	 * @author Yongchao.Yang
	 * @since 2015年4月17日上午10:18:24
	 */
	public static boolean isEmpty(Object param) {
		if (param == null) {
			return true;
		}
		if (param instanceof String) {
			String newParam = (String) param;
			if ("".equals(newParam) || "null".equals(newParam)) {
				return true;
			}
		} else if (param instanceof Map<?, ?>) {
			Map<?, ?> newParam = (Map<?, ?>) param;
			if (newParam.size() == 0) {
				return true;
			}

		} else if (param instanceof Set<?>) {
			Set<?> newParam = (Set<?>) param;
			if (newParam.size() == 0) {
				return true;
			}

		} else if (param instanceof List<?>) {
			List<?> newParam = (List<?>) param;
			if (newParam.size() == 0) {
				return true;
			}
		} else if (param instanceof String[]) {
			String[] newParam = (String[]) param;
			if (newParam.length == 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkKeyWord(String txt, List<String> keys) {
		txt = txt.replaceAll("\\s*", "");

		for (int i = 0; i < keys.size(); i++) {
			Pattern pattern = Pattern.compile(keys.get(i));
			if (pattern.matcher(txt).find()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 替换敏感词
	 * 
	 * @param txt
	 * @param keys
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月26日下午10:39:37
	 */
	public static String replaceKeyWord(String txt, List<String> keys) {
		txt = txt.replaceAll("\\s*", "");
		long time = System.currentTimeMillis();
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			txt = txt.replaceAll(key, "[禁]");
		}
		System.out.println(System.currentTimeMillis() - time);
		return txt;
	}

	/**
	 * 判断是否为数字
	 * 
	 * @param string
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月17日下午9:22:27
	 */
	public static boolean isNumber(String string) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher m = pattern.matcher(string);
		return m.matches();
	}
	
	/**
	 * 判断是否为整数
	 * 
	 * @param string
	 * @return
	 * @author Yongchao.Yang
	 * @since 2016年3月17日下午9:22:27
	 */
	public static boolean isInt(String string){
		Pattern pattern = Pattern.compile("^-?\\d+$");
		Matcher m = pattern.matcher(string);
		return m.matches();
	}
	
	/**
	 * 判断是否为数字
	 * @param string
	 * @return
	 */
	public static boolean isFloat(String string){
		Pattern pattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
		Matcher m = pattern.matcher(string);
		return m.matches();
	}
	
	public static void main(String[] args) {
		ArrayList<String> list = new ArrayList<String>();
			list.add("胡锦涛");
			list.add("习近平");
			list.add("操");
			list.add("傻逼");
		//System.out.println(Validator.replaceKeyWord("胡锦涛", list));;
		// System.out.println(str1);
		// System.out.println();
	}
}
