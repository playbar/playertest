package com.rednovo.ace.ui.activity.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.AceApplication;
import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.common.WebViewConfig;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.share.BaseShareApi;
import com.rednovo.ace.data.share.ShareFactory;
import com.rednovo.ace.data.share.SinaShareApi;
import com.rednovo.ace.net.api.ReqRoomApi;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.BeginLiveResult;
import com.rednovo.ace.net.parser.ShareInfoResult;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.request.HttpEngine;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.net.request.StringCallback;
import com.rednovo.ace.view.dialog.SingleOptionDialog;
import com.rednovo.ace.view.dialog.live.CreateLiveDialog;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.KeyBoardUtils;
import com.rednovo.libs.common.LogUtils;
import com.rednovo.libs.common.NetWorkUtil;
import com.rednovo.libs.common.ScreenUtils;
import com.rednovo.libs.common.SharedPreferenceKey;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StatusBarUtils;
import com.rednovo.libs.common.StorageUtils;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.common.SubstringUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;
import com.rednovo.libs.ui.base.BaseActivity;
import com.seu.magicfilter.camera.CameraManager;

public class CreateLiveActivity extends BaseActivity implements SurfaceHolder.Callback, OnClickListener, CameraManager.CameraReadyListener, BaseShareApi.ShareResultCallback {

    private static final String LOG_TAG = "CreateLiveActivity";
    private static final String WECHAT_SHARE_CHANNEL = "1";

    private static final String QQ_SHARE_CHANNEL = "2";

    private static final String SINA_SHARE_CHANNEL = "5";
    //    private SurfaceView mSurfaceView;
//    private SurfaceHolder mSurfaceHolder;
//    private CameraManager mCmeraManager;
    private ScrollView mScrollView;
    private Button btnStart;
    private RelativeLayout rlParent;
    private EditText etRoomName;
    private RelativeLayout rlEditPhoto;
    private boolean isResume;
    private SimpleDraweeView imgPhoto;
    private String uploadImageUrl;

    private UserInfoResult.UserEntity userInfo;
    private int imgPhotoId;
    private View ivWeixin, ivFriends, ivQQ, ivQQZone, ivSina, tvEmpty;
    private BaseShareApi shareApi;
    private boolean shareState;
    private String photoUrl, coverUrl;
    private FrameLayout frameLayout;
    private String shareChannel;
    private ShareInfoResult shareInfo;
    private SingleOptionDialog singleOptionDialog;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_create_room);
        // EventBus.getDefault().register(this);
        frameLayout = (FrameLayout) findViewById(R.id.fl_id);
        int screenHeight = ScreenUtils.getScreenHeight(this);
        int screenWidth = ScreenUtils.getScreenWidth(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, screenHeight));
        frameLayout.setOnTouchListener(mOnTouchListener);
        rlEditPhoto = (RelativeLayout) findViewById(R.id.rl_edit_photo);
//        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview_id);
        mScrollView = (ScrollView) findViewById(R.id.scrollView_id);
        imgPhoto = (SimpleDraweeView) findViewById(R.id.img_photo);
        mScrollView.setOnTouchListener(mOnTouchListener);

        rlParent = (RelativeLayout) findViewById(R.id.rl_parent);
        btnStart = (Button) findViewById(R.id.btn_start);
        etRoomName = (EditText) findViewById(R.id.et_live_name);
        tvEmpty = findViewById(R.id.tv_empty);
        etRoomName.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        rlEditPhoto.setOnClickListener(this);
        findViewById(R.id.img_switch).setOnClickListener(this);
        findViewById(R.id.img_finsh).setOnClickListener(this);
        ivWeixin = findViewById(R.id.iv_weixin);
        ivFriends = findViewById(R.id.iv_friends);
        ivQQ = findViewById(R.id.iv_qq);
        ivQQZone = findViewById(R.id.iv_qqzone);
        ivSina = findViewById(R.id.iv_sina);
        ivWeixin.setOnClickListener(this);
        ivFriends.setOnClickListener(this);
        ivQQ.setOnClickListener(this);
        ivQQZone.setOnClickListener(this);
        ivSina.setOnClickListener(this);
