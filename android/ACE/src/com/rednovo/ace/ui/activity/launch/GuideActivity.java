package com.rednovo.ace.ui.activity.launch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.rednovo.ace.R;
import com.rednovo.ace.common.CacheUserInfoUtils;
import com.rednovo.ace.ui.activity.LoginActivity;
import com.rednovo.ace.ui.activity.MainActivity;
import com.rednovo.libs.common.StatusBarUtils;
import com.rednovo.libs.ui.base.BaseActivity;

import java.util.ArrayList;


public class GuideActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager guidePager;
    /**
     * 装分页显示的view的数组
     */
    private ArrayList<View> pageViews;
    private GuidePagerAdapter adapter;
    private LayoutInflater mInflater;
    private ImageView imgGuideOne;
    private ImageView imgGuideTwo;
    private ImageView imgGuideThree;

    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_guide_layout);
        guidePager = (ViewPager) findViewById(R.id.guidePager);
        pageViews = new ArrayList<View>(3);
        adapter = new GuidePagerAdapter();
        mInflater = getLayoutInflater();
        View viewOne = mInflater.inflate(R.layout.layout_guide_one, null);
        View viewTwo = mInflater.inflate(R.layout.layout_guide_two, null);
        View viewThree = mInflater.inflate(R.layout.layout_guide_three, null);
        imgGuideOne = (ImageView) viewOne.findViewById(R.id.img_guide_one);
        imgGuideTwo = (ImageView) viewTwo.findViewById(R.id.img_guide_two);
        imgGuideThree = (ImageView) viewThree.findViewById(R.id.img_guide_three);
        viewThree.findViewById(R.id.start).setOnClickListener(this);
        pageViews.add(viewOne);
        pageViews.add(viewTwo);
        pageViews.add(viewThree);
        guidePager.setAdapter(adapter);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setTranslucentImmersionBar(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        CacheUserInfoUtils.addVersionCode();
        Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
        intent.putExtra("isLaunchGo", true);
        redirectClose(intent);
    }

    private class GuidePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pageViews.size();
        }


        @Override
        public Object instantiateItem(View collection, int position) {

            ((ViewPager) collection).addView(pageViews.get(position), 0);
            return pageViews.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView(pageViews.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (object);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imgGuideOne != null) {
            imgGuideOne.setImageResource(0);
            imgGuideOne.setBackgroundResource(0);
        }
        if (imgGuideTwo != null) {
            imgGuideTwo.setImageResource(0);
            imgGuideTwo.setBackgroundResource(0);
        }
        if (imgGuideThree != null) {
            imgGuideThree.setImageResource(0);
            imgGuideThree.setBackgroundResource(0);
        }
    }
}
