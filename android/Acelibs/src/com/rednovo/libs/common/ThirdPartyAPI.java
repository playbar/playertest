package com.rednovo.libs.common;

import android.content.Context;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

/**
 * 第三方API初始化及常用方法
 * Created by Dk on 16/3/5.
 */
public class ThirdPartyAPI {

    /**
     * 腾讯API对象
     */
    public static Tencent mTencent;

    /**
     * 微信API对象
     */
    public static IWXAPI mIWXAPI;

    /**
     * 新浪API对象
     */
    public static IWeiboShareAPI mWeiboShareAPI;

    /**
     * 低于此版本的微博sdk不可多条分享
     */
    private static final int SINA_ONLY_SINGLE_SHARE_SUPPORT = 10351;

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        mTencent = Tencent.createInstance(Constant.QQ_APPID, context);

        mIWXAPI = WXAPIFactory.createWXAPI(context, Constant.WX_APPID, true);
        mIWXAPI.registerApp(Constant.WX_APPID);

        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, Constant.SINA_APPID);
        mWeiboShareAPI.registerApp();
    }

    public static boolean isQQSessionValid(){
        return mTencent.isSessionValid();
    }

    /**
     * 微信是否安装
     *
     * @return true 安装  false 未安装
     */
    public static boolean isWXAppInstalled() {
        return mIWXAPI.isWXAppInstalled();
    }

    /**
     * 检测微信版本
     *
     * @return true 可使用的版本  false 不可使用的版本
     */
    public static boolean isWXAppSupportAPI() {
        return mIWXAPI.isWXAppSupportAPI();
    }

    /**
     * 微博是否安装
     *
     * @return true 安装  false 未安装
     */
    public static boolean isWeiboAppInstalled() {
        return mWeiboShareAPI.isWeiboAppInstalled();
    }

    /**
     * 检测微博版本
     *
     * @return true 可使用的版本  false 不可使用的版本
     */
    public static boolean isWeiboAppSupportAPI() {
        return mWeiboShareAPI.isWeiboAppSupportAPI();
    }

    /**
     * 微博是否支持多条分享
     *
     * @return
     */
    public static boolean isMultipleShare() {
        int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
        return supportApi > SINA_ONLY_SINGLE_SHARE_SUPPORT || supportApi == SINA_ONLY_SINGLE_SHARE_SUPPORT;
    }
}
