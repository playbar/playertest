package com.rednovo.ace.widget.gift;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
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
 * Created by lilong on 16/5/9.
 * 花束礼物动画布局
 */
public class FlowerGiftView extends BaseGiftView {

    private StartView startFirst;
    private StartView startSecond;
    private StartView startThird;
    private StartView startFourth;
    private StartView startFifth;
    private StartView startSixth;
    private StartView startSeventh;
    private StartView startEighth;
    private AnimatorSet animatorSet;

    public FlowerGiftView(Context context) {
        super(context, R.layout.flower_layout);


        startFirst = (StartView) findViewById(R.id.start_first);
        startSecond = (StartView) findViewById(R.id.start_second);
        startThird = (StartView) findViewById(R.id.start_third);
        startFourth = (StartView) findViewById(R.id.start_fourth);
        startFifth = (StartView) findViewById(R.id.start_fifth);
        startSixth = (StartView) findViewById(R.id.start_sixth);
        startSeventh = (StartView) findViewById(R.id.start_seventh);
        startEighth = (StartView) findViewById(R.id.start_eighth);

    }

    @Override
    public void startAnimation(ViewGroup speciaGiftContainer, AnimationEndListener animationEndListener) {
        super.startAnimation(speciaGiftContainer, animationEndListener);
        if(speciaGiftContainer.getChildAt(0) instanceof FlowerGiftView) {
            giftView.setVisibility(View.VISIBLE);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            params.bottomMargin = ShowUtils.dip2px(mContext, 60);
            speciaGiftContainer.addView(this);
            this.setLayoutParams(params);

        }

        startAnimation(animationEndListener);
    }

    private void startAnimation(final AnimationEndListener animationEndListener) {
        PropertyValuesHolder transY = PropertyValuesHolder.ofFloat("translationY", ScreenUtils.getScreenHeight(mContext), 0F);
        PropertyValuesHolder alphaIn = PropertyValuesHolder.ofFloat("alpha", 0F, 1.0F);
        PropertyValuesHolder alphaOut = PropertyValuesHolder.ofFloat("alpha", 1.0F, 0F);

        // 1.花束移入屏幕伴随淡入动画
        ObjectAnimator viewIn = ObjectAnimator.ofPropertyValuesHolder(this, transY, alphaIn);
        viewIn.setDuration(800);
        viewIn.setInterpolator(new DecelerateInterpolator());

        // 2.花束停留在屏幕中
        ObjectAnimator emptyAnim = ObjectAnimator.ofFloat(this, "alpha", 1.0F, 1.0F);
        emptyAnim.setDuration(3000);
        emptyAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                // 3.1花束移入动画停止时，启动花束上星星闪烁动画
                startFirst.setAnima(View.VISIBLE);
                startSecond.setAnima(View.VISIBLE);
                startThird.setAnima(View.VISIBLE);
                startFourth.setAnima(View.VISIBLE);
                startFifth.setAnima(View.VISIBLE);
                startSixth.setAnima(View.VISIBLE);
                startSeventh.setAnima(View.VISIBLE);
                startEighth.setAnima(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 3.2花束开始退出屏幕时隐藏星星
                startFirst.setAnima(View.GONE);
                startSecond.setAnima(View.GONE);
                startThird.setAnima(View.GONE);
                startFourth.setAnima(View.GONE);
                startFifth.setAnima(View.GONE);
                startSixth.setAnima(View.GONE);
                startSeventh.setAnima(View.GONE);
                startEighth.setAnima(View.GONE);
            }
        });

        // 4.花束淡出屏幕动画
        ObjectAnimator viewOut = ObjectAnimator.ofPropertyValuesHolder(this, alphaOut);
        viewOut.setDuration(800);
        viewOut.setInterpolator(new LinearInterpolator());
        viewOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                giftView.setVisibility(View.GONE);
                isRunning = false;
                if(animationEndListener != null) {
                    animationEndListener.onAnimationEnd();
                }
            }
        });

        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(viewIn, emptyAnim, viewOut);
        animatorSet.start();
    }

    @Override
    public void recyclResource() {
        ((ImageView)giftView.findViewById(R.id.iv_flower_bouquet)).setImageResource(0);
        removeAllViews();
    }

    @Override
    public void cancleAnimation() {

        if(animatorSet != null) {
            animatorSet.cancel();
        }
    }
}
