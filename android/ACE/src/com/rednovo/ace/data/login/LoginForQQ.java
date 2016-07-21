package com.rednovo.ace.data.login;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.OtherReqApi;
import com.rednovo.ace.net.parser.QQInfoResult;
import com.rednovo.ace.net.request.OtherRequestCallback;
import com.rednovo.libs.common.ThirdPartyAPI;
import com.rednovo.libs.common.Constant;
import com.rednovo.libs.common.ShowUtils;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 使用QQ登录
 * Created by Dk on 16/3/6.
 */
public class LoginForQQ {

    private Activity activity;

    private UserInfoUtils.OnLoginFinish onLoginFinish;

    public LoginForQQ(Activity activity, UserInfoUtils.OnLoginFinish onLoginFinish) {
        this.activity = activity;
        this.onLoginFinish = onLoginFinish;
    }

    public void login() {
        if (ThirdPartyAPI.isQQSessionValid()) {
            ThirdPartyAPI.mTencent.login(activity, "all", mIUiListener);
        } else {
            ThirdPartyAPI.mTencent.login(activity, "all", mIUiListener);
            ThirdPartyAPI.mTencent.logout(activity);
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == Constants.RESULT_LOGIN) {
            if(resultCode == Constants.RESULT_LOGIN) {
                ThirdPartyAPI.mTencent.handleResultData(data, mIUiListener);
                return true;
            }
        }
        return false;
    }

    IUiListener mIUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            if (null == o) {
                return;
            }

            JSONObject jsonResponse = (JSONObject) o;

            if (null != jsonResponse && jsonResponse.length() == 0) {
                return;
            }

            try {
                String token = jsonResponse.getString(Constants.PARAM_ACCESS_TOKEN);
                String openId = jsonResponse.getString(Constants.PARAM_OPEN_ID);
                String expires = jsonResponse.getString(Constants.PARAM_EXPIRES_IN);
                if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                        && !TextUtils.isEmpty(openId)) {
                    ThirdPartyAPI.mTencent.setAccessToken(token, expires);
                    ThirdPartyAPI.mTencent.setOpenId(openId);
                }
                ShowUtils.showProgressDialog(activity, R.string.logining);
                getQQInfo(token, Constant.QQ_APPID, openId);
            } catch (JSONException e) {
                onLoginFinish.loginFailed(null);
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            onLoginFinish.loginFailed(null);
        }

        @Override
        public void onCancel() {
            onLoginFinish.loginFailed(null);
        }
    };

    private void getQQInfo(String account_token, String oauth_consumer, final String openid){
        OtherReqApi.requestQQInfo(account_token, oauth_consumer, openid, new OtherRequestCallback() {
            @Override
            public void onRequestSuccess(Object object) {
                QQInfoResult result = (QQInfoResult)object;
                String name = result.getNickname();
                if(name.length() > 8){
                    name = name.substring(0, 7);
                }
                UserInfoUtils.login(activity, openid, null, UserInfoUtils.QQ_LOGIN_TYPE, name, result.getFigureurl_qq_2(), onLoginFinish);
            }

            @Override
            public void onRequestFailure(Object error) {
                ShowUtils.dismissProgressDialog();
            }
        });
    }
}
