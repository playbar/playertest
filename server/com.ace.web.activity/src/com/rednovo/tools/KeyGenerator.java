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
 *                  fileName：com.popo.util.KeyGenerator.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.tools;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * 键生成器
 * 
 * @author YongChao.Yang
 * 
 */
public class KeyGenerator {
	/**
	 * 格式化工具
	 */
	private static DecimalFormat decimalFormat = new DecimalFormat("000000");
	/**
	 * 随机数
	 */
	private static Random radm = new Random();

	/**
	 * 获取六位随机数，且不为0开头的数字
	 * 
	 * @return
	 * @author sg.z
	 * @since 2014年8月18日下午10:33:56
	 */
	public static String getSixRandom() {
		return "" + (int) ((Math.random() * 9 + 1) * 100000);
	}

	public static String getRandom() {
		return decimalFormat.format(radm.nextInt(999999));
	}

	/**
	 * 生成美播官方5位用户号码
	 * @return
	 * @author sg.z
	 * @since  2014年9月16日上午10:54:22
	 */
	public static String getMeiboNum() {
		Random random = new Random();
		int num = random.nextInt(10000) + 90000;
		return num + "";
	}

	/**
	 * 生成一个唯一的数据库主键,带有计数器
	 * 
	 * @return
	 */
	public static String createTransReferenceId(String transActionType) {
		StringBuffer pk = new StringBuffer(transActionType);
		pk.append("-");
		pk.append(DateUtil.getNo(6));
		return pk.toString();
	}

	/**
	 * 获取唯一主键值
	 * 
	 * @return
	 * @author YongChao.Yang/2012-9-20/2012
	 */
	public static String createUniqueId() {
		StringBuffer sb = new StringBuffer(DateUtil.getNo(6));
		sb.append(getRandom());
		return sb.toString();
	}
}
