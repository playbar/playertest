package com.rednovo.ace.core.video;

import android.util.Log;

import com.rednovo.libs.common.LogUtils;

public class NovoAVEngine1 {

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

    public native int Close();

    public native int Release();

    public native int SetAudioMute(int iMute);

    //ultrafast,superfast, veryfast, faster, fast, medium, slow, slower, veryslow
    public native int SetEncoderPreset(String strPreset);

    static {
        try {
            System.loadLibrary("NovoAVEngine-jni");
            LogUtils.v(LOG_TAG, "LOADLIBRARY NOVOAVENGINE SUCCESS");
        } catch (Throwable ex) {
            LogUtils.e(LOG_TAG, "LOADLIBRARY NOVOAVENGINE ERROR");
        }

    }


    public void create() {
        if (Create() >= 0) {
            LogUtils.v(LOG_TAG, "create success");
        } else {
            LogUtils.e(LOG_TAG, "create error");
        }

    }

    public void setInputWxH(String srcWxH) {
        if (SetInputWxH(srcWxH) >= 0) {
            LogUtils.e(LOG_TAG, "setInputWxH success");
        } else {
            LogUtils.e(LOG_TAG, "setInputWxH error");
        }
    }

    public void setOutputWxh(String outWxH) {
        if (SetOutputWxh(outWxH) >= 0) {
            Log.v(LOG_TAG, "SetOutputWxh success");
        } else {
            Log.e(LOG_TAG, "SetOutputWxh error");
        }
    }

    public void setBitRate(int br) {
        if (SetBitRate(br) >= 0) {
            LogUtils.v(LOG_TAG, "SetBitRate success");
        } else {
            LogUtils.e(LOG_TAG, "SetBitRate error");
        }
    }

    public void setMaxBitRate(int maxbr) {
        if (SetMaxBitRate(maxbr) >= 0) {
            LogUtils.v(LOG_TAG, "SetMaxBitRate success");
        } else {
            LogUtils.e(LOG_TAG, "SetMaxBitRate error");
        }
    }

    public void setMinBitRate(int minbr) {
        if (SetMinBitRate(minbr) >= 0) {
            LogUtils.v(LOG_TAG, "SetMinBitRate success");
        } else {
            LogUtils.e(LOG_TAG, "SetMinBitRate error");
        }
    }

    public void setFrameRate(int fr) {

        if (SetFrameRate(fr) >= 0) {
            LogUtils.v(LOG_TAG, "setFrameRate success");
        } else {
            LogUtils.e(LOG_TAG, "setFrameRate error");
        }

    }

    public void setKeyFrameInt(int kf) {
        if (SetKeyFrameInt(kf) >= 0) {
            LogUtils.v(LOG_TAG, "setKeyFrameInt success");
        } else {
            LogUtils.e(LOG_TAG, "setKeyFrameInt error");
        }
    }

    public void setPrintLogAndSave(boolean pl, boolean sl) {
        if (SetPrintLogAndSave(pl, sl) >= 0) {
            LogUtils.v(LOG_TAG, "setPrintLogAndSave success");
        } else {
            LogUtils.e(LOG_TAG, "setPrintLogAndSave error");
        }
    }

    public void setLogLevel(int ill) {
        if (SetLogLevel(ill) >= 0) {
            LogUtils.v(LOG_TAG, "SetLogLevel success");
        } else {
            LogUtils.e(LOG_TAG, "SetLogLevel error");
        }
    }

    public void setOutputURL(String rtmp) {
        if (SetOutputURL(rtmp) >= 0) {
            LogUtils.v(LOG_TAG, "SetOutputURL success");
        } else {
            LogUtils.e(LOG_TAG, "SetOutputURL error");
        }
    }

    public void setAudioBitrate(int iab) {
        if (SetAudioBitrate(iab) >= 0) {
            LogUtils.v(LOG_TAG, "SetAudioBitrate success");
        } else {
            LogUtils.e(LOG_TAG, "SetAudioBitrate error");
        }
    }

    public void setAudioChannels(int iac) {
        if (SetAudioChannels(iac) >= 0) {
            LogUtils.v(LOG_TAG, "SetAudioChannels success");
        } else {
            LogUtils.e(LOG_TAG, "SetAudioChannels error");
        }
    }

    public void setAudioSamplerate(int iar) {
        if (SetAudioSamplerate(iar) >= 0) {
            LogUtils.v(LOG_TAG, "SetAudioSamplerate success");
        } else {
            LogUtils.e(LOG_TAG, "SetAudioSamplerate error");
        }
    }

    public void initVideoEngine() {
        if (Init() >= 0) {
            LogUtils.v(LOG_TAG, "init success");
            initStatue = true;
        } else {
            initStatue = false;
        }
    }

    public void encodeVideo(byte[] frameBuf) {
        try {
            if (initStatue && Encode(frameBuf, 0) >= 0) {
                LogUtils.v(LOG_TAG, "encodeVideo success");
            } else {
                LogUtils.v(LOG_TAG, "encodeVideo error");
            }
        } catch (Exception ex) {
            LogUtils.e(LOG_TAG, "encodeVideo error");
        }

    }

    public void encodeAudio(byte[] frameBuf) {
        try {
            if (initStatue && Encode(frameBuf, 1) >= 0) {
                LogUtils.v(LOG_TAG, "encodeAudio success");
            } else {
                LogUtils.v(LOG_TAG, "encodeAudio failed");
            }
        } catch (Exception ex) {
            LogUtils.e(LOG_TAG, "encodeAudio error");
        }

    }

    public boolean close() {
        try {
            if (initStatue && Close() >= 0) {
                LogUtils.v(LOG_TAG, "Close success");
                return true;
            } else {
                LogUtils.e(LOG_TAG, "Close error");
                return false;
            }
        } catch (Exception ex) {
            LogUtils.e(LOG_TAG, "close error");
            return false;
        }

    }

    public void setRotation(int iRotation) {
        if (SetRotation(iRotation) >= 0) {
            LogUtils.v(LOG_TAG, "setRotation success");
        } else {
            LogUtils.e(LOG_TAG, "setRotation error");
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
                LogUtils.v(LOG_TAG, "setAudioMute success");
            } else {
                LogUtils.v(LOG_TAG, "setAudioMute error");
            }
        } catch (Throwable ex) {
            LogUtils.v(LOG_TAG, "setAudioMute error");
        }

    }

    public void setEncoderPreset(String strPreset) {
        if (SetEncoderPreset(strPreset) >= 0) {
            LogUtils.v(LOG_TAG, "setEncoderPreset success");
        } else {
            LogUtils.v(LOG_TAG, "setEncoderPreset error");
        }
    }


}
