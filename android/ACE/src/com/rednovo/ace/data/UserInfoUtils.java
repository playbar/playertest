package com.rednovo.ace.data;

import android.app.Activity;
import android.text.TextUtils;

import com.rednovo.ace.AceApplication;
import com.rednovo.ace.R;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.parser.LoginInfoResult;
import com.rednovo.ace.net.parser.UserBalanceResult;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.libs.common.CacheKey;
import com.rednovo.libs.common.CacheUtils;
import com.rednovo.libs.common.ShowUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Dk on 16/3/7.
 */
public class UserInfoUtils {

    private static final String LOG_TAG="UserInfoUtils";

    public static final String WECHAT_LOGIN_TYPE = "1";

    public static final String QQ_LOGIN_TYPE = "2";

    public static final String PHONE_LOGIN_TYPE = "3";

    public static final String LOACT_LOGIN_TYPE = "4";

    public static final String SINA_LOGIN_TYPE = "5";

    public interface OnLoginFinish {
        void loginSuccess(String loginType);

        void loginFailed(UserInfoResult error);
    }

    public static UserInfoResult.UserEntity getUserInfo() {
        return ((UserInfoResult) CacheUtils.getObjectCache().get(CacheKey.USER_INFO)).getUser();
    }

    public static UserBalanceResult getUserBalance() {
        return (UserBalanceResult) CacheUtils.getObjectCache().get(CacheKey.USER_BALANCE);
    }


    /**
     * 请求用户信息
     *
     * @param object
     * @param userId
     */
    public static void requestUserInfo(Object object, String userId) {
        ReqUserApi.requestUserInfo(object, userId, "", new RequestCallback<UserInfoResult>() {
            @Override
            public void onRequestSuccess(UserInfoResult object) {
                CacheUtils.getObjectCache().add(CacheKey.USER_INFO, object);
            }

            @Override
            public void onRequestFailure(UserInfoResult object) {

            }
        });
    }


    /**
     * 请求用户账户信息
     *
     * @param activity
     * @param userId
     */
    public static void requestUserBalance(Object activity, String userId) {
        ReqUserApi.requsetUserBalance(activity, userId, new RequestCallback<UserBalanceResult>() {
            @Override
            public void onRequestSuccess(UserBalanceResult object) {
                CacheUtils.getObjectCache().add(CacheKey.USER_BALANCE, object);
            }

            @Override
            public void onRequestFailure(UserBalanceResult object) {

            }
        });
    }

    /**
     * 登录
     *
     * @param activity
     * @param userId
     * @param passwd
     */
    public static void login(Activity activity, final String userId, final String passwd, final String type, final String nickName, final String profile, final OnLoginFinish onLoginFinish) {
        ReqUserApi.requestLogin(activity, userId, passwd, type, nickName, profile, new RequestCallback<UserInfoResult>() {
            @Override
            public void onRequestSuccess(UserInfoResult object) {
                CacheUtils.getObjectCache().add(CacheKey.USER_INFO, object);
                LoginInfoResult mLoginInfoResult = saveUserLoginInfo(userId, passwd, type, nickName, profile);
                CacheUtils.getObjectCache().add(CacheKey.KEY_USER_LOGIN_INFO, mLoginInfoResult);
                onLoginFinish.loginSuccess(type);
                String registrationID = JPushInterface.getRegistrationID(AceApplication.getApplication().getApplicationContext());
                if (object.getUser().getExtendData().getPushDevNo() != null) {
                    if (!object.getUser().getExtendData().getPushDevNo().equals(registrationID)) {
                        sendRegistrationID(object);
                    }
                } else {
                    sendRegistrationID(object);
                }
            }

            @Override
            public void onRequestFailure(UserInfoResult object) {
                onLoginFinish.loginFailed(object);
            }
        });
    }

    /**
     * 发送用户设备的RegistrationID，用于极光推送筛选
     *
     * @param object
     */
    private static void sendRegistrationID(UserInfoResult object) {
        String registrationID = JPushInterface.getRegistrationID(AceApplication.getApplication().getApplicationContext());
        if (!TextUtils.isEmpty(registrationID) && object != null) {
            ReqUserApi.sendRegistrationID(AceApplication.getApplication(), object.getUser().getUserId(), registrationID, new RequestCallback<BaseResult>() {
                @Override
                public void onRequestSuccess(BaseResult object) {

                }

                @Override
                public void onRequestFailure(BaseResult error) {
                }
            });
        }
    }

