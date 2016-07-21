<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<title>报名成功</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0">
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">
	<link rel="stylesheet" href="css/common.css"/>
	<script type="text/javascript" src="js/uploadImage.js" ></script>
	<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/jquery.json-2.4.js"></script>
	<style type="text/css">
		body{
			/*background-color:red;*/
		}
		form {
			width:290px;
			margin: 10px auto;
		}

		input {
			font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
			border:1px solid #ccc;
			font-size:20px;
			width:290px;
			min-height:40px;
			display:block;
			margin-bottom:25px;
			margin-top:5px;
			outline: none;

			-webkit-border-radius:5px;
			border-radius:5px;
		}
		.title{
			display: block;
			top: 1em;
			width: 100%;
			font-size: 24px;
			text-align: center;
			font-color:#ffffff
		}
		.background{
			background-color: #ffff00;
		}
		p{
			line-height: 25px;
			text-align: left;
		}
		.submitbtn{
			/*background-image: url(img/submit.png);*/

		}

		.bg1{
			width: 100%;

		}
		body{
			background-image: url(img/school_activity_img/bg.png);
			background-size: 100%;
		}
		.bg{
			background-image: url(img/school_activity_img/bg_top.png);
			background-size:100%;
			width: 100%;
			position: relative;
		}
		.banner{
			position: absolute;
			top: 0;
			max-width: 100%;
		}
		.form-style{
			background-color: #6fefae;
			margin-left: 1em;
			margin-right: 1em;
			border-radius: 0.5em;
			padding-top: 1em;
			padding-bottom: 0.2em;
		}
		.upload-img{
			width: 30%;
			height: 30%;
			margin:0 auto;
		}
		#file{ 
			position:absolute; 
			z-index:100; 
			margin-left:-180px; 
			font-size:60px;
			opacity:0;
			filter:alpha(opacity=0); 
			margin-top:-5px;
		}
	</style>
</head>
<body>

	<div class="bg">
		<img src="img/school_activity_img/banner.png">
	</div>

	<div class="form-style">
		<form>
<input style = "margin-top:40px;background-color:#ffff00;" class="submitbtn" id="button" name="button" type="button" value="报名成功,点击此处返回" /> 
		<br/>
			<%
				String showId = request.getParameter("user");
				out.println("<font color=red>" + showId + "操作成功!</font>");
			%>
			<p>特殊说明:<br/>本次大赛只针对在校学生，领取奖品时需要通过Ace直播平台官方审核学生身份。</p>
		</form>
			
				<br/>
				<br/>
	</div>
</body>
<script type="text/javascript">
$(function() {
		$("#button").click(function() {
			window.location.href="school_activity_home.html"; 	
		});

	});

</script>
</html>
