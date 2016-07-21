package com.rednovo.ace.widget.live;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 监听键盘的弹出和收回
 * 当使用了透明电池栏后,此监听失效
 */
public class MonitorSizeEventFrameLayout extends FrameLayout {

    private int mPreviousHeight;

    
    public MonitorSizeEventFrameLayout(Context context) {
        super(context);
    }
    /**
     * MonitorSizeEventFrameLayout
     *
     * @param context context
     * @param attrs   attrs
     */
    public MonitorSizeEventFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (mPreviousHeight != 0) {
        	int navigationBarHeight = getNavigationBarHeight();
            if (measureHeight < mPreviousHeight - navigationBarHeight) {	// 减去底部虚拟键盘高度，过滤掉虚拟键盘显隐产生的页面高度变化
//                EventBus.getDefault().post(new KeyBoardEvent(true));
            } else if (measureHeight - navigationBarHeight > mPreviousHeight) {
//                EventBus.getDefault().post(new KeyBoardEvent(false));
            }
        }
        mPreviousHeight = measureHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    /**
     * 获取底部虚拟键Navigation Bar的高度
     * @return
     */
    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}
