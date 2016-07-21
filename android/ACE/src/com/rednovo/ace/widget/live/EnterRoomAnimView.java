/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.widget.live;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rednovo.ace.R;

/**
 * @author Zhen.Li
 * @fileNmae EnterRoomAnimView
 * @since 2016-03-08
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class EnterRoomAnimView extends RelativeLayout {


    private int count = 0;
    private ImageView tvRoomAnim;
    private Animation animation;
    private AnimatorSet animatorSet;
    private ObjectAnimator aplphaIn;
    private ObjectAnimator scaleX;
    private ObjectAnimator scaleY;
    private ObjectAnimator aplphaOut;

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                int count = getCount();
                int resourceId = 0;
                if (count == 2) {
                    resourceId = R.drawable.enter_room_ready;
                } else {
                    resourceId = R.drawable.enter_room_go;
                }
                tvRoomAnim.setImageResource(resourceId);

                if (count == 2) {
                    startAnim(tvRoomAnim, null);
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                } else {
                    startAnim(tvRoomAnim, animatorListenerAdapter);
                }


            }
        }
    };

    public EnterRoomAnimView(Context context) {
        super(context);
        initView(context);
    }

    public EnterRoomAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    public EnterRoomAnimView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    protected void initView(Context context) {
        setClickable(true);
        setFocusable(true);
        setBackgroundResource(R.color.clolor_88000000);
        tvRoomAnim = new ImageView(context);
        tvRoomAnim.setScaleType(ImageView.ScaleType.FIT_CENTER);
        tvRoomAnim.setClickable(false);
        tvRoomAnim.setFocusable(false);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); // ShowUtils.dip2px(context, 102), ShowUtils.dip2px(context, 39)
        layoutParams.addRule(CENTER_IN_PARENT);
        addView(tvRoomAnim, layoutParams);
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_room_in);

    }


    public void startAnimView() {
        mHandler.sendEmptyMessageDelayed(0, 1000);

    }


    private void startAnim(final ImageView view, AnimatorListenerAdapter animatorListenerAdapter) {
        animatorSet = new AnimatorSet();
        aplphaIn = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1.0f);
        scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f, 1.5f);
        scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f, 1.5f);
        aplphaOut = ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0.0f);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new LinearInterpolator());
        if (animatorListenerAdapter != null) {
            animatorSet.addListener(animatorListenerAdapter);
        }
        animatorSet.playTogether(aplphaIn, scaleX, scaleY);
        animatorSet.start();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }


    private int getCount() {
        count--;
        if (count <= 0 || count >= 2) {
            count = 2;
        }
        return count;
    }

    private AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(android.animation.Animator animation) {
            super.onAnimationEnd(animation);
            EnterRoomAnimView.this.setVisibility(View.GONE);
        }
    };


}
