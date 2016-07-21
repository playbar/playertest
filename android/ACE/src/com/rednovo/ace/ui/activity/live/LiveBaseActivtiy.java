package com.rednovo.ace.ui.activity.live;

import android.os.Bundle;
import android.view.WindowManager;

import com.rednovo.ace.R;
import com.rednovo.ace.core.session.SendUtils;
import com.rednovo.ace.core.session.SessionEngine;
import com.rednovo.ace.common.ScreenListener;
import com.rednovo.ace.view.dialog.SingleOptionDialog;
import com.rednovo.libs.ui.base.BaseActivity;

public abstract class LiveBaseActivtiy extends BaseActivity {

    protected ScreenListener mScreenListener;
    protected SessionEngine sessionEngine;
    protected boolean isInitSuccess;
    protected SingleOptionDialog singleOptionDialog = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        // 保持屏幕亮度显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sessionEngine = SessionEngine.getSessionEngine();
        startSession();
        mScreenListener = new ScreenListener();
//        RefWatcher refWatcher = AceApplication.getRefWatcher(this);
//        refWatcher.watch(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // // clear屏幕亮度的显示
    }

    /**
     * 开启会话
     */
    protected void startSession() {
        sessionEngine.start();
    }

    /**
     * 发送进房间通知
     *
     * @param showId
     */
    protected void sendEnterRoom(String userId, String showId) {
        SendUtils.enterRoom(userId, showId);
    }

    /**
     * 发送退出房间通知
     */
    protected void sendExitRoom(String userId, String showId) {
        SendUtils.exitRoom(userId, showId);
    }

    /**
     * 关闭会话
     */
    protected void closeSession() {
        sessionEngine.close();
    }


    /**
     * 封禁的dialog
     *
     * @param title
     * @param content
     */
    protected void freezeDilaog(String title, String content) {
        if (singleOptionDialog == null) {
            singleOptionDialog = new SingleOptionDialog(this);
        }
        singleOptionDialog.setText(title, content, getString(R.string.i_know));
        singleOptionDialog.setCancelable(false);
        singleOptionDialog.setCanceledOnTouchOutside(false);
        singleOptionDialog.setOnSingleButtonClickListener(onSingleButtonClickListener);
        singleOptionDialog.show();
    }

    protected SingleOptionDialog.OnSingleButtonClickListener onSingleButtonClickListener = new SingleOptionDialog.OnSingleButtonClickListener() {
        @Override
        public void onClick() {
            freezeBtnClick();
        }
    };

    protected void freezeBtnClick() {

    }
}
