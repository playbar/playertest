package com.rednovo.ace.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.datasource.DataSource;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.AceApplication;
import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.cell.LiveInfo;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.api.ReqUtils;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.live.LiveActivity;
import com.rednovo.ace.ui.adapter.UserZonePagerAdapter;
import com.rednovo.ace.ui.fragment.UserFansFragment;
import com.rednovo.ace.ui.fragment.UserSubscribeFragment;
import com.rednovo.ace.view.dialog.SimpleLoginDialog;
import com.rednovo.libs.common.BlurUtils;
import com.rednovo.libs.common.LevelUtils;
import com.rednovo.libs.common.StatusBarUtils;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;
import com.rednovo.libs.ui.base.BaseActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dk on 16/2/26.
 */
public class UserZoneActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final int SUBSCRIBE_POSITION = 0;

    private static final int FANS_POSITION = 1;
    public static String USER = "user";
    public static String USER_ID = "user_id";

    private UserSubscribeFragment mUserSubscribeFragment;

    private UserFansFragment mUserFansFragment;

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private ViewPager vpInfo;
    private ImageView ivBackBtn;
    private SimpleDraweeView frescoUserHead;
    private TextView tvUserNickname;
    private ImageView ivUserLevel;
    private TextView tvUserIntro;
    private TextView tvUserPosition;
    private TextView tvUserID;
    private TextView tvUserSubscribeNum;
    private TextView tvUserFansNum;
    private RelativeLayout rlUserSubscribe;
    private RelativeLayout rlUserFans;
    private View ivUserSubscribeSelected;
    private View ivUserFansSelected;
    private TextView tvUserSubscribe;
    private TextView tvUserFnas;
    private RelativeLayout rlUserInfoLayout;
    private RelativeLayout rlLiveingFlag;
    private AnimatorSet animatorSet;
    private TextView tvSubscribeBtn;
    private String userId;
    private UserZonePagerAdapter fragmentAdapter;

    private UserInfoResult.UserEntity user = null;

    private boolean isShowLiveingBtn = true;
    private boolean isSubscribe = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 100:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        rlUserInfoLayout.setBackground((Drawable) msg.obj);
                    }else {
                        rlUserInfoLayout.setBackgroundDrawable((Drawable) msg.obj);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_user_home_page);

        rlUserInfoLayout = (RelativeLayout) findViewById(R.id.rl_user_info_layout);
        ivBackBtn = (ImageView) findViewById(R.id.iv_back_btn);
        frescoUserHead = (SimpleDraweeView) findViewById(R.id.fresco_user_head);
        tvUserNickname = (TextView) findViewById(R.id.tv_user_nickname);
        ivUserLevel = (ImageView) findViewById(R.id.iv_user_level_icon);
        ivUserLevel.setVisibility(View.GONE);
        tvUserIntro = (TextView) findViewById(R.id.tv_user_intro);
        tvUserPosition = (TextView) findViewById(R.id.tv_user_position);
        tvUserID = (TextView) findViewById(R.id.tv_user_id);
        rlUserSubscribe = (RelativeLayout) findViewById(R.id.rl_user_subscribe);
        rlUserFans = (RelativeLayout) findViewById(R.id.rl_user_fans);
        tvUserSubscribeNum = (TextView) findViewById(R.id.tv_user_subscribe_num);
        tvUserSubscribe = (TextView) findViewById(R.id.tv_user_subscribe);
        tvUserFansNum = (TextView) findViewById(R.id.tv_user_fans_num);
        tvUserFnas = (TextView) findViewById(R.id.tv_user_fans);
        vpInfo = (ViewPager) findViewById(R.id.id_star_info_viewpager);
        ivUserSubscribeSelected = findViewById(R.id.user_subscribe_selected_view);
        ivUserFansSelected = findViewById(R.id.user_fans_selected_view);
        rlLiveingFlag = (RelativeLayout) findViewById(R.id.rl_liveing_flag);
        tvSubscribeBtn = (TextView) findViewById(R.id.tv_subscribe_btn);

        ivBackBtn.setOnClickListener(this);
        frescoUserHead.setOnClickListener(this);
        rlUserSubscribe.setOnClickListener(this);
        rlUserFans.setOnClickListener(this);
        rlLiveingFlag.setOnClickListener(this);
        tvSubscribeBtn.setOnClickListener(this);
        vpInfo.setOnPageChangeListener(this);
        vpInfo.setOverScrollMode(View.OVER_SCROLL_NEVER);

        isShowLiveingBtn = getIntent().getBooleanExtra(Globle.KEY_SHOW_LIVEING_BTN, true);
        Serializable extra = getIntent().getSerializableExtra(USER);

        userId = getIntent().getStringExtra(USER_ID);
        if (extra != null) {
            UserInfoResult.UserEntity currentUser = (UserInfoResult.UserEntity) extra;
            userId = currentUser.getUserId();
        } else if (!StringUtils.isEmpty(userId)) {

        } else {
            return;
        }

        reqUserInfo(userId);
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userId);
        bundle.putBoolean(Globle.KEY_SHOW_LIVEING_BTN, isShowLiveingBtn);

        mUserSubscribeFragment = new UserSubscribeFragment();
        mUserSubscribeFragment.setArguments(bundle);
        mUserFansFragment = new UserFansFragment();
        mUserFansFragment.setArguments(bundle);
        mFragmentList.add(mUserSubscribeFragment);
        mFragmentList.add(mUserFansFragment);
        fragmentAdapter = new UserZonePagerAdapter(getSupportFragmentManager(), this, mFragmentList);
        vpInfo.setAdapter(fragmentAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(UserInfoUtils.isAlreadyLogin() && userId.equals(UserInfoUtils.getUserInfo().getUserId())) {
            tvSubscribeBtn.setVisibility(View.GONE);
        } else {
            tvSubscribeBtn.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void startLiveingAnimator() {

        if(animatorSet != null) {
            animatorSet.start();
            return;
        }
        ImageView ivLiveingOne = (ImageView) findViewById(R.id.iv_liveing_one);
        ImageView ivLiveingTwo = (ImageView) findViewById(R.id.iv_liveing_two);

        ObjectAnimator twoAlphaBurn = ObjectAnimator.ofFloat(ivLiveingTwo, View.ALPHA, 0.4F, 1F);
        ObjectAnimator twoScaleSmallY = ObjectAnimator.ofFloat(ivLiveingTwo, View.SCALE_Y, 1.0F, 0.6F);
        ObjectAnimator twoScaleSmallX = ObjectAnimator.ofFloat(ivLiveingTwo, View.SCALE_X, 1.0F, 0.6F);

        AnimatorSet twoFirstAnimSet = new AnimatorSet();
        twoFirstAnimSet.setInterpolator(new LinearInterpolator());
        twoFirstAnimSet.setDuration(500);
        twoFirstAnimSet.playTogether(twoAlphaBurn, twoScaleSmallY, twoScaleSmallX);

        ObjectAnimator twoAlphaShoal = ObjectAnimator.ofFloat(ivLiveingTwo, View.ALPHA, 1.0F, 0.4F);
        ObjectAnimator twoScaleBigY = ObjectAnimator.ofFloat(ivLiveingTwo, View.SCALE_Y, 0.6F, 1.0F);
        ObjectAnimator twoScaleBigX = ObjectAnimator.ofFloat(ivLiveingTwo, View.SCALE_X, 0.6F, 1.0F);

        AnimatorSet twoSecondAnimSet = new AnimatorSet();
        twoSecondAnimSet.setInterpolator(new LinearInterpolator());
        twoSecondAnimSet.setDuration(500);
        twoSecondAnimSet.playTogether(twoAlphaShoal, twoScaleBigX, twoScaleBigY);

        animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(animListener);
        animatorSet.playSequentially(twoFirstAnimSet, twoSecondAnimSet);
        animatorSet.start();

    }

    private AnimatorListenerAdapter animListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            startLiveingAnimator();
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String userId = intent.getStringExtra(USER_ID);

        reqUserInfo(userId);

        mUserFansFragment.initData(userId);
        mUserSubscribeFragment.initData(userId);
        mUserSubscribeFragment.reqSubscribed(userId);
        mUserFansFragment.reqFans(userId);
        vpInfo.setCurrentItem(0);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setTranslucentImmersionBar(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_btn:
                finish();
                break;
            case R.id.fresco_user_head:

                break;
            case R.id.rl_user_subscribe:
                vpInfo.setCurrentItem(SUBSCRIBE_POSITION);
                break;
            case R.id.rl_user_fans:
                vpInfo.setCurrentItem(FANS_POSITION);
                break;
            case R.id.rl_liveing_flag:
                finish();
                if (UserInfoUtils.isAlreadyLogin() && user.getUserId().equals(UserInfoUtils.getUserInfo().getUserId())) {
                    break;
                }
                Intent intent = new Intent(UserZoneActivity.this, LiveActivity.class);
                LiveInfo liveInfo = new LiveInfo();
                liveInfo.setNickName(user.getNickName());
                liveInfo.setProfile(user.getProfile());
                liveInfo.setRank(user.getRank());
                liveInfo.setSex(user.getSex());

                liveInfo.setStarId(user.getUserId());
                liveInfo.setShowId(user.getExtendData().getShowId());
                liveInfo.setAudienceCnt("0");
                liveInfo.setDownStreanUrl(user.getExtendData().getDownStreanUrl());

                intent.putExtra(Globle.KEY_LIVE_INFO, liveInfo);
                startActivity(intent);
                break;
            case R.id.tv_subscribe_btn:
                if (UserInfoUtils.isAlreadyLogin()) {
                    if (isSubscribe) {
                        ReqUtils.reqCancelSubscibe(this, UserInfoUtils.getUserInfo().getUserId(), userId);
                        isSubscribe = false;
                        tvSubscribeBtn.setText(getString(R.string.hall_subscribe_text));
                        int fansNum = Integer.parseInt(tvUserFansNum.getText().toString());

                        tvUserFansNum.setText((fansNum - 1) + "");
                    } else {
                        ReqUtils.reqSubscibe(this, UserInfoUtils.getUserInfo().getUserId(), userId);
                        isSubscribe = true;
                        tvSubscribeBtn.setText(getString(R.string.subscribe_already));

                        int fansNum = Integer.parseInt(tvUserFansNum.getText().toString());

                        tvUserFansNum.setText((fansNum + 1) + "");
                    }

                } else{
                    SimpleLoginDialog loginDialog = new SimpleLoginDialog(this);
                    loginDialog.show();
                }
                break;
            default:

                break;
        }
    }

    private void onSwitchPager(int pager) {
        int color_ffd200 = getResources().getColor(R.color.color_ffd200);
        int color_white = getResources().getColor(R.color.color_white);
        switch (pager) {
            case SUBSCRIBE_POSITION:
                tvUserFansNum.setTextColor(color_white);
                tvUserFnas.setTextColor(color_white);
                tvUserSubscribeNum.setTextColor(color_ffd200);
                tvUserSubscribe.setTextColor(color_ffd200);
                ivUserSubscribeSelected.setVisibility(View.VISIBLE);
                ivUserFansSelected.setVisibility(View.INVISIBLE);
                break;
            case FANS_POSITION:
                tvUserFansNum.setTextColor(color_ffd200);
                tvUserFnas.setTextColor(color_ffd200);
                tvUserSubscribeNum.setTextColor(color_white);
                tvUserSubscribe.setTextColor(color_white);
                ivUserSubscribeSelected.setVisibility(View.INVISIBLE);
                ivUserFansSelected.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void reqUserInfo(String starId) {
        String userId = "";
        if(UserInfoUtils.isAlreadyLogin())
            userId = UserInfoUtils.getUserInfo().getUserId();
        ReqUserApi.requestUserInfo(this, starId, userId, new RequestCallback<UserInfoResult>() {

            @Override
            public void onRequestSuccess(UserInfoResult object) {

                if (object != null) {
                    user = object.getUser();
                    frescoUserHead.getHierarchy().setFailureImage(AceApplication.getApplication().getResources().getDrawable(R.drawable.head_online));
                    FrescoEngine.setSimpleDraweeView(frescoUserHead, user.getProfile(), ImageRequest.ImageType.DEFAULT);

                    rlUserInfoLayout.setBackgroundResource(R.color.color_white);
                    FrescoEngine.setSimpleDraweeView(UserZoneActivity.this, user.getProfile(), new BaseBitmapDataSubscriber() {
                        @Override
                        public void onNewResultImpl(@Nullable Bitmap bitmap) {
                            Bitmap blurBitmap = BlurUtils.processNatively(bitmap, 50);
                            Drawable drawable = new BitmapDrawable(getResources(), blurBitmap);
                            Message msg = mHandler.obtainMessage();

                            msg.obj = drawable;
                            msg.what = 100;
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void onFailureImpl(DataSource dataSource) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head_online);
                            Bitmap blurBitmap = BlurUtils.processNatively(bitmap, 50);
                            Drawable drawable = new BitmapDrawable(getResources(), blurBitmap);
                            Message msg = mHandler.obtainMessage();

                            msg.obj = drawable;
                            msg.what = 100;
                            mHandler.sendMessage(msg);
                        }
                    });

                    tvUserNickname.setText(user.getNickName());

                    if(StringUtils.isEmpty(user.getSignature())) {
                        tvUserIntro.setText(getString(R.string.signature_default_text));
                    } else {
                        tvUserIntro.setText(user.getSignature());
                    }
                    tvUserID.setText("ID:" + user.getUserId());
                    String position = "";
                    if (StringUtils.isEmpty(user.getExtendData().getPostion())) {
                        position = getString(R.string.position_default_text);
                    } else {
                        position = user.getExtendData().getPostion();
                    }
                    tvUserPosition.setText(position);
                    tvUserFansNum.setText(user.getExtendData().getFansCnt() + "");
                    tvUserSubscribeNum.setText(user.getExtendData().getSubscribeCnt());

                    ivUserLevel.setImageResource(LevelUtils.getLevelIcon(user.getRank()));

                    if(isShowLiveingBtn) {
                        if(user.getExtendData().getIsShow() == 1) {
                            rlLiveingFlag.setVisibility(View.VISIBLE);
                            startLiveingAnimator();
                        }else {
                            rlLiveingFlag.setVisibility(View.GONE);
                        }
                    } else {
                        rlLiveingFlag.setVisibility(View.GONE);
                    }

                    int relation = 0;
                    try {
                        relation = Integer.parseInt(user.getExtendData().getRelatoin());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } finally {
                        if(UserInfoUtils.isAlreadyLogin()){
                            if(UserInfoUtils.getUserInfo().getUserId().equals(user.getUserId())) {
                                relation = -1;
                            }
                        }
                    }

                    tvSubscribeBtn.setVisibility(View.VISIBLE);
                    if(relation == 1) {
                        isSubscribe = true;
                        tvSubscribeBtn.setText(getString(R.string.subscribe_already));
                    } else if(relation == 0){
                        isSubscribe = false;
                        tvSubscribeBtn.setText(getString(R.string.hall_subscribe_text));
                    } else {
                        tvSubscribeBtn.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onRequestFailure(UserInfoResult error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rlUserInfoLayout.setBackground(null);
        }else {
            rlUserInfoLayout.setBackgroundDrawable(null);
        }

        if(animatorSet != null) {
            animatorSet.cancel();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        onSwitchPager(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
