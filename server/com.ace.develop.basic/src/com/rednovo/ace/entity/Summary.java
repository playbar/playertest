/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播手机版
 *                  公司名称:多宝科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月16日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：Summary.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.entity;


/**
 * 消息摘要
 * 
 * @author yongchao.Yang/2014年10月16日
 */
public class Summary implements Cloneable {

	private String msgId = "";

	private String senderSex = "";
	/**
	 * 发送者的会话Id
	 */
	private String sendSessionId = "";
	/**
	 * 发送者ID
	 */
	private String senderId = "";
	private String senderName = "";
	/**
	 * 接受者ID
	 */
	private String receiverId = "";
	private String receiverName = "";
	/**
	 * 信息接受方的sessionId。主要针对游客设置
	 */
	private String receiveSessionId = "";
	/**
	 * 文件类型 文本 / 文件 1是文本消息 2 媒体消息 3 用户图像 4 群图像 ,...,8群站内分享
	 */
	private String msgType = "";
	/**
	 * 文件名称
	 */
	private String fileName = "";
	/**
	 * 语音时长
	 */
	private String duration = "";

	/**
	 * 聊天模式
	 */
	private String chatMode = "";
	/**
	 * 消息发送时间
	 */
	private String sendTime = "";

	private String requestKey = "";
	private String interactMode = "";
	private String showId = "";

	// private String groupName = "";

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String type) {
		this.msgType = type;
	}

	public String getChatMode() {
		return chatMode;
	}

	public void setChatMode(String mode) {
		this.chatMode = mode;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRequestKey() {
		return requestKey;
	}

	public void setRequestKey(String requestKey) {
		this.requestKey = requestKey;
	}

	public String getInteractMode() {
		return interactMode;
	}

	public void setInteractMode(String mode) {
		this.interactMode = mode;
	}

	public String getShowId() {
		return showId;
	}

	public void setShowId(String groupId) {
		this.showId = groupId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getSenderSex() {
		return senderSex;
	}

	public void setSenderSex(String senderSex) {
		this.senderSex = senderSex;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getReceiveSessionId() {
		return receiveSessionId;
	}

	public void setReceiveSessionId(String receiveSessionId) {
		this.receiveSessionId = receiveSessionId;
	}

	public String getSendSessionId() {
		return sendSessionId;
	}

	public void setSendSessionId(String sendSessionId) {
		this.sendSessionId = sendSessionId;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
