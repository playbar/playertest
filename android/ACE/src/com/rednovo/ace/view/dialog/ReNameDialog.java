package com.rednovo.ace.view.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.my.UserDataActivity;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.widget.dialog.BaseDialog;

/**
 * Created by Dk on 16/3/11.
 */
public class ReNameDialog extends BaseDialog{

    private EditText etReName;

    private Handler callback;

    public ReNameDialog(Context context, Handler callback) {
        super(context, R.layout.layout_rename_dialog);
        this.callback = callback;
        etReName = (EditText) findViewById(R.id.et_rename_dialog);
        findViewById(R.id.btn_rename_dialog_cancel).setOnClickListener(onClickListener);
        findViewById(R.id.btn_rename_dialog_sure).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_rename_dialog_cancel:
                    dismiss();
                    break;
                case R.id.btn_rename_dialog_sure:
                    rename();
                    break;
                default:

                    break;
            }
        }
    };

    private void rename(){
        if(TextUtils.isEmpty(etReName.getText().toString())){
            ShowUtils.showToast(R.string.edit_can_not_null);
            return;
        }
        if(etReName.getText().toString().contains(" ")){
            ShowUtils.showToast(R.string.edit_can_not_contains_space);
            return;
        }
        ReqUserApi.requestUpdateNickName(null, UserInfoUtils.getUserInfo().getUserId(), etReName.getText().toString(), new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                ShowUtils.showToast(R.string.rename_success);
                UserInfoUtils.getUserInfo().setNickName(etReName.getText().toString());
                Message msg = new Message();
                msg.what = UserDataActivity.MSG_UPDATE_NICKNAME;
                msg.obj = etReName.getText().toString();
                callback.sendMessage(msg);
                dismiss();
            }

            @Override
            public void onRequestFailure(BaseResult error) {
                ShowUtils.showToast(R.string.rename_failed);
            }
        });
    }
}
