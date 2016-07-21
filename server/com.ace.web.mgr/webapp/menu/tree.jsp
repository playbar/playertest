<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>	
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>	
<%@page import="org.apache.commons.configuration.Configuration"%>	
<%@page import="com.rednovo.tools.PPConfiguration"%>	
<%@page import="com.rednovo.tools.web.HttpSender"%>	
<%@page import="com.alibaba.fastjson.JSONObject"%>		
<%
	String userId =(String)request.getParameter("userId");
	HashMap<String, String> para = new HashMap<String, String>();
	para.put("userId", userId);
	String jsonData = HttpSender.httpClientRequest(PPConfiguration.getProperties("cfg.properties").getString("http.server.url") + "/001-036", para);
	JSONObject jsonObj = JSONObject.parseObject(jsonData);
	String jsonMenu = jsonObj.getString("menuList");
	jsonMenu = jsonMenu.replace("[", "");
	jsonMenu = jsonMenu.replace("]", "");
	jsonMenu = jsonMenu.replace("\"", "");
	String menus[] = jsonMenu.split(","); 
    List<String> menuList = new ArrayList<String>(); //创建一个集合
    for (int i = 0; i < menus.length; i++) {
    	menuList.add(menus[i]);
    }
	StringBuffer sbf = new StringBuffer();
	Configuration cf = PPConfiguration.getXML("tree.xml");
	List<Object> node = cf.getList("node");
	sbf.append("[");
	for (int i = 0; i < menuList.size(); i++) {
		if (menuList.get(i).length() != 3) {
			continue;
		}
		for (int n = 0; n < node.size(); n++) {
			String nodeText = cf.getString("node(" + n + ")[@id]");
			if (menuList.get(i).equals(nodeText)) {
				String text = cf.getString("node(" + n + ")[@text]");
				String url = cf.getString("node(" + n + ")[@url]");
				String id = cf.getString("node(" + n + ")[@id]");
				if (!menuList.contains(id)) {
					continue;
				}
				sbf.append("{");
				sbf.append("\"text\":\"" + text + "\",");
				sbf.append("\"url\":\"" + url + "\",");
				sbf.append("\"state\":\"closed\",");
				sbf.append("\"children\":[");
				List<Object> menu = cf.getList("node(" + n + ").menu");
				for (int m = 0; m < menu.size(); m++) {
					String menuText = cf.getString("node(" + n + ").menu(" + m + ")[@text]");
					String menuUrl = cf.getString("node(" + n + ").menu(" + m + ")[@url]");
					String menuId = cf.getString("node(" + n + ").menu(" + m + ")[@id]");
					if (!menuList.contains(menuId)) {
						if((m+1) == menu.size()){
							//sbf.append("{");
						}
						continue;
					}
					sbf.append("{");
					sbf.append("\"text\":\"" + menuText + "\",");
					sbf.append("\"url\":\"" + menuUrl + "\"");
					sbf.append("},");
				}
				sbf.setLength(sbf.length() - 1);
				sbf.append("]},");
			}
		}
	}
	sbf.setLength(sbf.length() - 1);
	if(menuList !=null && menuList.size() >0){
		sbf.append("]");
	}
	out.print(sbf.toString());
	//System.out.println(sbf.toString());
%>