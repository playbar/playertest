package com.rednovo.ace.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.MainActivity;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.ui.base.BaseActivity;

/**
 * 修改密码
 * Created by Dk on 16/2/27.
 */
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText etOldPwd;

    private EditText etNewPwd;

    private EditText etNewPwdAgain;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_change_password);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.change_password);
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.btn_change_password).setOnClickListener(this);

        etOldPwd = (EditText) findViewById(R.id.et_change_password_old_password);
        etNewPwd = (EditText) findViewById(R.id.et_change_password_new_password);
        etNewPwdAgain = (EditText) findViewById(R.id.et_change_password_new_password_again);
    }

    private void changePassword() {
        if(!UserInfoUtils.checkPwdValid(etOldPwd.getText().toString())){
            return;
//            ShowUtils.showToast(R.string.please_edit_old_passwoed);
        }
        if(!UserInfoUtils.checkPwdValid(etNewPwd.getText().toString())){
            return;
//            ShowUtils.showToast(R.string.please_edit_new_passwoed);
        }

        if (!etNewPwd.getText().toString().equals(etNewPwdAgain.getText().toString())) {
            ShowUtils.showToast(R.string.password_not_same);
            return;
        }
        ShowUtils.showProgressDialog(this, R.string.text_loading);
        ReqUserApi.requestUpdatePasswd(this, UserInfoUtils.getUserInfo().getUserId(), etOldPwd.getText().toString(), etNewPwd.getText().toString(), new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                ShowUtils.dismissProgressDialog();
                ShowUtils.showToast(R.string.change_password_success);
                UserInfoUtils.logout();
                startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onRequestFailure(BaseResult error) {
                if (error.getErrCode() == 203) {
                    ShowUtils.showToast(R.string.password_wrong);
                } else {
                    ShowUtils.showToast(R.string.change_password_failed);
                }
                ShowUtils.dismissProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;

            case R.id.btn_change_password:
                changePassword();
                break;
            default:

                break;
        }
    }
}
