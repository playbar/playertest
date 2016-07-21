package com.rednovo.ace.ui.activity.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.common.WebViewConfig;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.UserBalanceResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.ACEWebActivity;
import com.rednovo.libs.common.CacheKey;
import com.rednovo.libs.common.CacheUtils;
import com.rednovo.libs.ui.base.BaseActivity;

/**
 * 账号中心
 * Created by Dk on 16/2/25.
 */
public class AccountCenterActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvLevelNum;

    private TextView tvLevelInfo;

    private TextView tvCoin;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_account_center);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.account_center);
        findViewById(R.id.back_btn).setOnClickListener(this);
        tvLevelInfo = (TextView) findViewById(R.id.tv_account_center_level_info);
        tvLevelNum = (TextView) findViewById(R.id.tv_account_center_level_num);
        tvCoin = (TextView) findViewById(R.id.tv_account_center_coin);

        findViewById(R.id.tv_account_center_recharge).setOnClickListener(this);
        findViewById(R.id.rl_account_center_recharge_records).setOnClickListener(this);
        findViewById(R.id.rl_account_center_consumption_records).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestUserBalance(this, UserInfoUtils.getUserInfo().getUserId());
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(UserInfoUtils.getUserBalance() != null){
//            tvCoin.setText(UserInfoUtils.getUserBalance().getBlance());
//            tvLevelNum.setText(UserInfoUtils.getUserInfo().getRank());
//        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.tv_account_center_recharge:
                startActivity(new Intent(AccountCenterActivity.this, RechargeActivity.class));
                break;
            case R.id.rl_account_center_recharge_records:
                intent = new Intent(AccountCenterActivity.this, ACEWebActivity.class);
                intent.putExtra("url", WebViewConfig.getUrl() + WebViewConfig.RECHARGERECORDS + UserInfoUtils.getUserInfo().getUserId());
                startActivity(intent);
                break;

            case R.id.rl_account_center_consumption_records:
                intent = new Intent(AccountCenterActivity.this, ACEWebActivity.class);
                intent.putExtra("url", WebViewConfig.getUrl() + WebViewConfig.CONSUMPTIONRECORDS + UserInfoUtils.getUserInfo().getUserId());
                intent.putExtra(ACEWebActivity.CAN_GOBACK,false);
                startActivity(intent);
                break;
            default:

                break;
        }
    }

    private void requestUserBalance(Activity activity, String userId) {
        ReqUserApi.requsetUserBalance(activity, userId, new RequestCallback<UserBalanceResult>() {
            @Override
            public void onRequestSuccess(UserBalanceResult object) {
                CacheUtils.getObjectCache().add(CacheKey.USER_BALANCE, object);
                tvCoin.setText(object.getBlance());
            }

            @Override
            public void onRequestFailure(UserBalanceResult object) {

            }
        });
    }
}
