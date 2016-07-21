package com.seu.magicfilter.utils;

import android.annotation.SuppressLint;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class VideoUtils {

    private static boolean checkCameraFacing(final int facing) {
        if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        CameraInfo info = new CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasBackFacingCamera() {
        return checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    public static boolean hasFrontFacingCamera() {
        return checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    public static int getSdkVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 相机触摸聚焦
     *
     * @param event
     * @param mCamera
     * @param mParameters
     */
    public static void focusOnTouch(MotionEvent event, Camera mCamera, Camera.Parameters mParameters) {
        Rect focusRect = calculateTapArea(event.getRawX(), event.getRawY(), 1f, mCamera);
        Rect meteringRect = calculateTapArea(event.getRawX(), event.getRawY(), 1.5f, mCamera);
        mCamera.cancelAutoFocus();
        if (mParameters != null) {
            List allFocus = mParameters.getSupportedFocusModes();
            Camera.Parameters parameters = mCamera.getParameters();
            if (allFocus.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                focusAreas.add(new Camera.Area(focusRect, 1000));

                parameters.setFocusAreas(focusAreas);
            }

            if (parameters.getMaxNumMeteringAreas() > 0) {
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(meteringRect, 1000));

                parameters.setMeteringAreas(meteringAreas);
            }

            mCamera.setParameters(parameters);
            mCamera.autoFocus(new AutoFocusCallback() {

                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                }
            });
        }
    }

    private static Rect calculateTapArea(float x, float y, float coefficient, Camera mCamera) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int centerX = (int) (x / getResolution(mCamera).width - 1000);
        int centerY = (int) (y / getResolution(mCamera).height - 1000);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    public static Camera.Size getResolution(Camera mCamera) {
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size s = params.getPreviewSize();
        return s;
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    /**
     * 判断是否支持闪光灯
     *
     * @param pm
     * @return
     */
    public static boolean isSupportCameraLedflash(PackageManager pm) {
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo f : features) {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name))
                        return true;
                }
            }
        }
        return false;
    }
}
