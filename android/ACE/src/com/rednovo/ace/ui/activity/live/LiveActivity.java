package com.rednovo.ace.ui.activity.live;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.common.LiveInfoUtils;
import com.rednovo.ace.core.session.SendUtils;
import com.rednovo.ace.core.session.SessionEngine;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.cell.LiveInfo;
import com.rednovo.ace.data.events.BaseEvent;
import com.rednovo.ace.data.events.KeyBoardEvent;
import com.rednovo.ace.data.events.LivePauseEvent;
import com.rednovo.ace.data.events.ReciveGiftInfo;
import com.rednovo.ace.data.share.ShareFactory;
import com.rednovo.ace.net.api.ReqRoomApi;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.request.HttpEngine;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.net.request.StringCallback;
import com.rednovo.ace.view.dialog.SimpleDialog;
import com.rednovo.ace.widget.gift.SpeciaGiftContainer;
import com.rednovo.ace.widget.live.Cocos2dxAnimationLayout;
import com.rednovo.ace.widget.live.EndLiveView;
import com.rednovo.ace.widget.live.LiveLoadingView;
import com.rednovo.ace.widget.live.LivePauseView;
import com.rednovo.ace.widget.live.LiveView;
import com.rednovo.libs.common.LogUtils;
import com.rednovo.libs.common.ScreenUtils;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StatusBarUtils;
import com.rednovo.libs.common.SubstringUtils;
import com.rednovo.libs.common.Utils;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.widget.IjkVideoView;


public class LiveActivity extends LiveBaseActivtiy implements IMediaPlayer.OnPreparedListener, View.OnClickListener, IMediaPlayer.OnErrorListener, IWeiboHandler.Response, IMediaPlayer.OnInfoListener {
    private static final String LOG_TAG = "LiveActivity";
    /**
     * 直播间(观众版)
     */
    private LiveView lView;
    private IjkVideoView mIjkVideoView;
    /**
     * 键盘的显示状态
     */
    private boolean isKeyBoardVisible;
    private View vParent;
    private LiveInfo liveInfo;
    private EndLiveView liveEndView;
    private SimpleDialog simpleDialog, simpleRetryDialog;
    private LiveLoadingView liveLoadingView;
    private LivePauseView livePause;
    protected boolean isDestory;
    private SpeciaGiftContainer speciaGiftContainer;

    private Cocos2dxAnimationLayout mCocos2dxAnimationLayout;

    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        EventBus.getDefault().register(this);
        super.onCreate(arg0);
        setContentView(R.layout.activity_live);

        initData(getIntent());
        mCocos2dxAnimationLayout = (Cocos2dxAnimationLayout) findViewById(R.id.cocos2d_animation);
        mCocos2dxAnimationLayout.onCreate();
        lView = (LiveView) findViewById(R.id.live_view);
        liveLoadingView = (LiveLoadingView) findViewById(R.id.live_load_view_id);
        livePause = (LivePauseView) findViewById(R.id.live_pause);
        mIjkVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mIjkVideoView.setOnPreparedListener(this);
        mIjkVideoView.setOnErrorListener(this);
        mIjkVideoView.setOnInfoListener(this);
        liveEndView = (EndLiveView) findViewById(R.id.rl_close);
        liveEndView.setEditView(lView.getEditView());
        showLoading();
        ViewTreeObserver vto = mIjkVideoView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (mIjkVideoView != null) {
                    int height = mIjkVideoView.getMeasuredHeight();
                    int width = mIjkVideoView.getMeasuredWidth();
                    mIjkVideoView.setSize(width, height);
                }
                return true;
            }
        });
        ViewGroup.LayoutParams layoutParams = findViewById(R.id.ll_whole).getLayoutParams();
        if (Utils.getAndroidSDKVersion() >= 19) {
            layoutParams.height = ScreenUtils.getScreenHeight(this);
        } else {
            layoutParams.height = ScreenUtils.getScreenHeight(this) - ScreenUtils.getStatusHeight(this);
        }

        vParent = findViewById(R.id.v_parent);
        monitorKeyboardHeight(vParent);
        lView.setIsAnchor(false);
        ReqNGBUrl(liveInfo.getDownStreanUrl());
        //mIjkVideoView.setVideoPath("http://pull.a8.com/live/1461291402790227.flv");
        //mIjkVideoView.start();
        lView.initAnChorData();

        speciaGiftContainer = (SpeciaGiftContainer) findViewById(R.id.specia_gift_container);
        speciaGiftContainer.setVisibility(View.GONE);
    }

    private void initData(Intent intent) {
        if (intent == null) {
            this.finish();
        }
        liveInfo = (LiveInfo) intent.getSerializableExtra(Globle.KEY_LIVE_INFO);
        if (liveInfo == null) {
            this.finish();
        }
        LiveInfoUtils.putShowImg(liveInfo.getShowImg());
        LiveInfoUtils.putShowId(liveInfo.getShowId());
        LiveInfoUtils.putStartId(liveInfo.getStarId());
        LiveInfoUtils.putNickName(liveInfo.getNickName());
        LiveInfoUtils.putProFile(liveInfo.getProfile());
        LiveInfoUtils.putAudienceCnt(liveInfo.getAudienceCnt());

    }

    private void ReqNGBUrl(final String downRmtpUrl) {
        String subUpRtmp = SubstringUtils.substringAfter(downRmtpUrl, "rtmp://");
        ReqRoomApi.reqNGBUrl(subUpRtmp, this, new StringCallback() {

            @Override
            public void onRequestSuccess(String rspRtmpUrl) {
                if (mIjkVideoView != null && rspRtmpUrl != null) {
                    rspRtmpUrl = SubstringUtils.substringBefore(rspRtmpUrl, "\n");
                }
                if (mIjkVideoView != null) {
                    mIjkVideoView.setVideoPath(rspRtmpUrl);
                    mIjkVideoView.start();
                }
            }

            @Override
            public void onRequestFailure(String error) {
                if (mIjkVideoView != null) {
                    mIjkVideoView.setVideoPath(downRmtpUrl);
                    mIjkVideoView.start();
                }
            }

        });
    }

