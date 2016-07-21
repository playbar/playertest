package com.rednovo.ace.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class LazyFragment extends Fragment {

	protected boolean isVisible;
	protected boolean isOnCreateView = false;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		isOnCreateView = true;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		isOnCreateView = false;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}

	protected void onVisible() {
		lazyLoad();
	}

	protected abstract void lazyLoad();

	protected void onInvisible() {}

}
