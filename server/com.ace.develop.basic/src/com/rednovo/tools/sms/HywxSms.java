package com.rednovo.tools.sms;

import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.rednovo.tools.MD5;
import com.rednovo.tools.web.HttpSender;

/**
 * 互亿无线
 * web 登录入口    http://www.ihuyi.com/product.php?cid=33
 * @author sg.z/2014年11月3日
 */
public class HywxSms {

	/**
	 * 账号信息
	 */
	public static final String account = "cf_cqhn";
	public static final String password = "LEduo0_48Mb";
	public static final String SEND_URL = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
	public static final String SEL_SUM_URL = "http://106.ihuyi.cn/webservice/sms.php?method=GetNum";

	/**
	 * 互亿无线发送短信
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 * @author sg.z
	 * @since 2014年11月3日下午4:59:18
	 */
	public static boolean sendSms(String mobile, String content) {
		// 查询余额
		int num = getSelSum();
		if (num > 0) {
			if (StringUtils.isNotBlank(content) && content.length() < 67) {
				HashMap<String, String> param = new HashMap<String, String>();
				param.put("account", account);
				param.put("password", MD5.md5(password));
				param.put("mobile", mobile);
				param.put("content", content);
				String result = HttpSender.httpClientRequest(SEND_URL, param);
				if (StringUtils.isNotBlank(result)) {
					Document doc;
					try {
						doc = DocumentHelper.parseText(result);
					} catch (DocumentException e) {
						return false;
					}
					Element root = doc.getRootElement();
					String code = root.elementText("code");
					if (StringUtils.equals(code, "2")) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
		return false;

	}

	/**
	 * 查询余额
	 * 
	 * @return
	 * @author sg.z
	 * @since 2014年11月3日下午5:09:40
	 */
	public static int getSelSum() {
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("account", account);
		param.put("password", password);
		String result = HttpSender.httpClientRequest(SEL_SUM_URL, param);
		if (StringUtils.isNotBlank(result)) {
			Document doc;
			try {
				doc = DocumentHelper.parseText(result);
			} catch (DocumentException e) {
				return 0;
			}
			Element root = doc.getRootElement();
			String code = root.elementText("code");
			// String msg = root.elementText("msg");
			String num = root.elementText("num");
			if (StringUtils.equals(code, "2")) {
				return Integer.valueOf(num).intValue();
			} else {
				return 0;
			}
		}
		return 0;
	}

	public static void main(String[] args) {
		boolean str = sendSms("", "您的验证码是：888888。");
		System.err.println("发送结果:" + str);
	}

}
