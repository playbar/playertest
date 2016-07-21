package com.rednovo.ace.ui.activity.live;

import android.content.Intent;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;

import com.rednovo.ace.AceApplication;
import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.common.LiveInfoUtils;
import com.rednovo.ace.common.ScreenListener;
import com.rednovo.ace.core.session.SessionEngine;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.events.BaseEvent;
import com.rednovo.ace.data.events.KeyBoardEvent;
import com.rednovo.ace.data.events.ReciveGiftInfo;
import com.rednovo.ace.net.request.HttpEngine;
import com.rednovo.ace.view.dialog.SimpleDialog;
import com.rednovo.ace.view.dialog.SingleOptionDialog;
import com.rednovo.ace.widget.gift.SpeciaGiftContainer;
import com.rednovo.ace.widget.live.CloseLiveView;
import com.rednovo.ace.widget.live.Cocos2dxAnimationLayout;
import com.rednovo.ace.widget.live.EnterRoomAnimView;
import com.rednovo.ace.widget.live.LiveView;
import com.rednovo.libs.common.LogUtils;
import com.rednovo.libs.common.ScreenUtils;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StatusBarUtils;
import com.rednovo.libs.common.Utils;
import com.seu.magicfilter.camera.CameraManager;
import com.seu.magicfilter.displayGL30.ALVideoEngine;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * 直播间(主播版)
 */
public class LiveRecordActivity extends LiveBaseActivtiy implements View.OnClickListener, CameraManager.CameraReadyListener {

    private LiveView lView;
    /**
     * 键盘的显示状态
     */
    private static final String LOG_TAG = "LiveRecordActivity";

    private View vParent;
    private GLSurfaceView glSurfaceView;
    private EnterRoomAnimView mEnterRoomAnimView;
    private CloseLiveView liveEndView;
    private SimpleDialog simpleDialog = null;
    private Cocos2dxAnimationLayout mCocos2dxAnimationLayout;

    private boolean isKeyBoardVisible;
    private boolean bBackground;
    private int camerafacing;
    private String rtmpUrl;
    private AceApplication aceApplication;
    private String userId;
    protected boolean isDestory;
    private int screenWidth;
    private SingleOptionDialog singleOptionDialog;
    private int nfreezeType = -1;
    private static final int TYPE_FREEZE_ACCOUNT = 1;
    private static final int TYPE_FREEZE_LIVE = 2;
    private SpeciaGiftContainer speciaGiftContainer;
    private ALVideoEngine mALVideoEngine = null;
    private boolean isBeauty;
    private long lastClickTime;


    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        EventBus.getDefault().register(this);
        super.onCreate(arg0);
        setContentView(R.layout.activity_live_record);
        initData();
        glSurfaceView = (GLSurfaceView) findViewById(R.id.recoder_surfaceview_id);
        mEnterRoomAnimView = (EnterRoomAnimView) findViewById(R.id.rl_room_anim_view_id);
        liveEndView = (CloseLiveView) findViewById(R.id.live_end_id);
        lView = (LiveView) findViewById(R.id.live_view);
        vParent = findViewById(R.id.v_parent);
        monitorKeyboardHeight(vParent);
        lView.setIsAnchor(true);
        lView.setOnClickListener(this);

        mALVideoEngine = new ALVideoEngine(this, glSurfaceView, rtmpUrl);

        mEnterRoomAnimView.startAnimView();
        setFlashState(camerafacing);
        mScreenListener.begin(new ScreenState());
        lView.initAnChorData();

        mCocos2dxAnimationLayout = (Cocos2dxAnimationLayout) findViewById(R.id.cocos2d_animation);
        mCocos2dxAnimationLayout.onCreate();

        ViewGroup.LayoutParams layoutParams = findViewById(R.id.ll_whole).getLayoutParams();
        if (Utils.getAndroidSDKVersion() >= 19) {
            layoutParams.height = ScreenUtils.getScreenHeight(this);
        } else {
            layoutParams.height = ScreenUtils.getScreenHeight(this) - ScreenUtils.getStatusHeight(this);
        }
        screenWidth = ScreenUtils.getScreenWidth(this);

        speciaGiftContainer = (SpeciaGiftContainer) findViewById(R.id.specia_gift_container);

        mALVideoEngine.startVideo();

