package com.rednovo.libs.widget.pulltorefresh.library.internal;

import android.util.Log;

public class PulltoRefreshUtils {

	
	/**
	 * 刷新成功
	 */
	public static final int PUll_REFRESH_SUCCESS = 1;
	/**
	 * 刷新失败
	 */
	public static final int PUll_REFRESH_FAIL = 2;
	/**
	 * 已经加载全部数据
	 */
	public static final int PUll_REFRESH_ALL = 3;

	static final String LOG_TAG = "PullToRefresh";

	public static void warnDeprecation(String depreacted, String replacement) {
		Log.w(LOG_TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
	}

	
}
