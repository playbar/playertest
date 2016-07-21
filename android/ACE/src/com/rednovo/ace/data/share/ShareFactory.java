package com.rednovo.ace.data.share;

import android.app.Activity;
import android.content.Context;

import com.rednovo.ace.common.LiveInfoUtils;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.cell.LiveInfo;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.parser.ShareInfoResult;
import com.rednovo.ace.net.request.RequestCallback;

/**
 * Created by Dk on 16/3/1.
 */
public class ShareFactory {

    public static final int WECHAT_SHARE = 0;
    public static final int WECHAT_FRIEND_SHARE = 1;
    public static final int QQ_SHARE = 2;
    public static final int QQ_ZONE_SHARE = 3;
    public static final int SINA_SHARE = 4;

    public static final String ANCHOR_SHARE = "1";
    public static final String USER_SHARE = "0";

    public static BaseShareApi getShareApi(Activity activity, int type, ShareInfoResult shareInfo) {
        BaseShareApi api = null;
        switch (type) {
            case WECHAT_SHARE:
                api = new WechatShareApi(activity, WECHAT_SHARE, shareInfo);
                break;
            case WECHAT_FRIEND_SHARE:
                api = new WechatShareApi(activity, WECHAT_FRIEND_SHARE, shareInfo);
                break;
            case QQ_SHARE:
                api = new QQShareApi(activity, QQ_SHARE, shareInfo);
                break;
            case QQ_ZONE_SHARE:
                api = new QQShareApi(activity, QQ_ZONE_SHARE, shareInfo);
                break;
            case SINA_SHARE:
                api = new SinaShareApi(activity, shareInfo);
                break;
        }
        return api;
    }

    public static void shareLater(Activity activity, String channel){
        String userId = "-1";
        String showId = "-1";
        if(UserInfoUtils.isAlreadyLogin() && UserInfoUtils.getUserInfo().getUserId() != null){
            userId = UserInfoUtils.getUserInfo().getUserId();
        }
        if(LiveInfoUtils.getShowId() != null){
            showId = LiveInfoUtils.getShowId();
        }
        ReqUserApi.requestShareLater(activity, userId, showId, channel, new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {

            }

            @Override
            public void onRequestFailure(BaseResult error) {

            }
        });
    }
}
