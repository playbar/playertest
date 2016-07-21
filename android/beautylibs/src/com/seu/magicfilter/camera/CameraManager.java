package com.seu.magicfilter.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

import com.seu.magicfilter.utils.Logger;
import com.seu.magicfilter.utils.VideoUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraManager {
    private String LOG_TAG = "CameraManager";
    private Camera mCamera = null;
    private int mCurrentFacing = Camera.CameraInfo.CAMERA_FACING_FRONT; // 默认前置摄像头;
    private CameraReadyListener cameraReadyListener;
    private boolean isOpenFlash;
    private Context mContext;
    private int nCameraNum = 2;
    private boolean mCameraReady;
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();

    private Handler mHandler = new Handler(Looper.myLooper());
    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
        }
    };

    private Parameters parameters;


    public CameraManager(Context context) {
        this.mContext = context;
        mCameraReady = true;
    }

    public Camera getCamera() {
        return mCamera;
    }

    private Camera initCamera(int cameraId) {
        int frontIndex = -1;
        int backIndex = -1;
        int cameraCount = Camera.getNumberOfCameras();
        LogV("cameraCount: " + cameraCount);
        this.nCameraNum = cameraCount;
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontIndex = cameraIndex;
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backIndex = cameraIndex;
            }
        }
        mCurrentFacing = cameraId;
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT && frontIndex != -1) {
            return Camera.open(frontIndex);
        } else if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK && backIndex != -1) {
            return Camera.open(backIndex);
        }
        return null;
    }

    public void switchCameraFacing() {
        if (mCurrentFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        isOpenFlash = false;
    }

    public synchronized void switchFlash() {
        if (mCurrentFacing == Camera.CameraInfo.CAMERA_FACING_FRONT)
            return;
        if (!VideoUtils.isSupportCameraLedflash(mContext.getPackageManager())) {
            return;
        }
        Camera.Parameters mParameters = mCamera.getParameters();
        if (isOpenFlash) {
            isOpenFlash = false;
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        } else {
            isOpenFlash = true;
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }

        mCamera.setParameters(mParameters);
    }

    public void focusOnTouch(MotionEvent event) {
        VideoUtils.focusOnTouch(event, mCamera, parameters);
    }

    public void openCamera() {
        openCamera(mCurrentFacing);
    }

    public synchronized void openCamera(int cameraId) {
        if (mCamera != null) {
            releaseCamera();
        }
        try {
            mCamera = initCamera(cameraId);
            setDefaultParameters();
            mCameraReady = true;
        } catch (Throwable e) {
            // if (cameraReadyListener != null) {
            //   cameraReadyListener.onCameraFailed();
            //}
        }
    }

    public synchronized void releaseCamera() {
        if (mCamera != null && mCameraReady) {
            isOpenFlash = false;
            mCamera.cancelAutoFocus();
            mHandler.removeCallbacks(mRunable);
            mHandler.removeCallbacksAndMessages(null);
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            mCameraReady = false;
        }
    }

    public void resumeCamera() {
        openCamera();
    }

    public void setParameters(Parameters parameters) {
        mCamera.setParameters(parameters);
    }

    public Parameters getParameters() {
        if (mCamera != null)
            mCamera.getParameters();
        return null;
    }

    private void setDefaultParameters() {
        parameters = mCamera.getParameters();
//        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
//            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        }
        if (parameters.getSupportedFocusModes().equals(mCamera.getParameters().FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        List<Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
        Point previewSize = findBestPanoPreviewSize(sizes);
        //parameters.setPreviewSize(1280, 720);//640：360
        parameters.setPreviewSize(previewSize.x, previewSize.y);
        mCamera.setParameters(parameters);
    }

    public Point findBestPanoPreviewSize(List<Size> supportedSizes) {
        Collections.sort(supportedSizes, sizeComparator);
        Point output = null;
        for (Size size : supportedSizes) {
            int h = size.height;
            int w = size.width;
            //h:360 w:640

            if (h < 360 && w < 640) {
                continue;
            }
            if ((h * 16 != w * 9)) {
                continue;
            }
            output = new Point(w, h);
        }
        if (output == null) {
            output = new Point(1280, 720);
        }
        return output;
    }


    private Size getLargePreviewSize() {
        if (mCamera != null) {
            List<Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
            Size temp = sizes.get(0);

            for (int i = 1; i < sizes.size(); i++) {
                if (temp.width < sizes.get(i).width)
                    temp = sizes.get(i);
            }
            return temp;
        }
        return null;
    }


    public Size getPreviewSize() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void startPreview(SurfaceTexture surfaceTexture) {
        try {
            if (mCamera != null && surfaceTexture != null && mCameraReady) {
                mCamera.setPreviewTexture(surfaceTexture);
                mCamera.startPreview();
                mCamera.cancelAutoFocus();
            }
            mHandler.postDelayed(mRunable, 1000);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            if (cameraReadyListener != null) {
                cameraReadyListener.onCameraFailed();
            }
        }
    }

    public void startPreview() {
        if (mCamera != null)
            mCamera.startPreview();

    }

    public void stopPreview() {
        mCamera.stopPreview();
    }

    public CameraInfo getCameraInfo() {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(mCurrentFacing, cameraInfo);
        return cameraInfo;
    }

    public int getOrientation() {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(mCurrentFacing, cameraInfo);
        return cameraInfo.orientation;
    }

    public void setCameraReadyListener(CameraReadyListener onCameraReadyListener) {
        this.cameraReadyListener = onCameraReadyListener;
    }

    public interface CameraReadyListener {
        void onCameraFailed();
    }

    public boolean isFlipHorizontal() {
        return getCameraInfo().facing == CameraInfo.CAMERA_FACING_FRONT ? true : false;
    }


    public void setRotation(int rotation) {
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotation);
        mCamera.setParameters(params);
    }

    public void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback, Camera.PictureCallback jpegCallback) {
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    public int getnCameraNum() {
        return nCameraNum;
    }

    public int getCamerFacing() {
        return mCurrentFacing;
    }

    public void LogV(String tag) {
        Logger.v(LOG_TAG, tag);
    }

    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            if (mCamera != null && mCameraReady)
                mCamera.autoFocus(mAutoFocusCallback);
        }
    };

    public class CameraSizeComparator implements Comparator<Size> {
        // 按升序排列
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }
}
