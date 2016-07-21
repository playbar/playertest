package com.rednovo.ace.widget.gift;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.rednovo.ace.R;
import com.rednovo.libs.common.ScreenUtils;
import com.rednovo.libs.common.ShowUtils;

/**
 * Created by lilong on 16/5/10.
 * 游轮礼物动画布局
 */
public class BoatGiftView extends BaseGiftView {

    private View beforeWater;
    private View behindWater;
    private View allBoatBody;
    private ImageView boatBody;
    private View boatShadow;
    private AnimationDrawable boatBodyDrawable;

    public BoatGiftView(Context context) {
        super(context, R.layout.boat_layout);

        beforeWater = findViewById(R.id.rl_water_layer_before);
        behindWater = findViewById(R.id.rl_water_layer_behind);
        allBoatBody = findViewById(R.id.rl_boat_body);
        boatBody = (ImageView) findViewById(R.id.iv_boat_body);
        boatShadow = findViewById(R.id.iv_boat_shadow);
    }

    @Override
    protected void startAnimation(ViewGroup speciaGiftContainer, AnimationEndListener animationEndListener) {
        super.startAnimation(speciaGiftContainer, animationEndListener);
        View child = speciaGiftContainer.getChildAt(0);
        if(child != null && child instanceof BoatGiftView) {
            giftView.setVisibility(View.VISIBLE);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            speciaGiftContainer.addView(this);
            this.setLayoutParams(params);
        }

        if(Build.MODEL.contains("Coolpad")) {
            System.out.println("==========Coolpad");
            startAnimationForCoolpad(animationEndListener);
        } else {
            startAnimationForCommon(animationEndListener);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void startAnimationForCommon(final AnimationEndListener animationEndListener) {
        // 1.水浪淡入动画
        android.animation.ObjectAnimator beforeWaterIn = android.animation.ObjectAnimator.ofFloat(beforeWater, View.ALPHA, 0, 1.0F);
        beforeWaterIn.setDuration(500);
        beforeWaterIn.setInterpolator(new LinearInterpolator());
        android.animation.ObjectAnimator behindWaterIn = android.animation.ObjectAnimator.ofFloat(behindWater, View.ALPHA, 0, 1.0F);
        behindWaterIn.setDuration(500);
        behindWaterIn.setInterpolator(new LinearInterpolator());
        // 2.1上层水浪左右移动动画
        android.animation.PropertyValuesHolder beforeWaterToRight = android.animation.PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -ShowUtils.dip2px(mContext, 20), 0);
        android.animation.ObjectAnimator beforeWaterTransRight = android.animation.ObjectAnimator.ofPropertyValuesHolder(beforeWater, beforeWaterToRight);
        beforeWaterTransRight.setDuration(6000);
        beforeWaterTransRight.setInterpolator(new CycleInterpolator(5));

        // 2.2下层水浪左右移动动画
        android.animation.PropertyValuesHolder behindWaterToLeft= android.animation.PropertyValuesHolder.ofFloat(View.TRANSLATION_X, ShowUtils.dip2px(mContext, 20), 0);
        android.animation.ObjectAnimator behindWaterTrans = android.animation.ObjectAnimator.ofPropertyValuesHolder(behindWater, behindWaterToLeft);
        behindWaterTrans.setDuration(6000);
        behindWaterTrans.setInterpolator(new CycleInterpolator(5));

        // 3.船从左侧移入到屏幕中央，并伴随放大动画
        android.animation.PropertyValuesHolder allBoatBodyTransX = android.animation.PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -ScreenUtils.getScreenWidth(mContext), 0);
        android.animation.PropertyValuesHolder allBoatBodyTransY = android.animation.PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -ShowUtils.dip2px(mContext, 200) / 2, 0);
        android.animation.PropertyValuesHolder allBoatBodyScaleX = android.animation.PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5F, 1.0F);
        android.animation.PropertyValuesHolder allBoatBodyScaleY = android.animation.PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5F, 1.0F);
        android.animation.ObjectAnimator allBoatBodyIn = android.animation.ObjectAnimator.ofPropertyValuesHolder(allBoatBody, allBoatBodyTransX, allBoatBodyTransY, allBoatBodyScaleX, allBoatBodyScaleY);
        allBoatBodyIn.setDuration(1000);
        allBoatBodyIn.setInterpolator(new AccelerateDecelerateInterpolator());

        // 4.船在水面上上下浮动动画，船影伴随缩放动画
        android.animation.ObjectAnimator boatBodyFloat = android.animation.ObjectAnimator.ofFloat(boatBody, View.TRANSLATION_Y, 0, 20);
        boatBodyFloat.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {
                boatBody.setImageResource(R.drawable.boat_frame_anim);
                boatBodyDrawable = (AnimationDrawable) boatBody.getDrawable();
                boatBodyDrawable.start();
            }
        });
        android.animation.PropertyValuesHolder boatShadowScaleX = android.animation.PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F);
        android.animation.PropertyValuesHolder boatShadowScaleY = android.animation.PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F);
        android.animation.ObjectAnimator boatShadowScale = android.animation.ObjectAnimator.ofPropertyValuesHolder(boatShadow, boatShadowScaleX, boatShadowScaleY);

        // 4.1组合船体和船影动画
        android.animation.AnimatorSet boatStopSet = new android.animation.AnimatorSet();
        boatStopSet.setInterpolator(new CycleInterpolator(3));
        boatStopSet.setDuration(3000);
        boatStopSet.playTogether(boatBodyFloat, boatShadowScale);

        // 5.船体和水浪最为一个整体向下移和船水平向右移动，并且伴随水浪的淡出
        android.animation.ObjectAnimator bodyTransOut = android.animation.ObjectAnimator.ofFloat(giftView, View.TRANSLATION_Y, 0F, ShowUtils.dip2px(mContext, 350));
        android.animation.ObjectAnimator beforeWaterAlphaOut = android.animation.ObjectAnimator.ofFloat(beforeWater, View.ALPHA, 1.0F, 0.2F);
        android.animation.ObjectAnimator behindWaterAlphaOut = android.animation.ObjectAnimator.ofFloat(behindWater, View.ALPHA, 1.0F, 0.2F);
        android.animation.ObjectAnimator boatBodyTransOut = android.animation.ObjectAnimator.ofFloat(allBoatBody, View.TRANSLATION_X, 0, ScreenUtils.getScreenWidth(mContext));
        android.animation.AnimatorSet outSet = new android.animation.AnimatorSet();
        outSet.setInterpolator(new LinearInterpolator());
        outSet.setDuration(1000);
        outSet.playTogether(bodyTransOut, beforeWaterAlphaOut, behindWaterAlphaOut, boatBodyTransOut);

        android.animation.AnimatorSet boatBodySet = new android.animation.AnimatorSet();
        boatBodySet.playSequentially(allBoatBodyIn, boatStopSet, outSet);
        android.animation.AnimatorSet animatorSet = new android.animation.AnimatorSet();
        animatorSet.playTogether(beforeWaterIn, behindWaterIn, boatBodySet, beforeWaterTransRight, behindWaterTrans);
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                giftView.setVisibility(View.GONE);
                giftView.setTranslationY(0F);
                isRunning = false;
                if(boatBodyDrawable != null) {
                    boatBodyDrawable.stop();
                }
                if(animationEndListener != null) {
                    animationEndListener.onAnimationEnd();
                }
            }
        });
        animatorSet.start();

    }

    private void startAnimationForCoolpad(final AnimationEndListener animationEndListener) {
        // 1.水浪淡入动画
        ObjectAnimator beforeWaterIn = ObjectAnimator.ofFloat(beforeWater, "alpha", 0, 1.0F);
        beforeWaterIn.setDuration(500);
        beforeWaterIn.setInterpolator(new LinearInterpolator());
        ObjectAnimator behindWaterIn = ObjectAnimator.ofFloat(behindWater, "alpha", 0, 1.0F);
        behindWaterIn.setDuration(500);
        behindWaterIn.setInterpolator(new LinearInterpolator());
        // 2.1上层水浪左右移动动画
        PropertyValuesHolder beforeWaterToRight = PropertyValuesHolder.ofFloat("translationX", -ShowUtils.dip2px(mContext, 20), 0);
        ObjectAnimator beforeWaterTransRight = ObjectAnimator.ofPropertyValuesHolder(beforeWater, beforeWaterToRight);
        beforeWaterTransRight.setDuration(5000);
        beforeWaterTransRight.setInterpolator(new CycleInterpolator(5));

        // 2.2下层水浪左右移动动画
        PropertyValuesHolder behindWaterToLeft= PropertyValuesHolder.ofFloat("translationX", ShowUtils.dip2px(mContext, 20), 0);
        ObjectAnimator behindWaterTrans = ObjectAnimator.ofPropertyValuesHolder(behindWater, behindWaterToLeft);
        behindWaterTrans.setDuration(5000);
        behindWaterTrans.setInterpolator(new CycleInterpolator(5));

        // 3.船从左侧移入到屏幕中央，并伴随放大动画
        PropertyValuesHolder allBoatBodyTransX = PropertyValuesHolder.ofFloat("translationX", -ScreenUtils.getScreenWidth(mContext), 0);
        PropertyValuesHolder allBoatBodyTransY = PropertyValuesHolder.ofFloat("translationY", -ShowUtils.dip2px(mContext, 200) / 2, 0);
        PropertyValuesHolder allBoatBodyScaleX = PropertyValuesHolder.ofFloat("scaleX", 0.5F, 1.0F);
        PropertyValuesHolder allBoatBodyScaleY = PropertyValuesHolder.ofFloat("scaleY", 0.5F, 1.0F);
        ObjectAnimator allBoatBodyIn = ObjectAnimator.ofPropertyValuesHolder(allBoatBody, allBoatBodyTransX, allBoatBodyTransY, allBoatBodyScaleX, allBoatBodyScaleY);
        allBoatBodyIn.setDuration(1000);
        allBoatBodyIn.setInterpolator(new AccelerateDecelerateInterpolator());

        // 4.船在水面上上下浮动动画，船影伴随缩放动画
        ObjectAnimator boatBodyFloat = ObjectAnimator.ofFloat(boatBody, "translationY", 0, 20);
        boatBodyFloat.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                boatBody.setImageResource(R.drawable.boat_frame_anim);
                boatBodyDrawable = (AnimationDrawable) boatBody.getDrawable();
                boatBodyDrawable.start();
            }
        });
        PropertyValuesHolder boatShadowScaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0F, 1.2F);
        PropertyValuesHolder boatShadowScaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0F, 1.2F);
        ObjectAnimator boatShadowScale = ObjectAnimator.ofPropertyValuesHolder(boatShadow, boatShadowScaleX, boatShadowScaleY);

        // 4.1组合船体和船影动画
        AnimatorSet boatStopSet = new AnimatorSet();
        boatStopSet.setInterpolator(new CycleInterpolator(2));
        boatStopSet.setDuration(2000);
        boatStopSet.playTogether(boatBodyFloat, boatShadowScale);

        // 5.船体和水浪最为一个整体向下移和船水平向右移动，并且伴随水浪的淡出
        ObjectAnimator bodyTransOut = ObjectAnimator.ofFloat(giftView, "translationY", 0F, ShowUtils.dip2px(mContext, 350));
        ObjectAnimator beforeWaterAlphaOut = ObjectAnimator.ofFloat(beforeWater, "alpha", 1.0F, 0.2F);
        ObjectAnimator behindWaterAlphaOut = ObjectAnimator.ofFloat(behindWater, "alpha", 1.0F, 0.2F);
        ObjectAnimator boatBodyTransOut = ObjectAnimator.ofFloat(allBoatBody, "translationX", 0, ScreenUtils.getScreenWidth(mContext));
        AnimatorSet outSet = new AnimatorSet();
        outSet.setInterpolator(new LinearInterpolator());
        outSet.setDuration(1000);
        outSet.playTogether(bodyTransOut, beforeWaterAlphaOut, behindWaterAlphaOut, boatBodyTransOut);

        AnimatorSet boatBodySet = new AnimatorSet();
        boatBodySet.playSequentially(allBoatBodyIn, boatStopSet, outSet);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(beforeWaterIn, behindWaterIn, boatBodySet, beforeWaterTransRight, behindWaterTrans);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                giftView.setVisibility(View.GONE);
                giftView.setTranslationY(0F);
                isRunning = false;
                if(boatBodyDrawable != null) {
                    boatBodyDrawable.stop();
                }
                if(animationEndListener != null) {
                    animationEndListener.onAnimationEnd();
                }
            }
        });
        animatorSet.start();

    }

    @Override
    protected void recyclResource() {
        ((ImageView)giftView.findViewById(R.id.iv_water_one)).setImageResource(0);
        ((ImageView)giftView.findViewById(R.id.iv_water_two)).setImageResource(0);
        ((ImageView)giftView.findViewById(R.id.iv_water_three)).setImageResource(0);
        ((ImageView)giftView.findViewById(R.id.iv_water_four)).setImageResource(0);
        ((ImageView)giftView.findViewById(R.id.iv_boat_body)).setImageResource(0);
        removeAllViews();

        if(boatBodyDrawable != null) {
            boatBodyDrawable.stop();
            boatBodyDrawable = null;
        }
    }

    @Override
    public void cancleAnimation() {

    }
}
