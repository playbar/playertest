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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 移动客户端请求控制器，用于统一接收请求并执行相应Service
 * 
 * @author changqing
 * 
 */
public class MobileRequestControler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MobileRequestControler.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			// 从请求的URI中获取接口编号
			req.setCharacterEncoding("UTF-8");
			String uri = req.getRequestURI();
			int index = uri.lastIndexOf("service") + 8;
			uri = uri.substring(index, uri.length());
			if (uri.endsWith("/")) {
				uri = uri.substring(0, uri.length() - 1);
			}
			logger.info("[监测到请求" + uri + "]");
			// 通过接口编号从配置文件中获取对应服务类名，构造服务类实例，执行服务方法
			String serviceName = ServiceDefine.getServiceName(uri);
			if (serviceName != null) {
				BasicServiceAdapter basicService = (BasicServiceAdapter) Class.forName(serviceName).newInstance();
				basicService.setKey(uri);
				basicService.setRequest(req);
				basicService.setResponse(res);
				basicService.handleRequest();

			}
			return;
		} catch (Exception e) {
			logger.error("[移动客户端请求控制器doPost错误]", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
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
	public void fireView(HttpServletRequest req, HttpServletResponse resp, String view, boolean idRedirect) throws Exception {
		if (view == null) {
			return;
		}
		if (idRedirect) {
			resp.sendRedirect(view);// 重定向
		} else {
			req.getRequestDispatcher(view).forward(req, resp);// 请求转发
		}
	}
}
