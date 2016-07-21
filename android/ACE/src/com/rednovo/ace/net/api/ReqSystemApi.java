package com.rednovo.ace.net.api;

import android.app.Activity;
import android.app.Application;

import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.parser.GoodListResult;
import com.rednovo.ace.net.parser.SystemResult;
import com.rednovo.ace.net.parser.UserBalanceResult;
import com.rednovo.ace.net.request.HttpBase;
import com.rednovo.ace.net.request.HttpMethodPost;
import com.rednovo.ace.net.request.RequestCallback;

/**
 * 系统相关,总控版本
 */
public class ReqSystemApi {
    private static String URL_SUFFIX = "service/";

    private static String KEY_MOBILE_NO = "mobileNo";

    /**
     * 总控版本信息
     * @param application
     * @param callback
     * @return
     */
    public static HttpBase reqSystemVersion(Application application, RequestCallback<SystemResult> callback) {
        return new HttpMethodPost<SystemResult>(SystemResult.class)
                .addUrlArgument(ReqConfig.getUrl() + URL_SUFFIX + ReqConfig.KEY_SYSTEM)
                .addTag(application)
                .execute(callback);
    }

    /**
     * 发送短信验证码
     * @param activity
     * @param phoneNum
     * @param callback
     * @return
     */
    public static HttpBase reqSendSms(Activity activity, String phoneNum, RequestCallback<BaseResult> callback){
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + URL_SUFFIX + ReqConfig.KEY_SMS)
                .addArguments(KEY_MOBILE_NO, phoneNum)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 请求商品列表
     * @param activity
     * @param callback
     * @return
     */
    public static HttpBase reqGoodList(Activity activity, RequestCallback<GoodListResult> callback){
        return new HttpMethodPost<GoodListResult>(GoodListResult.class)
                .addUrlArgument(ReqConfig.getUrl() + URL_SUFFIX + ReqConfig.KEY_RECHARGE_LIST)
                .addTag(activity)
                .execute(callback);
    }
}
