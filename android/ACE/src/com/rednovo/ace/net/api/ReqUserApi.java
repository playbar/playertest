/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.net.api;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.parser.BindWechatResult;
import com.rednovo.ace.net.parser.CertifyResult;
import com.rednovo.ace.net.parser.IncomeBalanceResult;
import com.rednovo.ace.net.parser.ShareInfoResult;
import com.rednovo.ace.net.parser.UpdatePortraitResult;
import com.rednovo.ace.net.parser.UserBalanceResult;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.request.HttpBase;
import com.rednovo.ace.net.request.HttpMethodPost;
import com.rednovo.ace.net.request.RequestCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * -- responseType: 1 常规请求,返回文本内容 responseType: 2 流请求,返回文件流 responseType:
 * 用户相关的请求
 *
 * @author Zhen.Li
 * @fileNmae HttpReuestUser
 * @since 2016-03-04
 */
public class ReqUserApi {

    private static final String SERVICE = "service/";

    private static final String KEY_TOKEN_ID = "tokenId";
    private static final String KEY_STAR_ID = "starId";
    private static final String KEY_PASSWD = "passwd";
    private static final String KEY_CHANNEL = "channel";
    private static final String KEY_NICK_NAME = "nickName";
    private static final String KEY_PROFILE = "profile";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_OLDPASSWD = "oldPasswd";
    private static final String KEY_NEWPASSWD = "newPasswd";
    private static final String KEY_NICKNAME = "nickName";
    private static final String KEY_SIGNATURE = "signature";
    private static final String KEY_SEX = "sex";
    private static final String KEY_MOBILE_ID = "mobileId";
    private static final String KEY_WECHAT_ID = "weChatId";
    private static final String KEY_VERIFYCODE = "verifyCode";
    private static final String KEY_COINAMOUT = "coinAmount";
    private static final String KEY_RMBAMOUT = "rmbAmount";
    private static final String KEY_NEWPASSWORD = "newPasswd";
    private static final String KEY_SHOW_ID = "showId";
    private static final String KEY_TYPE = "type";

    private static final String KEY_DEVICE_TYPE = "deviceType";
    private static final String KEY_PROVIDER = "provider";

