package com.rednovo.ace.widget.live;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rednovo.ace.R;
import com.rednovo.libs.common.ShowUtils;

/**
 * 直播暂停与恢复
 */
public class LivePauseView extends RelativeLayout {
    private ImageView pauseImg;
    public LivePauseView(Context context) {
        super(context);
        initView(context);
    }

    public LivePauseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LivePauseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context mContext) {
        pauseImg = new ImageView(mContext);
        pauseImg.setImageDrawable(getResources().getDrawable(R.drawable.live_pause));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_HORIZONTAL);
        layoutParams.topMargin = ShowUtils.dip2px(mContext, 200);
        addView(pauseImg, layoutParams);
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            pauseImg.setImageResource(0);
            if (Build.VERSION.SDK_INT >= 16) {
                setBackground(null);

            } else {
                setBackgroundDrawable(null);
            }
        } catch (Exception ex) {

        }
    }

    public boolean isPause(){
        return getVisibility() == View.VISIBLE;
    }

    /**
     * 是否显示暂停
     * @param needPause
     */
    public void pause(boolean needPause){
        if(needPause){
            setVisibility(VISIBLE);
            Drawable drawable = getResources().getDrawable(R.drawable.live_fuzzy_bg);
            if (Build.VERSION.SDK_INT >= 16) {
                setBackground(drawable);

            } else {
                setBackgroundDrawable(drawable);
            }
        }else{
            setVisibility(GONE);
            try {
                if (Build.VERSION.SDK_INT >= 16) {
                    setBackground(null);

                } else {
                    setBackgroundDrawable(null);
                }
            } catch (Exception ex) {

            }
        }
    }
}
