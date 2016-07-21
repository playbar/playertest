package com.rednovo.tools.sms;

import org.apache.commons.lang.StringUtils;

/**
 * 发送短信
 * 
 * @author sg.z/2014年10月14日
 */
public class SendSMS {

	/**
	 * 单个发送短信
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 * @author sg.z
	 * @since 2014年11月3日下午5:36:10
	 */
	public static boolean sendSms(String mobile, String content) {

		if (StringUtils.isBlank(mobile) || StringUtils.isBlank(content)) {
			return false;
		}

		String con = "您的验证码是：" + content.trim() + "。";

		boolean res = HywxSms.sendSms(mobile, con);
		if (!res) {// 备用的
			res = batchSend(new String[] { mobile }, con, "");
		}
		return res;
	}
	
	public static void main(String[] args) {
		System.out.println(sendSms("","555555"));
	}


	/**
	 * 批量发送短信
	 * 
	 * @param mobiles 电话号码
	 * @param content 内容
	 * @param sendTime 发送时间，格式：yyyy-MM-dd hh:MM:ss
	 *            可以为空，空为立即发送，不为空则为定时发送，发送时间必须为当前时间之后，之前视为立即发送
	 * @return
	 * @author sg.z
	 * @since 2014年10月14日下午3:54:30
	 */
	private static boolean batchSend(String[] mobiles, String content, String sendTime) {

		if (mobiles == null || mobiles.length == 0 || StringUtils.isBlank(content)) {
			return false;
		}

		boolean res = batchSendRd(mobiles, content, sendTime);
		if (!res) {
			res = batchSendSZ(mobiles, content, sendTime);
		}
		return res;
	}

	/**
	 * 神州短信
	 * 
	 * @param mobiles 电话号码
	 * @param content 内容
	 * @param sendTime 发送时间，格式：yyyyMMddhhMMss
	 *            可以为空，空为立即发送，不为空则为定时发送，发送时间必须为当前时间之后，之前视为立即发送
	 * @return
	 * @author sg.z
	 * @since 2014年10月14日下午4:09:05
	 */
	private static boolean batchSendSZ(String[] mobiles, String content, String sendTime) {
		// 检查余额
		String selSum = SzSMSUtil.getSelSum();
		if (StringUtils.isNotBlank(selSum) && isLong(selSum)) {
			StringBuffer mbs = new StringBuffer();
			for (int i = 0; i < mobiles.length; i++) {
				mbs.append(mobiles[i]);
				if (i != mobiles.length - 1) {
					mbs.append(",");
				}
			}
			String result = SzSMSUtil.batchSend(mbs.toString(),content, sendTime);
			if (StringUtils.equals("发送成功", result)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 容大友信
	 * 
	 * @param mobiles 电话号码
	 * @param content 内容
	 * @param sendTime 发送时间，格式：yyyy-MM-dd hh:MM:ss
	 *            可以为空，空为立即发送，不为空则为定时发送，发送时间必须为当前时间之后，之前视为立即发送
	 * @return
	 * @author sg.z
	 * @since 2014年10月14日下午4:09:05
	 */
	private static boolean batchSendRd(String[] mobiles, String content, String sendTime) {
		// 检查余额
		String selSum = RdSMSUtil.getSelSum();
		if (StringUtils.isNotBlank(selSum) && isLong(selSum)) {
			StringBuffer mbs = new StringBuffer();
			for (int i = 0; i < mobiles.length; i++) {
				mbs.append(mobiles[i]);
				if (i != mobiles.length - 1) {
					mbs.append(";");
				}
			}
			String result = RdSMSUtil.batchSend(mbs.toString(),"【ACE直播】 " + content, sendTime);
			if (StringUtils.equals("发送成功", result)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否是长整形数据
	 */
	private static boolean isLong(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		try {
			Long.parseLong(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
}
