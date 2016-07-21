/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月15日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.commucation.server
 *                  fileName：AndroidLogger.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author yongchao.Yang/2016年3月15日
 */
public class AndroidLogger {

	public static void printLog(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		System.out.println(sw.getBuffer().toString());
	}
}
