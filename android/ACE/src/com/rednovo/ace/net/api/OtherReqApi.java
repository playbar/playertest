package com.rednovo.ace.net.api;

import com.rednovo.ace.net.parser.QQInfoResult;
import com.rednovo.ace.net.parser.SinaInfoResult;
import com.rednovo.ace.net.parser.WechatInfoResult;
import com.rednovo.ace.net.parser.WechatTokenResult;
import com.rednovo.ace.net.request.OtherHttpMethodGet;
import com.rednovo.ace.net.request.OtherRequestCallback;

/**
 * Created by Dk on 16/3/18.
 */
public class OtherReqApi {
    /**
     * 请求qq用户的基本信息URL
     */
    private static final String QQ_INFO_URL = "https://graph.qq.com/user/get_simple_userinfo";
    /**
     * 请求微信用户的token等信息URL
     */
    private static final String WECHAT_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    /**
     * 请求微信用户的基本信息URL
     */
    private static final String WECHAT_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";

    /**
     * 请求新浪用户的基本信息URL
     */
    private static final String SINA_INFO_URL = "https://api.weibo.com/2/users/show.json";

    private static final String KEY_ACCOUNT_TOKEN = "access_token";
    private static final String KEY_OAUTH_CONSUMER = "oauth_consumer_key";
    private static final String KEY_OPNEID = "openid";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_APPID = "appid";
    private static final String KEY_SECRET = "secret";
    private static final String KEY_CODE = "code";
    private static final String KEY_GRANT_TYPE = "grant_type";
    private static final String KEY_LONG = "lang";
    private static final String KEY_UID = "uid";

    /**
     * 请求QQ用户基本信息
     *
     * @param account_token
     * @param oauth_consumer_key
     * @param openid
     * @param requestCallback
     * @return
     */
    public static final OtherHttpMethodGet requestQQInfo(String account_token, String oauth_consumer_key, String openid, OtherRequestCallback requestCallback) {
        return new OtherHttpMethodGet(QQInfoResult.class)
                .addUrlArgument(QQ_INFO_URL)
                .addArguments(KEY_ACCOUNT_TOKEN, account_token)
                .addArguments(KEY_OAUTH_CONSUMER, oauth_consumer_key)
                .addArguments(KEY_OPNEID, openid)
                .addArguments(KEY_FORMAT, "json")
                .execute(requestCallback);
    }

    /**
     * 请求微信用户Token
     *
     * @param appId
     * @param secret
     * @param code
     * @param requestCallback
     * @return
     */
    public static final OtherHttpMethodGet reuqestWechatToken(String appId, String secret, String code, OtherRequestCallback requestCallback) {
        return new OtherHttpMethodGet(WechatTokenResult.class)
                .addUrlArgument(WECHAT_TOKEN_URL)
                .addArguments(KEY_APPID, appId)
                .addArguments(KEY_SECRET, secret)
                .addArguments(KEY_CODE, code)
                .addArguments(KEY_GRANT_TYPE, "authorization_code")
                .execute(requestCallback);
    }

    /**
     * 请求微信用户基本信息
     *
     * @param account_token
     * @param openId
     * @param requestCallback
     * @return
     */
    public static final OtherHttpMethodGet reuqestWechatInfo(String account_token, String openId, OtherRequestCallback requestCallback) {
        return new OtherHttpMethodGet(WechatInfoResult.class)
                .addUrlArgument(WECHAT_INFO_URL)
                .addArguments(KEY_ACCOUNT_TOKEN, account_token)
                .addArguments(KEY_OPNEID, openId)
                .addArguments(KEY_LONG, "zh_CN")
                .execute(requestCallback);
    }

    /**
     * 请求新浪用户基本信息
     * @param account_token
     * @param uid
     * @param requestCallback
     * @return
     */
    public static final OtherHttpMethodGet requestSinaInfo(String account_token, String uid, OtherRequestCallback requestCallback){
        return new OtherHttpMethodGet(SinaInfoResult.class)
                .addUrlArgument(SINA_INFO_URL)
                .addArguments(KEY_ACCOUNT_TOKEN, account_token)
                .addArguments(KEY_UID, uid)
                .execute(requestCallback);
    }

}