//往数据库中写入浏览记录，V1.0.4已废弃
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (UserInfoUtils.isAlreadyLogin()) {
//            if (!UserInfoUtils.getUserInfo().getUserId().equals(liveInfo.getStarId())) {
//                SqliteHelper.getInstance().insertHistory(liveInfo);
//            }
//        }
//    }

    @Override
    public void setStatusBar() {
        StatusBarUtils.setTranslucentImmersionBar(this);
    }

    /**
     * 监听键盘的弹出和收回
     *
     * @param parentLayout
     */
    private void monitorKeyboardHeight(final View parentLayout) {
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                parentLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = parentLayout.getRootView().getHeight();
                int keyboardHeight = screenHeight - (r.bottom);
                if (keyboardHeight > 100) {
                    if (!isKeyBoardVisible)
                        EventBus.getDefault().post(new KeyBoardEvent(true));
                    isKeyBoardVisible = true;
                } else {
                    if (isKeyBoardVisible)
                        EventBus.getDefault().post(new KeyBoardEvent(false));
                    isKeyBoardVisible = false;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCocos2dxAnimationLayout.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (liveEndView.isVisibility()) {
                return true;
            }
            if (lView.canGoBack()) {
                return true;
            }
            if (simpleDialog == null) {
                simpleDialog = new SimpleDialog(this, simpleDialogBtnClickListener, R.string.text_sure_exit);
            }
            simpleDialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        lView.clearSpecialEffects();
        lView.destroyPariseView();
        if (!isDestory) {
            destoryAll(true);
        }
        HttpEngine.cancelTag(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();

        mCocos2dxAnimationLayout.onDestory();
    }

    private boolean isLiveEnd = true;

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onMainEvent(BaseEvent event) {
        switch (event.id) {
            case Globle.KEY_EVENT_LOGIN_SUCCESS:
            case Globle.KEY_EVENT_SESSION_SUCCESS://Socket连接成功
                if (UserInfoUtils.isAlreadyLogin()) {
                    sendEnterRoom(UserInfoUtils.getUserInfo().getUserId(), liveInfo.getShowId());
                } else {
                    //未登录游客
                    sendEnterRoom("", liveInfo.getShowId());
                }
                break;
            case Globle.KEY_LIVE_FINISH:
                if (isLiveEnd) {
                    isLiveEnd = false;
                    destoryAll(true);
                    requestRelation(liveInfo.getStarId());
                    liveEndView.setVisibility(View.VISIBLE);
                }
                break;
            case Globle.KEY_ONCLICK_LIVE_FINISH:
                destoryAll(true);
                this.finish();
                break;
//            case Globle.KEY_EVENT_SEAL_NUMBER:
            //TODO 账号封停
//                lView.hideDialog();
//                SealNumberEvent sealNum = (SealNumberEvent) event;
//                String txt = "";
//                if (!TextUtils.isEmpty(sealNum.content)) {
//                    txt = sealNum.content;
//                } else {
//                    txt = getString(R.string.freeze_acount_content, "24");
//                }
//                freezeDilaog(getString(R.string.account_freeze), txt);
//                break;
            case Globle.KEY_EVENT_RECEIVE_SPECIAL_GIFT:
//                speciaGiftContainer.addGift((ReciveGiftInfo) event);
                mCocos2dxAnimationLayout.addGift((ReciveGiftInfo) event);
                break;
            case Globle.KEY_EVENT_LIVE_PAUSE:
                LivePauseEvent pause = (LivePauseEvent) event;
                livePause.pause(pause.isPause);
                break;
            default:
                break;
        }
    }

    @Override
    protected void freezeBtnClick() {
        UserInfoUtils.logout();
        destoryAll(true);
        this.finish();
        super.freezeBtnClick();

    }

    @Override
    public void onPrepared(IMediaPlayer mp) {

    }


    private void showLoading() {
        liveLoadingView.startLoading();
    }

    private void hideLoading() {
        liveLoadingView.stopLoading();
        speciaGiftContainer.setVisibility(View.VISIBLE);
    }


    /**
     * 请求是否订阅了主播
     *
     * @param startId
     */
    private void requestRelation(String startId) {
        String userId = "";
        if (UserInfoUtils.isAlreadyLogin()) {
            userId = UserInfoUtils.getUserInfo().getUserId();
        }
        ReqUserApi.requestUserInfo(this, startId, userId, new RequestCallback<UserInfoResult>() {
            @Override
            public void onRequestSuccess(UserInfoResult object) {
                if (object != null) {
                    String relatoin = object.getUser().getExtendData().getRelatoin();
                    if (!TextUtils.isEmpty(relatoin) && relatoin.equals("1")) {
                        liveEndView.setSubscribeState(true);
                    }
                }
            }

            @Override
            public void onRequestFailure(UserInfoResult error) {

            }
        });
    }

    private SimpleDialog.OnSimpleDialogBtnClickListener simpleDialogBtnClickListener = new SimpleDialog.OnSimpleDialogBtnClickListener() {
        @Override
        public void onSimpleDialogLeftBtnClick() {

        }

        @Override
        public void onSimpleDialogRightBtnClick() {
            destoryAll(true);
            LiveActivity.this.finish();
        }
    };

    private void destoryAll(final boolean isCloseSocket) {
        if (simpleDialog != null && simpleDialog.isShowing()) {
            simpleDialog.dismiss();
        }
        if (simpleRetryDialog != null && simpleRetryDialog.isShowing()) {
            simpleRetryDialog.dismiss();
        }
        if (onSingleButtonClickListener != null) {
            onSingleButtonClickListener = null;
        }
        lView.hideDialog();
        LiveInfoUtils.clearLiveInfo();
        if (UserInfoUtils.isAlreadyLogin()) {
            SendUtils.exitRoom(UserInfoUtils.getUserInfo().getUserId(), liveInfo.getShowId());
            UserInfoUtils.requestUserBalance("", UserInfoUtils.getUserInfo().getUserId());
            UserInfoUtils.requestUserInfo("", UserInfoUtils.getUserInfo().getUserId());
        } else {
            SendUtils.exitRoom("", liveInfo.getShowId());
        }
        mIjkVideoView.removeListener();
        new MyThread(isCloseSocket, mIjkVideoView).start();
        isDestory = true;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (speciaGiftContainer.getVisibility() == View.VISIBLE)
            speciaGiftContainer.setGiftContainerVisible(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.v(LOG_TAG, "onStop");
        speciaGiftContainer.setGiftContainerVisible(false);
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        if (liveEndView.getVisibility() == View.VISIBLE || isDestory) {
            return true;
        }
        simpleRetryDialog = new SimpleDialog(LiveActivity.this, new SimpleDialog.OnSimpleDialogBtnClickListener() {
            @Override
            public void onSimpleDialogLeftBtnClick() {
                destoryAll(true);
                LiveActivity.this.finish();
            }

            @Override
            public void onSimpleDialogRightBtnClick() {
                if (!liveLoadingView.isShowLoading()) {
                    showLoading();
                }
                if (mIjkVideoView != null) {
                    mIjkVideoView.stopPlayback();
                }
                ReqNGBUrl(liveInfo.getDownStreanUrl());
            }
        }, R.string.video_load_error, R.string.text_cancel, R.string.retry);
        simpleRetryDialog.setCanceledOnTouchOutside(false);
        simpleRetryDialog.setCancelable(false);
        simpleRetryDialog.show();
        return true;
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        if (baseResponse != null) {
            switch (baseResponse.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    ShareFactory.shareLater(this, "004");
                    ShowUtils.showToast(R.string.share_success);
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    ShowUtils.showToast(R.string.share_cancle);
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    ShowUtils.showToast(R.string.share_fail);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        if (lView != null)
            lView.hideKeyBoard();
        super.onResume();
        mCocos2dxAnimationLayout.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCocos2dxAnimationLayout.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                hideLoading();
                break;
        }
        return false;
    }

    private static class MyThread extends Thread {
        private boolean isCloseSocket = false;
        private IjkVideoView ijkVideoView;

        public MyThread(boolean isCloseSocket, IjkVideoView ijkVideoView) {
            this.isCloseSocket = isCloseSocket;
            this.ijkVideoView = ijkVideoView;
        }

        @Override
        public void run() {
            if (ijkVideoView != null) {
                ijkVideoView.stopPlayback();
                ijkVideoView = null;
            }
            if (isCloseSocket) {
                SystemClock.sleep(300);
                SessionEngine.getSessionEngine().close();
            }
        }
    }
}
