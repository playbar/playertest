package com.rednovo.ace.common;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.rednovo.ace.R;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.CacheKey;
import com.rednovo.libs.common.CacheUtils;
import com.rednovo.libs.common.DownloadUtils;
import com.rednovo.libs.common.ZipUtils;
import com.sina.weibo.sdk.utils.LogUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dk on 16/5/30.
 */
public class Cocos2dxAnimationAttacher {

    private static final String GIFT_ANIMATION_DOWNLOAD_URL = "http://download.17ace.cn/gift/";

    private static final String TAG = "Cocos2dxAnimationAttacher";

    /**
     * 下载完成ZIP包保存在SD卡根目录的文件夹名
     */
    private static final String DOWNLOAD_FOLDER_NAME = "Downloads/";

    /**
     * ZIP解压文件夹名
     */
    private static final String UNZIP_FOLDER_NAME = "/anim/";

    private static Cocos2dxAnimationAttacher instance;

    private List<String> giftIdList;

    private Context context;
    private DownloadUtils download;

    private String unZipPath;//解压目录
    private String downloadPath;//下载目录

    private boolean isInit = false;

    /**
     * @param context getApplicationContext()
     * @return
     */
    public static Cocos2dxAnimationAttacher getInstance() {
        synchronized (Cocos2dxAnimationAttacher.class) {
            if (instance == null) {
                instance = new Cocos2dxAnimationAttacher(BaseApplication.getApplication().getApplicationContext());
            }
            return instance;
        }
    }

    /**
     * 必须先调用此方法。把所有的下载地址传进
     *
     * @param list
     */
    public void initAttacher(List<String> list) {
        if (list != null && list.size() > 0) {
            this.giftIdList = list;
            initStroge();
            download = DownloadUtils.getInstance(context);
            isInit = true;
        } else {
            LogUtil.e(TAG, "urlList  null");
        }
    }

    /**
     * 检查所有礼物ID
     */
    public void detectionAll() {
        if (isInit) {
            for (String giftId : giftIdList) {
                detection(giftId);
            }
        } else {
            LogUtil.e(TAG, "detectionAll  unInit");
        }
    }

    /**
     * 检查以下
     * 1.素材目录是否存在对应素材
     * 2.下载目录是否存在已下载的ZIP文件
     * 3.下载队列是否存在此路径
     * 4.动画列表是否有此礼物动画
     * @param giftId
     * @return
     */
    public boolean detection(String giftId) {
        if (giftId != null) {
            if(!giftIdList.contains(giftId)){//4
                Log.e(TAG, "not produced");
                return false;
            }
            String fileName = getFileName(giftId);
            String url = GIFT_ANIMATION_DOWNLOAD_URL + fileName;
            if (!new File(unZipPath + giftId).exists()) {//1
                Log.e(TAG, "not found " + unZipPath + giftId + " directory");
                if (!new File(downloadPath + fileName).exists()) {//2
                    Log.e(TAG, "not found " + downloadPath + fileName);
                    download(url, giftId);
                    return false;
                } else {
                    Log.e(TAG, "Zip file:" + downloadPath + fileName + "  unZip file:" + unZipPath);
                    ZipUtils.unZipFiles(downloadPath + fileName, unZipPath);
                    return false;
                }
            }else{
                return true;
            }
        } else {
            Log.e(TAG, "giftId null");
            return false;
        }
    }

    public String getUnZipPath(String giftId) {
        if(detection(giftId)){
            return unZipPath + giftId;
        }
        return null;
    }

    private Cocos2dxAnimationAttacher(Context context) {
        this.context = context;
    }

    /**
     * 添加到下载队列
     *
     * @param url
     */
    private void download(String url, String giftId) {
        for (long l : download.getQuery().keySet()) {//3
            if (download.getQuery().get(l).getDownloadUrl().equals(url)) {
                return;
            }
        }
        DownloadUtils.Cocos2dxAnimDownloadResult result = new DownloadUtils.Cocos2dxAnimDownloadResult();
        result.setUnZipPath(unZipPath + giftId);
        result.setDownloadUrl(url);
        result.setDownloadType(DownloadUtils.DOWNLOAD_TYPE_ANIM);
        result.setDownloadPath(downloadPath + getFileName(giftId));
        download.addDownloadTask(result, "", "", false, true, DOWNLOAD_FOLDER_NAME, getFileName(giftId));
    }

    /**
     * 获取礼物ID
     */
    private String getGiftID(String url) {
        int start = url.lastIndexOf("/") + 1;
        int end = url.lastIndexOf(".");
        String giftID = url.substring(start, end);
        LogUtil.e(TAG, "getGiftID " + giftID);
        return giftID;
    }

    /**
     * 获取文件名称
     */
    private String getFileName(String giftId) {
        return giftId + ".zip";
    }

    /**
     * 初始化下载目录和解压缩目录
     * *如果SD卡根目录存在与下载目录同名的文件时，则会创建目录
     * *解压目录如果获取不到应用目录，则解压到下载目录
     */
    private void initStroge() {
        LogUtil.e(TAG, "initStroge");
        downloadPath = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME).toString() + "/";
        File downloadFile = new File(downloadPath);
        File unZipPath = BaseApplication.getApplication().getExternalFilesDir(null);

        if (!downloadFile.exists() || !downloadFile.isDirectory()) {//存在的与文件名相同的是文件而不是文件夹时，则会抛出异常，避免这种情况加上创建这一句
            downloadFile.mkdirs();
        }

        if (unZipPath == null) {
            this.unZipPath = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME).toString() + "/";
        } else {
            this.unZipPath = unZipPath.toString() + "/";
        }
    }
}
