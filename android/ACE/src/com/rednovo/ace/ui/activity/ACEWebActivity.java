package com.rednovo.ace.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.ui.base.BaseActivity;

/**
 * 网页页面
 * Created by Dk on 16/3/14.
 */
public class ACEWebActivity extends BaseActivity implements View.OnClickListener {

    private static final int VISIBLE_WEB = 0;
    private static final int VISIBLE_ERROR = 1;
    private final static int FILECHOOSER_RESULTCODE = 0x000001;
    public static String CAN_GOBACK = "goback";

    private boolean mIsLaunchAD;

    private WebView wvAce;

    private TextView tvTitle;

    private LinearLayout llWebErrorInfo;

    private ValueCallback<Uri> mUploadMessage;

    private boolean isFirst = false;
    private boolean canGoBack = true;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_ace_web);
        wvAce = (WebView) findViewById(R.id.wv_ace);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        llWebErrorInfo = (LinearLayout) findViewById(R.id.ll_web_error_info);

        findViewById(R.id.back_btn).setOnClickListener(this);

        Intent intent = getIntent();
        String pathUrl = intent.getStringExtra("url");
        canGoBack = intent.getBooleanExtra(CAN_GOBACK,true);
        mIsLaunchAD = intent.getBooleanExtra("IS_LAUNCH_AD", false);
        String newsTitle = intent.getStringExtra("INTENT_TITLE");
        tvTitle.setText(newsTitle);

        wvAce.getSettings().setJavaScriptEnabled(true);
        wvAce.getSettings().setUseWideViewPort(true);
        wvAce.getSettings().setLoadWithOverviewMode(true);
        wvAce.setHorizontalScrollBarEnabled(false);
        wvAce.setVerticalScrollBarEnabled(false);
        wvAce.setWebViewClient(new MyWebViewClient());
        wvAce.setWebChromeClient(new MyWebChromeClient());
        wvAce.loadUrl(pathUrl);

        wvAce.addJavascriptInterface(this, "ace");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
//            wvAce.reload();
        }
        isFirst = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wvAce.canGoBack() && canGoBack) {
            wvAce.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;
            Uri result = data == null || resultCode != RESULT_OK ? null
                    : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                if (wvAce.canGoBack() && canGoBack) {
                    wvAce.goBack();
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public void finish() {
        if (mIsLaunchAD) {
            if (UserInfoUtils.isAlreadyLogin()) {
                redirect(MainActivity.class);
                overridePendingTransition(R.anim.anim_alpha_in,R.anim.anim_alpha_out);
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("isLaunchGo", true);
                redirect(intent);
                overridePendingTransition(R.anim.anim_alpha_in,R.anim.anim_alpha_out);
            }
        }
        super.finish();
    }

    private void selectView(int ref) {
        switch (ref) {
            case VISIBLE_WEB:
                wvAce.setVisibility(View.VISIBLE);
                llWebErrorInfo.setVisibility(View.GONE);
                break;
            case VISIBLE_ERROR:
                llWebErrorInfo.setVisibility(View.VISIBLE);
                wvAce.setVisibility(View.GONE);
                break;
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            selectView(VISIBLE_ERROR);
           // ShowUtils.dismissProgressDialog();
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
           // ShowUtils.showProgressDialog(ACEWebActivity.this, R.string.text_loading, false, true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            selectView(VISIBLE_WEB);
            ShowUtils.dismissProgressDialog();

            if(!StringUtils.isEmpty(view.getTitle())) {
                tvTitle.setText(view.getTitle());
            }
        }
    }

    private class MyWebChromeClient extends WebChromeClient{
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            if (mUploadMessage != null) return;
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult( Intent.createChooser( i, "File Chooser" ), FILECHOOSER_RESULTCODE );
        }
        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser( uploadMsg, "" );
        }
        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg, acceptType);
        }
    }
}
