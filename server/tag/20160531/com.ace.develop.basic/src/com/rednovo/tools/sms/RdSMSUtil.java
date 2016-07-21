package com.rednovo.tools.sms;

import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import com.rednovo.tools.web.HttpSender;

/**
 * 容大友信 
 * web 登录入口   http://116.213.72.20/WebClient/default.aspx
 * 
 * @author sg.z/2014年10月13日
 */
public class RdSMSUtil {

	/**
	 * 接口地址
	 */
	private final static String SELSUM_URL = "http://116.213.72.20/SMSHttpService/Balance.aspx";
	/**
	 * 群发送短信地址
	 */
	private final static String BATCHSEND_URL = "http://116.213.72.20/SMSHttpService/send.aspx";
	/**
	 * 用户名和密码
	 */
	private final static String username = "cqhn";
	private final static String password = "cqhn";

	/**
	 * 获取余额
	 * 
	 * @author sg.z
	 * @since 2014年10月13日下午6:23:42
	 */
	public static String getSelSum() {
		HashMap<String,String> param = new HashMap<String,String>();
		param.put("username",username);
		param.put("password",password);
		return HttpSender.httpClientRequest(SELSUM_URL, param);
	}

	/**
	 * 批量发送短信
	 * 
	 * @param mobiles 接收手机号码，多个手机号码用分号隔开 不能为空
	 * @param content 发送内容 不能为空
	 * @param sendTime 空字符串或过期日期为立即发送, 格式：yyyy-MM-dd HH:mm:ss 可以为空
	 * @return
	 * @author sg.z
	 * @since 2014年10月14日下午12:04:48
	 */
	public static String batchSend(String mobiles, String content, String sendTime) {

		if (StringUtils.isBlank(content) || StringUtils.isBlank(mobiles)) {
			return "发送号码和内容不能为空";
		}
		HashMap<String,String> param = new HashMap<String,String>();
		param.put("username",username);
		param.put("password",password);
		param.put("mobile",mobiles);
		param.put("content",content);
		param.put("senddate",sendTime);
		param.put("extcode","");
		param.put("batchID","");
		String result = HttpSender.httpClientRequest(BATCHSEND_URL, param);
		if (StringUtils.isNotBlank(result)) {
			long res = Long.valueOf(result).longValue();
			if (res == 0) {
				return "发送成功";
			} else if (res == -1) {
				return "数据保存失败";
			} else if (res == -2) {
				return "提交的号码中包含不符合格式的手机号码";
			} else if (res == 1001) {
				return "帐号或密码错误";
			} else if (res == 1002) {
				return "余额不足，请先充值";
			} else if (res == 1003) {
				return "参数错误";
			} else if (res == 1004) {
				return "其它错误";
			}
		}
		return "";

	}

	public static void main(String[] args) {
		System.out.println(batchSend("", "【ACE直播】 您的验证码是：666666。", ""));
		System.out.println(getSelSum());
	}

}
