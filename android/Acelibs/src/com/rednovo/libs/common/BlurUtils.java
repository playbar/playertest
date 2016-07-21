package com.rednovo.libs.common;

import com.enrique.stackblur.JavaBlurProcess;
import com.enrique.stackblur.NativeBlurProcess;

import android.graphics.Bitmap;

public class BlurUtils {
	private static final String LOG_TAG = "BlurUtils";

	/**
	 * 通过C底层处理毛玻璃效果
	 * 
	 * @param mBitmap
	 * @param radius
	 * @return
	 */
	public static Bitmap processNatively(Bitmap mBitmap, int radius) {
		Bitmap result = null;
		try {
			NativeBlurProcess blur = new NativeBlurProcess();
			result = blur.blur(mBitmap, radius);
		} catch (Exception ex) {
			LogUtils.e(LOG_TAG, "processNatively error");
		}
		return result;
	}

	/**
	 * java层实现毛玻璃效果
	 * 
	 * @param mBitmap
	 * @param radius
	 * @return
	 */
	public static Bitmap processJava(Bitmap mBitmap, int radius) {
		Bitmap result = null;
		try {
			JavaBlurProcess blurProcess = new JavaBlurProcess();
			result = blurProcess.blur(mBitmap, radius);
		} catch (Exception ex) {
			LogUtils.e(LOG_TAG, "processJava error");
		}
		return result;
	}
}
