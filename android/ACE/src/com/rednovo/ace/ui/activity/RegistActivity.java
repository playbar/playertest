package com.rednovo.ace.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqSystemApi;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.ui.base.BaseActivity;

/**
 * 注册
 * Created by Dk on 16/2/28.
 */
public class RegistActivity extends BaseActivity implements View.OnClickListener, UserInfoUtils.OnLoginFinish {

    private Button btnGetIdentifyingCode;

    private EditText etPhoneNum;

    private EditText etPwd;

    private EditText etIdentifyingCode;

    private ImageView imgCleanInput;

    private ImageView imgCleanInputIdentify;

    private ImageView imgCleanInputPassword;

    private boolean isLaunchGo = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_regist);
        isLaunchGo = getIntent().getBooleanExtra("isLaunchGo", false);
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.regist);
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.btn_regist_regist).setOnClickListener(this);

        btnGetIdentifyingCode = (Button) findViewById(R.id.btn_regist_get_identifying_code);
        etPhoneNum = (EditText) findViewById(R.id.et_regist_phone_num);
        etPwd = (EditText) findViewById(R.id.et_regist_password);
        etIdentifyingCode = (EditText) findViewById(R.id.et_regist_identifying_code);
        imgCleanInput = (ImageView) findViewById(R.id.img_clean_input);
        imgCleanInputIdentify = (ImageView) findViewById(R.id.img_clean_input_identify);
        imgCleanInputPassword = (ImageView) findViewById(R.id.img_clean_input_password);

        btnGetIdentifyingCode.setOnClickListener(this);
        imgCleanInput.setOnClickListener(this);
        imgCleanInputIdentify.setOnClickListener(this);
        imgCleanInputPassword.setOnClickListener(this);
        etPhoneNum.addTextChangedListener(phoneNumTextWathcher);
        etIdentifyingCode.addTextChangedListener(identifyingCodeTextWatcher);
        etPwd.addTextChangedListener(passwordTextWatcher);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(isLaunchGo){
                redirectClose(MainActivity.class);
            }else{
                finish();
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    CountDownTimer validationTimer = new CountDownTimer(120000, 1000) {

        @Override
        public void onFinish() {
            btnGetIdentifyingCode.setText(R.string.get_identifying_code);
            btnGetIdentifyingCode.setEnabled(true);

        }

        @Override
        public void onTick(long tickFinished) {
            btnGetIdentifyingCode.setText(getString(R.string.after_second_get, tickFinished / 1000));
        }

    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                if(isLaunchGo){
                    redirectClose(MainActivity.class);
                }else{
                    finish();
                }
                break;
            case R.id.btn_regist_get_identifying_code:
                sendIdentifyingCode();
                break;
            case R.id.btn_regist_regist:
                if (etPhoneNum.getText() == null || etPhoneNum.getText().toString().equals("") || !UserInfoUtils.checkPhoneValid(etPhoneNum.getText().toString())) {
                    ShowUtils.showToast(R.string.please_edit_right_phone_number);
                    return;
                }
                if (!UserInfoUtils.checkPwdValid(etPwd.getText().toString())) {
                    return;
                }
                if (etIdentifyingCode.getText() == null || etIdentifyingCode.getText().toString().equals("")) {
                    ShowUtils.showToast(R.string.identifying_code_can_not_null);
                    return;
                }

                UserInfoUtils.regist(this, etPhoneNum.getText().toString(), etPwd.getText().toString(), etIdentifyingCode.getText().toString(), this);
                ShowUtils.showProgressDialog(RegistActivity.this, R.string.registing);
                break;
            case R.id.img_clean_input:
                etPhoneNum.setText("");
                break;
            case R.id.img_clean_input_identify:
                etIdentifyingCode.setText("");
                break;
            case R.id.img_clean_input_password:
                etPwd.setText("");
                break;
            default:

                break;
        }
    }

    private void sendIdentifyingCode() {
        if (etPhoneNum.getText() == null || etPhoneNum.getText().toString().equals("") || !UserInfoUtils.checkPhoneValid(etPhoneNum.getText().toString())) {
            ShowUtils.showToast(R.string.please_edit_right_phone_number);
            return;
        }
        btnGetIdentifyingCode.setEnabled(false);
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
                ShowUtils.dismissProgressDialog();
                btnGetIdentifyingCode.setEnabled(true);
                ShowUtils.showToast(R.string.identifying_code_get_failed);
            }
        });
    }

    @Override
    public void loginSuccess(String loginType) {
        ShowUtils.dismissProgressDialog();
        if(isLaunchGo){
            redirectClose(MainActivity.class);
        }else{
            finish();
        }
    }

    @Override
    public void loginFailed(UserInfoResult error) {
        ShowUtils.dismissProgressDialog();
        if (error.getErrCode() == 210) {
            ShowUtils.showToast(R.string.phone_already_regist);
        } else {
            ShowUtils.showToast(R.string.regist_failed);
        }
    }

    TextWatcher phoneNumTextWathcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s.toString())) {
                imgCleanInput.setVisibility(View.VISIBLE);
            } else {
                imgCleanInput.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher identifyingCodeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s.toString())) {
                imgCleanInputIdentify.setVisibility(View.VISIBLE);
            } else {
                imgCleanInputIdentify.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s.toString())) {
                imgCleanInputPassword.setVisibility(View.VISIBLE);
            } else {
                imgCleanInputPassword.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
