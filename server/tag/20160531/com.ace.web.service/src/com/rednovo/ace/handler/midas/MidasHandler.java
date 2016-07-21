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
package com.rednovo.ace.handler.midas;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ace.database.service.OrderService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qq.open.OpensnsException;
import com.qq.open.SnsSigCheck;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.entity.GoodInfo;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.ace.handler.BasicServiceAdapter;
import com.rednovo.tools.Validator;
import com.rednovo.tools.web.HttpSender;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class MidasHandler extends BasicServiceAdapter {
	private String appKey = "Ed6OWJ7c8rLpktjOaPDjgWj43IFmpHZz";// 沙箱

	// String private appKey="swRzsPf7iNrTqkWIPdpLQJoEZO5rCEU5";//生产

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if ("008-002".equals(key)) {// 米大师订单
			this.createOrder();
		}
		if ("008-003".equals(key)) {// 米大师订单
			this.openOrder();
		}
	}

	/**
	 * 新建订单
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月4日下午10:09:00
	 */
	private void createOrder() {
		String payerId = this.getWebHelper().getString("payerId");
		String receiverId = this.getWebHelper().getString("receiverId");
		String goodId = this.getWebHelper().getString("goodId");
		int goodCnt = this.getWebHelper().getInt("goodCnt");
		String payChannel = this.getWebHelper().getString("payChannel");
		com.rednovo.ace.constant.Constant.payChannel pay = null;
		if (payChannel.equals(Constant.payChannel.ALIPAY.getValue())) {
			pay = Constant.payChannel.ALIPAY;
		} else if (payChannel.equals(Constant.payChannel.WECHAT.getValue())) {
			pay = Constant.payChannel.WECHAT;
		} else if (payChannel.equals(Constant.payChannel.MIDAS.getValue())) {
			pay = Constant.payChannel.MIDAS;
		}

		if (pay == null) {
			this.setError("707");
			return;
		}

		String orderId = OrderService.createOrder(payerId, "", receiverId, goodId, goodCnt, pay);
		if (Validator.isEmpty(orderId)) {
			this.setError("709");
			return;
		}

		String openId = this.getWebHelper().getString("openId");
		String openKey = this.getWebHelper().getString("openKey");
		String payToken = this.getWebHelper().getString("pay_token");
		String sessionId = this.getWebHelper().getString("session_id");
		String session_type = this.getWebHelper().getString("session_type");

		String appId = "1450006670";
		// String appKey = "Ed6OWJ7c8rLpktjOaPDjgWj43IFmpHZz";// 沙箱
		String appKey = "swRzsPf7iNrTqkWIPdpLQJoEZO5rCEU5";// 生产

		String pf = this.getWebHelper().getString("pf");
		String pfKey = this.getWebHelper().getString("pfKey");

		GoodInfo good = StaticDataManager.getGoodInfo(goodId);

		// 填充URL请求参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("openid", openId);
		params.put("openkey", openKey);
		params.put("pay_token", payToken);// 微信登录为空
		params.put("appid", appId);
		params.put("appmode", "1");
		params.put("ts", String.valueOf(System.currentTimeMillis() / 1000));
		params.put("payitem", goodId + "*" + good.getCoinPrice() + "*" + goodCnt);
		params.put("goodsmeta", good.getName() + "*" + good.getDescription());
		params.put("pf", pf);
		params.put("pfkey", pfKey);
		params.put("zoneid", "1");
		params.put("app_metadata", "orderId*" + orderId + "*receiverId*" + receiverId);

		HashMap<String, String> cookie = new HashMap<String, String>();
		cookie.put("session_id", sessionId);
		cookie.put("session_type", session_type);
		cookie.put("org_loc", "/mpay/buy_goods_m");

		try {
			String sig = SnsSigCheck.makeSig("get", "/mpay/buy_goods_m", params, appKey + "&");
			params.put("sig", sig);

			String resp = HttpSender.httpClientGet("http://msdk.qq.com/mpay/buy_goods_m", params, cookie);
			JSONObject jo = JSON.parseObject(resp);
			if ("0".equals(jo.getString("ret"))) {
				String token = jo.getString("token");
				this.setSuccess();
				this.setValue("url_params", jo.getString("url_params"));
				System.out.println("------------------------------订单提交成功-----------------------");
				System.out.println("token==" + token);
				System.out.println("------------------------------token-----------------------");
			} else {
				this.setError(jo.getString("ret"));
			}

		} catch (OpensnsException e1) {
			e1.printStackTrace();
		}

		if (Validator.isEmpty(orderId)) {
			this.setError("709");
		} else {
			this.setValue("orderId", orderId);
			this.setSuccess();
		}
	}

	/**
	 * 开通订单
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月4日下午10:16:44
	 */
	private void openOrder() {

		System.out.println("----------监控到URL的回调-------------------");
		System.out.println("URL:" + this.getWebHelper().getRequest().getRequestURL());;
		System.out.println("URI:" + this.getWebHelper().getRequest().getRequestURI());
		System.out.println("QueryString:" + this.getWebHelper().getRequest().getQueryString());
		System.out.println("----------监控到URL的回调-------------------");

		HashMap<String, String> params = new HashMap<String, String>();
		Map<String, String[]> params_ = this.getWebHelper().getRequest().getParameterMap();

		Iterator<String> keys = params_.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			String val = params_.get(key)[0];
			params.put(key, val);
		}

		String sgin_qq = params.get("sig");
		params.remove("sig");

		String[] backParam = params.get("appmeta").split("\\*");
		String orderId = backParam[1], receiverId = backParam[3];
		System.out.println("[----------------orderId:" + orderId + ",receiverId:" + receiverId + "------------------]");

		try {

			if (SnsSigCheck.verifySig("get", "/service/008-003", params, appKey + "&", sgin_qq)) {
				System.out.println("-----------------------SUCESSED------------------------");
			} else {
				System.out.println("-----------------------FAILED------------------------");
			}
		} catch (OpensnsException e) {
			e.printStackTrace();
		}

		String exeRes = OrderService.openOrder(orderId, receiverId);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeRes)) {
			this.setSuccess();
			this.setValue("ret", 0);
			this.setValue("msg", "OK");
		} else {
			this.setError(exeRes);
		}
	}
}
