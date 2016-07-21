
<%@page import="com.rednovo.ace.globalData.UserManager"%>
<%@page import="com.rednovo.tools.web.HttpSender"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.rednovo.ace.robot.communication.client.com.rednovo.ace.robot.communication.client.RobotSession"%>
<%@page import="com.rednovo.ace.launch.LogicOperator"%>
<%@page import="com.rednovo.ace.launch.ClientLauncher"%>
<%@page import="com.rednovo.tools.Validator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<html>
<head></head>
<body>



<a href="./index.jsp">返回</a>


<br/>
<br/>

	<form action="./freeze-user.jsp" method="post">
		用户<input type="text" name="userId"> 所在房间<input type="text"
			name="showId"> <input type="submit" value="踢出并拉黑">
	</form>
	<br/>
(房间号选填，填入则同时将该用户踢出房间)
</body>
</html>


<%
	String showId = request.getParameter("showId");
	String userId = request.getParameter("userId");
	if (Validator.isEmpty(userId)) {
		return;
	}

	UserManager.freezeUser(userId);
	
	out.println("<font color=red>操作成功!</font>");

	if (Validator.isEmpty(showId)) {
		return;
	}

	ClientLauncher cl = new ClientLauncher();
	cl.openSession();
	try {
		Thread.sleep(2000);
	} catch (Exception e) {
		// TODO: handle exception
	}
	LogicOperator.kickMan(userId, showId);
	try {
		Thread.sleep(2000);
	} catch (Exception e) {
		// TODO: handle exception
	}
	RobotSession.getInstance().stopHeartBeat();
	RobotSession.getInstance().closeSession();
%>

