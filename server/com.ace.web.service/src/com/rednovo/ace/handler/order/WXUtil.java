package com.rednovo.ace.handler.order;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.qq.open.https.MyX509TrustManager;
import com.rednovo.tools.MD5;
import com.rednovo.tools.PPConfiguration;

/**
 * 微信辅助类
 * @author lxg
 *
 */
public class WXUtil {
	private static Logger log = Logger.getLogger(WXUtil.class);
	public final static String APPID = PPConfiguration.getProperties("cfg.properties").getString("wx.APPID");//服务号的应用号
	public final static String MCH_ID =  PPConfiguration.getProperties("cfg.properties").getString("wx.MCH_ID");//商户号
	public final static String API_KEY = PPConfiguration.getProperties("cfg.properties").getString("wx.API_KEY");
	public final static String SIGN_TYPE = "MD5";
	public final static String PAG = PPConfiguration.getProperties("cfg.properties").getString("wx.PAG");
	public final static String NOTIFY_URL = PPConfiguration.getProperties("cfg.properties").getString("wx.NOTIFY_URL");
	public final static String UNIFIED_ORDER_URL = PPConfiguration.getProperties("cfg.properties").getString("wx.UNIFIED_ORDER_URL");
	
	/**
	 * 微信业务请求
	 * @param requestUrl
	 * @param requestMethod
	 * @param outputStr
	 * @return
	 */
	public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		try {
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(requestMethod.toUpperCase());
			conn.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			return buffer.toString();
		} catch (ConnectException ce) {
			log.error("连接超时：{}", ce);
		} catch (Exception e) {
			log.error("https请求异常：{}", e);
		}
		return null;
	}
		
	/**
	 * 获取签名
	 * @param parameters
	 * @param data
	 * @return
	 */
	public static String getSign(WxData data){
		  SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
	      parameters.put("appid", data.getAppid()); 
	      parameters.put("attach", data.getAttach());
	      parameters.put("body", data.getBody());
	      parameters.put("mch_id", data.getMch_id());
	      parameters.put("nonce_str", data.getNonce_str());
	      parameters.put("notify_url", data.getNotify_url());
	      parameters.put("out_trade_no", data.getOut_trade_no());
	      parameters.put("total_fee", data.getTotal_fee());
	      parameters.put("trade_type", data.getTrade_type());
	      parameters.put("spbill_create_ip", data.getSpbill_create_ip());
	      parameters.put("body", data.getBody());
	      return createSign(parameters);
	}
	
	/**
	 * 下订单
	 * @param data
	 * @return
	 */
	public static String unifiedOrder(WxData data){
	    String returnXml = null;
	    try {
	      SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
	      parameters.put("appid", data.getAppid()); 
	      parameters.put("attach", data.getAttach());
	      parameters.put("body", data.getBody());
	      parameters.put("mch_id", data.getMch_id());
	      parameters.put("nonce_str", data.getNonce_str());
	      parameters.put("notify_url", data.getNotify_url());
	      parameters.put("out_trade_no", data.getOut_trade_no());
	      parameters.put("total_fee", data.getTotal_fee());
	      parameters.put("trade_type", data.getTrade_type());
	      parameters.put("spbill_create_ip", data.getSpbill_create_ip());
	      parameters.put("body", data.getBody());
	      log.info("SIGN:"+createSign(parameters));
	      parameters.put("sign", createSign(parameters));
	      String xml = getRequestXml(parameters);
	      log.info("统一下单xml为:\n" + xml);
	      returnXml = httpsRequest(UNIFIED_ORDER_URL, "post", xml);
	      log.info("返回结果:" + returnXml);
	    } catch (Exception e) {
	      e.printStackTrace();
	    } 
	    return returnXml;
	}
	
	/**
	 * map转换xml字符串
	 * @param parameters
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getRequestXml(SortedMap<Object,Object> parameters){
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		Set es = parameters.entrySet();
		Iterator it = es.iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			String k = (String)entry.getKey();
			if ("attach".equalsIgnoreCase(k)||"body".equalsIgnoreCase(k)||"sign".equalsIgnoreCase(k)) {
				sb.append("<"+k+">"+"<![CDATA["+entry.getValue()+"]]></"+k+">");
			}else {
				sb.append("<"+k+">"+entry.getValue()+"</"+k+">");
			}
		}
		sb.append("</xml>");
		return sb.toString();
	}
		
	/**
	 * 创建签名
	 * @param parameters
	 * @param characterEncoding
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String createSign(SortedMap<Object,Object> parameters){
		StringBuffer sb = new StringBuffer();
		Set es = parameters.entrySet();
		Iterator it = es.iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			String k = (String)entry.getKey();
			Object v = entry.getValue();
			if(null != v && !"".equals(v) 
					&& !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + API_KEY);
		String sign = MD5.md5(sb.toString()).toUpperCase();
		return sign;
	}
	
	/**
	 * xml字符串解析成map结构
	 * @param strxml
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map doXMLParse(String strxml) throws JDOMException, IOException {
		strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
		if(null == strxml || "".equals(strxml)) {
			return null;
		}
		Map m = new HashMap();
		InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while(it.hasNext()) {
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if(children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = getChildrenText(children);
			}
			m.put(k, v);
		}
		//关闭流
		in.close();
		return m;
	}
	
	/*
	 * 生产随机数字
	 */
	public static String CreateNoncestr() {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String res = "";
		for (int i = 0; i < 16; i++) {
			Random rd = new Random();
			res += chars.charAt(rd.nextInt(chars.length() - 1));
		}
		return res;
	}
	
	/**
	 * 获取子结点的xml
	 * @param children
	 * @return String
	 */
	@SuppressWarnings("rawtypes")
	public static String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if(!children.isEmpty()) {
			Iterator it = children.iterator();
			while(it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if(!list.isEmpty()) {
					sb.append(getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}
		return sb.toString();
	}
	
	public static String setXML(String return_code, String return_msg) {
		return "<xml><return_code><![CDATA[" + return_code
				+ "]]></return_code><return_msg><![CDATA[" + return_msg
				+ "]]></return_msg></xml>";
	}
	
}
