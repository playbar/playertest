package com.rednovo.ace.ui.activity.my;

import android.content.Intent;
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
import com.rednovo.ace.net.parser.BindWechatResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.ui.base.BaseActivity;

/**
 * 提现提示信息
 * Created by Dk on 16/3/19.
 */
public class WithdrawalsInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvRmbNum;
    private TextView tvCoinNum;
    private TextView tvId;
    private TextView tvWechatNum;
    private TextView etPhoneNum;
    private Button btnGetIdentifyCode;
    private EditText etIdentifyCode;

    private String money;
    private String silver;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_withdrawals_info);

        money = getIntent().getStringExtra("money");
        silver = getIntent().getStringExtra("silver");
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.withdrawals_info);
        findViewById(R.id.back_btn).setOnClickListener(this);

        tvRmbNum = (TextView) findViewById(R.id.tv_withdrawals_rmb_num);
        tvCoinNum = (TextView) findViewById(R.id.tv_withdrawals_coin);
        tvId = (TextView) findViewById(R.id.tv_withdrawals_account);
        tvWechatNum = (TextView) findViewById(R.id.tv_withdrawals_wechat);
        etPhoneNum = (TextView) findViewById(R.id.tv_phone_num);
        btnGetIdentifyCode = (Button) findViewById(R.id.btn_withdrawal_get_identifying_code);
        etIdentifyCode = (EditText) findViewById(R.id.et_withdrawal_identifying_code);
        findViewById(R.id.btn_commit_withdrawal).setOnClickListener(this);
        findViewById(R.id.btn_return_input_withdrawal_info).setOnClickListener(this);
        btnGetIdentifyCode.setOnClickListener(this);

        tvRmbNum.setText(money);
        tvCoinNum.setText(silver);
        tvId.setText(UserInfoUtils.getUserInfo().getUserId());
        ShowUtils.showProgressDialog(this, "", false, false);
        getBindWechatState(UserInfoUtils.getUserInfo().getUserId());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.btn_commit_withdrawal:
                withdrawalsInfo(UserInfoUtils.getUserInfo().getUserId(), silver, money);
                break;
            case R.id.btn_return_input_withdrawal_info:
                startActivity(new Intent(this, WithdrawalsActivity.class));
                finish();
                break;
            case R.id.btn_withdrawal_get_identifying_code:
                sendIdentifyingCode();
                break;
        }
    }

    private void getBindWechatState(String userId) {
        ReqUserApi.requestBindWechatState(this, userId, new RequestCallback<BindWechatResult>() {
            @Override
            public void onRequestSuccess(BindWechatResult object) {
                tvWechatNum.setText(object.getBind().getWeChatId());
                etPhoneNum.setText(object.getBind().getMobileId());
                ShowUtils.dismissProgressDialog();
            }

            @Override
            public void onRequestFailure(BindWechatResult error) {
                ShowUtils.dismissProgressDialog();
            }
        });
    }

    private void sendIdentifyingCode() {
        if (TextUtils.isEmpty(etPhoneNum.getText().toString()) || !UserInfoUtils.checkPhoneValid(etPhoneNum.getText().toString())) {
            ShowUtils.showToast(R.string.please_edit_right_phone_number);
            return;
        }
        ShowUtils.showProgressDialog(this, R.string.text_loading);
        ReqSystemApi.reqSendSms(this, etPhoneNum.getText().toString(), new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                ShowUtils.dismissProgressDialog();
                btnGetIdentifyCode.setEnabled(false);
                ShowUtils.showToast(R.string.identifying_code_get_success);
                validationTimer.start();
            }

            @Override
            public void onRequestFailure(BaseResult error) {
                ShowUtils.dismissProgressDialog();
                ShowUtils.showToast(error.getErrCode() + error.getErrMsg());
            }
        });
    }

    private void withdrawalsInfo(String userId, String coinAmount, String rmbAmount) {
        if (etIdentifyCode.length() < 6) {
            ShowUtils.showToast(R.string.verify_error);
            return;
        }
        ReqUserApi.requestExchange(this, userId, coinAmount, rmbAmount, etIdentifyCode.getText().toString(), new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                ShowUtils.showToast(R.string.withdrawals_success);
                finish();
            }

            @Override
            public void onRequestFailure(BaseResult error) {
                ShowUtils.showToast(R.string.withdrawals_failed);
            }
        });
    }

    CountDownTimer validationTimer = new CountDownTimer(120000, 1000) {

        @Override
        public void onFinish() {
            btnGetIdentifyCode.setText(R.string.get_identifying_code);
            btnGetIdentifyCode.setEnabled(true);

        }

        @Override
        public void onTick(long tickFinished) {
            btnGetIdentifyCode.setText(getString(R.string.after_second_get, tickFinished / 1000));
        }

    };
}
