package com.seu.magicfilter.displayGL30;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera.Size;
import android.media.AudioFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.rednovo.ace.core.video.VideoManager;
import com.seu.magicfilter.camera.CameraManager;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.filter.helper.MagicFilterParam;
import com.seu.magicfilter.utils.OpenGLUtils;
import com.seu.magicfilter.utils.Rotation;
import com.seu.magicfilter.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * MagicCameraDisplay is used for camera preview
 */
@SuppressLint("NewApi")
public class ALVideoEngine extends ALBaseMagicDisplay {
    /**
     * 用于绘制相机预览数据，当无滤镜及mFilters为Null或者大小为0时，绘制到屏幕中， 否则，绘制到FrameBuffer中纹理
     */
    private final ALCameraInputFilter mCameraInputFilter;
    private final GPUImageFilter mMyFilter;
    private int mOutWidth = 360;
    private int mOutHeight = 640;

    /**
     * 纹理坐标
     */
    protected FloatBuffer mGLTextureBufferReadPixel;

    protected FloatBuffer mGLTextureBufferFrameRender;


    private String LOG_TAG = "ALVideoEngine";

    /**
     * Camera预览数据接收层，必须和OpenGL绑定 过程见{@link OpenGLUtils.getExternalOESTextureID()};
     */
    private SurfaceTexture mSurfaceTexture;
    private VideoManager mVideoManager = null;
    private CameraManager mCameraManager = null;
    private String mRtmpUrl;
    private Context mContext;
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;

    public ALVideoEngine(Context context, GLSurfaceView glSurfaceView, String rtmpurl) {
        super(context, glSurfaceView);
        this.mContext = context;
        this.mRtmpUrl = rtmpurl;
        mCameraInputFilter = new ALCameraInputFilter();

        mGLTextureBufferReadPixel = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        mGLTextureBufferReadPixel.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);

        mGLTextureBufferFrameRender = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        mGLTextureBufferFrameRender.put(TextureRotationUtil.getRotation(Rotation.ROTATION_180, false, true)).position(0);

        mMyFilter = new GPUImageFilter();

        mVideoManager = new VideoManager(context);

        mCameraManager = new CameraManager(context);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
        MagicFilterParam.initMagicFilterParam(gl);
        if (mFilters == null) {
            mCameraInputFilter.init();
        } else {
            mFilters.init();
        }
        mMyFilter.init();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        onFilterChanged();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurfaceTexture.updateTexImage();
        float[] mtx = new float[16];
        mSurfaceTexture.getTransformMatrix(mtx);

