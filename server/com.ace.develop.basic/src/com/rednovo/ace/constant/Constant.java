/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月22日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：Constant.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * BB语音，系统内置常量
 * 
 * @author yongchao.Yang/2014年10月22日
 */
public class Constant {
	private static List<String> keyWord = new ArrayList<String>();

	public static void updateKeyWord(List<String> list) {
		keyWord = list;
	}

	public static List<String> getKeyWord() {
		return keyWord;
	}

	/**
	 * 聊天模式，私聊，群聊
	 * 
	 * @author yongchao.Yang/2015年4月16日
	 */
	public enum KeyWordType {
		NAME("name"), CHAT("chat");
		private String value = "";

		private KeyWordType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 聊天模式，私聊，群聊
	 * 
	 * @author yongchao.Yang/2015年4月16日
	 */
	public enum ChatMode {
		PRIVATE("0"), GROUP("1");
		private String value = "";

		private ChatMode(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 消息类型
	 * 
	 * @author yongchao.Yang/2015年4月16日
	 */
	public enum MsgType {
		TXT_MSG("0"), MEDIA_MSG_AUDIO("1"), MEDIA_MSG_PIC("2"), PHOTO_PRIVATE("4"), BACKGROUND_PHOTO_PRIVATE("5"), PHOTO_GROUP("6"), BACKGROUND_PHOTO_GROUP("7"), GROUP_SHARE("8");
		private String value = "";

		private MsgType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 互动模式
	 * 
	 * @author yongchao.Yang/2015年4月16日
	 */
	public enum InteractMode {
		REQUEST("0"), RESPONSE("1");
		private String value = "";

		private InteractMode(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 内置系统用户
	 * 
	 * @author yongchao.Yang/2015年4月16日
	 */
	public enum SysUser {
		SERVER("999999");
		private String value = "";

		private SysUser(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 系统指定事件
	 * 
	 * @author yongchao.Yang/2015年4月16日
	 */
	public enum SysEvent {
		PING("009-001"), BROAD_MSG("009-002"), BROAD_SHOW_ME("009-003"), HEART_BEAT("009-004");
		private String value = "";

		private SysEvent(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 操作结果
	 * 
	 * @author yongchao.Yang/2015年5月11日
	 */
	public static enum OperaterStatus {
		SUCESSED("1"), FAILED("0"),;
		private String value;

		private OperaterStatus(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 订单状态
	 * 
	 * @author yongchao.Yang/2016年3月4日
	 */
	public static enum OrderStatus {
		UNPAYED("1"), OPENED("2"), UNOPENED("3"),;
		private String value;

		private OrderStatus(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 账户变化类型
	 * 
	 * @author yongchao.Yang/2016年3月4日
	 */
	public static enum ChangeType {
		ADD("1"), REDUCE("0"),;
		private String value;

		private ChangeType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 账户变化业务类型
	 * 
	 * @author yongchao.Yang/2016年3月4日
	 */
	public static enum logicType {
		ORDER("1"), SYS_ADD_MONEY("2"), SEND_GIFT("3"), APPLY_CASH("4"), SYS_DEC_MONEY("5"), EXCHANGE("6");
		private String value;

		private logicType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 支付渠道
	 * 
	 * @author yongchao.Yang/2016年3月5日
	 */
	public static enum payChannel {
		WECHAT("1"), ALIPAY("2"), APPLE_INNEL("3"), MIDAS("4");
		private String value;

		private payChannel(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 登录方式
	 * 
	 * @author yongchao.Yang/2016年3月9日
	 */
	public static enum loginChannel {
		WECHAT("1"), QQ("2"), MOBILE("3"), LOCAL("4"), SINA_WEIBO("5"), WECHAT_TECENT("6"), QQ_TECENT("7");
		private String value;

		private loginChannel(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 用户状态
	 * 
	 * @author yongchao.Yang/2016年5月31日
	 */
	public static enum userStatus {
		FREEZE("0"), FREE("1"), FORBIDSHOW("1");
		private String value;

		private userStatus(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * banner状态
	 * 
	 * @author lxg/2016年5月9日
	 */
	public static enum ADStatus {
		ABLE("1"), OPEN("2"), DISABLE("0"),;
		private String value;

		private ADStatus(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 推送类型
	 * 
	 * @author lxg/2016年5月9日
	 */
	public static enum UserPushType {
		JG("0"), OTHER("1");
		private String value;

		private UserPushType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 推送类型
	 * 
	 * @author lxg/2016年5月9日
	 */
	public static enum UserCertify {
		FAILED("0"), SUCCESS("1");
		private String value;

		private UserCertify(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	// 1 已认证 0 未认证 2 正在审核 3 认证失败
	public static enum CertifyProcess {
		FAILED("3"), SUCCESS("1"), PROCESS("2"), NOT("0");
		;
		private String value;

		private CertifyProcess(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}
}
