package com.rednovo.ace.widget.tabbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.rednovo.ace.R;
import com.rednovo.libs.common.LogUtils;

/**
 * @author zhen.Li/2015-10-13
 */
public class NavigationBar extends LinearLayout {

    private TabBarClickListener mTanBarClickListener;
    private TabRadioButton[] rbs;
    private int mIndex = 0;

    public NavigationBar(Context context) {
        super(context);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        initView();
        super.onFinishInflate();
    }


    private void initView() {
        rbs = new TabRadioButton[2];
        rbs[0] = (TabRadioButton) findViewById(R.id.rd_hall);
        rbs[1] = (TabRadioButton) findViewById(R.id.rd_my);
    }

    public void selectFristTab() {

        changeTab(rbs[0].getId());
    }

    public void setSelection(int id) {
        if (id != rbs[mIndex].getId()) {
            changeTab(id);
        }
        if (mTanBarClickListener != null)
            mTanBarClickListener.onTabBarClickListener(mIndex);
    }

    public void setNavigationBarClickListener(TabBarClickListener tabBarClickListener) {
        this.mTanBarClickListener = tabBarClickListener;

    }

    public interface TabBarClickListener {
        public void onTabBarClickListener(int itemId);
    }

    public void changeTab(int tabId) {
        for (int idx = 0; idx < rbs.length; idx++) {
            LogUtils.v("MainActivity", "idx=" + rbs[idx].getId());
            if (tabId == rbs[idx].getId()) {
                rbs[idx].check(true);
                //rbs[idx].setClickable(false);
                //rbs[idx].setFocusable(false);
                mIndex = idx;
            } else {
                rbs[idx].check(false);
            }
        }
    }

}
