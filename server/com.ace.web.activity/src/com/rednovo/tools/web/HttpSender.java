/**
 * com.swordfish.net.SpiderPaw.java@com.tax.splider
 */
package com.rednovo.tools.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.rednovo.tools.AndroidLogger;

/**
 * 
 * @author Yongchao.Yang@2013年10月29日
 * 
 */
public class HttpSender {
	// private static Logger logger = Logger.getLogger(HttpSender.class);

	/**
	 * 基于HttpClient抓取,适用服务器端
	 * 
	 * @param uri String 指定URL地址
	 * @param readOnly boolean <br/>
	 *            true 只分析页面中是否有指定关键字，不进行URL的二次获取<br/>
	 *            false 既要分析页面关键字，还要摘取满足规范的URL，进行二次抓取
	 */
	public static String httpClientGet(String uri, HashMap<String, String> params, HashMap<String, String> cookie) {
		CloseableHttpResponse response = null;
		try {

			// 构造参数
			StringBuffer sb = new StringBuffer();
			if (params != null && params.size() > 0) {
				Iterator<String> keyss = params.keySet().iterator();
				while (keyss.hasNext()) {
					String key = keyss.next();
					String val = params.get(key);
					sb.append(key + "=" + URLEncoder.encode(val, "UTF-8") + "&");
				}
				sb.delete(sb.length() - 1, sb.length());
			}

			uri = uri + "?" + sb.toString();

			System.out.println("-----------------------请求URL------------------------");
			System.out.println(uri);
			System.out.println("-----------------------请求URL------------------------");
			HttpGet httpGet = new HttpGet(uri);
			httpGet.setConfig(PawConfig.getConfig());

			// 设置参数

			if (cookie != null && cookie.size() > 0) {
				StringBuffer cookSb = new StringBuffer();
				Iterator<String> keys = cookie.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					String val = cookie.get(key);
					cookSb.append(key + "=" + val + ";");
				}
				httpGet.setHeader("Cookie", cookSb.toString());
			}

			response = SpiderFactory.getSplider().execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity, "UTF-8");
			} else {
				System.out.println("[HttpSender][没有抓取到数据]");
			}
			return "";

		} catch (Exception e) {
			System.out.println("[HttpSender][ 抓取异常" + uri + " (" + Thread.currentThread().getName() + ") ]");
			AndroidLogger.printLog(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {
				AndroidLogger.printLog(e);
			}
		}
		return "";
	}

	public static String httpClientRequest(String uri, HashMap<String, String> params) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setConfig(PawConfig.getConfig());

			// 构造参数
			if (params != null && params.size() > 0) {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				Iterator<String> keyss = params.keySet().iterator();
				while (keyss.hasNext()) {
					String key = keyss.next();
					nvps.add(new BasicNameValuePair(key, params.get(key)));
				}
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
				httpPost.setEntity(formEntity);

			}

			response = SpiderFactory.getSplider().execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity, "UTF-8");
			} else {
				System.out.println("[HttpSender][没有抓取到数据]");
			}
			return "";

		} catch (Exception e) {
			System.out.println("[HttpSender][ 抓取异常" + uri + " (" + Thread.currentThread().getName() + ") ]");
			AndroidLogger.printLog(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {
				AndroidLogger.printLog(e);
			}
		}
		return "";
	}

	/**
	 * 
	 * 基于URL类实现的抓取，适用移动端
	 */
	public static String urlRequest(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) {
		HashMap<String, String> para = new HashMap<String, String>();
		para.put("name", "yyc");
		para.put("name1", "yyc1");
		System.out.println(HttpSender.httpClientGet("http://api.17ace.cn/cookie.jsp", para, para));

	}
}
