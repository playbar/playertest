package com.rednovo.libs.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 *
 * @param <T>
 */
public class ViewPagerAdapter<T extends View> extends PagerAdapter {

    private List<T> mViews;

    /**
     * constructor
     *
     * @param views views
     */
    public ViewPagerAdapter(List<T> views) {
        mViews = views;
    }

    @Override
    public int getCount() {
        return mViews != null ? mViews.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position));
        return mViews.get(position);
    }
}