    /**
     * 注册
     */
    public static void regist(final Activity activity, final String mobile, String passwd, String verifyCode, final OnLoginFinish onLoginFinish) {
        ReqUserApi.requestRegist(activity, mobile, passwd, verifyCode, new RequestCallback<UserInfoResult>() {
            @Override
            public void onRequestSuccess(UserInfoResult object) {
                CacheUtils.getObjectCache().add(CacheKey.USER_INFO, object);
                onLoginFinish.loginSuccess(PHONE_LOGIN_TYPE);
                sendRegistrationID(object);
            }

            @Override
            public void onRequestFailure(UserInfoResult error) {
                onLoginFinish.loginFailed(error);
            }
        });
    }

    /**
     * 是否登录
     *
     * @return
     */
    public static boolean isAlreadyLogin() {
        return CacheUtils.getObjectCache().contain(CacheKey.USER_INFO);
    }


    /**
     * 登出
     */
    public static void logout() {
        CacheUtils.getObjectCache().delete(CacheKey.USER_INFO);
        CacheUtils.getObjectCache().delete(CacheKey.USER_BALANCE);
        CacheUtils.getObjectCache().delete(CacheKey.KEY_USER_LOGIN_INFO);
        MobclickAgent.onProfileSignOff();//友盟统计退出登录
    }

    /**
     * 检查手机号是否合法
     *
     * @param phoneNum
     * @return
     */
    public static boolean checkPhoneValid(String phoneNum) {
        Pattern userPattern = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,1-9])|()17[0-9])\\d{8}$");
        Matcher matcher = userPattern.matcher(phoneNum);
        if (phoneNum.trim().length() == 0) {

            return false;
        }
        if (phoneNum.length() < 11) {

            return false;
        }
        if (matcher.matches() == false) {
            return false;
        }
        return true;
    }

    /**
     * 检查密码是否合法
     *
     * @param password
     * @return
     */
    public static boolean checkPwdValid(String password) {
        if (TextUtils.isEmpty(password)) {
            ShowUtils.showToast(R.string.password_not_null);
            return false;
        }
        if (password.length() > 18 || password.length() < 6) {
            ShowUtils.showToast(R.string.password_format_wrong);
            return false;
        }
        if (containsEmoji(password)) {
            ShowUtils.showToast(R.string.emoji_not_password);
            return false;
        }
        Pattern passwordPattern = Pattern.compile(".*[\u4E00-\u9FFF].*");
        Matcher matcher = passwordPattern.matcher(password);
        if (matcher.find()) {
            ShowUtils.showToast(R.string.password_not_chinese);
            return false;
        }
        return true;
    }

    /**
     * 判断字符串中是否包含emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    /**
     * 检查输入的是ID还是手机号true为ID，false为手机号
     *
     * @param text
     * @return
     */
    public static boolean checkIdOrPhoneNum(String text) {
        if (text.length() < 11) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 自动登录
     *
     * @param activity
     * @param onLoginFinish
     */
    public static void autoLogin(Activity activity, OnLoginFinish onLoginFinish) {
        LoginInfoResult loginInfoResult = (LoginInfoResult) CacheUtils.getObjectCache().get(CacheKey.KEY_USER_LOGIN_INFO);

        if (isAlreadyLogin()) {
            if (loginInfoResult != null) {
                login(activity, loginInfoResult.userId, loginInfoResult.pword, loginInfoResult.loginType, loginInfoResult.nickName, loginInfoResult.profile, onLoginFinish);
            } else {
                ShowUtils.showToast(R.string.login_info_validate_failed);
                logout();
            }
        }
    }

    private static LoginInfoResult saveUserLoginInfo(String userId, String pword, String loginType, String nickName, String profile) {
        LoginInfoResult mLoginInfoResul = new LoginInfoResult();
        mLoginInfoResul.userId = userId == null ? "" : userId;
        mLoginInfoResul.pword = pword == null ? "" : pword;
        mLoginInfoResul.loginType = loginType == null ? "" : loginType;
        mLoginInfoResul.nickName = nickName == null ? "" : nickName;
        mLoginInfoResul.profile = profile == null ? "" : profile;
        return mLoginInfoResul;
    }
}
