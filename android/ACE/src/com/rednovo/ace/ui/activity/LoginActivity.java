package com.rednovo.ace.ui.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.common.WebViewConfig;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.events.BaseEvent;
import com.rednovo.ace.data.login.LoginForQQ;
import com.rednovo.ace.data.login.LoginForSina;
import com.rednovo.ace.data.login.LoginForWechat;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.parser.WechatInfoResult;
import com.rednovo.ace.ui.activity.my.GetBackPasswordActivity;
import com.rednovo.ace.view.dialog.SingleOptionDialog;
import com.rednovo.libs.common.ErrorMsg;
import com.rednovo.libs.common.NetWorkUtil;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StatusBarUtils;
import com.rednovo.libs.ui.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * 登录
 * Created by Dk on 16/2/28.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, UserInfoUtils.OnLoginFinish {

    private EditText etUserName;
    private EditText etUserPwd;
    private LoginForSina loginForSina;
    private ImageView imgCleanInput;
    private ImageView imgCleanInputPassword;
    private ImageView imgLogo;
    private ScrollView scrollView;
    private ImageView imageClose;
    private Button btnLogin;
    private Button btnRegister;
    private TextView tvCasualWatch;
    private SingleOptionDialog mSingleOptionDialog = null;
    private boolean isLaunchGo = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_login);
        isLaunchGo = getIntent().getBooleanExtra("isLaunchGo", false);
        etUserName = (EditText) findViewById(R.id.et_login_name);
        etUserPwd = (EditText) findViewById(R.id.et_login_passwoed);
        imgCleanInput = (ImageView) findViewById(R.id.img_clean_input);
        imgCleanInputPassword = (ImageView) findViewById(R.id.img_clean_input_password);
        imgLogo = (ImageView) findViewById(R.id.img_logo);
        scrollView = (ScrollView) findViewById(R.id.parent_id);

        findViewById(R.id.img_login_for_qq).setOnClickListener(this);
        findViewById(R.id.img_login_for_wechat).setOnClickListener(this);
        findViewById(R.id.img_login_for_sina).setOnClickListener(this);

        imageClose = (ImageView) findViewById(R.id.img_login_close);
        btnLogin = (Button) findViewById(R.id.btn_login_login);
        btnRegister = (Button) findViewById(R.id.btn_login_regist);
        imageClose.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        findViewById(R.id.tv_login_get_back_password).setOnClickListener(this);
        findViewById(R.id.tv_user_login_protocol).setOnClickListener(this);
        imgCleanInput.setOnClickListener(this);
        imgCleanInputPassword.setOnClickListener(this);
        etUserName.addTextChangedListener(userNameTextWatcher);
        etUserPwd.addTextChangedListener(userPasswordTextWatcher);

        if (isLaunchGo) {//随便看看按钮初始化
            tvCasualWatch = (TextView) findViewById(R.id.tv_casual_watch);
            tvCasualWatch.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            tvCasualWatch.getPaint().setAntiAlias(true);
            tvCasualWatch.setVisibility(View.VISIBLE);
            tvCasualWatch.setOnClickListener(this);
            imageClose.setVisibility(View.GONE);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        imgLogo.setImageResource(0);
        imgLogo.setBackgroundResource(0);
        scrollView.setBackgroundResource(0);
        imageClose.setImageResource(0);
        btnLogin.setBackgroundResource(0);
        btnRegister.setBackgroundResource(0);
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.img_login_close:
                if (isLaunchGo) {
                    redirectClose(MainActivity.class);
                } else {
                    finish();
                }
                break;
            case R.id.img_login_for_qq:
                LoginForQQ loginForQQ = new LoginForQQ(LoginActivity.this, LoginActivity.this);
                loginForQQ.login();
                break;
            case R.id.img_login_for_wechat:
                LoginForWechat loginForWechat = new LoginForWechat();
                loginForWechat.login();
                break;
            case R.id.img_login_for_sina:
                loginForSina = new LoginForSina(LoginActivity.this, LoginActivity.this);
                loginForSina.login();
                break;
            case R.id.btn_login_login:
                login();
                break;
            case R.id.btn_login_regist:
                intent = new Intent(LoginActivity.this, RegistActivity.class);
                intent.putExtra("isLaunchGo", true);
                redirectClose(intent);

                break;
            case R.id.tv_login_get_back_password:
                startActivity(new Intent(LoginActivity.this, GetBackPasswordActivity.class));
                break;
            case R.id.img_clean_input:
                etUserName.setText("");
                break;
            case R.id.img_clean_input_password:
                etUserPwd.setText("");
                break;
            case R.id.tv_user_login_protocol:
                intent = new Intent(LoginActivity.this, ACEWebActivity.class);
                intent.putExtra("url", WebViewConfig.getUrl() + WebViewConfig.USER_PROTOCOL);
                startActivity(intent);
                break;
            case R.id.tv_casual_watch:
                redirectClose(MainActivity.class);
                break;
            default:

                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            if(inputMethodManager.isActive()){
//                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//            }
            login();
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (isLaunchGo) {
                redirectClose(MainActivity.class);
            } else {
                finish();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void setStatusBar() {
//        super.setStatusBar();
        StatusBarUtils.setTranslucentImmersionBar(this);
    }

    private void login() {
        if (etUserName.getText() == null || etUserName.getText().toString().equals("")) {
            ShowUtils.showToast(R.string.please_input_phone_num_or_id);
            return;
        }
        if (!UserInfoUtils.checkPwdValid(etUserPwd.getText().toString())) {
            return;
        }

        String name = etUserName.getText().toString();
        String pwd = etUserPwd.getText().toString();
        ShowUtils.showProgressDialog(this, R.string.logining);
        String type = null;
        if (UserInfoUtils.checkIdOrPhoneNum(name)) {
            type = UserInfoUtils.LOACT_LOGIN_TYPE;
        } else {
            type = UserInfoUtils.PHONE_LOGIN_TYPE;
        }
        if (NetWorkUtil.checkNetwork()) {
            UserInfoUtils.login(this, name, pwd, type, null, null, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (loginForSina != null) {
            loginForSina.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void loginSuccess(String loginType) {
        if (UserInfoUtils.isAlreadyLogin()) {
            EventBus.getDefault().post(new BaseEvent(Globle.KEY_EVENT_LOGIN_SUCCESS));
            UserInfoUtils.requestUserBalance(this, UserInfoUtils.getUserInfo().getUserId());
        }
        MobclickAgent.onProfileSignIn(loginType, UserInfoUtils.getUserInfo().getUserId());//友盟统计
        ShowUtils.dismissProgressDialog();
        if (isLaunchGo) {
            redirectClose(MainActivity.class);
        } else {
            finish();
        }
    }

    @Override
    public void loginFailed(UserInfoResult error) {
        if (error != null) {
            if (error.getErrCode() == 208) {
                if (mSingleOptionDialog == null) {
                    mSingleOptionDialog = new SingleOptionDialog(this, getString(R.string.account_freeze), getString(R.string.freeze_acount_content), getString(R.string.i_know));
                }
                mSingleOptionDialog.show();
            } else {
                String errorContent = ErrorMsg.getErrMsg(error.getErrCode());
                if (!TextUtils.isEmpty(errorContent)) {
                    ShowUtils.showToast(errorContent);
                } else {
                    ShowUtils.showToast(R.string.login_failed);
                }
            }
        }
        ShowUtils.dismissProgressDialog();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onMainEvent(BaseEvent event) {
        switch (event.id) {
            case Globle.KEY_EVENT_WECHAT_LOGIN_SUCCESS://微信登录成功
                String name = ((WechatInfoResult) event.object).getNickname();
                if (name.length() > 8) {
                    name = name.substring(0, 7);
                }
                UserInfoUtils.login(this, ((WechatInfoResult) event.object).getUnionid(), null, UserInfoUtils.WECHAT_LOGIN_TYPE, name, ((WechatInfoResult) event.object).getHeadimgurl(), this);
                break;
            case Globle.KEY_EVENT_WECHAT_LOGIN_FAILED://微信登录失败
                loginFailed(null);
                break;
            default:
                break;
        }
    }

    TextWatcher userNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s.toString())) {
                imgCleanInput.setVisibility(View.INVISIBLE);
            } else {
                imgCleanInput.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher userPasswordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s.toString())) {
                imgCleanInputPassword.setVisibility(View.INVISIBLE);
            } else {
                imgCleanInputPassword.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
