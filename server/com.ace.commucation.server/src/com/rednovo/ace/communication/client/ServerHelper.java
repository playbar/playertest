/**
 * com.swordfish.net.SpiderPaw.java@com.tax.splider
 */
package com.rednovo.ace.communication.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ServerHelper {

	public static JSONObject getServer() throws Exception {
//		// //URL url = new URL("http://service.17meibo.com/web/get-server-list.jsp");
//		URL url = new URL("http://service.17meibo.com/get-server-list.jsp");
//		// URL url = new URL("http://172.16.150.21:8080/get-server-list.jsp");
//		// //URL url = new URL("http://101.251.251.92:8080/get-server-list.jsp");
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");// 增加报头，模拟浏览器，防止屏蔽
//		conn.setRequestProperty("Accept", "text/html");// 只接受text/html类型，当然也可以接受图片,pdf,*/*任意，就是tomcat/conf/web里面定义那些
//
//		conn.setConnectTimeout(10000);
//		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//			throw new Exception("获取服务器列表网络异常");
//		}
//		InputStream input = conn.getInputStream();
//		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
//		String line = null;
		StringBuffer sb = new StringBuffer();
//		while ((line = reader.readLine()) != null) {
//			sb.append(line).append("\r\n");
//		}
//		if (reader != null) {
//			reader.close();
//		}
//		if (conn != null) {
//			conn.disconnect();
//		}

		JSONObject jo = JSON.parseObject(sb.toString());
		//
		if (sb.length() == 0) {// 默认服务器
			jo = new JSONObject();
			// jo.put("ip", "114.112.189.244");
//			jo.put("ip", "114.112.189.236");
			// jo.put("ip", "172.16.150.21");
			 jo.put("ip", "127.0.0.1");
			jo.put("port", "9999");
		} else {
			jo = JSON.parseObject(sb.toString());
		}
		return jo;
	}

}
