package com.rednovo.ace.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.common.UpdateAttacher;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.ui.activity.LoginActivity;
import com.rednovo.ace.ui.activity.RegistActivity;
import com.rednovo.ace.view.dialog.SimpleDialog;
import com.rednovo.ace.view.dialog.SimpleDialog.OnSimpleDialogBtnClickListener;
import com.rednovo.libs.common.CacheUtils;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.Utils;
import com.rednovo.libs.net.fresco.FrescoEngine;
import com.rednovo.libs.ui.base.BaseActivity;

/**
 * 设置
 * Created by Dk on 16/2/25.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener, OnSimpleDialogBtnClickListener {

    private TextView tvCache;

    private SimpleDialog logoutDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_setting);

        tvCache = (TextView) findViewById(R.id.tv_setting_cache);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.setting);
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.rl_setting_account_manager).setOnClickListener(this);
        findViewById(R.id.rl_setting_clean_cache).setOnClickListener(this);
        findViewById(R.id.rl_setting_suggestion_feedback).setOnClickListener(this);
        findViewById(R.id.rl_setting_update_version).setOnClickListener(this);
        findViewById(R.id.rl_setting_about).setOnClickListener(this);
        findViewById(R.id.rl_setting_logout).setOnClickListener(this);

        if(!UserInfoUtils.isAlreadyLogin()){
            findViewById(R.id.rl_setting_account_manager).setVisibility(View.GONE);
            findViewById(R.id.rl_setting_logout).setVisibility(View.GONE);
        }
        tvCache.setText(Utils.getImageCacheSize(this)+"");
        if(UserInfoUtils.isAlreadyLogin() && UserInfoUtils.getUserInfo() != null){
            if(UserInfoUtils.getUserInfo().getChannel() == null || UserInfoUtils.getUserInfo().getChannel().equals("1") || UserInfoUtils.getUserInfo().getChannel().equals("2") || UserInfoUtils.getUserInfo().getChannel().equals("5")){
                findViewById(R.id.rl_setting_account_manager).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting_account_manager:
                startActivity(new Intent(SettingActivity.this, AccountManagerActivity.class));
                break;
            case R.id.rl_setting_clean_cache:
                ShowUtils.showProgressDialog(this, R.string.cache_cleaning);
                Utils.cleanCache(this);
                ShowUtils.dismissProgressDialog();
                ShowUtils.showToast(R.string.clean_cache_success);
                tvCache.setText("0.0M");
                break;
            case R.id.rl_setting_suggestion_feedback:
                startActivity(new Intent(SettingActivity.this, SuggestionFeedbackActivity.class));
                break;
            case R.id.rl_setting_update_version:
                UpdateAttacher updateAttacher = new UpdateAttacher(this);
                if (updateAttacher.checkUpdateVer()) {
                    updateAttacher.showUpdateDialog();
                }else{
                    ShowUtils.showToast(R.string.not_find_new_version);
                }
                break;
            case R.id.rl_setting_about:
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));
                break;
            case R.id.rl_setting_logout:
                logoutDialog = new SimpleDialog(this, this, R.string.is_quit, R.string.text_cancel, R.string.quit);
                logoutDialog.show();
                break;
            case R.id.back_btn:
                finish();
                break;
            default:

                break;
        }
    }

    @Override
    public void onSimpleDialogLeftBtnClick() {

    }

    @Override
    public void onSimpleDialogRightBtnClick() {
        ShowUtils.showToast(R.string.logout_success);
        UserInfoUtils.logout();
        finish();
    }
}
