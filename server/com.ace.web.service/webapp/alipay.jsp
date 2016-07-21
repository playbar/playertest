
<%@page import="com.alibaba.fastjson.JSON"%>
<%@page import="com.rednovo.ace.constant.Constant.OperaterStatus"%>
<%@page import="com.rednovo.ace.constant.Constant.SysUser"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="com.rednovo.ace.constant.Constant.OrderStatus"%>
<%@page import="com.ace.database.service.OrderService"%>
<%@page import="com.rednovo.ace.entity.Order"%>
<%@page import="org.enhydra.jdbc.oracle.OracleXAConnection"%>
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

	//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
	//商户订单号

	String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

	//支付宝交易号

	String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

	//交易状态
	String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
	String total_fee = new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8");

	//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

	if (AlipayNotify.verify(params)) {//验证成功
		//////////////////////////////////////////////////////////////////////////////////////////
		//请在这里加上商户的业务逻辑程序代码
		//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
		Order order = OrderService.getOrder(out_trade_no);
		if (trade_status.equals("TRADE_FINISHED")) {
			//判断该笔订单是否在商户网站中已经做过处理
			//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
			//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
			//如果有做过处理，不执行商户的业务程序

			if (OrderStatus.OPENED.getValue().equals(order.getStatus())) {
				out.println("success");
			} else {
				BigDecimal orderMoney = order.getRmbAmount();
				BigDecimal aliMoney = new BigDecimal(total_fee);
				System.out.println("订单金额:" + orderMoney.toString());
				System.out.println("阿里金额:" + aliMoney.toString());
				if (orderMoney.compareTo(aliMoney) == 0) {
					String exeCode = OrderService.openOrder(out_trade_no, SysUser.SERVER.getValue());
					if (exeCode.equals(OperaterStatus.SUCESSED.getValue())) {
						System.out.println("[开通订单成功][支付人民币" + orderMoney.toString() + ",获取金币" + order.getCoinAmount().toString() + "]");
						out.println("success");
					} else {
						System.out.println("[开通订单失败][错误代码" + exeCode + "]");
					}
				} else {
					System.out.println("[开通订单失败][订单金额:" + orderMoney.toString() + "，支付金额:" + aliMoney.toString() + "。金额不一致]");
				}

			}
			//注意：
			//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
		} else if (trade_status.equals("TRADE_SUCCESS")) {
			//判断该笔订单是否在商户网站中已经做过处理
			//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
			//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
			//如果有做过处理，不执行商户的业务程序
			if (OrderStatus.OPENED.getValue().equals(order.getStatus())) {
				out.println("success");
			} else {
				BigDecimal orderMoney = order.getRmbAmount();
				BigDecimal aliMoney = new BigDecimal(total_fee);
				System.out.println("订单金额:" + orderMoney.toString());
				System.out.println("阿里金额:" + aliMoney.toString());
				if (orderMoney.compareTo(aliMoney) == 0) {
					String exeCode = OrderService.openOrder(out_trade_no, SysUser.SERVER.getValue());
					if (exeCode.equals(OperaterStatus.SUCESSED.getValue())) {
						System.out.println("开通订单");
						out.println("success");
					} else {
						System.out.println("[开通订单失败][错误代码" + exeCode + "]");
					}
				} else {
					System.out.println("[开通订单失败][订单金额:" + orderMoney.toString() + "，支付金额:" + aliMoney.toString() + "。金额不一致]");
				}

			}
			//注意：
			//付款完成后，支付宝系统发送该交易状态通知
		}

		//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
		out.println("fail");
		//请不要修改或删除

		//////////////////////////////////////////////////////////////////////////////////////////
	} else {//验证失败
		System.out.println("[开通订单失败][参数校验失败]");
		out.println("fail");
	}
%>
