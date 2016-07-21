package com.enrique.stackblur;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;

interface BlurProcess {
	int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
	ExecutorService EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS);

	/**
	 * Process the given image, blurring by the supplied radius. If radius is 0, this will return original
	 * 
	 * @param original the bitmap to be blurred
	 * @param radius the radius in pixels to blur the image
	 * @return the blurred version of the image.
	 */
	public Bitmap blur(Bitmap original, float radius);
}
