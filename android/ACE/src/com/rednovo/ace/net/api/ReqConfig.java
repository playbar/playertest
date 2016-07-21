/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.net.api;

import com.rednovo.ace.BuildConfig;

/**
 * @author Zhen.Li
 * @fileNmae RequestUrl
 * @since 2016-03-04
 */
public class ReqConfig {
    private static boolean debug = false;
    /////http://172.16.1.157/
    private static final String TEST_URL = "http://172.16.150.23:8080/";
    private static final String BASE_URL = "http://api.17ace.cn/";
    private static final String NGB_URL = "http://sdkoptedge.chinanetcenter.com/";


    // ------------------用户相关------------------------
    // 获取用户资料
    public static final String KEY_GET_USER_INFO = "001-001";

    // 账户余额
    public static final String KEY_ACCOUNT_BALANCE = "001-002";

    // 等级列表
    public static final String KEY_LEVEL_LIST = "001-003";

    // 充值列表
    public static final String KEY_RECHARGE_LIST = "001-004";

    // 充值明细
    public static final String KEY_RECHARGE_DETAIL = "001-005";

    // 消费明细
    public static final String KEY_CONSUME_BALANCE = "001-006";

    //注册
    public static final String KEY_REGIST = "001-007";

    //登录
    public static final String KEY_LOGIN = "001-013";

    //修改头像
    public static final String KEY_UPDATE_PROFILE = "001-015";

    //修改密码
    public static final String KEY_UPDATE_PASSWD = "001-016";

    //修改昵称
    public static final String KEY_UPDATE_NICKNAME = "001-017";

    //修改签名
    public static final String KEY_UPDATE_SIGNATURE = "001-018";

    //A豆余额
    public static final String KEY_INCOME_BALANCE = "001-019";

    public static final String KEY_SEND_GIF = "001-010";

    //找回密码
    public static final String KEY_UPDATE_RESET_PASSWORD = "001-022";

    //修改性别
    public static final String KEY_UPDATE_SEX = "001-023";

    public static final String KEY_GET_BIND_INFO = "001-024";

    //提现绑定微信、手机
    public static final String KEY_BIND_EXCHANGE_INFO = "001-025";

    //提现申请
    public static final String KEY_EXCHANGE_REQUEST = "001-026";

    //极光推送设备注册ID
    public static final String KEY_REGISTRATION_ID = "001-027";

    //开播实名认证
    public static final String KEY_CERTIFY = "001-043";

    // ------------------房间操作------------------------
    // 开播
    public static final String KEY_BEGIN_LIVE = "002-002";
    // 图像/直播封面
    public static final String KEY_UPLOAD_PHOTO = "002-003";
    // 观众列表
    public static final String KEY_AUDIENCE_LIVE = "002-005";
    // 直播列表
    public static final String KEY_LIVE_LIST = "002-004";
    // 分享
    public static final String KEY_SHARE = "002-011";
    //分享相关的内容
    public static final String KEY_SHARE_INFO = "002-019";
    //禁言
    public static final String KEY_SHUP_UP = "002-018";

    // ------------------关系相关------------------------
    // 订阅
    public final static String KEY_SUBSCRIBE = "003-001";
    // 取消订阅
    public final static String KEY_CANCEL_SUBSCRIBE = "003-006";
    // 举报
    public final static String KEY_REPORT = "003-002";
    // 建议
    public final static String KEY_SUGGEST = "003-003";
    // 粉丝列表
    public final static String KEY_FANS_LIST = "003-004";
    // 订阅列表
    public final static String KEY_SUBSCRIBE_LIST = "003-005";
    // 礼物列表
    public final static String KEY_GIFT_LIST = "001-014";

    // ------------------订单相关--------------------------
    // 新建订单
    public final static String KEY_NEW_ORDER = "001-008";

    // -----------------Banner---------------------------
    public final static String KEY_BANNER = "009-002";
    // -----------------总控版本---------------------------
    public final static String KEY_SYSTEM = "009-001";

    public final static String KEY_SMS = "009-003";

    public final static String KEY_SEARCH = "001-021";

    // 首页订阅列表
    public final static String KEY_HALL_SUBSCRIBE = "001-029";


    public static String getUrl() {
        return (BuildConfig.LOG_DEBUG) ? TEST_URL : BASE_URL;
    }

    public static String getNgbUrl() {
        return NGB_URL;
    }
}
