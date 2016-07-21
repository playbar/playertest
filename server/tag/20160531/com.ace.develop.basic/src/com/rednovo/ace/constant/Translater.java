/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月5日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.web.service
 *                  fileName：Translater.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.constant;

/**
 * @author yongchao.Yang/2016年3月5日
 */
public class Translater {

	/**
	 * 
	 */
	public Translater() {
		// TODO Auto-generated constructor stub
	}

	public static String getLogicName(Constant.logicType type) {
		if (type.getValue().equals(Constant.logicType.ORDER.getValue())) {
			return "订单充值";
		} else if (type.getValue().equals(Constant.logicType.SEND_GIFT.getValue())) {
			return "赠送礼物";
		} else if (type.getValue().equals(Constant.logicType.SYS_ADD_MONEY.getValue())) {
			return "系统加币";
		} else if (type.getValue().equals(Constant.logicType.EXCHANGE.getValue())) {
			return "兑点";
		} else if(type.getValue().equals(Constant.logicType.SYS_DEC_MONEY.getValue())){
			return "系统减币";
		}else {
			return "";
		}
	}
}
