package com.rednovo.ace.widget.gift;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.rednovo.ace.R;
import com.rednovo.libs.common.ScreenUtils;
import com.rednovo.libs.common.ShowUtils;

/**
 * Created by lilong on 16/5/6.
 * 赛车礼物动画布局
 */
public class CarGiftView extends BaseGiftView {

    private View bodyView;
    private View trackView;
    private View beforeLight;
    private View afterLight;
    private ImageView carportView;
    private AnimatorSet animatorSet;

    public CarGiftView(Context context) {
        super(context, R.layout.car_layout);


        bodyView = giftView.findViewById(R.id.rl_car_body);
        trackView = giftView.findViewById(R.id.iv_car_track);
        beforeLight = giftView.findViewById(R.id.iv_car_before_light);
        afterLight = giftView.findViewById(R.id.iv_car_after_light);
        carportView = (ImageView) giftView.findViewById(R.id.iv_car_carport);

        // 屏幕适配
        if (ScreenUtils.getScreenWidth(mContext) > 480) {
            float scale = getResources().getDisplayMetrics().density;
            int increB = 0;
            int increA = 0;
            if (scale < 2) {        // 屏幕尺寸过大而分辨率小产生屏幕密度过小
                increB = 20;
                increA = 30;
            } else {
                increB = increA = ShowUtils.dip2px(mContext, 5);
            }
            RelativeLayout.LayoutParams beforeLightParams = (RelativeLayout.LayoutParams) beforeLight.getLayoutParams();
            beforeLightParams.topMargin = beforeLightParams.topMargin + increB;
            beforeLight.setLayoutParams(beforeLightParams);
            RelativeLayout.LayoutParams afterLightParams = (RelativeLayout.LayoutParams) afterLight.getLayoutParams();
            afterLightParams.topMargin = afterLightParams.topMargin + increA;
            afterLight.setLayoutParams(afterLightParams);
        }
    }

    @Override
    public void startAnimation(ViewGroup giftViewContainer, AnimationEndListener animationEndListener) {
        super.startAnimation(giftViewContainer, animationEndListener);
        if(giftViewContainer.getChildAt(0) instanceof CarGiftView) {
            giftView.setVisibility(View.VISIBLE);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            params.bottomMargin = ShowUtils.dip2px(mContext,100);
            giftViewContainer.addView(this);
            this.setLayoutParams(params);
        }

        startAnimation(animationEndListener);
    }

