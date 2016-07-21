package com.rednovo.ace.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class UserZonePagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> pagerFragments;
	private Context mContext;

	public UserZonePagerAdapter(FragmentManager fm, Context cxt, List<Fragment> fragments) {

		super(fm);

		pagerFragments = fragments;
		mContext = cxt;
	}

	@Override
	public Fragment getItem(int position) {
		if(pagerFragments != null){
			return pagerFragments.get(position);
		}
		
		return null;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}

	private int mChildCount = 0;

	@Override
	public void notifyDataSetChanged() {
		mChildCount = getCount();
		super.notifyDataSetChanged();
	}

	@Override
	public int getItemPosition(Object object)   {
		if (mChildCount > 0) {
			mChildCount--;
			return POSITION_NONE;
		}
		return super.getItemPosition(object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}

	@Override
	public int getCount() {
		if(pagerFragments != null){
			return pagerFragments.size();
		}
		return 0;
	}

}
