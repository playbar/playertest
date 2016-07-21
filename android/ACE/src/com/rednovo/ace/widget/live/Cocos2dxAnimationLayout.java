package com.rednovo.ace.widget.live;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.rednovo.ace.common.Cocos2dxAnimationAttacher;
import com.rednovo.ace.data.events.ReciveGiftInfo;
import com.rednovo.ace.ui.activity.live.LiveBaseActivtiy;
import com.rednovo.libs.BaseApplication;
import com.seu.magiccamera.activity.Constants;
import com.seu.magicfilter.camera.MagicCameraDisplay;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxEditBoxHelper;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxHandler;
import org.cocos2dx.lib.Cocos2dxHelper;
import org.cocos2dx.lib.Cocos2dxRenderer;
import org.cocos2dx.lib.Cocos2dxVideoHelper;
import org.cocos2dx.lib.Cocos2dxVideoView;
import org.cocos2dx.lib.Cocos2dxWebViewHelper;
import org.cocos2dx.lib.ResizeLayout;

import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by Dk on 16/5/23.
 */
public class Cocos2dxAnimationLayout extends ResizeLayout implements Cocos2dxHelper.Cocos2dxHelperListener, Cocos2dxVideoView.OnVideoEventListener {

    private static final String TAG = "Cocos2dxAnimationLayout";

    private MagicCameraDisplay mMagicCameraDisplay;
    private Cocos2dxRenderer mRenderer;
    private Cocos2dxGLSurfaceView mGLSurfaceView = null;
    private LiveBaseActivtiy mActivity;
    private int[] mGLContextAttrs = null;
    private Cocos2dxHandler mHandler = null;
    private Cocos2dxVideoHelper mVideoHelper = null;
    private Cocos2dxWebViewHelper mWebViewHelper = null;
    private Cocos2dxEditBoxHelper mEditBoxHelper = null;
    private boolean hasFocus = false;

    public Cocos2dxAnimationLayout(Context context) {
        super(context);
    }

    public Cocos2dxAnimationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void showDialog(String pTitle, String pMessage) {
        Message msg = new Message();
        msg.what = Cocos2dxHandler.HANDLER_SHOW_DIALOG;
        msg.obj = new Cocos2dxHandler.DialogMessage(pTitle, pMessage);
        this.mHandler.sendMessage(msg);
    }

