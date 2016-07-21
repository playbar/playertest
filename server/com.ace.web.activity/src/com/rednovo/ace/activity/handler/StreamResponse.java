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
 *                  fileName：com.power.handler.StreamResponse.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.activity.handler;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Yongchao.Yang
 * 
 */
public class StreamResponse extends HttpServletResponseWrapper {

	public StreamResponse(HttpServletResponse response) {
		super(response);
	}

}
