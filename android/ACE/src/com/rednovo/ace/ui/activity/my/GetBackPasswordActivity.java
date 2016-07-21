package com.rednovo.ace.ui.activity.my;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqSystemApi;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.ui.base.BaseActivity;

/**
 * 忘记密码
 * Created by Dk on 16/3/6.
 */
public class GetBackPasswordActivity extends BaseActivity implements View.OnClickListener{

    private EditText etIdentifyingCode;

    private EditText etPhoneNum;

    private EditText etNewPassword;

    private TextView tvWrong;

    private Button btnIdentifyingCode;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_get_back_password);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.forget_password_title);
        findViewById(R.id.back_btn).setOnClickListener(this);
        etIdentifyingCode = (EditText) findViewById(R.id.et_get_back_password_identifying_code);
        etPhoneNum = (EditText) findViewById(R.id.et_get_back_password_number);
        etNewPassword = (EditText) findViewById(R.id.et_get_back_password_new_password);
        tvWrong = (TextView) findViewById(R.id.tv_get_back_password_identifying_code_wrong);
        btnIdentifyingCode = (Button) findViewById(R.id.btn_get_back_password_get_identifying_code);
        findViewById(R.id.btn_next_step).setOnClickListener(this);
        btnIdentifyingCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.btn_get_back_password_get_identifying_code:
                sendIdentifyingCode();
                break;
            case R.id.btn_next_step:
                getBackPassWord();
                break;
        }
    }

    CountDownTimer validationTimer = new CountDownTimer(120000, 1000) {

        @Override
        public void onFinish() {
            btnIdentifyingCode.setText(R.string.get_identifying_code);
            btnIdentifyingCode.setEnabled(true);

        }

        @Override
        public void onTick(long tickFinished) {
            btnIdentifyingCode.setText(getString(R.string.after_second_get, tickFinished / 1000));
        }

    };

    private void getBackPassWord(){
        if(TextUtils.isEmpty(etPhoneNum.getText().toString()) || !UserInfoUtils.checkPhoneValid(etPhoneNum.getText().toString())){
            ShowUtils.showToast(R.string.please_edit_right_phone_number);
            return;
        }

        if(TextUtils.isEmpty(etIdentifyingCode.getText().toString()) || etIdentifyingCode.getText().toString().length() < 6){
            ShowUtils.showToast(R.string.identifying_code_wrong);
            return;
        }

        if (!UserInfoUtils.checkPwdValid(etNewPassword.getText().toString())) {
            return;
        }

        ShowUtils.showProgressDialog(this, R.string.text_loading);
        ReqUserApi.requestResetPassword(this, etPhoneNum.getText().toString(), etIdentifyingCode.getText().toString(), etNewPassword.getText().toString(), new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                ShowUtils.dismissProgressDialog();
                ShowUtils.showToast(R.string.reset_password_success);
                finish();
            }

            @Override
            public void onRequestFailure(BaseResult error) {
                ShowUtils.showToast(R.string.reset_password_error);
                ShowUtils.dismissProgressDialog();
            }
        });

    }

    private void sendIdentifyingCode(){
        if (etPhoneNum.getText() == null || etPhoneNum.getText().toString().equals("") || !UserInfoUtils.checkPhoneValid(etPhoneNum.getText().toString())) {
            ShowUtils.showToast(R.string.please_edit_right_phone_number);
            return;
        }
        btnIdentifyingCode.setEnabled(false);
        ShowUtils.showProgressDialog(this, R.string.text_loading);
        ReqSystemApi.reqSendSms(this, etPhoneNum.getText().toString(), new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                ShowUtils.dismissProgressDialog();
                ShowUtils.showToast(R.string.identifying_code_get_success);
                validationTimer.start();
            }

            @Override
            public void onRequestFailure(BaseResult error) {
                btnIdentifyingCode.setEnabled(true);
                ShowUtils.dismissProgressDialog();
                ShowUtils.showToast(R.string.identifying_code_get_failed);
            }
        });
    }
}
