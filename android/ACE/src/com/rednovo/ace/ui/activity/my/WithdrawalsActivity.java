package com.rednovo.ace.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.common.WebViewConfig;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.BindWechatResult;
import com.rednovo.ace.net.parser.IncomeBalanceResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.ACEWebActivity;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.ui.base.BaseActivity;


/**
 * 提现
 * Created by Dk on 16/2/25.
 */
public class WithdrawalsActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private TextView tvWithdrawalsNum;

    private TextView tvSilverNum;

    private TextView tvNeedSilverNum;

    private EditText etMoney;

    private int rate = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_withdrawals);

        tvWithdrawalsNum = (TextView) findViewById(R.id.tv_withdrawals_num);
        tvSilverNum = (TextView) findViewById(R.id.tv_withdrawals_silver_num);
        tvNeedSilverNum = (TextView) findViewById(R.id.tv_withdrawals_need_silver_num);
        etMoney = (EditText) findViewById(R.id.et_withdrawal_money);

        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.tv_withdrawals_record).setOnClickListener(this);
        findViewById(R.id.btn_withdrawals).setOnClickListener(this);
        findViewById(R.id.tv_read_withdrawals_doc).setOnClickListener(this);
        getIncomeBalance(UserInfoUtils.getUserInfo().getUserId());
        etMoney.addTextChangedListener(this);
        ShowUtils.showProgressDialog(this, "", false, false);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.tv_withdrawals_record:
                intent = new Intent(WithdrawalsActivity.this, ACEWebActivity.class);
                intent.putExtra("url", WebViewConfig.getUrl() + WebViewConfig.WITHDRAWALSRECOEDS + UserInfoUtils.getUserInfo().getUserId());
                startActivity(intent);
                break;
            case R.id.btn_withdrawals:
                withdrawals();
                break;
            case R.id.tv_read_withdrawals_doc:
                intent = new Intent(WithdrawalsActivity.this, ACEWebActivity.class);
                intent.putExtra("url", WebViewConfig.getUrl() + WebViewConfig.WITHDRAWALSDOC);
                startActivity(intent);
                break;
        }
    }

    private void withdrawals() {
        if (etMoney.getText().toString().equals("") || etMoney.getText().toString().equals("0")) {
            ShowUtils.showToast(R.string.please_input_withdrawals_money);
            return;
        }
        if (Integer.parseInt(etMoney.getText().toString()) > Integer.parseInt(tvWithdrawalsNum.getText().toString()) || Integer.parseInt(tvSilverNum.getText().toString()) == 0) {
            ShowUtils.showToast(R.string.silver_num_error);
            return;
        }
        if(Integer.parseInt(etMoney.getText().toString()) == 0 || Integer.parseInt(etMoney.getText().toString()) % 100 != 0){
            ShowUtils.showToast(R.string.withdrawals_num_error);
            return;
        }
        ShowUtils.showProgressDialog(this, "", false, false);
        getBindWechatState(UserInfoUtils.getUserInfo().getUserId());

    }


    private void getIncomeBalance(String userId) {
        ReqUserApi.requsestIncomeBalance(this, userId, new RequestCallback<IncomeBalanceResult>() {
            @Override
            public void onRequestSuccess(IncomeBalanceResult object) {
                rate = Integer.parseInt(object.getRate());
                ShowUtils.dismissProgressDialog();
                tvSilverNum.setText(object.getBalance());
                if (tvWithdrawalsNum != null) {
                    tvWithdrawalsNum.setText(Integer.parseInt(object.getBalance()) / Integer.parseInt(object.getRate()) + "");
                }
            }

            @Override
            public void onRequestFailure(IncomeBalanceResult error) {
                ShowUtils.dismissProgressDialog();
                ShowUtils.showToast(R.string.withdrawals_error);
                finish();
            }
        });
    }

    private void getBindWechatState(String userId) {
        ReqUserApi.requestBindWechatState(this, userId, new RequestCallback<BindWechatResult>() {
            @Override
            public void onRequestSuccess(BindWechatResult object) {
                ShowUtils.dismissProgressDialog();
                Intent intent = new Intent(WithdrawalsActivity.this, WithdrawalsInfoActivity.class);
                intent.putExtra("money", etMoney.getText().toString());
                intent.putExtra("silver", tvNeedSilverNum.getText().toString());
                startActivity(intent);
                finish();
            }

            @Override
            public void onRequestFailure(BindWechatResult error) {
                ShowUtils.dismissProgressDialog();
                if (error.getErrCode() == 104) {
                    Intent intent = new Intent(WithdrawalsActivity.this, BindWechatActivity.class);
                    intent.putExtra("money", etMoney.getText().toString());
                    intent.putExtra("silver", tvNeedSilverNum.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    ShowUtils.showToast(R.string.withdrawals_error);
                }
            }
        });
    }
    private boolean firstNumZero = false;
    private String beforeStr;
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.toString().equals("0") && start == 0){
            firstNumZero = true;
        }
        if(s.length() > 0 && Integer.parseInt(s.toString()) > Integer.parseInt(tvWithdrawalsNum.getText().toString())){
            beforeStr = s.toString().substring(before, s.length() - count);
        }

        if (!s.toString().equals("") && Integer.parseInt(s.toString()) > 0 && beforeStr == null) {
            tvNeedSilverNum.setText((Integer.parseInt(s.toString()) * rate) + "");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(firstNumZero){
            firstNumZero = false;
            s.clear();
        }
        if(beforeStr != null && s.length() > 0){
            beforeStr = null;
            s.delete(s.length() - 1, s.length());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ShowUtils.isProgressDialogShowing()){
            ShowUtils.dismissProgressDialog();
        }
    }
}