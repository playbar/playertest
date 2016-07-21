package com.rednovo.ace.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rednovo.ace.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dk on 16/2/29.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    List<BaseFragment> fragmentList = new ArrayList<BaseFragment>();
    public ViewPagerAdapter(FragmentManager fm,List<BaseFragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public BaseFragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
