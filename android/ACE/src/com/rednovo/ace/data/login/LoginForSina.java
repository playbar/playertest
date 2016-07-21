package com.rednovo.ace.data.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.OtherReqApi;
import com.rednovo.ace.net.parser.SinaInfoResult;
import com.rednovo.ace.net.request.OtherRequestCallback;
import com.rednovo.libs.common.Constant;
import com.rednovo.libs.common.ShowUtils;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * 使用微博登录
 * Created by Dk on 16/3/6.
 */
public class LoginForSina {

    private Activity activity;

    private AuthInfo mAuthInfo;

    private SsoHandler mSsoHandler;

    private UserInfoUtils.OnLoginFinish onLoginFinish;

    public LoginForSina(Activity activity, UserInfoUtils.OnLoginFinish onLoginFinish) {
        this.activity = activity;
        this.onLoginFinish = onLoginFinish;
        mAuthInfo = new AuthInfo(activity, Constant.SINA_APPID, Constant.SINA_REDIRECT_URL, null);
        mSsoHandler = new SsoHandler(activity, mAuthInfo);
    }

    public void login() {
        mSsoHandler.authorize(mAuthListener);
    }

    /**
     * 使用该控件进行授权登陆时，需要手动调用该函数。
     * <p>
     * 重要：使用该控件的 Activity 必须重写 {@link Activity#onActivityResult(int, int, Intent)}，
     * 并在内部调用该函数，否则无法授权成功。</p>
     * <p>Sample Code：</p>
     * <pre class="prettyprint">
     * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     * super.onActivityResult(requestCode, resultCode, data);
     * <p/>
     * // 在此处调用
     * mLoginButton.onActivityResult(requestCode, resultCode, data);
     * }
     * </pre>
     *
     * @param requestCode 请查看 {@link Activity#onActivityResult(int, int, Intent)}
     * @param resultCode  请查看 {@link Activity#onActivityResult(int, int, Intent)}
     * @param data        请查看 {@link Activity#onActivityResult(int, int, Intent)}
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 登录完成后的监听
     */
    WeiboAuthListener mAuthListener = new WeiboAuthListener() {
        @Override
        public void onComplete(Bundle bundle) {
            ShowUtils.showProgressDialog(activity, R.string.text_loading);
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
            if (accessToken != null && accessToken.isSessionValid()) {
                String uid = accessToken.getUid();
                String token = accessToken.getToken();
                OtherReqApi.requestSinaInfo(token, uid, new OtherRequestCallback() {
                    @Override
                    public void onRequestSuccess(Object object) {
                        SinaInfoResult result = (SinaInfoResult) object;
                        String name = result.getName();
                        if(name.length() > 8){
                            name = name.substring(0, 7);
                        }
                        UserInfoUtils.login(activity, result.getId() + "", null, UserInfoUtils.SINA_LOGIN_TYPE, name, result.getProfile_image_url(), onLoginFinish);
                    }

                    @Override
                    public void onRequestFailure(Object error) {
                        onLoginFinish.loginFailed(null);
                    }
                });
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ShowUtils.showToast(e.getMessage());
        }

        @Override
        public void onCancel() {
            ShowUtils.showToast("用户取消");
        }
    };
}