        if (mFilters == null) {
            mCameraInputFilter.setTextureTransformMatrix(mtx);
            int textureID = mCameraInputFilter.onDrawToTexture(mVideoManager, mTextureId, mGLCubeBuffer, mGLTextureBufferReadPixel);
            if (mMyFilter != null) {
                mMyFilter.onDrawFrame(textureID, mGLCubeBuffer, mGLTextureBufferFrameRender);
            }
        } else {
            mFilters.setTextureTransformMatrix(mtx);
            int textureID = mFilters.onDrawFrameBeauty(mVideoManager, mTextureId, mGLCubeBuffer, mGLTextureBufferReadPixel);
            if (mMyFilter != null) {
                mMyFilter.onDrawFrame(textureID, mGLCubeBuffer, mGLTextureBufferFrameRender);
            }

        }
    }

    public void setCameraReadyListener(CameraManager.CameraReadyListener onCameraReadyListener) {
        mCameraManager.setCameraReadyListener(onCameraReadyListener);
    }

    public void onFocusOnTouch(MotionEvent event) {
        mCameraManager.focusOnTouch(event);
    }

    /**
     * 闪光灯
     */
    public void switchFlash() {
        mCameraManager.switchFlash();
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (mCameraManager != null) {
            mCameraManager.switchCameraFacing();
            boolean flipHorizontal = mCameraManager.isFlipHorizontal();
            adjustPosition(mCameraManager.getOrientation(), flipHorizontal, !flipHorizontal);
            setUpCamera();
        }
    }

    public int getCamraNum() {
        return mCameraManager.getnCameraNum();
    }

    public int getCamerFacing() {
        return mCameraManager.getCamerFacing();
    }

    /**
     * 设置静音
     *
     * @param bMute
     */
    public void setMute(boolean bMute) {
        if (mVideoManager != null) {
            mVideoManager.setAudioMute(bMute);
        }
    }

    public void onResume() {
        super.onResume();
        // startVideo();
    }

    public void onPause() {
        super.onPause();

    }


    public void startVideo() {
        mCameraManager.openCamera();
        boolean flipHorizontal = mCameraManager.isFlipHorizontal();
        adjustPosition(mCameraManager.getOrientation(), flipHorizontal, !flipHorizontal);
        setUpCamera();
    }


    public void stopVideo() {
        mCameraManager.releaseCamera();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mCameraManager != null) {
            mCameraManager.releaseCamera();
            mCameraManager = null;
        }
        if (mVideoManager != null) {
            mVideoManager.destory();
            mVideoManager = null;
        }
        if (mFilters != null) {
            mFilters.destroy();
        }
        if (mMyFilter != null) {
            mMyFilter.destroy();
        }
        if (mCameraInputFilter != null) {
            mCameraInputFilter.destroy();
        }

    }

    private OnFrameAvailableListener mOnFrameAvailableListener = new OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            // TODO Auto-generated method stub
            mGLSurfaceView.requestRender();
        }
    };

    private void setUpCamera() {
        mGLSurfaceView.queueEvent(new Runnable() {

            @Override
            public void run() {
                if (mTextureId == OpenGLUtils.NO_TEXTURE) {
                    mTextureId = OpenGLUtils.getExternalOESTextureID();
                    mSurfaceTexture = new SurfaceTexture(mTextureId);
                    mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
                }
                Size size = mCameraManager.getPreviewSize();
                int orientation = mCameraManager.getOrientation();
                if (orientation == 90 || orientation == 270) {
                    mImageWidth = size.height;
                    mImageHeight = size.width;
                } else {
                    mImageWidth = size.width;
                    mImageHeight = size.height;
                }

                if (mFilters == null) {
                    mCameraInputFilter.onOutputSizeChanged(mOutWidth, mOutHeight);
                } else {
                    mFilters.onOutputSizeChanged(mOutWidth, mOutHeight);
                }
                mVideoManager.initAVEngine(mRtmpUrl, mOutWidth, mOutHeight);
                mCameraManager.startPreview(mSurfaceTexture);

            }
        });
    }

    protected void onFilterChanged() {
        super.onFilterChanged();
        if (mFilters == null) {
            mCameraInputFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
            mCameraInputFilter.initCameraFrameBuffer(mOutWidth, mOutHeight);
        } else {
            mCameraInputFilter.destroyFramebuffers();
            mFilters.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
        }
    }


    private void adjustPosition(int orientation, boolean flipHorizontal, boolean flipVertical) {
        Rotation mRotation = Rotation.fromInt(orientation);
        float[] textureCords = TextureRotationUtil.getRotation(mRotation, flipHorizontal, flipVertical);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(textureCords).position(0);

        if (270 == orientation) {

            mRotation = Rotation.fromInt(0);
            textureCords = TextureRotationUtil.getRotation(mRotation, true, false);
            mGLTextureBufferFrameRender.clear();
            mGLTextureBufferFrameRender.put(textureCords).position(0);

            mRotation = Rotation.fromInt(orientation);
            textureCords = TextureRotationUtil.getRotation(mRotation, false, true);
            mGLTextureBufferReadPixel.clear();
            mGLTextureBufferReadPixel.put(textureCords).position(0);

        } else if (90 == orientation) {

            mRotation = Rotation.fromInt(0);
            textureCords = TextureRotationUtil.getRotation(mRotation, false, false);
            mGLTextureBufferFrameRender.clear();
            mGLTextureBufferFrameRender.put(textureCords).position(0);

            mRotation = Rotation.fromInt(270);
            textureCords = TextureRotationUtil.getRotation(mRotation, false, false);
            mGLTextureBufferReadPixel.clear();
            mGLTextureBufferReadPixel.put(textureCords).position(0);

        }
    }

}
