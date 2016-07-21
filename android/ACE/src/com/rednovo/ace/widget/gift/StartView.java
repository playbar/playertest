package com.rednovo.ace.widget.gift;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

/**
 * Created by lilong on 16/5/5.
 */
public class StartView extends ImageView{

    private ObjectAnimator viewShow;
    private ObjectAnimator viewHide;
    private boolean isAnimaStop = false;

    private Handler mHandler = new Handler(Looper.myLooper());

    public StartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public StartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StartView(Context context) {
        super(context);
        init();
    }

    @Override
    public void setVisibility(int visibility) {
        if(visibility == View.GONE) {
            visibility = View.INVISIBLE;
        }
        super.setVisibility(visibility);
    }

    private void init() {
        this.setVisibility(View.INVISIBLE);
    }


    public void setAnima(int visibility) {
        if(visibility == View.VISIBLE) {
            isAnimaStop = false;
            setRotation((float) (Math.random() * 360));
            startAnimation();
        } else {
            isAnimaStop = true;
            if(viewShow != null && viewShow.isRunning()) {
                viewShow.cancel();
            }

            if(viewHide != null && viewHide.isRunning()) {
                viewHide.cancel();
            }
            setVisibility(View.INVISIBLE);
        }
    }

    private void startAnimation() {
        PropertyValuesHolder alphaIn = PropertyValuesHolder.ofFloat("alpha", 0F, 1.0F);
        PropertyValuesHolder alphaOut = PropertyValuesHolder.ofFloat("alpha", 1.0F, 0F);
        PropertyValuesHolder scaleUpX = PropertyValuesHolder.ofFloat("scaleX", 0F, 1.0F);
        PropertyValuesHolder scaleUpY = PropertyValuesHolder.ofFloat("scaleY", 0F, 1.0F);
        PropertyValuesHolder scaleDownX = PropertyValuesHolder.ofFloat("scaleX", 1.0F, 0F);
        PropertyValuesHolder scaleDownY = PropertyValuesHolder.ofFloat("scaleY", 1.0F, 0F);

        viewShow = ObjectAnimator.ofPropertyValuesHolder(this, alphaIn, scaleUpX, scaleUpY);
        viewShow.setDuration(400);
        viewShow.setInterpolator(new LinearInterpolator());
        viewShow.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isAnimaStop) {
                    viewHide.start();
                }
            }
        });

        viewHide = ObjectAnimator.ofPropertyValuesHolder(this, alphaOut, scaleDownX, scaleDownY);
        viewHide.setDuration(400);
        viewHide.setInterpolator(new LinearInterpolator());
        viewHide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isAnimaStop) {
                    viewShow.start();
                }
            }
        });
        long delayed = (long) (Math.random() * 200);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                viewShow.start();
            }
        }, delayed);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(viewShow, viewHide);
        set.setInterpolator(new LinearInterpolator());
    }

}
