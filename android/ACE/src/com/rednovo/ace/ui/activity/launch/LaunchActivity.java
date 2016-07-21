package com.rednovo.ace.ui.activity.launch;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.R;
import com.rednovo.ace.common.CacheUserInfoUtils;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.request.HttpEngine;
import com.rednovo.ace.ui.activity.ACEWebActivity;
import com.rednovo.ace.ui.activity.LoginActivity;
import com.rednovo.ace.ui.activity.MainActivity;
import com.rednovo.libs.common.ErrorMsg;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StatusBarUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;
import com.rednovo.libs.ui.base.BaseActivity;

public class LaunchActivity extends BaseActivity implements UserInfoUtils.OnLoginFinish, View.OnClickListener {

    private SimpleDraweeView rlSplash;
    private CountTime countTime;
    private Handler mHandler = new Handler();
    private LinearLayout rlGotoLayout;
    private static final int TYPE_REDIRECT_H5 = 1;
    private static final int TYPE_REDIRECT_ROOM = 2;
    private int nType = -1;
    private final int LAST_TIME = 1;
    private String strBootPic = "";
    private String strClickUrl = "";
    private boolean isClick = true;
    private TextView tvTime;
    private TextView tvIndicator;

    @Override
    protected void onCreate(Bundle arg0) {
        leaksTest();
        super.onCreate(arg0);
        //每次进入应用更新用户信息
        if (UserInfoUtils.isAlreadyLogin()) {
            UserInfoUtils.autoLogin(this, this);
        }
        setContentView(R.layout.activity_launch);
        initData();
    }

    private void initData() {
        rlGotoLayout = (LinearLayout) findViewById(R.id.ll_goto_id);
        rlSplash = (SimpleDraweeView) findViewById(R.id.rl_bg_splash);
        tvTime = (TextView) findViewById(R.id.tv_time_count);
        tvIndicator = (TextView) findViewById(R.id.tv_indicator);
        rlSplash.setOnClickListener(this);
        rlGotoLayout.setOnClickListener(this);
        String bootResult = CacheUserInfoUtils.getBootPic();
        parserBootAd(bootResult);
        if (CacheUserInfoUtils.isVersionChanged()) {
            redirectClose(GuideActivity.class);
        } else {
            if (!TextUtils.isEmpty(strBootPic)) {
                // "http://cache.17ace.cn/images/6/20160510123746121264290548-profile.png"
                FrescoEngine.setSimpleDraweeView(rlSplash, strBootPic, ImageRequest.ImageType.DEFAULT, mControllerListener);
                countTime = new CountTime(5000, 1000);
                countTime.start();
            } else {
                rlGotoLayout.setVisibility(View.INVISIBLE);
                gotoLogic();
            }
        }
    }

    @Override
    public void loginSuccess(String loginType) {

    }

    @Override
    public void loginFailed(UserInfoResult error) {
        String errorMsg = ErrorMsg.getErrMsg(error.getErrCode());
        if (!TextUtils.isEmpty(errorMsg)) {
            ShowUtils.showToast(errorMsg);
        }else{
            ShowUtils.showToast("错误代码" + error.getErrCode() + ":" + "请重新登录");
        }
        if (UserInfoUtils.isAlreadyLogin()) {
            UserInfoUtils.logout();
        }
    }


    @Override
    public void setStatusBar() {
        StatusBarUtils.setTranslucentImmersionBar(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destory();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_bg_splash://跳转广告页
                gotoAdLogic();
                break;
            case R.id.ll_goto_id:
                cancelTime();
                gotoLogic();
                break;
            default:
                break;
        }
    }


    private class CountTime extends CountDownTimer {

        public CountTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long count = millisUntilFinished / 1000;
            if (LAST_TIME == count) {
                gotoLogic();
            }
        }

        @Override
        public void onFinish() {

        }

    }

    private void cancelTime() {
        if (countTime != null) {
            countTime.cancel();
            countTime = null;
        }
    }

    private void gotoAdLogic() {
        if (isClick) {
            switch (nType) {
                case TYPE_REDIRECT_H5:
                    if (!TextUtils.isEmpty(strClickUrl)) {
                        cancelTime();
                        Intent intent = new Intent(this, ACEWebActivity.class);
                        intent.putExtra("IS_LAUNCH_AD", true);
                        intent.putExtra("url", strClickUrl);
                        redirectClose(intent);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    break;
                case TYPE_REDIRECT_ROOM:
                    break;
                default:
                    break;
            }
        }
    }


    private void gotoLogic() {
        if (UserInfoUtils.isAlreadyLogin()) {
            redirectClose(MainActivity.class);
            overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out);
        } else {
            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
            intent.putExtra("isLaunchGo", true);
            redirectClose(intent);
            overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out);
        }
    }


    private void parserBootAd(String result) {
        try {
            JSONObject jsonObject = JSON.parseObject(result);
            strBootPic = jsonObject.getString("picUrl");
            strClickUrl = jsonObject.getString("linkUrl");
            nType = jsonObject.getIntValue("type");
            String showId = jsonObject.getString("showId");
        } catch (Exception ex) {

        }
    }

    private void leaksTest() {
//        RefWatcher refWatcher = AceApplication.getRefWatcher(this);
//        refWatcher.watch(this);
    }

    private void destory() {
        mHandler.removeCallbacksAndMessages(null);
        HttpEngine.cancelTag(this);
        if (Build.VERSION.SDK_INT >= 16) {
            getWindow().getDecorView().setBackground(null);

        } else {
            getWindow().getDecorView().setBackgroundDrawable(null);
        }
        if (rlGotoLayout != null) {
            if (Build.VERSION.SDK_INT >= 16) {
                rlGotoLayout.setBackground(null);
            } else {
                rlGotoLayout.setBackgroundDrawable(null);
            }
        }

        if (rlSplash != null) {
            if (Build.VERSION.SDK_INT >= 16) {
                rlSplash.setBackground(null);
            } else {
                rlSplash.setBackgroundDrawable(null);
            }
            rlSplash = null;
        }
        if (tvTime != null) {
            if (Build.VERSION.SDK_INT >= 16) {
                tvTime.setBackground(null);
            } else {
                tvTime.setBackgroundDrawable(null);
            }
            tvTime = null;
        }
        if (tvIndicator != null) {
            if (Build.VERSION.SDK_INT >= 16) {
                tvIndicator.setBackground(null);
            } else {
                tvIndicator.setBackgroundDrawable(null);
            }
            tvIndicator = null;
        }

        if (mControllerListener != null) {
            mControllerListener = null;
        }
        cancelTime();

    }


    private ControllerListener mControllerListener = new ControllerListener() {
        @Override
        public void onSubmit(String s, Object o) {
        }

        @Override
        public void onFinalImageSet(String s, Object o, Animatable animatable) {
            isClick = true;
            rlGotoLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onIntermediateImageSet(String s, Object o) {
        }

        @Override
        public void onIntermediateImageFailed(String s, Throwable throwable) {
            isClick = false;
            rlGotoLayout.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onFailure(String s, Throwable throwable) {
            isClick = false;
            rlGotoLayout.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onRelease(String s) {
        }
    };

}
