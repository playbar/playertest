package com.rednovo.libs.widget.emoji;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import com.rednovo.libs.ui.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HorizontalElasticityContainerView extends ViewPager {

    /**
     * HorizontalElasticityContainerView
     *
     * @param context context
     */
    public HorizontalElasticityContainerView(Context context) {
        super(context);
        setOnPageChangeListener(new PageChangeListener());
    }

    /**
     * setContentView
     *
     * @param contentView contentView
     */
    public void setContentView(View contentView) {
        View leftView = new View(getContext());
        View rightView = new View(getContext());
        List<View> views = new ArrayList<View>(3);
        views.add(leftView);
        views.add(contentView);
        views.add(rightView);
        setAdapter(new ViewPagerAdapter<View>(views));
        setCurrentItem(1);
    }

    private class PageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            if (position != 1) {
                setCurrentItem(1);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
    private boolean mIsDisallowIntercept = false;
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // keep the info about if the innerViews do
        // requestDisallowInterceptTouchEvent
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // the incorrect array size will only happen in the multi-touch
        // scenario.
        if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }
}
