package com.rednovo.ace.widget.gift;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rednovo.ace.R;

/**
 * Created by lilong on 16/5/6.
 */
public abstract class BaseGiftView extends RelativeLayout {

    protected Context mContext;
    protected View giftView;
    /**
     * 动画是否正在执行中标识
     * true：正在执行中
     * false：已停止
     */
    protected boolean isRunning = false;

    public BaseGiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public BaseGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public BaseGiftView(Context context, int layoutId) {
        this(context);
        init(layoutId);
    }

    public BaseGiftView(Context context) {
        super(context);
        mContext = context;
    }

    private void init(int layoutId) {
        giftView = View.inflate(mContext, layoutId, this);
    }

    public abstract void cancleAnimation();

    interface AnimationEndListener{

        void onAnimationEnd();
    }

    protected void startAnimation(ViewGroup speciaGiftContainer, AnimationEndListener animationEndListener) {
        isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    protected abstract void recyclResource();
}
