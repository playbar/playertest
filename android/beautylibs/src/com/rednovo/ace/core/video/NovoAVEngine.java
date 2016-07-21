package com.rednovo.ace.core.video;

import android.util.Log;

import com.seu.magicfilter.utils.Logger;

public class NovoAVEngine {

    public static int AV_LOG_QUIET = -8;

    /**
     * Something went really wrong and we will crash now.
     */
    public static int AV_LOG_PANIC = 0;

    /**
     * Something went wrong and recovery is not possible. For example, no header was found for a format which depends on headers or an illegal combination of parameters is used.
     */
    public static int AV_LOG_FATAL = 8;

    /**
     * Something went wrong and cannot losslessly be recovered. However, not all future data is affected.
     */
    public static int AV_LOG_ERROR = 16;

    /**
     * Something somehow does not look correct. This may or may not lead to problems. An example would be the use of '-vstrict -2'.
     */
    public static int AV_LOG_WARNING = 24;

    /**
     * Standard information.
     */
    public static int AV_LOG_INFO = 32;

    /**
     * Detailed information.
     */
    public static int AV_LOG_VERBOSE = 40;

    /**
     * Stuff which is only useful for libav* developers.
     */
    public static int AV_LOG_DEBUG = 48;

    /**
     * Extremely verbose debugging, useful for libav* development.
     */
    public static int AV_LOG_TRACE = 56;

    private static final String LOG_TAG = "NovoAVEngine";
    private boolean initStatue;

    public native int Create();

    public native int SetInputWxH(String srcWxH);

    public native int SetOutputWxh(String outWxH);

    public native int SetBitRate(int br);

    public native int SetMaxBitRate(int maxbr);

    public native int SetMinBitRate(int minbr);

    public native int SetFrameRate(int fr);

    public native int SetKeyFrameInt(int kf);

    public native int SetLogLevel(int ill);

    public native int SetPrintLogAndSave(boolean pl, boolean sl);

    public native int SetOutputURL(String rtmp);

    public native int SetRotation(int iRotation);

    // audio
    public native int SetAudioChannels(int iac);

    public native int SetAudioBitrate(int iab);

    public native int SetAudioSamplerate(int iar);

    public native int GetRequiredAudioSamples();

    public native int SetOnlyAudio(boolean boa);

    public native int SetOnlyVideo(boolean bov);

    public native int Init();

    public native int Encode(byte[] frameBuf, int dataType);

    public native void glReadPixelsPBO(int x, int y, int width, int height, int format, int type, int offsetPBO);

    public native int Encodegl(int[] frameBuf, int dataType);

    public native int Close();

    public native int Release();

    public native int SetAudioMute(int iMute);

    //ultrafast,superfast, veryfast, faster, fast, medium, slow, slower, veryslow
    public native int SetEncoderPreset(String strPreset);

    static {
        System.loadLibrary("NovoAVEngine-jni");
        System.loadLibrary("NovoGPUEnginejni");
    }

    public void initVideoEngine() {
        if (Init() >= 0) {
            Log.v(LOG_TAG, "init success");
            initStatue = true;
        } else {
            initStatue = false;
        }
    }

    public void encodeVideo(byte[] frameBuf) {
        if (initStatue && Encode(frameBuf, 0) >= 0) {
            LogV("Encode success");
        }
    }

    public void encodeAudio(byte[] frameBuf) {
        if (initStatue && Encode(frameBuf, 1) >= 0) {
            LogV("encodeAudio success" + "==" + initStatue);
        } else {
            LogV("encodeAudio failed");
        }
    }

    public void encodegl(int[] frameBuf, int dataType) {
        if (initStatue && Encodegl(frameBuf, dataType) >= 0) {
            LogV("Encodegl success");
        } else {
            LogV("Encodegl failed");
        }
    }

    public void setAudioMute(boolean state) {
        try {
            int iMute;
            if (state) {
                iMute = 1;//表示开启静音

            } else {
                iMute = 0;//表示取消静音
            }
            if (SetAudioMute(iMute) >= 0) {
                LogV("setAudioMute success");
            } else {
                LogV("setAudioMute error");
            }
        } catch (Throwable ex) {
            LogV("setAudioMute error");
        }

    }

    public void stop() {
        if (initStatue && Close() >= 0) {
            LogV("Close success");
        }
    }


    public void setEncoderPreset(String strPreset) {
        if (SetEncoderPreset(strPreset) >= 0) {
            LogV("setEncoderPreset success");
        } else {
            LogV("setEncoderPreset error");
        }
    }

    public void LogV(String tag) {
        Logger.v(LOG_TAG, tag);
    }

    public boolean close() {
        try {
            if (initStatue && Close() >= 0) {
                LogV("Close success");
                return true;
            } else {
                LogV("Close error");
                return false;
            }
        } catch (Exception ex) {
            LogV("close error");
            return false;
        }

    }

}
