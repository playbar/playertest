/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播移动
 *                  公司名称:美播娱乐
 *                  开发作者:sg.z
 *       			开发时间:2014年7月29日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自美播娱乐研发部，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：meibo-admin
 *                  fileName：UserHandler.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.handler.order;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

import com.ace.database.service.OrderService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.constant.Constant.OrderStatus;
import com.rednovo.ace.constant.Constant.payChannel;
import com.rednovo.ace.entity.GoodInfo;
import com.rednovo.ace.entity.Order;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.ace.globalData.UserManager;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.Base64;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.PPConfiguration;
import com.rednovo.tools.Validator;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class OrderHandler extends BasicServiceAdapter {
	private static Logger logger = Logger.getLogger(OrderHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if ("001-008".equals(key)) {// 新建订单
			this.createOrder();
		} else if ("001-009".equals(key)) {// 开通订单
			this.openOrder();
		} else if ("001-010".equals(key)) {// 赠送礼物
		} else if ("001-005".equals(key)) {
			this.getPayList();
		} else if ("001-004".equals(key)) {
			this.getGoodList();
		} else if ("001-020".equals(key)) {// 苹果支付回调
			this.applePayCallBack();
		} else if ("001-044".equals(key)) {
			this.wxCallBack();
		} else if ("009-004".equals(key)) {// 获取服务器列表
			this.getServerlist();
		} else if ("009-005".equals(key)) {
			this.updateServerlist();
		} else if ("001-033".equals(key)) {// 支付宝网页支付
			this.createOrderPC();
		} else if ("001-037".equals(key)) {// order list
			this.getOrderList();
		} else if ("001-039".equals(key)) {
			this.createBatchNo();
		}
	}

	/**
	 * 创建批量付款批次号
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月23日下午2:26:27
	 */
	private void createBatchNo() {
		logger.info("进入创建批量付款批次号业务");
		String batchNum = this.getWebHelper().getString("batchNum"); // 几笔
		// detailData = 0315006_userid^testture0002@126.com^常炜买家^20.00^hello|记录id^testture0002@126.com^常炜买家^20.00^hello
		String detailData = this.getWebHelper().getString("detailData");
		detailData.substring(0, detailData.length()-1);
		String[] details = detailData.split("\\|");
		BigDecimal batchFee = new BigDecimal(0);
		for (int i = 0; i < details.length; i++) {
			String[] user = details[i].split("\\^");
			System.out.println("name---"+user[2]);
			batchFee = batchFee.add(new BigDecimal(user[3]));
		}
		String batchNo = OrderService.createBatchInfo(batchFee.toString(), batchNum);
		this.getWebHelper().getRequest().setAttribute("batch_no", batchNo);
		this.getWebHelper().getRequest().setAttribute("batch_fee", batchFee);
		this.getWebHelper().getRequest().setAttribute("batch_num", batchNum);
		this.getWebHelper().getRequest().setAttribute("detail_data", detailData);
		this.getWebHelper().getRequest().setAttribute("pay_date", DateUtil.getUserDate("yyyyMMdd"));
	}

	/**
	 * 获取订单列表
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月13日下午5:05:57
	 */
	private void getOrderList() {
		String userId = this.getWebHelper().getString("userId");
		String startTime = this.getWebHelper().getString("startTime");
		String endTime = this.getWebHelper().getString("endTime");
		String status = this.getWebHelper().getString("status");
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");
		if (page <= 0) {
			page = 1;
		}
		if (pageSize <= 0) {
			pageSize = 10;
		}
		ArrayList<Order> list = OrderService.getOrderList(userId, startTime, endTime, status, page, pageSize);
		if (list == null) {
			this.setError("300");
		} else {
			this.setSuccess();
			this.setValue("orderLost", list);
		}

	}

	/**
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月12日下午4:03:35
	 */
	private void createOrderPC() {
		logger.info("进入创建订单业务");
		String payerId = this.getWebHelper().getString("payerId");
		String receiverId = this.getWebHelper().getString("receiverId");
		String goodId = this.getWebHelper().getString("goodId");
		int goodCnt = this.getWebHelper().getInt("goodCnt");
		String payChannel = this.getWebHelper().getString("payChannel");
		String orderChannel = this.getWebHelper().getString("orderChannel", "0");
		String channel = this.getWebHelper().getString("channel", "0");
		com.rednovo.ace.constant.Constant.payChannel pay = null;
		if (payChannel.equals(Constant.payChannel.ALIPAY.getValue())) {
			pay = Constant.payChannel.ALIPAY;
		} else if (payChannel.equals(Constant.payChannel.WECHAT.getValue())) {
			pay = Constant.payChannel.WECHAT;
		}

		if (pay == null) {
			this.setError("707");
			return;
		}
		GoodInfo good = StaticDataManager.getGoodInfo(goodId);
		BigDecimal fee = good.getRmbPrice().multiply(new BigDecimal(goodCnt));
		String orderId = OrderService.createOrder(payerId, DateUtil.getTimeInMillis(), receiverId, goodId, goodCnt, pay, orderChannel, channel);

		this.setValue("WIDout_trade_no", orderId);
		this.setValue("WIDsubject", good.getName());
		this.setValue("WIDtotal_fee", fee.toString());
		this.setValue("WIDbody", good.getDescription());
	}

	/**
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月8日上午11:24:39
	 */
	private void updateServerlist() {
		String id = this.getWebHelper().getString("id");
		String ip = this.getWebHelper().getString("ip");
		String port = this.getWebHelper().getString("port");
		String status = this.getWebHelper().getString("status");
		String description = this.getWebHelper().getString("description");
		String exeCode = Constant.OperaterStatus.SUCESSED.getValue();
		// 当ip有值时表示为添加或修改操作，当ip无值时则只传了用^拼接的id，为删除操作
		if (!Validator.isEmpty(ip)) {
			exeCode = OrderService.updateServerInfo(id, ip, port, status, description);
		} else {
			String[] ids = id.split("^");
			for (int i = 0; i < ids.length; i++) {
				exeCode = OrderService.delServerInfo(ids[i]);
			}
		}
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月8日上午11:24:36
	 */
	private void getServerlist() {

	}

	/**
	 * 充值明细
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月3日下午10:16:20
	 */
	private void getPayList() {
		String userId = this.getWebHelper().getString("userId");
		String startTime = this.getWebHelper().getString("startTime");
		String endTime = this.getWebHelper().getString("endTime");
		int page = this.getWebHelper().getInt("page");
		int pageSize = this.getWebHelper().getInt("pageSize");
		if (page <= 0) {
			page = 1;
		}
		if (pageSize <= 0) {
			pageSize = 10;
		}
		ArrayList<Order> list = OrderService.getOrderList(userId, startTime, endTime, Constant.OrderStatus.OPENED, page, pageSize);
		if (list == null) {
			this.setError("300");
		} else {
			this.setSuccess();
			this.setValue("payList", list);
		}

	}

	/**
	 * 新建订单
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月4日下午10:09:00
	 */
	private void createOrder() {
		logger.info("进入创建订单业务");
		String payerId = this.getWebHelper().getString("payerId");
		String receiverId = this.getWebHelper().getString("receiverId");
		String goodId = this.getWebHelper().getString("goodId");
		int goodCnt = this.getWebHelper().getInt("goodCnt");
		String payChannel = this.getWebHelper().getString("payChannel");
		String channel = this.getWebHelper().getString("channel", "0");
		com.rednovo.ace.constant.Constant.payChannel pay = null;
		if (payChannel.equals(Constant.payChannel.ALIPAY.getValue())) {
			pay = Constant.payChannel.ALIPAY;
		} else if (payChannel.equals(Constant.payChannel.WECHAT.getValue())) {
			pay = Constant.payChannel.WECHAT;
		}

		if (pay == null) {
			this.setError("707");
			return;
		}

		GoodInfo good = StaticDataManager.getGoodInfo(goodId);
		BigDecimal fee = good.getRmbPrice().multiply(new BigDecimal(goodCnt));
		String orderId = OrderService.createOrder(payerId, DateUtil.getTimeInMillis(), receiverId, goodId, goodCnt, pay, "0", channel);
		if (payChannel.equals(Constant.payChannel.WECHAT.getValue())) {
			String ip = this.getWebHelper().getString("ip");
			if (Validator.isEmpty(ip)) {
				logger.info("缺少参数ip");
				this.setError("709");
				return;
			}

			String randomStr = WXUtil.CreateNoncestr();
			WxData wxData = getPaySendData(good.getDescription(), orderId, orderId, (int) (fee.floatValue() * 100), randomStr, ip);
			String respXml = WXUtil.unifiedOrder(wxData);
			try {
				Map respMap = WXUtil.doXMLParse(respXml);
				String return_code = (String) respMap.get("return_code");
				if (!Validator.isEmpty(return_code) && return_code.toUpperCase().equals("SUCCESS")) {
					String result_code = (String) respMap.get("result_code");
					if (!Validator.isEmpty(result_code) && return_code.toUpperCase().equals("SUCCESS")) {
						SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
						parameters.put("prepayid", respMap.get("prepay_id"));
						parameters.put("appid", wxData.getAppid());
						parameters.put("partnerid", wxData.getMch_id());
						parameters.put("package", WXUtil.PAG);
						parameters.put("noncestr", wxData.getNonce_str());
						parameters.put("timestamp", DateUtil.getCurrDate());
						String sign = WXUtil.createSign(parameters);
						parameters.put("sign", sign);
						this.setValue("order", parameters);
						this.setValue("orderId", orderId);
						this.setSuccess();
						return;
					} else {
						logger.info("更新随机数字和ip错误");
						this.setError("709");
					}
				} else {
					logger.info("远程调用wechat与生成订单失败");
					this.setError("709");
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.setError("709");
				logger.info("数据异常！", e);
			}
			this.setSuccess();
		} else {
			String orderInfo = this.getOrderInfoForAli(orderId, fee.toString(), good.getName(), good.getDescription());
			String returnInfo = orderInfo + "&sign=\"" + sign(orderInfo) + "\"&sign_type=\"RSA\"";
			System.out.println("--------------------------------------------------");
			System.out.println("-------------------创建订单-------------------------");
			System.out.println("-------------------" + returnInfo + "-------------------------");

			System.out.println("--------------------------------------------------");

			if (Validator.isEmpty(orderId)) {
				this.setError("709");
			} else {
				this.setValue("orderId", orderId);
				this.setValue("returnInfo", returnInfo);
				this.setSuccess();
			}
		}
	}

	/**
	 * 开通订单
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月4日下午10:16:44
	 */
	private void openOrder() {
		String orderId = this.getWebHelper().getString("orderId");
		String openUserId = this.getWebHelper().getString("openUserId");
		String exeRes = OrderService.openOrder(orderId, openUserId);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
			this.setSuccess();
		} else {
			this.setError(exeRes);
		}
	}

	/**
	 * 苹果支付回调
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月17日下午7:11:04
	 */
	private void applePayCallBack() {
		String userId = this.getWebHelper().getString("userId");
		String ticket = this.getWebHelper().getString("ticket");
		String version = this.getWebHelper().getString("version");
		String onlieVer = StaticDataManager.getSysConfig("iosVer");
		String channel = this.getWebHelper().getString("channel", "0");

		if (Validator.isEmpty(ticket) || Validator.isEmpty(userId)) {
			this.setError("600");
			return;
		}

		String payUrl = "";
		if (version.compareTo(onlieVer) > 0) {
			payUrl = PPConfiguration.getProperties("cfg.properties").getString("apple.pay.url.debug");
		} else {
			payUrl = PPConfiguration.getProperties("cfg.properties").getString("apple.pay.url.online");
		}
		this.getLog().info("[payUrl]:" + payUrl);

		try {
			// 票据验证
			JSONObject jo = this.verifyApplePay(payUrl, ticket);
			if (jo == null || jo.getInteger("status").intValue() != 0) {
				this.setError("713");
				return;
			}
			JSONObject receipt = jo.getJSONObject("receipt");
			String appId = receipt.getString("bundle_id");
			if (!"com.rednovo.Ace".equals(appId)) {
				logger.info("[OrderHandler][-------票据验证失败----------]");
				this.setError("713");
				return;
			}

			JSONObject good = receipt.getJSONArray("in_app").getJSONObject(0);
			String cnt = good.getString("quantity");
			String transaction_id = good.getString("original_transaction_id");
			logger.info("[OrderHandler][transaction_id:" + transaction_id + "]");

			Order order = OrderService.getOrderWithThirdId(transaction_id);
			if (order != null && OrderStatus.OPENED.getValue().equals(order.getStatus())) {
				logger.info("[OrderHandler][applePayCallBack][发现重复订单]");
				this.setError("702");
				return;
			}
			String orderId = "";
			GoodInfo gi = StaticDataManager.getGoodInfo(good.getString("product_id"));
			// 创建订单
			if (order == null) {
				orderId = OrderService.createOrder(userId, transaction_id, userId, gi.getId(), Integer.parseInt(cnt), payChannel.APPLE_INNEL, "0", channel);
			} else {
				orderId = order.getOrderId();
			}

			if (Constant.OperaterStatus.SUCESSED.getValue().equals(OrderService.openOrder(orderId, userId))) {
				logger.info("[OrderHandler][=======开通订单成功======= ]");
				String balane = UserManager.getBalance(userId);
				BigDecimal coins = gi.getCoinPrice().multiply(new BigDecimal(cnt));
				if (Validator.isEmpty(balane)) {
					balane = "0";
				}
				this.setValue("balance", new BigDecimal(balane).add(coins).longValue());

				this.setSuccess();
				return;
			}

		} catch (Exception ex) {
			this.getLog().error("苹果支付异常", ex);
		}

		this.setError("713");
	}

	/**
	 * 获取商品列表
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月9日上午1:15:59
	 */
	public void getGoodList() {
		List<GoodInfo> list = StaticDataManager.getGoodList();
		Collections.sort(list);
		this.setValue("goodList", list);
		this.setSuccess();
	}

	private String getOrderInfoForAli(String orderId, String fee, String goodName, String goodDes) {
		String partnerId = PPConfiguration.getProperties("cfg.properties").getString("alipay.partenId");
		String sellerId = PPConfiguration.getProperties("cfg.properties").getString("alipay.sellerId");
		String callBackUrl = PPConfiguration.getProperties("cfg.properties").getString("alipay.callback.url");
		String timeOut = "30m";

		StringBuffer sb = new StringBuffer();
		sb.append("partner=" + "\"" + partnerId + "\"").append("&");
		sb.append("seller_id=" + "\"" + sellerId + "\"").append("&");
		sb.append("out_trade_no=" + "\"" + orderId + "\"").append("&");
		sb.append("subject=" + "\"" + goodName + "\"").append("&");
		sb.append("body=" + "\"" + goodDes + "\"").append("&");
		sb.append("total_fee=" + "\"" + fee + "\"").append("&");
		sb.append("notify_url=" + "\"" + callBackUrl + "\"").append("&");
		sb.append("service=" + "\"mobile.securitypay.pay\"").append("&");
		sb.append("payment_type=\"1\"").append("&");
		sb.append("_input_charset=\"utf-8\"").append("&");
		sb.append("it_b_pay=" + "\"" + timeOut + "\"").append("&");
		sb.append("return_url=\"m.alipay.com\"");

		return sb.toString();
	}

	private static String sign(String content) {
		// String privateKey = PPConfiguration.getProperties("cfg.properties").getString("alipay.rsa.private");
		String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANBXEaFI5qPc8xHDd00zRFPs8F7vATCq4qsbRFNF0RNMEP0DYDmRXVwD2gVBKuQVeJhGSOhKx0FSUrh8NrUuxg7Y4TnO8S0E5+bg5RmQr6PJjU9kZxtabBGuwQvQu9v54fDaUcbudSr7DoGsrofNPXQ/MrZkm3/G4IAlypw183ijAgMBAAECgYEAgOnfVtUtIafOH+e7ImHena+27IcnTV3v88BjfsNso2wl9ujn2bdA9XbMqQOx7n/6pv6WjmX29UxjMxRpJaNTmS1Mbam9oTq/gFT62Lnf9Jkgu9zG8hIWHLEbYTm1cQ4EKBBR1H194brejPaaenhJ0pYxqHxm434wSwNSLx3M8YECQQD89nwZoKBdUxtJRvO+NyC6avKllTpMbcWY9t9DtkK4iHHMawqOuKRFZGGLpaxli3Bv9B7XxvBLjs25q1gaMZlbAkEA0tduIIMIoyHhokD77OEp9wK3QzicD5u5E116CnrZbD6KL+2sxdaxE7MQy0ivpuXp865QJqqkww9g/RVnDn/4WQJAMzB14IG+seP1a5iuDln9h3vI6nUOPRUhnVinyX4CdnE2BhXLJyJ6K4iqrKW0A0B6Wk1eSG/7hG67ds0ToQlUbQJBAMoWcxf2gHDcKMi8QLvrla2MjNuBhxPuzpYhIriox31Y9Fq8FL4L6e5X0+EE6leuR2+pxGlLZmEQfIYX3Y+oWQECQFZpFMSLTCI4vXnE+Ob+36xm2tCcH+MdvLLogQp4eA/lZhaj8v1cyQouBPkTZtFSyTJuse8Uz9SBXkBA+Qk3fNQ=";

		String ALGORITHM = "RSA";
		String SIGN_ALGORITHMS = "SHA1WithRSA";
		String DEFAULT_CHARSET = "UTF-8";

		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(RSA_PRIVATE));
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();
			return URLEncoder.encode(Base64.encode(signed), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private JSONObject verifyApplePay(String url, String ticket) throws Exception {
		// this.getLog().info("[url]:" + url + ",[ticket]:" + ticket);
		URL appUrl = new URL(url);

		HttpsURLConnection connection = (HttpsURLConnection) appUrl.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setAllowUserInteraction(false);

		Map<String, String> map = new HashMap<String, String>();
		map.put("receipt-data", ticket);
		String jsonStr = JSON.toJSONString(map);

		PrintStream ps = new PrintStream(connection.getOutputStream());
		ps.print(jsonStr);
		ps.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String str;
		StringBuffer sb = new StringBuffer();
		while ((str = br.readLine()) != null) {
			sb.append(str);
		}
		br.close();

		return JSON.parseObject(sb.toString());

	}
	/*
	 * public static void main(String[] args) { String str =
	 * "orderInfo:partner=\"2088801770882180\"&seller_id=\"yejinqiang@funfun001.com\"&out_trade_no=\"20160310053441740088037790\"&subject=\"5000元充值\"&body=\"1500元充值\"&total_fee=\"1500\"&notify_url=\"http://172.16.150.2/serveice\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&return_url=\"m.alipay.com\"";
	 * System.out.println(OrderHandler.sign(str)); }
	 */

	/**
	 * 微信支付
	 * 
	 * @param goodDesc 商品描述
	 * @param attach 附加数据
	 * @param out_trade_no 订单号
	 * @param spbill_create_ip 移动端ip
	 * @param total_fee 金额
	 * @return
	 */
	private WxData getPaySendData(String goodDesc, String attach, String orderId, int total_fee, String nonceStr, String ip) {
		WxData wxData = new WxData();
		wxData.setAppid(WXUtil.APPID);
		wxData.setMch_id(WXUtil.MCH_ID);
		wxData.setNotify_url(WXUtil.NOTIFY_URL);
		wxData.setNonce_str(nonceStr);
		wxData.setBody(goodDesc);
		wxData.setAttach(attach);
		wxData.setOut_trade_no(orderId);
		wxData.setSpbill_create_ip(ip);
		wxData.setTotal_fee(total_fee);
		return wxData;
	}

	/**
	 * 微信回调接口
	 */
	private void wxCallBack() {
		logger.info("wxchat 回调开始");
		InputStream stream = null;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		String resp = "FAIL";
		try {
			stream = this.getWebHelper().getRequest().getInputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = stream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}

			String respXml = new String(outStream.toByteArray(), "utf-8");
			logger.info("回调接收参数[" + respXml + "]");
			Map respMap = WXUtil.doXMLParse(respXml);
			String return_code = (String) respMap.get("return_code");
			if (!Validator.isEmpty(return_code) && return_code.toUpperCase().equals("SUCCESS")) {
				String result_code = (String) respMap.get("result_code");
				if (!Validator.isEmpty(result_code) && return_code.toUpperCase().equals("SUCCESS")) {
					SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
					for (Object obj : respMap.keySet()) {
						if (!((String) obj).equals("sign")) {
							parameters.put(obj, respMap.get(obj));
						}
					}
					String localSign = WXUtil.createSign(parameters);
					String sign = (String) respMap.get("sign");

					logger.info("wechat 的 sign [" + sign + "]");
					logger.info("内部签名 	的 sign [" + localSign + "]");
					logger.info("内部签名和外部签名[" + localSign.equalsIgnoreCase(sign) + "]");
					if (!localSign.equalsIgnoreCase(sign)) {
						this.writeHtml(WXUtil.setXML(resp, ""));
						return;
					}

					String orderId = (String) respMap.get("attach");
					Order order = OrderService.getOrder(orderId);
					logger.info("当前订单id[" + order.getOrderId() + "];状态[" + OrderStatus.UNPAYED.getValue() + "}");
					if (order.getStatus().equalsIgnoreCase(OrderStatus.UNPAYED.getValue())) {
						String exeRes = OrderService.openOrder(orderId, order.getOpenUserId());
						if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
							resp = "SUCCESS";
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		this.writeHtml(WXUtil.setXML(resp, ""));
	}
}
