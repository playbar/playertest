package com.rednovo.ace.ui.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqRelationApi;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.ui.base.BaseActivity;

/**
 * 意见反馈
 * Created by Dk on 16/2/27.
 */
public class SuggestionFeedbackActivity extends BaseActivity implements View.OnClickListener {

    private EditText etSuggestionFeedback;

    private EditText etContactWay;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_suggestion_feedback);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.suggestion_feedback);
        findViewById(R.id.back_btn).setOnClickListener(this);

        etSuggestionFeedback = (EditText) findViewById(R.id.et_suggestion_feedback);
        etContactWay = (EditText) findViewById(R.id.et_suggestion_feedback_contact_way);
        findViewById(R.id.btn_commit_suggestion).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.btn_commit_suggestion:
                suggestionFeedback();
                break;
            default:

                break;
        }
    }

    private void suggestionFeedback(){
        if(etSuggestionFeedback.getText() == null || etSuggestionFeedback.getText().toString().equals("")){
            ShowUtils.showToast(R.string.feedback_can_not_null);
            return;
        }
        String userId = UserInfoUtils.isAlreadyLogin() ? UserInfoUtils.getUserInfo().getUserId() : "";
        ReqRelationApi.reqSuggestion(this, userId, etSuggestionFeedback.getText().toString(), etContactWay.getText().toString(), new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                ShowUtils.showToast(R.string.commit_success);
                finish();
            }

            @Override
            public void onRequestFailure(BaseResult error) {
                ShowUtils.showToast(R.string.commit_failed);
            }
        });
    }
}
