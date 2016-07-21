package com.rednovo.tools.sms;

import org.apache.commons.lang.StringUtils;

import com.rednovo.tools.web.HttpSender;

/**
 * 神州短信工具类
 * 
 * web登录入口  http://user.bjszrk.com/Frame/Index.aspx
 * 
 * @author sg.z/2014年10月13日
 */
public class SzSMSUtil {

	/**
	 * 接口地址
	 */
	private final static String SELSUM_URL = "http://api.bjszrk.com/sdk/SelSum.aspx";
	/**
	 * 群发送短信地址
	 */
	private final static String BATCHSEND_URL = "http://api.bjszrk.com/sdk/BatchSend.aspx";
	/**
	 * 用户名和密码
	 */
	private final static String CorpID = "SDK1981";
	private final static String Pwd = "hongnuo123";

	/**
	 * 获取余额
	 * 
	 * @author sg.z
	 * @since 2014年10月13日下午6:23:42
	 */
	public static String getSelSum() {
		StringBuffer sb = new StringBuffer();
		sb.append("CorpID=" + CorpID);
		sb.append("&Pwd=" + Pwd);

		String result = HttpSender.urlRequest(SELSUM_URL, sb.toString());
		if (StringUtils.isNotBlank(result)) {
			int res = Integer.valueOf(result).intValue();
			if (res >= 0) {
				return result;
			} else if (res == -1) {
				return "账号未注册";
			} else if (res == -2) {
				return "其他错误";
			} else if (res == -3) {
				return "帐号或密码错误";
			}
		}
		return "";

	}

	/**
	 * 批量发送短信
	 * 
	 * @param mobiles 手机号码，多个号码‘,’号隔开,一次提交信息不能超过10000个手机号码 不能为空
	 * @param content 发送内容 不能为空
	 * @param sendTime 定时发送时间，固定14位长度字符串，比如：20060912152435 可以为空
	 * @return
	 * @author sg.z
	 * @since 2014年10月14日下午12:04:48
	 */
	public static String batchSend(String mobiles, String content, String sendTime) {

		if (StringUtils.isBlank(content) || StringUtils.isBlank(mobiles)) {
			return "发送号码和内容不能为空";
		}
		if (content.length() <= 3 || content.length() >= 250) {
			return "发送内容需在3到250字之间";
		}
		if (StringUtils.isNotBlank(sendTime) && sendTime.length() != 14) {
			return "定时发送时间不是有效的时间格式";
		}

		StringBuffer sb = new StringBuffer();
		sb.append("CorpID=" + CorpID);
		sb.append("&Pwd=" + Pwd);		
		sb.append("&Mobile=" + mobiles);
		sb.append("&Content=" + content);
		sb.append("&SendTime=" + sendTime);

		String result = HttpSender.urlRequest(BATCHSEND_URL, sb.toString());
		if (StringUtils.isNotBlank(result)) {
			long res = Long.valueOf(result).longValue();
			if (res > 0) {
				return "发送成功";
			} else if (res == -1) {
				return "账号未注册";
			} else if (res == -2) {
				return "其他错误";
			} else if (res == -3) {
				return "帐号或密码错误";
			} else if (res == -4) {
				return "一次提交信息不能超过10000个手机号码，号码逗号隔开";
			} else if (res == -5) {
				return "余额不足，请先充值";
			} else if (res == -6) {
				return "定时发送时间不是有效的时间格式";
			} else if (res == -8) {
				return "发送内容需在3到250字之间";
			} else if (res == -9) {
				return "发送号码为空";
			}
		}
		return "";

	}

	public static void main(String[] args) {
		// System.out.println(getSelSum());
		// System.out.println(batchSend("18201102770", "尊敬的客户您好，您的验证码是666666", ""));
		System.out.println(batchSend("", "您的验证码是：111111。", ""));
	}
}
