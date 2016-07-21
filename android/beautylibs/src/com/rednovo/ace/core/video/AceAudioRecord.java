package com.rednovo.ace.core.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.os.Process;

import com.seu.magicfilter.utils.AudioUtils;
import com.seu.magicfilter.utils.Logger;

@SuppressLint("NewApi")
public class AceAudioRecord {

    private static final String TAG = "AceAudioRecord";
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;

    private static final int BUFFERS_PER_SECOND = 1000 / CALLBACK_BUFFER_SIZE_MS;

    // private final int framesPerBuffer;
    private final int sampleRate;

    private final AudioManager audioManager;
    private final Context mContext;

    private AudioRecord audioRecord = null;
    private AudioRecordThread audioThread = null;

    private AcousticEchoCanceler aec = null;
    private boolean useBuiltInAEC = false;
    private NovoAVEngine novoAVEngine;

    private class AudioRecordThread extends Thread {
        private volatile boolean keepAlive = true;

        public AudioRecordThread(String name) {
            super(name);
            initRecording(sampleRate);
            enableBuiltInAEC(true);
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
            Logd("AudioRecordThread" + AudioUtils.getThreadInfo());
            // 计算20ms的音频采样数
            int iSamples = novoAVEngine.GetRequiredAudioSamples();// 1024;//20*frequency/1000;
            int iBufsize = iSamples * 2;
            byte[] buffer = new byte[iBufsize];

            try {
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
                Loge("AudioRecord.startRecording failed: " + e.getMessage());
                keepAlive = false;
            }

            while (keepAlive) {

                int bytesRead = audioRecord.read(buffer, 0, iBufsize);

                if (bytesRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    keepAlive = false;
                    // 用户权限被禁掉，会走此处
                    Loge("read() returned AudioRecord.ERROR_INVALID_OPERATION");
                    break;
                } else if (bytesRead == AudioRecord.ERROR_BAD_VALUE) {
                    keepAlive = false;
                    Loge("read() returned AudioRecord.ERROR_BAD_VALUE");
                    break;
                } else if (bytesRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    keepAlive = false;
                    Loge("read() returned AudioRecord.ERROR_INVALID_OPERATION");
                    break;
                }

                Logd("encodeAudio before");
                try {
                    if (novoAVEngine != null) {
                        byte[] tmpBuf = new byte[bytesRead];
                        System.arraycopy(buffer, 0, tmpBuf, 0, bytesRead);
                        novoAVEngine.encodeAudio(tmpBuf);
                        Logd("encodeAudio after");
                    }
                } catch (Exception ex) {
                    Loge("encodeAudio  error");
                    keepAlive = false;
                    break;
                }
                Logd("encodeAudio done");
            }
            try {
                if (audioRecord != null) {
                    audioRecord.stop();
                    Logd("audioRecord stop");
                }
            } catch (IllegalStateException e) {
                Loge("AudioRecord.stop failed: " + e.getMessage());
            }
            Logd("run end");
        }

        public void joinThread() {
            keepAlive = false;
            interrupt();
//            while (isAlive()) {
//                try {
//                    join();
//                } catch (InterruptedException e) {
//                    // Ignore.
//                }
//            }
        }
    }

    public AceAudioRecord(Context context, NovoAVEngine mNovoAVEngine) {
        this.novoAVEngine = mNovoAVEngine;
        Logd("ctor" + AudioUtils.getThreadInfo());
        this.mContext = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        sampleRate = getNativeSampleRate();
        // framesPerBuffer = sampleRate / BUFFERS_PER_SECOND;
    }

    private int getNativeSampleRate() {
        return AudioUtils.getNativeSampleRate(audioManager);
    }

    public static boolean builtInAECIsAvailable() {
        if (!AudioUtils.runningOnJellyBeanOrHigher()) {
            return false;
        }
        return AcousticEchoCanceler.isAvailable();
    }

    private boolean enableBuiltInAEC(boolean enable) {
        // AcousticEchoCanceler was added in API level 16 (Jelly Bean).
        if (!AudioUtils.runningOnJellyBeanOrHigher()) {
            return false;
        }
        // Store the AEC state.
        useBuiltInAEC = enable;
        // Set AEC state if AEC has already been created.
        if (aec != null) {
            int ret = aec.setEnabled(enable);
            if (ret != AudioEffect.SUCCESS) {
                Loge("AcousticEchoCanceler.setEnabled failed");
                return false;
            }
            Logd("AcousticEchoCanceler.getEnabled: " + aec.getEnabled());
        }
        return true;
    }

    public void initRecording(int sampleRate) {
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (aec != null) {
            aec.release();
            aec = null;
        }

        try {
            if (audioRecord == null) {
                int recBufSize = minBufferSize * 2;
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, recBufSize);
            }

        } catch (IllegalArgumentException e) {
            Logd(e.getMessage());
        }

        assertTrue(audioRecord.getState() == AudioRecord.STATE_INITIALIZED);

        if (builtInAECIsAvailable()) {
            // return framesPerBuffer;

            aec = AcousticEchoCanceler.create(audioRecord.getAudioSessionId());
            if (aec != null) {

                int ret = aec.setEnabled(useBuiltInAEC);
                if (ret == AudioEffect.SUCCESS) {
                    AudioEffect.Descriptor descriptor = aec.getDescriptor();
                    Logd("AcousticEchoCanceler " + "name: " + descriptor.name + ", " + "implementor: " + descriptor.implementor + ", " + "uuid: " + descriptor.uuid);
                    Logd("AcousticEchoCanceler.getEnabled: " + aec.getEnabled());

                } else {
                    Loge("AcousticEchoCanceler.setEnabled failed");
                }

            } else {
                Loge("AcousticEchoCanceler.create failed");
            }

        }

        // return framesPerBuffer;
    }

    public boolean startRecording() {
        Logd("StartRecording");
        if (audioThread == null) {
            audioThread = new AudioRecordThread("AudioRecordJavaThread");
            audioThread.start();
            return true;
        }
        return false;
    }

    public boolean stopRecording() {
        Logd("StopRecording");
        if (audioThread != null) {
            audioThread.joinThread();
            audioThread = null;
            if (aec != null) {
                aec.release();
                aec = null;
            }

            audioRecord.release();
            audioRecord = null;

            return true;
        }
        return false;
    }

    /**
     * Helper method which throws an exception when an assertion has failed.
     */
    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    private static void Logd(String msg) {
        Logger.d(TAG, msg);
    }

    private static void Loge(String msg) {
        Logger.e(TAG, msg);
    }
}
