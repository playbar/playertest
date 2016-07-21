<%@ page contentType="text/html;charset=UTF-8"%>
   <%
   String userId = request.getParameter("userId");
   if(userId == null){
	   userId = "";
   }  %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>我的账单</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0,maximum-scale=1.0">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">
		<link rel="stylesheet" href="css/bill.css"/>
	</head>
	<script type="text/javascript" src="/js/jquery.js"></script>
	<script type="text/javascript" src="/js/jquery.json-2.4.js"></script>
	<script type="text/javascript" src="js/bill.js"></script>
	
	<body>
	<div class="nav">
		<div class="stage cur" id="stage0">
			<p>收礼记录</p>
			<div id="line0" class="select-line cur-select-line"></div>
		</div>
		<div class="stage" id="stage1">
			<p>消费记录</p>
			<div id="line1" class="select-line"></div>
		</div>
		<div class="stage" id="stage2">
			<p>充值记录</p>
			<div id="line2" class="select-line"></div>
		</div>
	</div>
	<div id="records">
		<!--  <iframe  src="receive.jsp?userId=<%=userId%>"></iframe>-->
	</div>
	</body>
</html>