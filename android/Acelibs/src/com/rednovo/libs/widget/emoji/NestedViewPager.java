package com.rednovo.libs.widget.emoji;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 嵌套使用ViewPager的时候，子ViewPager必须用这个控件，否则会导致滑动冲突
 *
 * @author ll
 * @version 3.0.0
 */
public class NestedViewPager extends ViewPager {

    private boolean mAllowTouch = true;
    private float mLastMotionX;
    private static final int SLOP = 5;

    /**
     * @param context context
     * @param attrs   attrs
     */
    public NestedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context context
     */
    public NestedViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                mAllowTouch = true;
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mAllowTouch) {
                    if (x - mLastMotionX > SLOP && getCurrentItem() == 0) {
                        mAllowTouch = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }


                    if (x - mLastMotionX < -SLOP && getCurrentItem() == getAdapter().getCount() - 1) {
                        mAllowTouch = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