        mALVideoEngine.setCameraReadyListener(this);
        LiveInfoUtils.putIsShow(true);
    }

    private void initData() {
        userId = UserInfoUtils.getUserInfo().getUserId();
        aceApplication = AceApplication.getApplication();
        camerafacing = getIntent().getIntExtra(Globle.KEY_FACING, 1);
        rtmpUrl = getIntent().getStringExtra(Globle.KEY_UP_STREAM_URL);
    }


    @Override
    protected void setStatusBar() {
        StatusBarUtils.setTranslucentImmersionBar(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                if (x > screenWidth / 10 && x < screenWidth * 9 / 10 && !liveEndView.isVisible() && mALVideoEngine != null)
                    mALVideoEngine.onFocusOnTouch(event);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (liveEndView.isVisible())
                return true;
            if (lView.canGoBack())
                return true;
            if (simpleDialog == null) {
                simpleDialog = new SimpleDialog(this, simpleDialogBtnClickListener, R.string.text_sure_exit);
            }
            simpleDialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Subscribe(threadMode = ThreadMode.MainThread)//在ui线程执行
    public void onMainThreadEvent(BaseEvent event) {
        switch (event.id) {
            case Globle.KEY_EVENT_PHONE_STATE://监听来电广播
                if (!liveEndView.isVisible()) {
                    destoryAll(false, true);
                }
                break;
            case Globle.KEY_EVENT_SESSION_SUCCESS://Socket连接成功
                sendEnterRoom(userId, userId);
                LogUtils.v(LOG_TAG, "sendEnterRoom");
                break;
            case Globle.KEY_ONCLICK_LIVE_FINISH:
                LogUtils.v(LOG_TAG, "KEY_ONCLICK_LIVE_FINISH");
                if (!liveEndView.isVisible()) {
//                    destoryAll(false);
                    if (simpleDialog == null) {
                        simpleDialog = new SimpleDialog(this, simpleDialogBtnClickListener, R.string.text_sure_exit);
                    }
                    simpleDialog.show();
                }
                break;
//            case Globle.KEY_EVENT_LIVE_BAN:
//                //TODO 直播封停
//                lView.hideDialog();
//                nfreezeType = TYPE_FREEZE_LIVE;
//                destoryViedo();
//                ForBidEvent forbid = (ForBidEvent) event;
//                String content = "";
//                if(!TextUtils.isEmpty(forbid.content)){
//                    content = forbid.content;
//                }else{
//                    content = getString(R.string.live_freeze_content,"24");
//                }
//                freezeDilaog(getString(R.string.live_freeze), content);
//                break;
//            case Globle.KEY_EVENT_SEAL_NUMBER:
//                lView.hideDialog();
//                //TODO 账号封停
//                destoryViedo();
//                nfreezeType = TYPE_FREEZE_ACCOUNT;
//                SealNumberEvent sealNum = (SealNumberEvent) event;
//                String txt = "";
//                if(!TextUtils.isEmpty(sealNum.content)){
//                    txt = sealNum.content;
//                }else{
//                    txt = getString(R.string.freeze_acount_content,"24");
//                }
//                freezeDilaog(getString(R.string.account_freeze),txt);
//                break;
            case Globle.KEY_EVENT_RECEIVE_SPECIAL_GIFT:
                speciaGiftContainer.addGift((ReciveGiftInfo) event);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCocos2dxAnimationLayout.onResume();
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        if (bBackground && !aceApplication.isBackground()) {
            //程序回到前台
            LogUtils.v(LOG_TAG, "onRestart");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCocos2dxAnimationLayout.onPause();
        bBackground = aceApplication.isBackground();
        if (bBackground) {
            //程序退到后台
            LogUtils.v(LOG_TAG, "onStop");
            if (simpleDialog != null && simpleDialog.isShowing()) {
                simpleDialog.dismiss();
            }
            if (!liveEndView.isVisible()) {
                destoryAll(false, true);
            }


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCocos2dxAnimationLayout.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    protected void onDestroy() {
        LiveInfoUtils.putIsShow(false);
        mCocos2dxAnimationLayout.onDestory();
        if (!isDestory) {
            destoryAll(true, false);
        }
        LogUtils.v(LOG_TAG, "onDestroy");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void freezeBtnClick() {
        super.freezeBtnClick();
        if (nfreezeType == TYPE_FREEZE_ACCOUNT) {
            UserInfoUtils.logout();
        }
        destoryAll(true, false);
        LiveRecordActivity.this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_voice:// 设置静麦
                if (multiClick()){
                    //防止CheckBox点击事件被屏蔽而自身的选中状态却已经改变
                    CheckBox voice = lView.getCheckBox(R.id.cb_voice);
                    if(voice != null)
                        voice.setChecked(!voice.isChecked());
                    return;
                }
                if (mALVideoEngine != null) {
                    mALVideoEngine.setMute(lView.isMuteChecked());
                }
                break;
            case R.id.iv_camera:// 切换摄像头
                if (multiClick()) return;
                if (mALVideoEngine != null && mALVideoEngine.getCamraNum() > 1) {
                    mALVideoEngine.switchCamera();
                    setFlashState(mALVideoEngine.getCamerFacing());
                    camerafacing = mALVideoEngine.getCamerFacing();
                }
                break;
            case R.id.cb_flicker:// 切换闪光灯
                if (multiClick()){
                    CheckBox flicker = lView.getCheckBox(R.id.cb_flicker);
                    if(flicker != null)
                        flicker.setChecked(!flicker.isChecked());
                    return;
                }
                if (mALVideoEngine != null) {
                    mALVideoEngine.switchFlash();
                }
                break;
            case R.id.cb_skincare:// 美颜开关
                if (multiClick()){
                    CheckBox skincare = lView.getCheckBox(R.id.cb_skincare);
                    if(skincare != null)
                        skincare.setChecked(!skincare.isChecked());
                    return;
                }
                //TODO 美颜开关
                if (isBeauty) {
                    isBeauty = false;
                } else {
                    isBeauty = true;
                }
                mALVideoEngine.openBeauty(isBeauty);
                break;
            default:
                break;
        }

    }

    /**
     * 设置闪光灯按钮状态
     */
    private void setFlashState(int camerafacing) {
        if (camerafacing == 0) {
            lView.setFlashswitch(true);
        } else {
            lView.setFlashswitch(false);
        }
    }

    @Override
    public void onCameraFailed() {
        LogUtils.v(LOG_TAG, "onCameraFailed");
        ShowUtils.showToast(R.string.text_check_permissions);
        this.finish();
    }


    private class ScreenState implements ScreenListener.ScreenStateListener {

        @Override
        public void onScreenOn() {// 屏幕点亮
            Log.v(LOG_TAG, "onScreenOn");
        }

        @Override
        public void onScreenOff() {//屏幕关闭
            Log.v(LOG_TAG, "onScreenOff");
            if (!liveEndView.isVisible()) {
                destoryAll(false, true);
            }
        }

        @Override
        public void onUserPresent() {//用户解锁
            Log.v(LOG_TAG, "onUserPresent");
        }

    }

    private SimpleDialog.OnSimpleDialogBtnClickListener simpleDialogBtnClickListener = new SimpleDialog.OnSimpleDialogBtnClickListener() {
        @Override
        public void onSimpleDialogLeftBtnClick() {

        }

        @Override
        public void onSimpleDialogRightBtnClick() {
            // 停止特效显示，清空点赞和普通礼物、特殊礼物的缓存
            lView.clearSpecialEffects();
            speciaGiftContainer.setGiftContainerVisible(false);

            if (!liveEndView.isVisible()) {
                destoryAll(false, true);
            }
        }

    };


    private void destoryAll(boolean isClose, boolean isEndView) {
        HttpEngine.cancelTag(this);
        if (simpleDialog != null && simpleDialog.isShowing()) {
            simpleDialog.dismiss();
        }
        lView.hideDialog();
        sendExitRoom(userId, userId);
        if(UserInfoUtils.isAlreadyLogin())
            liveEndView.setVisible(isEndView);
        if (mScreenListener != null) {
            mScreenListener.unregisterListener();
            mScreenListener = null;
        }
        destoryViedo();
        if (glSurfaceView != null) {
            glSurfaceView = null;
        }
        if (isClose) {
            new StopSessionThread().start();
        }
        if(UserInfoUtils.isAlreadyLogin()){
            //封号时候先退出账号了
            UserInfoUtils.requestUserBalance(this, UserInfoUtils.getUserInfo().getUserId());
            UserInfoUtils.requestUserInfo(this, UserInfoUtils.getUserInfo().getUserId());
        }
        isDestory = true;
    }

    public void destoryViedo() {
        if (mALVideoEngine != null) {
            mALVideoEngine.onDestroy();
            mALVideoEngine = null;
        }
    }

    private static class StopSessionThread extends Thread {

        @Override
        public void run() {
            super.run();
            SystemClock.sleep(500);
            SessionEngine.getSessionEngine().close();
        }
    }
    /**
     * 防止连续点击
     *
     * @return
     */
    private boolean multiClick() {
        if (System.currentTimeMillis() - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = System.currentTimeMillis();
        return false;
    }


}
