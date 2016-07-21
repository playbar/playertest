package com.rednovo.libs.common;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.webkit.MimeTypeMap;

import com.sina.weibo.sdk.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dk on 16/5/30.
 */
public class DownloadUtils {

    private static final String TAG = "DownloadUtils";

    /**
     * 下载类型，动画资源文件
     */
    public static final String DOWNLOAD_TYPE_ANIM = "anim";

    /**
     * 下载类型，APK文件
     */
    public static final String DOWNLOAD_TYPE_APK = "updateApk";

    private static DownloadUtils instance;

    private Context context;

    private Map<Long, DownloadResult> downloadIDs= new HashMap<Long, DownloadResult>();

    private DownloadManager dm;

    private DownloadManager.Query query;

    private CompleteReceiver mCompleteReceiver;

    private Handler handler;

    public static DownloadUtils getInstance(Context context){
        synchronized (DownloadUtils.class){
            if(instance == null){
                instance = new DownloadUtils(context);
            }
            return instance;
        }
    }

    /**
     * @param context
     * @param path 下载地址
     */
    private DownloadUtils(Context context){
        this.context = context;
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        query = new DownloadManager.Query();
        //注册下载完成广播
        mCompleteReceiver = new CompleteReceiver();
        context.registerReceiver(mCompleteReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * @param url 下载路径
     * @param isCustom 是否需要自定义 如需要则会在自定义属性完成之后执行下载
     * @return
     */
    public DownloadManager.Request addDownloadTask(DownloadResult result, boolean isCustom){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(result.getDownloadUrl()));
        String fileType = MimeTypeMap.getSingleton().getExtensionFromMimeType(MimeTypeMap.getFileExtensionFromUrl(result.getDownloadUrl()));
        request.setMimeType(fileType);
        if(isCustom){
            return request;
        }
        long downloadID = dm.enqueue(request);
        downloadIDs.put(downloadID, result);
        return request;
    }

    /**
     * @param url 下载路径
     * @param title 下载标题
     * @param description 下载显示的内容
     * @param isVisibleInDownloadsUi 是否在通知栏显示
     * @param isAllowByMONET 是否允许在非wifi环境下下载
     * @param dirType 保存的目录名
     * @param subPath 保存的文件名
     */
    public long addDownloadTask(DownloadResult result, String title, String description, boolean isVisibleInDownloadsUi, boolean isAllowByMONET, String dirType, String subPath){
        DownloadManager.Request request = addDownloadTask(result, true);
        setTitle(request, title);
        setDescription(request, description);
        setVisibleInDownloadsUi(request, isVisibleInDownloadsUi);
        setAllowByMONET(request, isAllowByMONET);
        setDestinationInExternalPublicDir(request, dirType, subPath);
        long downloadID = dm.enqueue(request);
        downloadIDs.put(downloadID, result);
        return downloadID;
    }

    /**
     * 获取队列对象
     * @return
     */
    public Map<Long, DownloadResult> getQuery(){
        return downloadIDs;
    }

    /**
     * 反注册下载广播
     */
    public void unregisterReceiver(){
        context.unregisterReceiver(mCompleteReceiver);
    }

    /**
     * 设置下载标题
     * @param title
     */
    private void setTitle(DownloadManager.Request request, String title){
        if(request != null){
            request.setTitle(title);
        }
    }

    /**
     * 设置下载显示的内容
     * @param description
     */
    private void setDescription(DownloadManager.Request request, String description){
        if(request != null){
            request.setDescription(description);
        }
    }

    /**
     * 是否在通知栏显示
     * @param isVisibleInDownloadsUi
     */
    private void setVisibleInDownloadsUi(DownloadManager.Request request, boolean isVisibleInDownloadsUi){
        if(request != null){
            request.setVisibleInDownloadsUi(isVisibleInDownloadsUi);
        }
        if(isVisibleInDownloadsUi){
            if (Build.VERSION.SDK_INT > 10) {
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
        }else{
            if (Build.VERSION.SDK_INT > 10) {
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            }
        }
    }

    /**
     * 设置是否允许在非wifi环境下下载
     * @param isAllowBMONETy true标示允许，false标示不允许
     */
    private void setAllowByMONET(DownloadManager.Request request, boolean isAllowByMONET){
        if(request != null){
            if(isAllowByMONET){
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
            }else{
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            }
        }
    }

    /**
     * 设置保存的目录和文件名
     * @param dirType
     * @param subPath
     */
    private void setDestinationInExternalPublicDir(DownloadManager.Request request, String dirType, String subPath){
        if(request != null){
            request.setDestinationInExternalPublicDir(dirType, subPath);
        }
    }

    private class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.e(TAG, "download-over");
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            if (downloadIDs.keySet().contains(new Long(id))) {
                DownloadResult downloadResult = downloadIDs.get(new Long(id));
                if(downloadResult.getDownloadType().equals(DOWNLOAD_TYPE_ANIM)){
                    //动画素材下载
                    Cocos2dxAnimDownloadResult result = (Cocos2dxAnimDownloadResult) downloadResult;
                    ZipUtils.unZipFiles(result.getDownloadPath(), result.getUnZipPath());
                    //从下载队列中移除
                    getQuery().remove(new Long(id));
                }else if(downloadResult.getDownloadType().equals(DOWNLOAD_TYPE_APK)){
                    //安装包更新下载
                    String fileName = downloadResult.getDownloadPath();
                    Uri path = Uri.parse(fileName);
                    if (path.getScheme() == null) {
                        path = Uri.fromFile(new File(fileName));
                    }
                    Intent activityIntent = new Intent(Intent.ACTION_VIEW);
                    activityIntent.setDataAndType(path, "application/vnd.android.package-archive");
                    activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(activityIntent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                    getQuery().remove(new Long(id));
                }
            }
        }
    }

    public static class DownloadResult{
        private String downloadPath;//下载后保存的路径
        private String downloadUrl;//下载地址
        private long downloadId;//下载ID
        private String downloadType;//下载类型

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public long getDownloadId() {
            return downloadId;
        }

        public void setDownloadId(long downloadId) {
            this.downloadId = downloadId;
        }

        public String getDownloadType() {
            return downloadType;
        }

        public void setDownloadType(String downloadType) {
            this.downloadType = downloadType;
        }

        public String getDownloadPath() {
            return downloadPath;
        }

        public void setDownloadPath(String downloadPath) {
            this.downloadPath = downloadPath;
        }
    }

    public static class Cocos2dxAnimDownloadResult extends DownloadUtils.DownloadResult{
        private String unZipPath;

        public String getUnZipPath() {
            return unZipPath;
        }

        public void setUnZipPath(String unZipPath) {
            this.unZipPath = unZipPath;
        }
    }
}