    @Override
    public void runOnGLThread(Runnable pRunnable) {
        this.mGLSurfaceView.queueEvent(pRunnable);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged() hasFocus=" + hasFocus);
        super.onWindowFocusChanged(hasFocus);

        this.hasFocus = hasFocus;
        resumeIfHasFocus();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public void onVideoEvent(int tag, int event) {
        runOnGLThread(new Cocos2dxVideoHelper.VideoEventRunnable(tag, event));
    }

    protected void onLoadNativeLibraries() {
        try {
            ApplicationInfo ai = mActivity.getPackageManager().getApplicationInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String libName = bundle.getString("android.app.lib_name");
            System.loadLibrary(libName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cocos2dxGLSurfaceView getGLSurfaceView(){
        return  mGLSurfaceView;
    }

    public void setKeepScreenOn(boolean value) {
        final boolean newValue = value;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGLSurfaceView.setKeepScreenOn(newValue);
            }
        });
    }

    public Cocos2dxGLSurfaceView onCreateView() {
        Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(mActivity);
        //this line is need on some device if we specify an alpha bits
        if(this.mGLContextAttrs[3] > 0)
            glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        Cocos2dxEGLConfigChooser chooser = new Cocos2dxEGLConfigChooser(this.mGLContextAttrs);
        glSurfaceView.setEGLConfigChooser(chooser);

        return glSurfaceView;
    }

    /**
     * 务必在activity的onCreate中执行
     */
    public void onCreate(){
        Log.d(TAG, "onCreate()");
        mActivity = (LiveBaseActivtiy) getContext();
        this.mHandler = new Cocos2dxHandler(mActivity);
        onLoadNativeLibraries();
        Cocos2dxHelper.init(this, mActivity);
        this.mGLContextAttrs = Cocos2dxActivity.getGLContextAttrs();
        this.init();
        initConstants();
        if (mVideoHelper == null) {
            mVideoHelper = new Cocos2dxVideoHelper(mActivity, this);
            mVideoHelper.setOnVideoEventListener(this);
            mVideoHelper.setCocos2dxHelperListener(this);
        }

        if(mWebViewHelper == null){
            mWebViewHelper = new Cocos2dxWebViewHelper(this);
        }

        if(mEditBoxHelper == null){
            mEditBoxHelper = new Cocos2dxEditBoxHelper(this);
        }

//        Window window = this.getWindow();
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    /**
     * 在activity的onResume中执行
     */
    public void onResume(){
        Log.d(TAG, "onResume()");
        resumeIfHasFocus();
        if(mMagicCameraDisplay != null){
            mMagicCameraDisplay.onResume();
        }
    }

    /**
     * 在activity的onPause中执行
     */
    public void onPause(){
        Log.d(TAG, "onPause()");
        Cocos2dxHelper.onPause();
        mGLSurfaceView.onPause();
        if(mMagicCameraDisplay != null){
            mMagicCameraDisplay.onPause();
        }
    }

    /**
     * 在activity的onDestory中执行
     */
    public void onDestory(){
        Log.d(TAG, "onDestory()");
        if(mMagicCameraDisplay != null){
            mMagicCameraDisplay.onDestroy();
        }
        mRenderer.onActivityDestroy();
    }

    /**
     * 在activity的onActivityResult中执行
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        for (PreferenceManager.OnActivityResultListener listener : Cocos2dxHelper.getOnActivityResultListeners()) {
            listener.onActivityResult(requestCode, resultCode, data);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initConstants() {
        Point outSize = new Point();
        mActivity.getWindowManager().getDefaultDisplay().getRealSize(outSize);
        Constants.mScreenWidth = outSize.x;
        Constants.mScreenHeight = outSize.y;
    }

    public void init() {

        // FrameLayout
        FrameLayout.LayoutParams framelayout_params =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);


        setLayoutParams(framelayout_params);

        // Cocos2dxEditText layout
//        ViewGroup.LayoutParams edittext_layout_params =
//            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                       ViewGroup.LayoutParams.WRAP_CONTENT);
//        Cocos2dxEditBox edittext = new Cocos2dxEditBox(this);
//        edittext.setLayoutParams(edittext_layout_params);
//
//
//        mFrameLayout.addView(edittext);

        // Cocos2dxGLSurfaceView
        this.mGLSurfaceView = this.onCreateView();
        mGLSurfaceView.setZOrderOnTop(true);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        if(mRenderer == null){
            mRenderer = new Cocos2dxRenderer();
        }

        //FrameLayout.LayoutParams params = new LayoutParams(Constants.mScreenWidth, Constants.mScreenHeight);
        //mGLSurfaceView.setLayoutParams(params);

        // Switch to supported OpenGL (ARGB888) mode on emulator
        if (isAndroidEmulator())
            this.mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        String animID = "bouquet";
        this.mGLSurfaceView.setCocos2dxRenderer(mRenderer);
        //this.mGLSurfaceView.setCocos2dxEditText(edittext);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        // ...add to FrameLayout
        addView(this.mGLSurfaceView);

    }

    public void addGift(ReciveGiftInfo giftInfo){
        String path = Cocos2dxAnimationAttacher.getInstance().getUnZipPath(giftInfo.getGiftId());
        if(path != null){
            mRenderer.addGift(path);
        }else{
            Log.e(TAG, "addLayer-path   null");
        }
    }

    private final static boolean isAndroidEmulator() {
        String model = Build.MODEL;
        Log.d(TAG, "model=" + model);
        String product = Build.PRODUCT;
        Log.d(TAG, "product=" + product);
        boolean isEmulator = false;
        if (product != null) {
            isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
        }
        Log.d(TAG, "isEmulator=" + isEmulator);
        return isEmulator;
    }

    private void resumeIfHasFocus() {
        if(hasFocus) {
            Cocos2dxHelper.onResume();
            mGLSurfaceView.onResume();
        }
    }

    //native method,call GLViewImpl::getGLContextAttrs() to get the OpenGL ES context attributions
//    private static native int [] getGLContextAttrs();

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    public class Cocos2dxEGLConfigChooser implements GLSurfaceView.EGLConfigChooser
    {
        protected int[] configAttribs;
        public Cocos2dxEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize)
        {
            configAttribs = new int[] {redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize};
        }
        public Cocos2dxEGLConfigChooser(int[] attribs)
        {
            configAttribs = attribs;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display,
                                     EGLConfig config, int attribute, int defaultValue) {
            int[] value = new int[1];
            if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
                return value[0];
            }
            return defaultValue;
        }

        class ConfigValue implements Comparable<ConfigValue> {

            public EGLConfig config = null;
            public int[] configAttribs = null;
            public int value = 0;
            private void calcValue() {
                // depth factor 29bit and [6,12)bit
                if (configAttribs[4] > 0) {
                    value = value + (1 << 29) + ((configAttribs[4]%64) << 6);
                }
                // stencil factor 28bit and [0, 6)bit
                if (configAttribs[5] > 0) {
                    value = value + (1 << 28) + ((configAttribs[5]%64));
                }
                // alpha factor 30bit and [24, 28)bit
                if (configAttribs[3] > 0) {
                    value = value + (1 << 30) + ((configAttribs[3]%16) << 24);
                }
                // green factor [20, 24)bit
                if (configAttribs[1] > 0) {
                    value = value + ((configAttribs[1]%16) << 20);
                }
                // blue factor [16, 20)bit
                if (configAttribs[2] > 0) {
                    value = value + ((configAttribs[2]%16) << 16);
                }
                // red factor [12, 16)bit
                if (configAttribs[0] > 0) {
                    value = value + ((configAttribs[0]%16) << 12);
                }
            }

            public ConfigValue(int[] attribs) {
                configAttribs = attribs;
                calcValue();
            }

            public ConfigValue(EGL10 egl, EGLDisplay display, EGLConfig config) {
                this.config = config;
                configAttribs = new int[6];
                configAttribs[0] = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
                configAttribs[1] = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
                configAttribs[2] = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
                configAttribs[3] = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);
                configAttribs[4] = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
                configAttribs[5] = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);
                calcValue();
            }

            @Override
            public int compareTo(ConfigValue another) {
                if (value < another.value) {
                    return -1;
                } else if (value > another.value) {
                    return 1;
                } else {
                    return 0;
                }
            }

            @Override
            public String toString() {
                return "{ color: " + configAttribs[3] + configAttribs[2] + configAttribs[1] + configAttribs[0] +
                        "; depth: " + configAttribs[4] + "; stencil: " + configAttribs[5] + ";}";
            }
        }

        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display)
        {
            int[] EGLattribs = {
                    EGL10.EGL_RED_SIZE, configAttribs[0],
                    EGL10.EGL_GREEN_SIZE, configAttribs[1],
                    EGL10.EGL_BLUE_SIZE, configAttribs[2],
                    EGL10.EGL_ALPHA_SIZE, configAttribs[3],
                    EGL10.EGL_DEPTH_SIZE, configAttribs[4],
                    EGL10.EGL_STENCIL_SIZE,configAttribs[5],
                    EGL10.EGL_RENDERABLE_TYPE, 4, //EGL_OPENGL_ES2_BIT
                    EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            boolean eglChooseResult = egl.eglChooseConfig(display, EGLattribs, configs, 1, numConfigs);
            if (eglChooseResult && numConfigs[0] > 0)
            {
                return configs[0];
            }

            // there's no config match the specific configAttribs, we should choose a closest one
            int[] EGLV2attribs = {
                    EGL10.EGL_RENDERABLE_TYPE, 4, //EGL_OPENGL_ES2_BIT
                    EGL10.EGL_NONE
            };
            eglChooseResult = egl.eglChooseConfig(display, EGLV2attribs, null, 0, numConfigs);
            if(eglChooseResult && numConfigs[0] > 0) {
                int num = numConfigs[0];
                ConfigValue[] cfgVals = new ConfigValue[num];

                // convert all config to ConfigValue
                configs = new EGLConfig[num];
                egl.eglChooseConfig(display, EGLV2attribs, configs, num, numConfigs);
                for (int i = 0; i < num; ++i) {
                    cfgVals[i] = new ConfigValue(egl, display, configs[i]);
                }

                ConfigValue e = new ConfigValue(configAttribs);
                // bin search
                int lo = 0;
                int hi = num;
                int mi;
                while (lo < hi - 1) {
                    mi = (lo + hi) / 2;
                    if (e.compareTo(cfgVals[mi]) < 0) {
                        hi = mi;
                    } else {
                        lo = mi;
                    }
                }
                if (lo != num - 1) {
                    lo = lo + 1;
                }
                Log.w("cocos2d", "Can't find EGLConfig match: " + e + ", instead of closest one:" + cfgVals[lo]);
                return cfgVals[lo].config;
            }

            Log.e(getContext().DEVICE_POLICY_SERVICE, "Can not select an EGLConfig for rendering.");
            return null;
        }

    }
}
