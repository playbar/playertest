package com.rednovo.libs.common;

/**
 * prj-name：ACE
 * fileName：${FILE_NAME}
 * Created by lizhen on Zhen.Li/02/27.
 * Copyright © 2016年 Zhen.Li. All rights reserved.
 *
 */
public class Constant {
    /**
     * hashCode()使用的系数
     */
    public static final int HASHCODE_FACTOR = 31;

    /**
     * 千(十进制)
     */
    public static final int THOUSAND = 1000;

    /**
     * 百(十进制)
     */
    public static final int HUNDRED = 100;

    /**
     * 十(十进制)
     */
    public static final int TEN = 10;

    /**
     * 千(二进制)
     */
    public static final int KILO = 1024;

    /**
     * 空字符串，用于避免字符串为null的场合
     */
    public static final String BLANK_STRING = "";

    /**
     * 临时文件扩展名
     */
    public static final String TMP_EXT = ".tmp";

    /**
     * 一分钟有多少毫秒
     */
    public static final long MILLS_PER_MIN = 60 * 1000;

    /**
     * 一天有多少毫秒
     */
    public static final long MILLS_PER_DAY = 24 * 60 * 60 * 1000;

    /**
     * 一小时有多少毫秒
     */
    public static final long MILLS_PER_HOUR = 60 * 60 * 1000;

    /**
     * 一分钟有多少秒
     */
    public static final int SECONDS_PER_MINUTE = 60;

    /**
     * 一秒有多少毫秒
     */
    public static final int MILLIS_PER_SECOND = 1000;


    /**
     * 对象缓存占用这个APP的内存比例
     */
    public static final float OBJECT_CACHE_MEM_PERCENT = 0.05f;
    /**
     *
     */
    public static final int PIC_WINDOW = 300;

    /**
     * 微信APPID
     */
    public static final String WX_APPID = "wx743d2275c0a223e3";

    /**
     * 微信APPSECRET
     */
    public static final String WX_APPSECRET = "e60d7678239e07261b9873fc1e29eb4a";

    /**
     * QQAPPID
     */
    public static final String QQ_APPID = "1105199076";

    /**
     * QQAPPKEY
     */
    public static final String QQ_APPKEY = "qQMsCo1bkbslvcqa";

    /**
     * 新浪APPID
     */
    public static final String SINA_APPID = "942489697";

    /**
     * 新浪APPSECRET
     */
    public static final String SINA_APPSECRET = "777a3db493c667f4a749e3746c3a319d";

    /**
     * 新浪回调页
     */ //http://www.51weibo.com/mobile/download.html
    public static final String SINA_REDIRECT_URL = "http://www.51weibo.com/mobile/download.html";


    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     *
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     *
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     *
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SINA_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    /**
     * 高德APPID
     */
    public static final String GD_APPID = "";

    /**
     * 支付宝APPID
     */
    public static final String ALIPAY_APPID = "";

    public static final String UMENG_APPKEY = "56dfe508e0f55a51af0024aa";

    /**
     * 表情正则
     */
    public static final String EMOJI_ZZ = "\\[([\u4e00-\u9fa5a-zA-Z0-9])+\\]";


}
