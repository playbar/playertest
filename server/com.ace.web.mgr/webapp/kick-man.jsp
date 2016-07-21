
<%@page import="com.rednovo.tools.Validator"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.rednovo.tools.web.HttpSender"%>
<%@page import="com.rednovo.ace.globalData.UserManager"%>
<%@page import="com.rednovo.ace.globalData.GlobalUserSessionMapping"%>
<%@page import="com.rednovo.ace.robot.communication.client.com.rednovo.ace.robot.communication.client.RobotSession"%>
<%@page import="com.rednovo.ace.launch.LogicOperator"%>
<%@page import="com.rednovo.ace.launch.ClientLauncher"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<html><head></head><body>

<a href="./index.jsp">返回</a>

</body></html>


<%
	String showId = request.getParameter("showId");
	if (Validator.isEmpty(showId)) {
		out.println("参数异常");
		return;
	}

	ClientLauncher cl = new ClientLauncher();
	cl.openSession();
	try {
		Thread.sleep(2000);
	} catch (Exception e) {
		// TODO: handle exception
	}
	LogicOperator.endShow(showId);
	try {
		Thread.sleep(2000);
	} catch (Exception e) {
		// TODO: handle exception
	}
	RobotSession.getInstance().stopHeartBeat();
	RobotSession.getInstance().closeSession();

	HashMap<String, String> params = new HashMap<String, String>();
	params.put("showId", showId);
	String res = HttpSender.httpClientRequest("http://api.17ace.cn/service/002-015", params);
	out.println("操作完成!");
%>