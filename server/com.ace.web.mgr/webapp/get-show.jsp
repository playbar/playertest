
<%@page import="java.text.DateFormat"%>
<%@page import="com.rednovo.tools.DateUtil"%>
<%@page import="com.rednovo.ace.entity.User"%>
<%@page import="com.rednovo.ace.entity.LiveShow"%>
<%@page import="com.alibaba.fastjson.JSONArray"%>
<%@page import="com.alibaba.fastjson.JSON"%>
<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.rednovo.tools.web.HttpSender"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<html>
	<title>在线直播列表</title>
	<body>
	<a href="./index.jsp">返回</a>
	<a href="javascript:window.location.reload();">刷新</a>
	
	<br/><br/>
	<table style="width:90%" border="1">
	
	<tr><td width="5%" align="center">序号</td ><td width="10%" align="center">id</td ><td  width="15%" align="center">标题</td> <td width="15%" align="center">位置</td><td width="15%" align="center">播主</td><td width="15%" align="center">开播时间</td><td width="10%" align="center">下线</td></tr>

<%
	HashMap<String, String> params = new HashMap<String, String>();
	params.put("page", "1");
	params.put("pageSize", "1000");
	String res = HttpSender.httpClientRequest("http://api.17ace.cn/service/002-004", params);
	JSONObject jo = JSON.parseObject(res);
	if ("1".equals(jo.getString("exeStatus"))) {
		JSONArray showList = jo.getJSONArray("showList"), userList = jo.getJSONArray("userList");

		for (int i = 0; i < showList.size(); i++) {
			LiveShow s = showList.getObject(i, LiveShow.class);
			User u = userList.getObject(i, User.class);
			out.println("<tr><td>" + (i+1)+ "</td><td>" + s.getShowId() + "</td><td>" + s.getTitle() + "</td><td>" + s.getPosition() + "</td><td>" + u.getNickName() + "</td><td>" + DateUtil.format(Long.parseLong(s.getStartTime()), DateUtil.dateFormatYYYYMMDDHHMMSS) + "</td><td align='center'> <a href='./kick-man.jsp?showId="+s.getShowId()+"'>下线</a></td></tr>");
		}

	} else {
		out.println("<tr><td>获取数据失败</td></tr>");
	}
%>
</table></body></html>