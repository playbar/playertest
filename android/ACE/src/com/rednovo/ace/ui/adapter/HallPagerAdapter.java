package com.rednovo.ace.ui.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HallPagerAdapter extends FragmentPagerAdapter {
	
	private List<Fragment> hallPagerFragments;
	private Context mContext;
	
	public HallPagerAdapter(FragmentManager fm, Context cxt, List<Fragment> fragments) {

		super(fm);
		
		hallPagerFragments = fragments;
		mContext = cxt;
	}

	@Override
	public Fragment getItem(int position) {
		if(hallPagerFragments != null){
			return hallPagerFragments.get(position);
		}
		
		return null;
	}

	@Override
	public int getCount() {
		if(hallPagerFragments != null){
			return hallPagerFragments.size();
		}
		return 0;
	}

}
