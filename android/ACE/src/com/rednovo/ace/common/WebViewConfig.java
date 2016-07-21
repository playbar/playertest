package com.rednovo.ace.common;

import android.text.TextUtils;

import com.rednovo.ace.BuildConfig;

/**
 * Created by Dk on 16/3/14.
 */
public class WebViewConfig {

    private static boolean debug = false;

    private static final String TEST_URL = "http://172.16.150.23:8080/";
    private static final String FORMAL_URL = "http://api.17ace.cn/";

    //消费记录
    public static final String CONSUMPTIONRECORDS = "app/consume.jsp?userId=";

    //充值记录
    public static final String RECHARGERECORDS = "app/recharge.jsp?userId=";

    //提现记录
    public static final String WITHDRAWALSRECOEDS = "app/cash_record.jsp?userId=";

    //提现详细文档
    public static final String WITHDRAWALSDOC = "app/cash_doc.jsp";

    //用户服务条款
    public static final String USER_PROTOCOL = "app/slas.html";

    //分享页面url
    public static final String SHARE_URL_PAGE = "http://api.17ace.cn/share/index.html?";

    public static String getUrl() {
        return (BuildConfig.LOG_DEBUG) ? TEST_URL : FORMAL_URL;
    }

    public static String getShareUrl(String showId) {
        return SHARE_URL_PAGE + "&showId=" + showId;
    }

//    public static String getprofile(String profile) {
//        String str = "";
//        if (TextUtils.isEmpty(profile)) {
//            str = "ace";
//        } else {
//            try {
//                int index = profile.lastIndexOf("/") + 1;
//                str = profile.substring(index, profile.length() - 4);
//            } catch (Throwable ex) {
//                str = "ace";
//            }
//
//        }
//        return str;
//    }
}
