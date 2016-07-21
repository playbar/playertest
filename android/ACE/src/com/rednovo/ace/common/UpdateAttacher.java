package com.rednovo.ace.common;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.rednovo.ace.R;
import com.rednovo.ace.view.dialog.UpdateVersionDialog;
import com.rednovo.libs.common.CacheKey;
import com.rednovo.libs.common.CacheUtils;
import com.rednovo.libs.common.DownloadUtils;
import com.rednovo.libs.common.SharedPreferenceKey;
import com.rednovo.libs.common.StorageUtils;
import com.rednovo.libs.common.SystemUtils;
import com.rednovo.libs.common.Utils;

import java.io.File;

/**
 * 版本更新控制类
 * @author Zhen.Li
 * @since 2016-05-16
 */
public class UpdateAttacher {

    public interface UpdateDialogOnClickListener{
        void onClick(View view);
    }

    private static final String LOG_TAG = "UpdateUtils";
    private static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    private static final String DOWNLOAD_FOLDER_NAME = "Downloads";
    private static final String DOWNLOAD_FILE_NAME = "Ace.apk";

    private DownloadUtils download;
    private DownloadObserver downloadObserver;
    private File downloadFile;
    private Context context;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public UpdateAttacher(Context context){
        this.context = context;

        downloadObserver = new DownloadObserver(handler);
        downloadFile = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
        if(!downloadFile.exists() || !downloadFile.isDirectory()){//存在的与文件名相同的是文件而不是文件夹时，则会抛出异常，避免这种情况加上创建这一句
            downloadFile.mkdirs();
            File file = new File(downloadFile.toString() + "/" + DOWNLOAD_FILE_NAME);
            if(file.exists()){
                file.delete();
            }
        }

    }

    private UpdateDialogOnClickListener updateDialogOnClickListener = new UpdateDialogOnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.id_update_dialog_left_btn:
                    if(!isForcibly()){
                        CacheUtils.getObjectCache().add(CacheKey.KEY_ONLINE_VERSION, getOnLineVer());
                    }
                    break;
                case R.id.id_update_dialog_right_btn:
                    updateVersion();
                    break;
            }
        }
    };

    /**
     * 检测是否需要版本更新
     */
    public boolean checkUpdateVer() {
        String onLineVer = getOnLineVer();
        String currentVer = Utils.getAppVersion(context);
        if (onLineVer != currentVer) {
            return true;
        }
        return false;
    }

    /**
     * 显示更新窗口
     */
    public void showUpdateDialog() {
        UpdateVersionDialog dialog = new UpdateVersionDialog(context, getOnLineVer(), getUpdateInfo(),  getUpdateUrl(), isForcibly(), updateDialogOnClickListener);
        dialog.show();
    }

    /**
    * 在activity onResume()中调用，监控下载
    */
    public void onResume(){
        context.getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);
    }

    /**
     * 在activity onPause()中调用，暂停监控
     */
    public void onPause(){
        context.getContentResolver().unregisterContentObserver(downloadObserver);
    }

    /**
     * 在activity onDestory()中调用，释放handler
     */
    public void onDestory(){
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 不选择进行更新，此版本不再提示更新
     * @return true为以忽略，false为未忽略
     */
    public boolean refuseUpdate(){
        if(CacheUtils.getObjectCache().contain(CacheKey.KEY_ONLINE_VERSION)){
            String cacheVersion = (String) CacheUtils.getObjectCache().get(CacheKey.KEY_ONLINE_VERSION);
            String onLineVersion = getOnLineVer();
            if(onLineVersion.equals(cacheVersion)){
                return true;
            }
        }
        return false;
    }

    /**
     * 更新版本
     */
    private void updateVersion(){
        download = DownloadUtils.getInstance(context);
        DownloadUtils.Cocos2dxAnimDownloadResult result = new DownloadUtils.Cocos2dxAnimDownloadResult();
        result.setDownloadUrl(getUpdateUrl());
        result.setDownloadType(DownloadUtils.DOWNLOAD_TYPE_APK);
        result.setDownloadPath(downloadFile + "/" + DOWNLOAD_FILE_NAME);
        download.addDownloadTask(result, context.getString(R.string.app_name), context.getString(R.string.share_body), true, false, DOWNLOAD_FOLDER_NAME, DOWNLOAD_FILE_NAME);
    }

    /**
     * 获取线上版本号
     * @return 版本号
     */
    private String getOnLineVer(){
        if(CacheUserInfoUtils.getSystemVersion() != null){
            return CacheUserInfoUtils.getSystemVersion().getAndroidVer();
        }
        return "0";
    }

    /**
     * 获取更新版本说明
     * @return
     */
    private String getUpdateInfo(){
        String updateInfo = "";
        if(CacheUserInfoUtils.getSystemVersion() != null){
            updateInfo = CacheUserInfoUtils.getSystemVersion().getUpdateInfo();
            if(updateInfo.contains("\\n")){//服务器返回字符修改为转义字符
                updateInfo = updateInfo.replace("\\n", "\n");
            }
            if(updateInfo.contains("\\r")){
                updateInfo = updateInfo.replace("\\r", "\r");
            }
            return updateInfo;
        }
        return null;
    }

    /**
     * 获取更新包url
     * @return
     */
    private String getUpdateUrl(){
        if(CacheUserInfoUtils.getSystemVersion() != null){
            return CacheUserInfoUtils.getSystemVersion().getUpdateURL() + "/" + SystemUtils.getIntegerChannel();
        }
        return null;
    }

    /**
     * 是否是强制更新  true为强制更新，false为普通更新
     * @return
     */
    private boolean isForcibly(){
//        if(CacheUserInfoUtils.getSystemVersion() != null){
//            return CacheUserInfoUtils.getSystemVersion().getSysUpdateType() == "0" ? false : true;
//        }
        return false;
    }

    private class DownloadObserver extends ContentObserver {

        private Handler handler;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public DownloadObserver(Handler handler) {
            super(handler);
            this.handler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            update();
        }

        private void update(){

        }
    }
}
