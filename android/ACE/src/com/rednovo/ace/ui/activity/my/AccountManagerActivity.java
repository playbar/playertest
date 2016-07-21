package com.rednovo.ace.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.libs.ui.base.BaseActivity;

/**
 * 账号管理
 * Created by Dk on 16/2/27.
 */
public class AccountManagerActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_account_manager);

        ((TextView)findViewById(R.id.tv_title)).setText(R.string.account_manager);
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.rl_account_manager_change_password).setOnClickListener(this);
        findViewById(R.id.rl_account_manager_bind_phone).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.rl_account_manager_change_password:
                startActivity(new Intent(AccountManagerActivity.this, ChangePasswordActivity.class));
                break;

            default:

                break;
        }
    }
}
