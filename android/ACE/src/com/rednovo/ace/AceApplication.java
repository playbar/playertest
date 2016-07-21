package com.rednovo.ace;

import com.rednovo.ace.common.CacheUserInfoUtils;
import com.rednovo.ace.common.Cocos2dxAnimationAttacher;
import com.rednovo.ace.common.GiftUtils;
import com.rednovo.ace.net.api.ReqSystemApi;
import com.rednovo.ace.net.parser.SystemResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.CacheKey;
import com.rednovo.libs.common.CacheUtils;
import com.rednovo.libs.common.LogUtils;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class AceApplication extends BaseApplication {
    private static AceApplication mApplication;

//    public static RefWatcher getRefWatcher(Context context) {
//        AceApplication application = (AceApplication) context.getApplicationContext();
//        return application.refWatcher;
//    }

    //    private RefWatcher refWatcher;
    private String giftVersion;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        // TODO:
//        refWatcher = LeakCanary.install(this);
        // if (BuildConfig.LOG_DEBUG) {
        LogUtils.mDebuggable = LogUtils.LEVEL_ERROR;
//        } else {
//            LogUtils.mDebuggable = LogUtils.LEVEL_NONE;
//        }
        //极光推送
        JPushInterface.init(this);
        JPushInterface.setDebugMode(BuildConfig.LOG_DEBUG);
        JPushInterface.setLatestNotificationNumber(getApplicationContext(), 3);

        //总控开关
        ReqSystemApi.reqSystemVersion(this, new RequestCallback<SystemResult>() {
            @Override
            public void onRequestSuccess(SystemResult object) {
                giftVersion = object.getGiftVer();
                String bootPic = object.getBootPic();
                String showTips = object.getShowTips();

                CacheUserInfoUtils.putBootPic(bootPic);
                CacheUserInfoUtils.putShowTips(showTips);
                CacheUtils.getObjectCache().add(CacheKey.KEY_SYSTEM, object);
                GiftUtils.init(giftVersion);
            }

            @Override
            public void onRequestFailure(SystemResult error) {

            }
        });
    }

    public static AceApplication getApplication() {
        return mApplication;
    }


}