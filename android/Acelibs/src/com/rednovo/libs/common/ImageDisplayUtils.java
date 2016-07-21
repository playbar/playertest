package com.rednovo.libs.common;


import android.widget.ImageView;

public class ImageDisplayUtils {
    /**
     * 设置ImageView的图片资源，捕获内存溢出Error，防止应用崩溃
     * @param imageView
     * @param resId
     */
    public static void setImageResourceSafely(ImageView imageView, int resId) {
        try {
            imageView.setImageResource(resId);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
