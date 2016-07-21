/*  ------------------------------------------------------------------------------ 
 *                  软件名称:股票池手机版
 *                  公司名称:呱呱财经
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2013年12月13日/2013
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自呱呱财经研发部，仅限呱呱视频内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ggcj.toolkit
 *                  fileName：BasicPayCallBack.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.activity.handler;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Yongchao.Yang
 * 
 */
public class DispatchResponse extends HttpServletResponseWrapper {
	private boolean isRedirect = false;
	private String dispathView;

	public DispatchResponse(HttpServletResponse response) {
		super(response);
	}

	public void setIsRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public boolean getIsRedirect() {
		return this.isRedirect;
	}

	public String getDispathView() {
		return dispathView;
	}

	public void setDispathView(String dispathView) {
		this.dispathView = dispathView;
	}

}