//        mSurfaceHolder = mSurfaceView.getHolder();
//        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置显示器类型，setType必须设置
//        mSurfaceHolder.addCallback(this);
//        mCmeraManager = new CameraManager();
//        mCmeraManager.setSurfaceView(mSurfaceView);
//        mCmeraManager.setCameraReadyListener(this);
        KeyBoardUtils.checkKeyboard(rlParent, mScrollView, tvEmpty);
        KeyBoardUtils.closeKeybord(etRoomName, this);
        imgPhotoId = R.drawable.img_upload_conver_default;

        userInfo = UserInfoUtils.getUserInfo();
        photoUrl = userInfo.getProfile();
        coverUrl = userInfo.getShowImg();
        Context cxt = BaseApplication.getApplication().getApplicationContext();
        imgPhoto.getHierarchy().setPlaceholderImage(R.drawable.img_upload_conver_default);
        ((GenericDraweeHierarchy) imgPhoto.getHierarchy()).setFailureImage(cxt.getResources().getDrawable(imgPhotoId));

        if (TextUtils.isEmpty(coverUrl)) {
            if (!TextUtils.isEmpty(photoUrl)) {
                FrescoEngine.setSimpleDraweeView(imgPhoto, photoUrl, ImageRequest.ImageType.SMALL);
            }
        } else {
            FrescoEngine.setSimpleDraweeView(imgPhoto, coverUrl, ImageRequest.ImageType.SMALL);
        }

        setShareInfo();
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            KeyBoardUtils.closeKeybord(etRoomName, CreateLiveActivity.this);

            return false;
        }
    };

    private int m_bestHeightVideo = 480;
    private int m_bestWidthVideo = 600;

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setTranslucentImmersionBar(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        DisplayMetrics metric = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int height1 = metric.heightPixels;
//        int width1 = metric.widthPixels;
//
//        float fScreen = (float) width1 / height1; // p
//        float fVideo = (float) m_bestHeightVideo / m_bestWidthVideo;
//
//        if (fScreen > fVideo) {
//            FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(
//                    width1, m_bestWidthVideo * width1 / m_bestHeightVideo,
//                    Gravity.CENTER);
//            mSurfaceView.setLayoutParams(cameraFL);
//        } else {
//            FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(
//                    height1 * m_bestHeightVideo / m_bestWidthVideo, height1,
//                    Gravity.CENTER);
//            mSurfaceView.setLayoutParams(cameraFL);
//        }
//        startCamera(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        releaseCamera();
    }

    @Override
    protected void onPause() {
        if (!isResume) {
            LogUtils.v(LOG_TAG, "onPause");
            isResume = true;
//            releaseCamera();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ScreenUtils.isFlyme()) {//判断是否是魅族手机
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        KeyBoardUtils.closeKeybord(etRoomName, this);
        if (isResume) {
            LogUtils.v(LOG_TAG, "onResume");
            isResume = false;
//            startCamera(mSurfaceHolder);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            overridePendingTransition(R.anim.trans_anim_no, R.anim.trans_anim_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_switch:
//                mCmeraManager.switchCameraFacing();
//                mCmeraManager.startPreview(mSurfaceHolder, null);
                break;
            case R.id.img_finsh:
                this.finish();
                overridePendingTransition(R.anim.trans_anim_no, R.anim.trans_anim_out);
                break;
            case R.id.et_live_name:
                KeyBoardUtils.openKeybord(etRoomName, this);
                break;
            case R.id.btn_start:

                if (!NetWorkUtil.checkNetwork())
                    return;
                String roomName = etRoomName.getText().toString();
                String userId = UserInfoUtils.getUserInfo().getUserId();
                setSatrBtnEnabled(false, getString(R.string.text_create_live_ing));
                reqCreateLive(userId, roomName, uploadImageUrl);
                KeyBoardUtils.closeKeybord(etRoomName, this);
                break;
            case R.id.rl_edit_photo:
                KeyBoardUtils.closeKeybord(etRoomName, this);
                CreateLiveDialog.getDialog(this, 0).show();
                break;
            case R.id.iv_weixin:
            case R.id.iv_friends:
            case R.id.iv_qq:
            case R.id.iv_qqzone:
            case R.id.iv_sina:
                shareClick(v.getId());
                break;
            default:
                break;
        }
    }

    private void shareClick(int id) {
        switch (id) {
            //TODO
            case R.id.iv_weixin:
                shareChannel = WECHAT_SHARE_CHANNEL;
                shareApi = ShareFactory.getShareApi(this, ShareFactory.WECHAT_SHARE, shareInfo);
                break;
            case R.id.iv_friends:
                shareChannel = WECHAT_SHARE_CHANNEL;
                shareApi = ShareFactory.getShareApi(this, ShareFactory.WECHAT_FRIEND_SHARE, shareInfo);
                break;
            case R.id.iv_qq:
                shareChannel = QQ_SHARE_CHANNEL;
                shareApi = ShareFactory.getShareApi(this, ShareFactory.QQ_SHARE, shareInfo);
                break;
            case R.id.iv_qqzone:
                shareChannel = QQ_SHARE_CHANNEL;
                shareApi = ShareFactory.getShareApi(this, ShareFactory.QQ_ZONE_SHARE, shareInfo);
                break;
            case R.id.iv_sina:
                shareChannel = SINA_SHARE_CHANNEL;
                shareApi = new SinaShareApi(this, shareInfo);
                break;
            default:
                break;
        }
        shareInfo.setUrl(shareInfo.getUrl());
        shareApi.setShareResultCallback(this);
        shareApi.doShare();
    }

    /**
     * 请求创建直播
     *
     * @param userId   用户ID
     * @param title    直播名字
     * @param imageUrl 封面本地路径
     */

    private void reqCreateLive(String userId, String title, String imageUrl) {
        String location = StorageUtils.getSharedPreferences().getString(SharedPreferenceKey.LOCATION, "");
        ReqRoomApi.reqCreateLive(this, userId, title, location, "image", imageUrl, new RequestCallback<BeginLiveResult>() {
            @Override
            public void onRequestSuccess(BeginLiveResult object) {
                String upStreamUrl = object.getUpStream();
                reqNGBUrl(upStreamUrl);
//                Intent intent = new Intent(CreateLiveActivity.this, LiveRecordActivity.class);
//                intent.putExtra(Globle.KEY_UP_STREAM_URL, upStreamUrl);
//                intent.putExtra(Globle.KEY_FACING, mCmeraManager.getCurrentFacing());
//                redirectClose(intent);

            }

            @Override
            public void onRequestFailure(BeginLiveResult e) {
                //LogUtils.v(LOG_TAG, e.getErrMsg());
                setSatrBtnEnabled(true, getString(R.string.text_start_live));
                if (e != null && e.getErrCode() == 222) {
                    //已经被禁播
                    if (!TextUtils.isEmpty(e.getErrMsg())) {
                        showForbid(e.getErrMsg());
                    } else {
                        showForbid(getString(R.string.live_freeze_content, "24"));
                    }
                } else if (e != null && e.getErrCode() == 208) {
                    showSealNumber("");
                } else {
                    ShowUtils.showToast(R.string.text_create_live_failure);
                }

            }
        });
    }

    /**
     * 禁播提醒
     */
    private void showForbid(String content) {
        if (singleOptionDialog == null) {
            singleOptionDialog = new SingleOptionDialog(this);
            singleOptionDialog.setCancelable(false);
        }
        singleOptionDialog.setText(getString(R.string.live_freeze), content, getString(R.string.i_know));
        singleOptionDialog.setOnSingleButtonClickListener(new SingleOptionDialog.OnSingleButtonClickListener() {
            @Override
            public void onClick() {
                CreateLiveActivity.this.finish();
            }
        });
        singleOptionDialog.show();
    }

    /**
     * 账号封停
     */
    private void showSealNumber(String content) {
        if (singleOptionDialog == null) {
            singleOptionDialog = new SingleOptionDialog(this);
            singleOptionDialog.setCancelable(false);
        }
        singleOptionDialog.setText(getString(R.string.account_freeze), getString(R.string.freeze_acount_content), getString(R.string.i_know));
        singleOptionDialog.setOnSingleButtonClickListener(new SingleOptionDialog.OnSingleButtonClickListener() {
            @Override
            public void onClick() {
                UserInfoUtils.logout();
                CreateLiveActivity.this.finish();
            }
        });
        singleOptionDialog.show();
    }

    /**
     * 请求NGB调度url
     */
    private void reqNGBUrl(final String upRtmpUrl) {
        String subUpRtmp = SubstringUtils.substringAfter(upRtmpUrl, "rtmp://");
        if (!TextUtils.isEmpty(subUpRtmp)) {
            ReqRoomApi.reqNGBUrl(subUpRtmp, this, new StringCallback() {
                @Override
                public void onRequestSuccess(String rtmpUrl) {
                    LogUtils.v(LOG_TAG, "NGBrtmpUrl=" + rtmpUrl);
                    rtmpUrl = SubstringUtils.substringBefore(rtmpUrl, "\n");
                    Intent intent = new Intent(CreateLiveActivity.this, LiveRecordActivity.class);
                    intent.putExtra(Globle.KEY_UP_STREAM_URL, rtmpUrl);
//                    if(mCmeraManager != null)
                    intent.putExtra(Globle.KEY_FACING, Camera.CameraInfo.CAMERA_FACING_FRONT);
                    redirectClose(intent);
                }

                @Override
                public void onRequestFailure(String error) {
                    LogUtils.v(LOG_TAG, "error=" + error);
                    Intent intent = new Intent(CreateLiveActivity.this, LiveRecordActivity.class);
                    intent.putExtra(Globle.KEY_UP_STREAM_URL, upRtmpUrl);
//                    if(mCmeraManager != null)
                    intent.putExtra(Globle.KEY_FACING, Camera.CameraInfo.CAMERA_FACING_FRONT);
                    redirectClose(intent);
                }
            });
        } else {
            Intent intent = new Intent(CreateLiveActivity.this, LiveRecordActivity.class);
            intent.putExtra(Globle.KEY_UP_STREAM_URL, upRtmpUrl);
//            if(mCmeraManager != null)
            intent.putExtra(Globle.KEY_FACING, Camera.CameraInfo.CAMERA_FACING_FRONT);
            redirectClose(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 0x100 && data != null) {
            uploadImageUrl = data.getStringExtra("image");
            // Bitmap bitmap = FileUtils.decodeBitmap(uploadImageUrl);
            ((GenericDraweeHierarchy) imgPhoto.getHierarchy()).setFailureImage(AceApplication.getApplication().getResources().getDrawable(imgPhotoId));
            FrescoEngine.setSimpleDraweeView(imgPhoto, Globle.PREFIX_FILE + uploadImageUrl, ImageRequest.ImageType.SMALL);

        }
    }

    @Override
    public void onCameraFailed() {
        LogUtils.v(LOG_TAG, "onCameraFailed");
        ShowUtils.showToast(R.string.text_check_permissions);
//        releaseCamera();
        this.finish();
    }
//
//    @Subscribe(threadMode = ThreadMode.MainThread) //在ui线程执行
//    public void onMainEvent(BaseEvent event) {
//        switch (event.id) {
//            case Globle.KEY_EVENT_PHONE_STATE://监听来电广播
//                break;
//            default:
//                break;
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgPhoto.setBackgroundResource(0);
        frameLayout.setBackgroundResource(0);
//        if (mCmeraManager != null) {
//            mCmeraManager.setSurfaceView(null);
//            mCmeraManager = null;
//        }
//        if (mSurfaceView != null) {
//            mSurfaceView = null;
//        }
//        if (mSurfaceHolder != null) {
//            mSurfaceHolder.removeCallback(this);
//            mSurfaceHolder = null;
//        }

        // EventBus.getDefault().unregister(this);
        HttpEngine.cancelTag(this);
        LogUtils.v(LOG_TAG, "onDestroy");
        imgPhotoId = 0;

    }


//    private void startCamera(SurfaceHolder surfaceHolder) {
//        mCmeraManager.openCamera();
//        mCmeraManager.startPreview(surfaceHolder, null);
//    }

//    private void releaseCamera() {
//        mCmeraManager.stopPreview();
//        mCmeraManager.releasePreviewDisplay();
//        mCmeraManager.releaseCamera();
//    }


    public void setSatrBtnEnabled(boolean enabled, String text) {
        btnStart.setEnabled(enabled);
        btnStart.setClickable(enabled);
        btnStart.setText(text);
    }

    @Override
    public void shareComplet() {
        ShareFactory.shareLater(this, shareChannel);
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

    /**
     * 设置分享信息
     */
    private void setShareInfo() {
        shareInfo = new ShareInfoResult();
        if (UserInfoUtils.getUserInfo().getNickName() != null) {
            shareInfo.setTitle(getString(R.string.share_title, UserInfoUtils.getUserInfo().getNickName()));
        } else {
            shareInfo.setTitle(getString(R.string.share_title, "Ace主播"));
        }
//        shareInfo.title = getString(R.string.share_title, LiveInfoUtils.getNickName());
        shareInfo.setSumy(getString(R.string.share_body));
        if (TextUtils.isEmpty(coverUrl)) {
//            if (!TextUtils.isEmpty(photoUrl)) {
//                shareInfo.imgUrl = photoUrl;
//            }else{
            try {
                Bitmap bitmap = BitmapFactory.decodeResource(AceApplication.getApplication().getResources(), R.drawable.ic_launcher);
                shareInfo.image = bitmap;
            } catch (Exception ex) {

            }
//            }
        } else {
            shareInfo.setImgSrc(coverUrl);
        }
        shareInfo.appName = getString(R.string.app_name);
        String shareUserId = "";
        if (UserInfoUtils.isAlreadyLogin()) {
            shareUserId = UserInfoUtils.getUserInfo().getUserId();
        } else {
            shareUserId = "-1";
        }
        //主播id就是showID
        shareInfo.setUrl(WebViewConfig.getShareUrl(shareUserId));
        ReqUserApi.requestShareInfo(this, UserInfoUtils.getUserInfo().getUserId(), UserInfoUtils.getUserInfo().getUserId(), ShareFactory.ANCHOR_SHARE, new RequestCallback<ShareInfoResult>() {
            @Override
            public void onRequestSuccess(ShareInfoResult object) {
                if (!StringUtils.isEmpty(object.getTitle())) {
                    shareInfo.setTitle(object.getTitle());
                }
                if (!StringUtils.isEmpty(object.getSumy())) {
                    shareInfo.setSumy(object.getSumy());
                }
                if (!StringUtils.isEmpty(object.getImgSrc())) {
                    shareInfo.setImgSrc("http://" + object.getImgSrc());
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
