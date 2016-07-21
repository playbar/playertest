
<%@page import="com.rednovo.tools.Validator"%>
<%@page import="com.alibaba.fastjson.JSON"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.ace.database.service.OrderService"%>
<%@page import="com.ace.database.service.ExchangeService"%>
<%
	/* *
	 功能：支付宝服务器异步通知页面
	 版本：3.3
	 日期：2012-08-17
	 说明：
	 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
	 该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
	 //***********页面功能说明***********
	 创建该页面文件时，请留心该页面文件中无任何HTML代码及空格。
	 该页面不能在本机电脑测试，请到服务器上做测试。请确保外部可以访问该页面。
	 该页面调试工具请使用写文本函数logResult，该函数在com.alipay.util文件夹的AlipayNotify.java类文件中
	 如果没有收到该页面返回的 success 信息，支付宝会在24小时内按一定的时间策略重发通知
	 //********************************
	 * */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.alipay.util.*"%>
<%@ page import="com.alipay.config.*"%>
<%
	//获取支付宝POST过来反馈信息
	Map<String, String> params = new HashMap<String, String>();
	Map requestParams = request.getParameterMap();
	for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
		String name = (String) iter.next();
		String[] values = (String[]) requestParams.get(name);
		String valueStr = "";
		for (int i = 0; i < values.length; i++) {
			valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
		}
		//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
		//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
		params.put(name, valueStr);
	}
	//批量付款数据中转账成功的详细信息 0315001^gonglei1@handsome.com.cn^龚本林^20.00^S^null^200810248427067^20081024143652|
	//流水号^收款方账号^收款账号姓名^付款金额^成功标识(S)^成功原因(null)^支付宝内部流水号^完成时间
	String success_details = "";
	if (!Validator.isEmpty(request.getParameter("success_details"))) {
		success_details = new String(request.getParameter("success_details").getBytes("ISO-8859-1"), "UTF-8");
	}
	//批量付款数据中转账失败的详细信息 0315006^xinjie_xj@163.com^星辰公司1^20.00^F^TXN_RESULT_TRANSFER_OUT_CAN_NOT_EQUAL_IN^200810248427065^20081024143651|
	//格式为：流水号^收款方账号^收款账号姓名^付款金额^失败标识(F)^失败原因^支付宝内部流水号^完成时间。
	String fail_details = "";
	if (!Validator.isEmpty(request.getParameter("fail_details"))) {
		fail_details = new String(request.getParameter("fail_details").getBytes("ISO-8859-1"), "UTF-8");
	}
	String batch_no = new String(request.getParameter("batch_no").getBytes("ISO-8859-1"), "UTF-8");
	System.out.println(success_details);
	System.out.println(fail_details);
	if (AlipayNotify.verify(params)) {//验证成功
	//if (true) {//验证成功
		//////////////////////////////////////////////////////////////////////////////////////////
		//请在这里加上商户的业务逻辑程序代码
		//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
		//判断该笔订单是否在商户网站中已经做过处理
		//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
		//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
		//如果有做过处理，不执行商户的业务程序
		if (OrderService.getBatchInfo(batch_no)) {
			//更改成功用户的状态 
			StringBuffer ids = new StringBuffer();
			StringBuffer applyids = new StringBuffer();
			if (!Validator.isEmpty(success_details)) {
				String[] success = success_details.split("\\|");
				for (int i = 0; i < success.length; i++) {
					ids.append(success[i].substring(0, success[i].indexOf('_')) + "^");
					applyids.append(success[i].substring(success[i].indexOf('_') + 1, success[i].indexOf('^') + 1));
				}
				ExchangeService.updateStepStatus(ids.toString(), "3", "1", applyids.toString(), "999999", "");
			}
			if (!Validator.isEmpty(fail_details)) {
				//添加失败记录
				String[] fail = fail_details.split("\\|");
				ids = new StringBuffer();
				applyids = new StringBuffer();
				StringBuffer dess = new StringBuffer();
				for (int i = 0; i < fail.length; i++) {
					ids.append(fail[i].substring(0, fail[i].indexOf('_')) + "^");
					applyids.append(fail[i].substring(fail[i].indexOf('_') + 1, fail[i].indexOf('^') + 1));
					String str = fail[i];
					int lastFirst = str.lastIndexOf('^');
					str = str.substring(0, lastFirst);
					lastFirst = str.lastIndexOf('^');
					str = str.substring(0, lastFirst);
					dess.append(str.substring(str.lastIndexOf('^')));

				}
				ExchangeService.updateStepStatus(ids.toString(), "3", "2", applyids.toString(), "999999", dess.toString().substring(1)); 
			}
		} else {
			System.out.println("[批量打款失败][批次号" + batch_no + "不存在]");
		}
		//注意：
		//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知

		//请不要修改或删除

		//////////////////////////////////////////////////////////////////////////////////////////
	} else {//验证失败
		System.out.println("[批量打款失败][参数校验失败]");
		out.println("fail");
	}
%>
