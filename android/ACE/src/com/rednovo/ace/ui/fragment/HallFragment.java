package com.rednovo.ace.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.ui.activity.SearchActivity;
import com.rednovo.ace.ui.adapter.HallPagerAdapter;
import com.rednovo.libs.common.SystemBarTintManager;

public class HallFragment extends BaseFragment implements OnPageChangeListener, OnClickListener {

	private final String KEY_ID = "key_id";

	private View hallView;
	private TextView tvHotBtn;
	private TextView tvSubscribeBtn;
	private ImageView imgHallTitleAnim;
	private LinearLayout llHallTitle;
	private ViewPager vpHotPager;

	private List<Fragment> fragments;

	private HallPagerAdapter hallPagerAdapter;

	private ImageView ivSearch;

	private int mCurrentPager = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		hallView = inflater.inflate(R.layout.fragment_hall_layout, container, false);
		tvHotBtn = (TextView) hallView.findViewById(R.id.tv_hot_btn);
		tvSubscribeBtn = (TextView) hallView.findViewById(R.id.tv_subscribe_btn);
		imgHallTitleAnim = (ImageView) hallView.findViewById(R.id.iv_hall_title_anim);
		llHallTitle = (LinearLayout) hallView.findViewById(R.id.ll_hall_title);
		vpHotPager = (ViewPager) hallView.findViewById(R.id.vp_hall_list);
		ivSearch = (ImageView) hallView.findViewById(R.id.iv_search);

		tvHotBtn.setOnClickListener(this);
		tvSubscribeBtn.setOnClickListener(this);
		ivSearch.setOnClickListener(this);
		vpHotPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

		initHotPager();
		return hallView;
	}





	private void initHotPager() {
		fragments = new ArrayList<Fragment>(2);

		fragments.add(new HallPagerFragment());
		fragments.add(new HallPagerFragment());

		for (int i = 0; i < fragments.size(); i++) {
			Bundle bundle = new Bundle();
			bundle.putInt(HallPagerFragment.KEY_ID, i);

			fragments.get(i).setArguments(bundle);
		}

		hallPagerAdapter = new HallPagerAdapter(getFragmentManager(), getActivity(), fragments);
		vpHotPager.setAdapter(hallPagerAdapter);

		vpHotPager.setOnPageChangeListener(this);
		// fragments.get(0).onLoadNewData();
	}

	private void switchTitleBar(int pager) {
		getSelectAnimation(pager);
		mCurrentPager = pager;
		if (pager == 0) {
			tvHotBtn.setEnabled(false);
			tvSubscribeBtn.setEnabled(true);
		} else if (pager == 1) {
			tvHotBtn.setEnabled(true);
			tvSubscribeBtn.setEnabled(false);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int pager) {
		switchTitleBar(pager);
		// fragments.get(pager).onLoadNewData();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_hot_btn:
			vpHotPager.setCurrentItem(0, true);
			break;
		case R.id.tv_subscribe_btn:
			vpHotPager.setCurrentItem(1, true);
			break;
		case R.id.iv_search:
			Intent intent = new Intent(getActivity(), SearchActivity.class);
			startActivity(intent);
			break;
		}
	}

	private void getSelectAnimation(int page){
		float formX = 0;
		float toX = 0;
		switch (page){
			case 0:
				formX = tvSubscribeBtn.getX();
				toX = tvHotBtn.getX();
				break;
			case 1:
				formX = tvHotBtn.getX();
				toX = tvSubscribeBtn.getX();
				break;
		}
		TranslateAnimation translate = new TranslateAnimation(formX, toX, 0, 0);
		translate.setDuration(500);
		translate.setFillAfter(true);
		translate.setInterpolator(new OvershootInterpolator());

		imgHallTitleAnim.startAnimation(translate);
	}

}
