package com.rednovo.ace.widget.live;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.libs.common.ShowUtils;

/**
 * Created by lizhen on 16/3/17.
 */
public class LiveLoadingView extends RelativeLayout {

    private ImageView loadingImg;
    private AnimationDrawable animationDrawable;
    private TextView textView;

    public LiveLoadingView(Context context) {
        super(context);
        initView(context);
    }

    public LiveLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LiveLoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context mContext) {
        loadingImg = new ImageView(mContext);
        loadingImg.setId(R.id.loading_view);
        loadingImg.setImageDrawable(getResources().getDrawable(R.drawable.live_load));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = ShowUtils.dip2px(mContext, 200);
        layoutParams.addRule(CENTER_HORIZONTAL);
        addView(loadingImg, layoutParams);
        textView = new TextView(mContext);
        textView.setBackgroundResource(R.drawable.live_loading_text);
        textView.setText(getContext().getString(R.string.live_loading));
        textView.setTextSize(12);
        textView.setTextColor(getResources().getColor(R.color.color_white));
        textView.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams params = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_HORIZONTAL);
        params.height = ShowUtils.dip2px(mContext, 23);
        params.addRule(RelativeLayout.BELOW, R.id.loading_view);
        params.topMargin = ShowUtils.dip2px(mContext, 20);
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        params.bottomMargin = ShowUtils.dip2px(mContext, 210);
        addView(textView, params);
    }

    public void startLoading() {
        setVisibility(VISIBLE);
        try {
            animationDrawable = (AnimationDrawable) loadingImg.getDrawable();
            animationDrawable.start();
            Drawable drawable = getResources().getDrawable(R.drawable.live_fuzzy_bg);
            if (Build.VERSION.SDK_INT >= 16) {
                setBackground(drawable);

            } else {
                setBackgroundDrawable(drawable);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void stopLoading() {
        setVisibility(GONE);
        try {
            if (animationDrawable != null) {
                loadingImg.clearAnimation();
                animationDrawable.stop();
                animationDrawable = null;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                setBackground(null);

            } else {
                setBackgroundDrawable(null);
            }
//            loadingImg.setImageDrawable(null);
//            loadingImg.setImageResource(0);
        } catch (Exception ex) {

        }

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animationDrawable != null) {
            animationDrawable.stop();
            animationDrawable = null;
        }
        loadingImg.setImageDrawable(null);
        loadingImg.setImageResource(0);
        textView.setBackgroundResource(0);
        if (Build.VERSION.SDK_INT >= 16) {
            setBackground(null);

        } else {
            setBackgroundDrawable(null);
        }

    }

    public boolean isShowLoading(){
        if(getVisibility() == View.VISIBLE){
            return true;
        }else{
            return false;
        }
    }
}
