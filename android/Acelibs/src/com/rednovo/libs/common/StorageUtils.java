package com.rednovo.libs.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rednovo.libs.BaseApplication;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.MEDIA_MOUNTED;

public final class StorageUtils {


    //数据缓存的文件夹路径
    private static final String DATA_FILE_NAME = "data_cache";
    private static final String CACHE_REC_AUDIO_PIC_PATH = "save_audio_pic";
    private static final String CACHE_LOCAL_SAVE_AUDIO_PATH = "record_audio";
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    private static SharedPreferences sSharedPreferences;

    private StorageUtils() {
    }

    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) { // (sh)it happens too (Issue #989)
            externalStorageState = "";
        }
        //判断SDK是否存在
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        //sdcard
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }


    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                Log.v("", "Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                Log.i("", "Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取缓存的文件夹
     *
     * @param context
     * @return
     */
    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    /**
     * 获取缓存的文件路径
     *
     * @return
     */
    public static String getCachePath() {
        Context context = BaseApplication.getApplication().getApplicationContext();
        File file = getCacheDirectory(context, true);

        return (file != null) ? file.getAbsolutePath() : context.getCacheDir().getAbsolutePath();
    }

    /**
     * 获取上传图片的缓存地址
     *
     * @return
     */
    public static String getCachePicPath() {
        Context context = BaseApplication.getApplication().getApplicationContext();
        File file = getCacheDirectory(context, true);
        String path = "";
        if (file != null) {
            path = file.getAbsolutePath() + File.separator + "image";
        } else {
            path = context.getCacheDir().getAbsolutePath() + File.separator + "image";
        }
        File picCache = new File(path);
        if (!picCache.exists()) {
            picCache.mkdirs();
        }
        return path;
    }

    /**
     * 数据缓存路径
     *
     * @return
     */
    public static String getDateCacheFolderPath() {
        String cachePath = getCachePath();
        return cachePath + File.separator + DATA_FILE_NAME;
    }

    /**
     * 接收服务器语音的存储路径和截图的保存路径
     *
     * @return
     */
    public static String getWritePath() {
        String cachePath = getCachePath();
        return cachePath + File.separator + CACHE_REC_AUDIO_PIC_PATH;
    }

    /**
     * 本机录制语音的存储路径
     *
     * @return
     */
    public static String getReadPath() {
        String cachePath = getCachePath();
        return cachePath + File.separator + CACHE_LOCAL_SAVE_AUDIO_PATH;
    }

    public static void initSharePreferences(Context context) {
        sSharedPreferences = context.getSharedPreferences("ACE-SharedPreFerences", Activity.MODE_PRIVATE);
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public static boolean sharedPreferencesContains(String key) {
        return sSharedPreferences.contains(key);
    }
}
