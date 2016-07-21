/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播手机版
 *                  公司名称:多宝科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月11日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：Message.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * @author yongchao.Yang/2014年10月11日
 */
public class Message implements Cloneable {
	/**
	 * 消息摘要
	 */
	private Summary sumy;
	/**
	 * 消息体，如果是文件，则为空
	 */
	private JSONObject body;

	public Summary getSumy() {
		return sumy;
	}

	public void setSumy(Summary sumy) {
		this.sumy = sumy;
	}

	public JSONObject getBody() {
		return body;
	}

	public void setBody(JSONObject body) {
		this.body = body;
	}
	
	

	@Override
	public Object clone() throws CloneNotSupportedException {
		Message msg = (Message) super.clone();
		msg.sumy = (Summary) msg.sumy.clone();
		return msg;
	}

}