    private void startAnimation(final BaseGiftView.AnimationEndListener listener) {
        this.setVisibility(View.VISIBLE);

        // 1.初始化赛车进场动画
        PropertyValuesHolder viewXTran = PropertyValuesHolder.ofFloat("translationX", ScreenUtils.getScreenWidth(mContext), 0F);
        PropertyValuesHolder viewYTran = PropertyValuesHolder.ofFloat("translationY", -ScreenUtils.getScreenWidth(mContext) / 2, 0F);
        PropertyValuesHolder viewXScale = PropertyValuesHolder.ofFloat("scaleX", 0.3F, 1.0F);
        PropertyValuesHolder viewYScale = PropertyValuesHolder.ofFloat("scaleY", 0.3F, 1.0F);
        ObjectAnimator viewTrans = ObjectAnimator.ofPropertyValuesHolder(this, viewXTran, viewYTran, viewXScale, viewYScale);

        viewTrans.setDuration(800);
        viewTrans.setInterpolator(new AccelerateDecelerateInterpolator());

        // 2.初始化赛车刹车后的抖动动画
        PropertyValuesHolder rotationTo = PropertyValuesHolder.ofFloat("rotation", 0F, -0.5F);
        PropertyValuesHolder rotationBack = PropertyValuesHolder.ofFloat("rotation", -0.5F, 0F);
        ObjectAnimator carportRPTo = ObjectAnimator.ofPropertyValuesHolder(carportView, rotationTo, rotationBack);
        carportRPTo.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                trackView.setVisibility(View.VISIBLE);
                ObjectAnimator trackAlpha = ObjectAnimator.ofFloat(trackView, "alpha", 0.0F, 1.0F);
                trackAlpha.setDuration(200);
                trackAlpha.setInterpolator(new LinearInterpolator());
                trackAlpha.start();
            }
        });
        carportRPTo.setDuration(300);

        // 3.初始化赛车停止运动后动画
        ObjectAnimator emptyAnima = ObjectAnimator.ofFloat(this, "alpha", 1.0F, 1.0F);
        emptyAnima.setDuration(2000);
        // 3.1初始化前车灯闪烁动画
        ObjectAnimator beforeLightAlpha = ObjectAnimator.ofFloat(beforeLight, "alpha", 0.0F, 1.0F);
        beforeLightAlpha.setDuration(200);
        beforeLightAlpha.setRepeatCount(3);
        // 3.2初始化后车灯闪烁动画
        ObjectAnimator afterLightAlpha = ObjectAnimator.ofFloat(afterLight, "alpha", 0.0F, 1.0F);
        afterLightAlpha.setDuration(200);
        afterLightAlpha.setRepeatCount(3);
        afterLightAlpha.addListener(new AnimatorListenerAdapter() {
            AnimationDrawable carportDrawable;

            @Override
            public void onAnimationStart(Animator animation) {
                // 3.3执行停车后闪关灯帧动画
                carportView.setImageResource(R.drawable.car_frame_anim);
                carportDrawable = (AnimationDrawable) carportView.getDrawable();
                carportDrawable.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 3.4终止帧动画释放资源
                carportView.setImageResource(R.drawable.car_body);
                carportDrawable.stop();
            }
        });

        // 3.5组装停车后一系列动画
        AnimatorSet viewStopSet = new AnimatorSet();
        viewStopSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                beforeLight.setVisibility(View.VISIBLE);
                afterLight.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                beforeLight.setAlpha(1.0F);
                beforeLight.setVisibility(View.GONE);
                afterLight.setVisibility(View.GONE);

            }
        });
        viewStopSet.playTogether(emptyAnima, beforeLightAlpha, afterLightAlpha);

        // 4.初始化车退出屏幕动画
        PropertyValuesHolder bodyViewXTran = PropertyValuesHolder.ofFloat("translationX", 0F, -ScreenUtils.getScreenWidth(mContext));
        PropertyValuesHolder bodyViewYTran = PropertyValuesHolder.ofFloat("translationY", 0, ScreenUtils.getScreenWidth(mContext) / 2);
        PropertyValuesHolder bodyViewXScale = PropertyValuesHolder.ofFloat("scaleX", 1.0F, 1.5F);
        PropertyValuesHolder bodyViewYScale = PropertyValuesHolder.ofFloat("scaleY", 1.0F, 1.5F);
        ObjectAnimator bodyTrans = ObjectAnimator.ofPropertyValuesHolder(bodyView, bodyViewXTran, bodyViewYTran, bodyViewXScale, bodyViewYScale);
        bodyTrans.setDuration(600);

        // 5.初始化刹车印淡出动画
        ObjectAnimator trackViewAlpha = ObjectAnimator.ofFloat(trackView, "alpha", 1.0F, 0F);
        trackViewAlpha.setDuration(600);

        // 6.组装赛车整体动画
        animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resetView();
                giftView.setVisibility(View.GONE);
                isRunning = false;
                if(listener != null) {
                    listener.onAnimationEnd();
                }
            }
        });
        animatorSet.playSequentially(viewTrans, carportRPTo, viewStopSet, bodyTrans, trackViewAlpha);
        animatorSet.start();
    }

    private void resetView() {
        CarGiftView.this.setVisibility(View.GONE);

        bodyView.setScaleX(1.0F);
        bodyView.setScaleY(1.0F);
        bodyView.setTranslationX(0);
        bodyView.setTranslationY(0);
        trackView.setAlpha(1.0F);
        beforeLight.setVisibility(View.GONE);
        afterLight.setVisibility(View.GONE);
    }

    @Override
    public void recyclResource() {
        ((ImageView)giftView.findViewById(R.id.iv_car_chassis)).setImageResource(0);
        ((ImageView)giftView.findViewById(R.id.iv_car_after_light)).setImageResource(0);
        ((ImageView)giftView.findViewById(R.id.iv_car_carport)).setImageResource(0);
        ((ImageView)giftView.findViewById(R.id.iv_car_before_light)).setImageResource(0);
        removeAllViews();
    }

    @Override
    public void cancleAnimation() {

        if(animatorSet != null) {
            animatorSet.cancel();
        }
    }
}
