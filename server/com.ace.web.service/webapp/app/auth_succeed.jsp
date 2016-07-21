<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>认证结果</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0,maximum-scale=1.0">
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">
	<!-- <link rel="stylesheet" href="/app/css/authentication.css"/>  -->
	<style type="text/css">
		.succ{
			position: absolute;
			width: 100%;
			text-align:center;
			top:30%;
		}
	</style>
</head>
<body style="background:#fff;">
	<div class="succ">
	<%
		String exeStatus =(String)request.getAttribute("exeStatus");
		if( exeStatus.equals("1") ){
			out.println("<img src=\"../app/image/succ.png\" width=\"50%\">");
		}else{
			out.println("<img src=\"../app/image/fail.png\" width=\"50%\">");
		}
	%>
	</div>
</body>
</html>