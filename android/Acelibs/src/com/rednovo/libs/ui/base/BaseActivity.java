package com.rednovo.libs.ui.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.rednovo.libs.R;
import com.rednovo.libs.common.ScreenUtils;
import com.rednovo.libs.common.StatusBarUtils;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;

@SuppressLint("NewApi")
public class BaseActivity extends FragmentActivity{
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        //取消手机底部navigation_bar
        AppManager.getAppManager().addActivity(this);
        if (ScreenUtils.isFlyme()) {//判断是否是魅族手机
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        // MobclickAgent.setDebugMode(true);
        setStatusBar();
    }

    protected void setStatusBar() {
        StatusBarUtils.setImmersionBarColor(this, getResources().getColor(R.color.default_color_ffd200));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getTitle().toString());
        MobclickAgent.onResume(this);//友盟统计
        JPushInterface.onResume(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getTitle().toString());
        MobclickAgent.onPause(this);//友盟统计
        JPushInterface.onPause(this);
    }

    private Intent redirect;

    /**
     * Activity跳转
     */
    protected void redirect(Class<?> cls) {
        redirect = new Intent(this, cls);
        startActivity(redirect);
    }

    /**
     * Activity跳转
     */
    protected void redirect(Intent intent) {
        startActivity(intent);
    }

    /**
     * Activity跳转并关闭当前页
     */
    protected void redirectClose(Class<?> cls) {
        redirect = new Intent(this, cls);
        startActivity(redirect);
        finish();
    }

    /**
     * Activity跳转并关闭当前页
     */
    protected void redirectClose(Intent intent) {
        startActivity(intent);
        finish();
    }

    /**
     * 跳转页面并接收页面返回时传回的数据
     */
    protected void redirectForResult(Class<?> cls, int requestCode) {
        redirect = new Intent(this, cls);
        startActivityForResult(redirect, requestCode);
    }

    /**
     * 跳转页面并接收页面返回时传回的数据
     */
    protected void redirectForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void finish() {
        super.finish();
        AppManager.getAppManager().removeActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
