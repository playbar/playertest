package com.rednovo.ace.widget.live;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.rednovo.ace.AceApplication;
import com.rednovo.ace.R;
import com.rednovo.ace.common.LiveInfoUtils;
import com.rednovo.ace.common.WebViewConfig;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.share.BaseShareApi;
import com.rednovo.ace.data.share.ShareFactory;
import com.rednovo.ace.data.share.SinaShareApi;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.ShareInfoResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.widget.dialog.BaseDialog;

/**
 * 分享直播对话框
 */
public class ShareDialog extends BaseDialog implements View.OnClickListener, BaseShareApi.ShareResultCallback {

    private static final String WECHAT_SHARE_CHANNEL = "1";

    private static final String QQ_SHARE_CHANNEL = "2";

    private static final String SINA_SHARE_CHANNEL = "5";


    private static ShareDialog mInstance;
    private View rlWeixin, rlFriends, rlQQ, rlQQZone, rlSina;

    private Activity activity;

    private BaseShareApi shareApi;
    private boolean shareState;
    private ShareInfoResult shareInfo;
    private String shareChannel = "";

    public static ShareDialog getShareDialog(Activity activity, int resId) {
        if (mInstance == null) {
            mInstance = new ShareDialog(activity, resId);
        }
        return mInstance;
    }

    private ShareDialog(Activity activity, int resId) {
        super(activity, R.layout.dialog_live_share, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        this.activity = activity;
        this.getWindow().setWindowAnimations(R.style.dialogBottomWindowAnim);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.dimAmount = 0f;
        getWindow().setAttributes(params);
        rlWeixin = findViewById(R.id.rl_weixin);
        rlFriends = findViewById(R.id.rl_friends);
        rlQQ = findViewById(R.id.rl_qq);
        rlQQZone = findViewById(R.id.rl_qqzone);
        rlSina = findViewById(R.id.rl_sina);
        rlWeixin.setOnClickListener(this);
        rlFriends.setOnClickListener(this);
        rlQQ.setOnClickListener(this);
        rlQQZone.setOnClickListener(this);
        rlSina.setOnClickListener(this);
        mInstance = this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //TODO
            case R.id.rl_weixin:
                shareChannel = WECHAT_SHARE_CHANNEL;
                shareApi = ShareFactory.getShareApi(activity, ShareFactory.WECHAT_SHARE, shareInfo);
                break;
            case R.id.rl_friends:
                shareChannel = WECHAT_SHARE_CHANNEL;
                shareApi = ShareFactory.getShareApi(activity, ShareFactory.WECHAT_FRIEND_SHARE, shareInfo);
                break;
            case R.id.rl_qq:
                shareChannel = QQ_SHARE_CHANNEL;
                shareApi = ShareFactory.getShareApi(activity, ShareFactory.QQ_SHARE, shareInfo);
                break;
            case R.id.rl_qqzone:
                shareChannel = QQ_SHARE_CHANNEL;
                shareApi = ShareFactory.getShareApi(activity, ShareFactory.QQ_ZONE_SHARE, shareInfo);
                break;
            case R.id.rl_sina:
                shareChannel = SINA_SHARE_CHANNEL;
                shareApi = new SinaShareApi(activity, shareInfo);
                break;
            default:
                break;
        }
        shareApi.setShareResultCallback(this);
        shareApi.doShare();

        dismiss();
    }

    public void setShare(boolean state) {
        this.shareState = state;
    }

    @Override
    public void show() {
        super.show();
        shareInfo = new ShareInfoResult();
//        String shareUserId = "";
//        if(UserInfoUtils.isAlreadyLogin()){
//            shareUserId = UserInfoUtils.getUserInfo().getUserId();
//        }else{
//            shareUserId = "-1";
//        }
        shareInfo.setUrl(WebViewConfig.getShareUrl(LiveInfoUtils.getShowId()));
        if (LiveInfoUtils.getNickName() != null) {
            shareInfo.setTitle(getContext().getString(R.string.share_title, LiveInfoUtils.getNickName()));
        } else {
            shareInfo.setTitle(getContext().getString(R.string.share_title, "Ace主播"));
        }
        shareInfo.setSumy(getContext().getString(R.string.share_body));
        String showImg = LiveInfoUtils.getShowImg();
        if (!TextUtils.isEmpty(showImg)) {
            shareInfo.setImgSrc(showImg);
        } else {
            try {
                Bitmap bitmap = BitmapFactory.decodeResource(AceApplication.getApplication().getResources(), R.drawable.ic_launcher);
                shareInfo.image = bitmap;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        shareInfo.appName = getContext().getString(R.string.app_name);
        getShareInfo();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mInstance = null;
    }

    @Override
    public void shareComplet() {
        ShareFactory.shareLater(activity, shareChannel);
        ShowUtils.showToast(R.string.share_success);
    }

    @Override
    public void shareError() {
        ShowUtils.showToast(R.string.share_fail);
    }

    @Override
    public void shareCancle() {
        ShowUtils.showToast(R.string.share_cancle);
    }

    private void getShareInfo() {
        String shareUserId = "";
        String shareShowId = LiveInfoUtils.getShowId();
        if (UserInfoUtils.isAlreadyLogin()) {
            shareUserId = UserInfoUtils.getUserInfo().getUserId();
        } else {
            shareUserId = "-1";
        }
        ReqUserApi.requestShareInfo(activity, shareUserId, shareShowId, ShareFactory.USER_SHARE, new RequestCallback<ShareInfoResult>() {
            @Override
            public void onRequestSuccess(ShareInfoResult object) {
                if (!StringUtils.isEmpty(object.getTitle())) {
                    shareInfo.setTitle(object.getTitle());
                }
                if (!StringUtils.isEmpty(object.getSumy())) {
                    shareInfo.setSumy(object.getSumy());
                }
                if (!StringUtils.isEmpty(object.getImgSrc())) {
                    shareInfo.setImgSrc(object.getImgSrc());
                }
                if (!StringUtils.isEmpty(object.getUrl())) {
                    shareInfo.setUrl(object.getUrl());
                }
            }

            @Override
            public void onRequestFailure(ShareInfoResult error) {

            }
        });
    }
}
