package com.rednovo.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by lilong on 16/3/8.
 */
public class CreateIAlixPayOrder {


    private static final String ALGORITHM = "RSA";

    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    private static final String DEFAULT_CHARSET = "UTF-8";

    /************************公司支付宝相关信息**************************/
    // 商户PID              **公司支付宝平台PID**
    public static final String PARTNER = "2088801770882180";
    // 商户收款账号          **公司支付宝账户**
    public static final String SELLER = "yejinqiang@funfun001.com";
    // 商户私钥，pkcs8格式   **公司私钥**
    public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANBXEaFI5qPc8xHDd00zRFPs8F7vATCq4qsbRFNF0RNMEP0DYDmRXVwD2gVBKuQVeJhGSOhKx0FSUrh8NrUuxg7Y4TnO8S0E5+bg5RmQr6PJjU9kZxtabBGuwQvQu9v54fDaUcbudSr7DoGsrofNPXQ/MrZkm3/G4IAlypw183ijAgMBAAECgYEAgOnfVtUtIafOH+e7ImHena+27IcnTV3v88BjfsNso2wl9ujn2bdA9XbMqQOx7n/6pv6WjmX29UxjMxRpJaNTmS1Mbam9oTq/gFT62Lnf9Jkgu9zG8hIWHLEbYTm1cQ4EKBBR1H194brejPaaenhJ0pYxqHxm434wSwNSLx3M8YECQQD89nwZoKBdUxtJRvO+NyC6avKllTpMbcWY9t9DtkK4iHHMawqOuKRFZGGLpaxli3Bv9B7XxvBLjs25q1gaMZlbAkEA0tduIIMIoyHhokD77OEp9wK3QzicD5u5E116CnrZbD6KL+2sxdaxE7MQy0ivpuXp865QJqqkww9g/RVnDn/4WQJAMzB14IG+seP1a5iuDln9h3vI6nUOPRUhnVinyX4CdnE2BhXLJyJ6K4iqrKW0A0B6Wk1eSG/7hG67ds0ToQlUbQJBAMoWcxf2gHDcKMi8QLvrla2MjNuBhxPuzpYhIriox31Y9Fq8FL4L6e5X0+EE6leuR2+pxGlLZmEQfIYX3Y+oWQECQFZpFMSLTCI4vXnE+Ob+36xm2tCcH+MdvLLogQp4eA/lZhaj8v1cyQouBPkTZtFSyTJuse8Uz9SBXkBA+Qk3fNQ=";
    /*******************************************************************/

    /**
     * 获取签名后订单信息
     * @param productame    商品名称
     * @param productDesc   商品详情描述
     * @param productPrice  商品总价
     * @return
     */
    public String getSignOrderStr(String productame, String productDesc, String productPrice){
        String orderInfo = getOrderInfo(productame, productDesc, productPrice);

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        return payInfo;
    }

    /**
     * create the order info. 创建订单信息
     *
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径   // ll 自己服务器配置这个URL
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * 签名
     * @param content
     * @return
     */
    public static String sign(String content) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.decode(RSA_PRIVATE));
            KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));

            byte[] signed = signature.sign();

            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
    
    public static void main(String[] args) {
		sign("你是我的效果你是我的效果你是我的效果你是我的效果你是我的效果你是我的效果");
	}
}
