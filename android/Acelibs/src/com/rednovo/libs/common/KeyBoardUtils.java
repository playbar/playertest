package com.rednovo.libs.common;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.R;

/**
 * 打开或关闭软键盘
 */
public class KeyBoardUtils{
	/**
	 * 打开软键盘
	 * @param mEditText 输入框
	 * @param mContext 上下文
	 */
	public static void openKeybord(EditText mEditText, Context mContext){
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	/**
	 * 关闭软键盘
	 * @param mEditText 输入框
	 * @param mContext 上下文
	 */
	public static void closeKeybord(EditText mEditText, Context mContext){
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
	}
	/** 键盘的显示状态*/
	private static boolean isKeyBoardVisible;
	public static void checkKeyboard(final View parentLayout,final ScrollView scrollview,final View v) {
		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);
						int screenHeight = parentLayout.getRootView().getHeight();
						int heightDifference = screenHeight - (r.bottom);
						if (heightDifference > 100) {
							if (!isKeyBoardVisible) {
								int[] location = new int[2];
								v.getLocationOnScreen(location);
								int offset = location[1] + v.getHeight() / 2 - scrollview.getMeasuredHeight();
								if (offset < 0) {
									offset = 0;
								}
								scrollview.smoothScrollTo(0, offset);
							}
							isKeyBoardVisible = true;
						} else {
							isKeyBoardVisible = false;
						}
					}
				});
	}

	/**
	 * 记录键盘高度
	 * @param parentLayout
	 */
	public static void recordKeyboardHeight(final View parentLayout) {
		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Rect r = new Rect();
				parentLayout.getWindowVisibleDisplayFrame(r);
				int screenHeight = parentLayout.getRootView().getHeight();
				int keyboardHeight = screenHeight - (r.bottom);
				if (keyboardHeight > 100) {
					if (!isKeyBoardVisible) {
						record(keyboardHeight);
					}
					isKeyBoardVisible = true;
				} else {
					isKeyBoardVisible = false;
				}
			}
		});
	}

	public static void record(int keyboardHeight) {
		int height = (Integer) SPUtils.get(BaseApplication.getApplication().getApplicationContext(), "keyboard_height", 0);
		if (height != keyboardHeight) {
            SPUtils.put(BaseApplication.getApplication().getApplicationContext(),"keyboard_height", keyboardHeight);
        }
	}

	/**
	 * 键盘高度
	 * @return
	 */
	public static int getKeyboardHeight() {
		if (LAST_SAVE_KEYBOARD_HEIGHT == 0) {
			LAST_SAVE_KEYBOARD_HEIGHT = (Integer) SPUtils.get(BaseApplication.getApplication().getApplicationContext(),"keyboard_height", 0);
		}
		return LAST_SAVE_KEYBOARD_HEIGHT;
	}
	private static int LAST_SAVE_KEYBOARD_HEIGHT = 0;

	public static boolean saveKeyboardHeight(final Context context, int keyboardHeight) {
		if (LAST_SAVE_KEYBOARD_HEIGHT == keyboardHeight) {
			return false;
		}

		if (keyboardHeight < 0) {
			return false;
		}

		LAST_SAVE_KEYBOARD_HEIGHT = keyboardHeight;
		LogUtils.d("KeyBordUtil", String.format("save keyboard: %d", keyboardHeight));
		record(keyboardHeight);
		return true;
	}
	public static int getValidPanelHeight(final Context context) {
		final int maxPanelHeight = getMaxPanelHeight(context.getResources());
		final int minPanelHeight = getMinPanelHeight(context.getResources());

		int validPanelHeight = getKeyboardHeight();

		validPanelHeight = Math.max(minPanelHeight, validPanelHeight);
		validPanelHeight = Math.min(maxPanelHeight, validPanelHeight);
//        return validPanelHeight;
		return minPanelHeight;
	}
	private static int MAX_PANEL_HEIGHT = 0;
	private static int MIN_PANEL_HEIGHT = 0;

	public static int getMaxPanelHeight(final Resources res) {
		if (MAX_PANEL_HEIGHT == 0) {
			MAX_PANEL_HEIGHT = res.getDimensionPixelSize(R.dimen.max_panel_height);
		}
		return MAX_PANEL_HEIGHT;
	}

	public static int getMinPanelHeight(final Resources res) {
		if (MIN_PANEL_HEIGHT == 0) {
			MIN_PANEL_HEIGHT = res.getDimensionPixelSize(R.dimen.min_panel_height);
		}
		return MIN_PANEL_HEIGHT;
	}
}
