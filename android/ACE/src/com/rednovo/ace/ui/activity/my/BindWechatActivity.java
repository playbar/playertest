package com.rednovo.ace.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
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
 * 绑定微信
 * Created by Dk on 16/3/19.
 */
public class BindWechatActivity extends BaseActivity implements OnClickListener {

    private EditText etWechatNum;
    private EditText etEtWechatNumAgain;
    private EditText etPhoneNum;
    private EditText etVerifyCode;
    private Button getVerifyCode;

    private String money;
    private String silver;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_bind_wechat);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.bind_wechat);
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.btn_bind_wechat).setOnClickListener(this);
        money = getIntent().getStringExtra("money");
        silver = getIntent().getStringExtra("silver");

        etWechatNum = (EditText) findViewById(R.id.et_wechat_num);
        etEtWechatNumAgain = (EditText) findViewById(R.id.et_wechat_num_again);
        etPhoneNum = (EditText) findViewById(R.id.et_bind_phone);
        etVerifyCode = (EditText) findViewById(R.id.et_withdrawal_identifying_code);
        getVerifyCode = (Button) findViewById(R.id.btn_withdrawal_get_identifying_code);
        getVerifyCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.btn_bind_wechat:
                bindExchangeInfo();
                break;
            case R.id.btn_withdrawal_get_identifying_code:
                sendIdentifyingCode();
                break;
        }
    }

    private void bindExchangeInfo() {
        if (TextUtils.isEmpty(etWechatNum.getText().toString()) || !etWechatNum.getText().toString().equals(etEtWechatNumAgain.getText().toString())) {
            ShowUtils.showToast(R.string.wechat_not_same);
            return;
        }
        if (TextUtils.isEmpty(etPhoneNum.getText().toString()) || !UserInfoUtils.checkPhoneValid(etPhoneNum.getText().toString())) {
            ShowUtils.showToast(R.string.please_edit_right_phone_number);
            return;
        }
        if (etVerifyCode.getText().toString().length() < 6) {
            ShowUtils.showToast(R.string.verify_error);
            return;
        }
        requestBindExchangeInfo(UserInfoUtils.getUserInfo().getUserId(), etPhoneNum.getText().toString(), etWechatNum.getText().toString(), etVerifyCode.getText().toString());

    }

    private void requestBindExchangeInfo(String userId, String mobileId, String weChatId, String verifyCode) {
        ReqUserApi.requestBindExchangeInfo(this, userId, mobileId, weChatId, verifyCode, new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                Intent intent = new Intent(BindWechatActivity.this, WithdrawalsInfoActivity.class);
                intent.putExtra("money", money);
                intent.putExtra("silver", silver);
                startActivity(intent);
                finish();
            }

            @Override
            public void onRequestFailure(BaseResult error) {
                ShowUtils.showToast(R.string.bind_failed);
            }
        });
    }

    private void sendIdentifyingCode() {
        if (etPhoneNum.getText() == null || etPhoneNum.getText().toString().equals("") || !UserInfoUtils.checkPhoneValid(etPhoneNum.getText().toString())) {
            ShowUtils.showToast(R.string.please_edit_right_phone_number);
            return;
        }
        getVerifyCode.setEnabled(false);
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
                getVerifyCode.setEnabled(false);
                ShowUtils.dismissProgressDialog();
                ShowUtils.showToast(R.string.identifying_code_get_failed);
            }
        });
    }

    CountDownTimer validationTimer = new CountDownTimer(120000, 1000) {

        @Override
        public void onFinish() {
            getVerifyCode.setText(R.string.get_identifying_code);
            getVerifyCode.setEnabled(true);

        }

        @Override
        public void onTick(long tickFinished) {
            getVerifyCode.setText(getString(R.string.after_second_get, tickFinished / 1000));
        }
    };
}
