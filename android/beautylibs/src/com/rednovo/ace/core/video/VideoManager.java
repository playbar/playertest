package com.rednovo.ace.core.video;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;

import com.seu.magicfilter.utils.AudioUtils;
import com.seu.magicfilter.utils.Logger;

@SuppressLint("NewApi")
public class VideoManager {

    private static final String LOG_TAG = "VideoManager";
    private boolean initAvEngine;
    private Context mContext;
    private NovoAVEngine mNovoAVEngine;
    private AceAudioRecord mAudioRecord = null;
    private AudioManager mAudioManager;

    public VideoManager(Context context) {
        this.mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        mNovoAVEngine = new NovoAVEngine();
        mAudioRecord = new AceAudioRecord(mContext, mNovoAVEngine);
    }

    /**
     * 初始化Video引擎
     *
     * @param rtmpUrl
     */
    public void initAVEngine(String rtmpUrl, int w, int h) {

        if (!initAvEngine) {
            try {

                String srcWxH = w + "x" + h;
                String distWxH = w + "x" + h;

                mNovoAVEngine.Create();

                mNovoAVEngine.SetInputWxH(srcWxH);

                mNovoAVEngine.SetOutputWxh(distWxH);

                mNovoAVEngine.SetBitRate(400);// 400kpbs

                mNovoAVEngine.SetMaxBitRate(800);//

                mNovoAVEngine.SetMinBitRate(100);

                mNovoAVEngine.SetFrameRate(30);

                mNovoAVEngine.SetKeyFrameInt(10);

                mNovoAVEngine.SetPrintLogAndSave(true, true);

                mNovoAVEngine.SetLogLevel(NovoAVEngine.AV_LOG_TRACE);

                mNovoAVEngine.SetOutputURL(rtmpUrl);

                mNovoAVEngine.SetRotation(270);

                mNovoAVEngine.SetOnlyVideo(true);

                mNovoAVEngine.SetAudioBitrate(96);
                mNovoAVEngine.SetAudioChannels(1);
                mNovoAVEngine.SetAudioSamplerate(AudioUtils.getNativeSampleRate(mAudioManager));

                mNovoAVEngine.SetOnlyAudio(false);
                mNovoAVEngine.SetOnlyVideo(false);

                mNovoAVEngine.initVideoEngine();
                // audio to start
                initAvEngine = true;
                startAudioThread();
            } catch (Exception ex) {
            }

        }

    }

    public void setAudioMute(boolean loudspeakerOn) {
        try {
            mNovoAVEngine.setAudioMute(loudspeakerOn);
        } catch (Exception ex) {
            logV("setMute error");
        }

    }

    public void encodegl(int[] frameBuf, int dataType) {
        mNovoAVEngine.encodegl(frameBuf, dataType);
    }

    private void closeAVEngine() {
        if (mNovoAVEngine.close()) {
            mNovoAVEngine = null;
        }
        initAvEngine = false;

    }

    private void destroyAVEngine() {
        if (mNovoAVEngine != null) {
            closeAVEngine();
        }
    }

    private void startAudioThread() {
        if (mAudioRecord != null)
            mAudioRecord.startRecording();

    }

    private void stopAudioThread() {
        if (mAudioRecord != null) {
            mAudioRecord.stopRecording();
        }
    }

    /**
     * 销毁所有资源
     */
    public void destory() {
        destroyAVEngine();
        stopAudioThread();
    }

    private void logV(String tag) {
        Logger.v(LOG_TAG, tag);
    }

}