    /**
     * 请求用户信息
     *
     * @param object
     * @param userId
     * @param callback
     * @return
     */
    public static HttpBase requestUserInfo(Object object, String otherId, String userId, RequestCallback<UserInfoResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_STAR_ID, otherId);
        params.put(KEY_USER_ID, userId);
        return new HttpMethodPost<UserInfoResult>(UserInfoResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_GET_USER_INFO)
                .addArguments(params)
                .addTag(object)
                .execute(callback);
    }

    /**
     * 请求登录
     *
     * @param activity
     * @param userId
     * @param passwd
     * @param type
     * @param nickName
     * @param profile
     * @param callback
     * @return
     */
    public static HttpBase requestLogin(Activity activity, String userId, String passwd, String type, String nickName, String profile, RequestCallback<UserInfoResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_TOKEN_ID, userId);
        params.put(KEY_PASSWD, passwd);
        params.put(KEY_CHANNEL, type);
        params.put(KEY_NICK_NAME, nickName);
        params.put(KEY_PROFILE, profile);
        return new HttpMethodPost<UserInfoResult>(UserInfoResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_LOGIN)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 请求注册
     *
     * @param activity
     * @param phoneNum
     * @param passwd
     * @param callback
     * @return
     */
    public static HttpBase requestRegist(Activity activity, String phoneNum, String passwd, String verifyCode, RequestCallback<UserInfoResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_MOBILE, phoneNum);
        params.put(KEY_PASSWD, passwd);
        params.put(KEY_VERIFYCODE, verifyCode);
        return new HttpMethodPost<UserInfoResult>(UserInfoResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_REGIST)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 请求账户信息
     *
     * @param object
     * @param userId
     * @param callback
     * @return
     */
    public static HttpBase requsetUserBalance(Object object, String userId, RequestCallback<UserBalanceResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        return new HttpMethodPost<UserBalanceResult>(UserBalanceResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_ACCOUNT_BALANCE)
                .addArguments(params)
                .addTag(object)
                .execute(callback);
    }

    /**
     * 请求修改密码
     *
     * @param activity
     * @param userId
     * @param oldPasswd
     * @param newPasswd
     * @param callback
     * @return
     */
    public static HttpBase requestUpdatePasswd(Activity activity, String userId, String oldPasswd, String newPasswd, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_OLDPASSWD, oldPasswd);
        params.put(KEY_NEWPASSWD, newPasswd);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_UPDATE_PASSWD)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 请求更新用户昵称
     *
     * @param activity
     * @param userId
     * @param nickName
     * @param callback
     * @return
     */
    public static HttpBase requestUpdateNickName(Activity activity, String userId, String nickName, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_NICKNAME, nickName);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_UPDATE_NICKNAME)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 请求更新用户签名
     *
     * @param activity
     * @param userId
     * @param signature
     * @param callback
     * @return
     */
    public static HttpBase requestUpdateSignature(Activity activity, String userId, String signature, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_SIGNATURE, signature);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_UPDATE_SIGNATURE)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 请求修改头像
     *
     * @param activity
     * @param userId
     * @param profileFile
     * @param callback
     * @return
     */
    public static HttpBase requestUpdateProfile(Activity activity, String userId, String fileName, String profileFile, RequestCallback<UpdatePortraitResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        HttpBase<UpdatePortraitResult> httpBase = new HttpMethodPost<UpdatePortraitResult>(UpdatePortraitResult.class).addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_UPDATE_PROFILE).addArguments(params).addTag(activity);
        if (!TextUtils.isEmpty(profileFile)) {
            httpBase.connTimeOut(10000);
            httpBase.readTimeOut(10000);
            httpBase.writeTimeOut(10000);
            httpBase.addFile(KEY_PROFILE, fileName, new File(profileFile));
        }

        httpBase.execute(callback);
        return httpBase;
    }

    /**
     * 请求修改性别
     *
     * @param activity
     * @param userId
     * @param sex
     * @param callback
     * @return
     */
    public static HttpBase requestUpdateSex(Activity activity, String userId, String sex, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_SEX, sex);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_UPDATE_SEX)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 获取A豆余额
     *
     * @param activity
     * @param userId
     * @param callback
     * @return
     */
    public static HttpBase requsestIncomeBalance(Activity activity, String userId, RequestCallback<IncomeBalanceResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        return new HttpMethodPost<IncomeBalanceResult>(IncomeBalanceResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_INCOME_BALANCE)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 获取微信绑定状态
     *
     * @param activity
     * @param userId
     * @param callback
     * @return
     */
    public static HttpBase requestBindWechatState(Activity activity, String userId, RequestCallback<BindWechatResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        return new HttpMethodPost<BindWechatResult>(BindWechatResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_GET_BIND_INFO)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 绑定微信账号和手机
     *
     * @param activity
     * @param userId
     * @param phoneNum
     * @param wechatId
     * @param verifycode
     * @param callback
     * @return
     */
    public static HttpBase requestBindExchangeInfo(Activity activity, String userId, String phoneNum, String wechatId, String verifycode, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_MOBILE_ID, phoneNum);
        params.put(KEY_WECHAT_ID, wechatId);
        params.put(KEY_VERIFYCODE, verifycode);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_BIND_EXCHANGE_INFO)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 提现
     *
     * @param activity
     * @param userId
     * @param coinAmount
     * @param rmbAmount
     * @param callback
     * @return
     */
    public static HttpBase requestExchange(Activity activity, String userId, String coinAmount, String rmbAmount, String verifycode, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_COINAMOUT, coinAmount);
        params.put(KEY_RMBAMOUT, rmbAmount);
        params.put(KEY_VERIFYCODE, verifycode);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_EXCHANGE_REQUEST)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 找回密码
     *
     * @param activity
     * @param phoneNum
     * @param verifycode
     * @param newPassword
     * @param callback
     * @return
     */
    public static HttpBase requestResetPassword(Activity activity, String phoneNum, String verifycode, String newPassword, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_MOBILE_ID, phoneNum);
        params.put(KEY_VERIFYCODE, verifycode);
        params.put(KEY_NEWPASSWORD, newPassword);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_UPDATE_RESET_PASSWORD)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 分享完成之后向服务器发送请求
     *
     * @param activity
     * @param userId
     * @param showId
     * @param channel
     * @param callback
     * @return
     */
    public static HttpBase requestShareLater(Activity activity, String userId, String showId, String channel, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_SHOW_ID, showId);
        params.put(KEY_CHANNEL, channel);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_SHARE)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 发送用户设备的RegistrationID，用于极光推送--deviceType 0，安卓；1，ios
     * provider 1极光
     *
     * @param application
     * @param userId
     * @param tokenId     极光ID
     * @param callback
     * @return
     */
    public static HttpBase sendRegistrationID(Application application, String userId, String tokenId, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_TOKEN_ID, tokenId);
        params.put(KEY_DEVICE_TYPE, "0");
        params.put(KEY_PROVIDER, "1");
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_REGISTRATION_ID)
                .addArguments(params)
                .addTag(application)
                .execute(callback);
    }


    /**
     * 实名认证
     *
     * @param activity
     * @param userId
     * @param callback
     * @return
     */
    public static HttpBase certify(Activity activity, String userId, RequestCallback<CertifyResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        return new HttpMethodPost<CertifyResult>(CertifyResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_CERTIFY)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 获取分享有关信息
     *
     * @param activity
     * @param userId
     * @param showId
     * @param callback
     * @return
     */
    public static HttpBase requestShareInfo(Activity activity, String userId, String showId, String type, RequestCallback<ShareInfoResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_SHOW_ID, showId);
        params.put(KEY_TYPE, type);
        return new HttpMethodPost<ShareInfoResult>(ShareInfoResult.class)
                .addUrlArgument(ReqConfig.getUrl() + SERVICE + ReqConfig.KEY_SHARE_INFO)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }
}
