package com.rednovo.ace.ui.activity.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.ui.base.BaseActivity;

/**
 * 修改签名
 * Created by Dk on 16/3/12.
 */
public class AutographActivity extends BaseActivity implements View.OnClickListener {

    private EditText etAutograph;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_autograph);

        findViewById(R.id.back_btn).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(getString(R.string.autograph));

        etAutograph = (EditText) findViewById(R.id.et_autograph);

        findViewById(R.id.btn_autograph_commit).setOnClickListener(this);

        etAutograph.setText(UserInfoUtils.getUserInfo().getSignature());
    }

    private void updateAutograph() {
        ReqUserApi.requestUpdateSignature(this, UserInfoUtils.getUserInfo().getUserId(), etAutograph.getText().toString(), new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                UserInfoUtils.getUserInfo().setSignature(etAutograph.getText().toString());
                ShowUtils.dismissProgressDialog();
                ShowUtils.showToast(R.string.update_autograph_success);
                finish();
            }

            @Override
            public void onRequestFailure(BaseResult error) {
                ShowUtils.dismissProgressDialog();
                ShowUtils.showToast(R.string.update_autograph_failed);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.btn_autograph_commit:
                ShowUtils.showProgressDialog(this, R.string.text_loading);
                updateAutograph();
                break;
            default:

                break;
        }
    }
}
