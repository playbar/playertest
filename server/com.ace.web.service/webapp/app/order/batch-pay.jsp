<%
/* *
 *功能：手机网站支付接口接入页
 *版本：3.4
 *修改日期：2016-03-08
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

 *************************注意*****************
 *如果您在接口集成过程中遇到问题，可以按照下面的途径来解决
 *1、开发文档中心（https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.2Z6TSk&treeId=60&articleId=103693&docType=1）
 *2、商户帮助中心（https://cshall.alipay.com/enterprise/help_detail.htm?help_id=473888）
 *3、支持中心（https://support.open.alipay.com/alipay/support/index.htm）
 *如果不想使用扩展功能请把扩展功能参数赋空值。
 **********************************************
 */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.alipay.config.*"%>
<%@ page import="com.alipay.util.*"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.rednovo.tools.PPConfiguration"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>支付宝手机网站支付接口</title>
	</head>
	<%
		////////////////////////////////////请求参数//////////////////////////////////////

        //批次号 格式：当天日期[8位]+序列号[3至16位]
        String batch_no = ""+request.getAttribute("batch_no");
		//付款总金额
        String batch_fee = ""+request.getAttribute("batch_fee");
        //付款笔数
        String batch_num = ""+request.getAttribute("batch_num");
        //付款详细数据
        //必填，格式：流水号1^收款方帐号1^真实姓名^付款金额1^备注说明1|流水号2^收款方帐号2^真实姓名^付款金额2^备注说明2....
        String detail_data = ""+request.getAttribute("detail_data");
        //付款当天日期 //必填，格式：年[4位]月[2位]日[2位]，如：20100801
        String pay_date = ""+request.getAttribute("pay_date");
		
		
		
		//////////////////////////////////////////////////////////////////////////////////
		
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "batch_trans_notify");
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("notify_url", AlipayConfig.batch_notify_url);
		sParaTemp.put("email", AlipayConfig.seller_id);
		sParaTemp.put("account_name", "重庆红诺科技有限公司");
		sParaTemp.put("pay_date", pay_date);
		sParaTemp.put("batch_no", batch_no);
		sParaTemp.put("batch_fee", batch_fee);
		sParaTemp.put("batch_num", batch_num);
		sParaTemp.put("detail_data", detail_data);
		//其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.2Z6TSk&treeId=60&articleId=103693&docType=1
        //如sParaTemp.put("参数名","参数值");

		
		//建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"post","确认");
		out.println(sHtmlText);
	%>
	<body>
	</body>
</html>
