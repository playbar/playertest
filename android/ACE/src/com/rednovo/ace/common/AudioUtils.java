package com.rednovo.ace.common;

import java.lang.Thread;
import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

public final class AudioUtils {
	// Use 44.1kHz as the default sampling rate.
	private static final int SAMPLE_RATE_HZ = 44100;
	private static final int VERSION_CODES_LOLLIPOP = 21;

	public static boolean runningOnJellyBeanOrHigher() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean runningOnJellyBeanMR1OrHigher() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	public static boolean runningOnLollipopOrHigher() {
		return Build.VERSION.SDK_INT >= VERSION_CODES_LOLLIPOP;
	}

	/** Helper method for building a string of thread information. */
	public static String getThreadInfo() {
		return "@[name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId() + "]";
	}

	/** Information about the current build, taken from system properties. */
	public static void logDeviceInfo(String tag) {
		Log.d(tag, "Android SDK: " + Build.VERSION.SDK_INT + ", " + "Release: " + Build.VERSION.RELEASE + ", " + "Brand: " + Build.BRAND + ", " + "Device: " + Build.DEVICE + ", " + "Id: " + Build.ID + ", " + "Hardware: " + Build.HARDWARE + ", " + "Manufacturer: " + Build.MANUFACTURER + ", "
				+ "Model: " + Build.MODEL + ", " + "Product: " + Build.PRODUCT);
	}

	/**
	 * Returns the native or optimal output sample rate for this device's primary output stream. Unit is in Hz.
	 */
	@SuppressLint("NewApi")
	public static int getNativeSampleRate(AudioManager audioManager) {
		if (!AudioUtils.runningOnJellyBeanMR1OrHigher()) {
			return SAMPLE_RATE_HZ;
		}
		String sampleRateString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
		return (sampleRateString == null) ? SAMPLE_RATE_HZ : Integer.parseInt(sampleRateString);
	}
}
