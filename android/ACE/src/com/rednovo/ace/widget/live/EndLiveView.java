package com.rednovo.ace.widget.live;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.common.LiveInfoUtils;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqUtils;
import com.rednovo.ace.view.dialog.SimpleLoginDialog;
import com.rednovo.libs.common.KeyBoardUtils;

/**
 * Created by lizhen on 16/3/17.
 */
public class EndLiveView extends RelativeLayout implements View.OnClickListener {
    private boolean isSubscribe;
    private TextView tvSubscribe;
    private TextView tvQuit;
    private RelativeLayout rlLiveEnd;
    private ImageView imgOffline;
    private SimpleLoginDialog simpleLoginDialog;
    private EditText et;


    public EndLiveView(Context context) {
        super(context);
        initView(context);
    }

    public EndLiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EndLiveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context mContext) {
        inflate(mContext, R.layout.layout_live_end, this);
        imgOffline = (ImageView) findViewById(R.id.iv_offline);
        tvSubscribe = (TextView) findViewById(R.id.tv_subscribe);
        tvSubscribe.setOnClickListener(this);
        tvQuit = (TextView) findViewById(R.id.tv_quit);
        rlLiveEnd = (RelativeLayout) findViewById(R.id.rl_live_end);
        tvQuit.setOnClickListener(this);
    }

    public void setSubscribeState(boolean state) {
        this.isSubscribe = state;
        if (state) {
            tvSubscribe.setText(getContext().getString(R.string.subscribe_already));
        }
    }

    public TextView getTvSubscribe() {
        return tvSubscribe;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_subscribe:
                if (UserInfoUtils.isAlreadyLogin()) {
                    if (isSubscribe) {
                        ReqUtils.reqCancelSubscibe(this, UserInfoUtils.getUserInfo().getUserId(), LiveInfoUtils.getStartId());
                        isSubscribe = false;
                        tvSubscribe.setText(getContext().getString(R.string.hall_subscribe_text));
                    } else {
                        ReqUtils.reqSubscibe(this, UserInfoUtils.getUserInfo().getUserId(), LiveInfoUtils.getStartId());
                        isSubscribe = true;
                        tvSubscribe.setText(getContext().getString(R.string.subscribe_already));
                    }

                }else{
                    showLoginDilaog();
                }
                break;
            case R.id.tv_quit:
                ((Activity) getContext()).finish();
                break;
            default:
                break;
        }
    }

    public boolean isVisibility() {
        if (getVisibility() == VISIBLE)
            return true;
        else
            return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        rlLiveEnd.setBackgroundResource(0);
        imgOffline.setImageResource(0);
        tvSubscribe.setBackgroundResource(0);
        tvQuit.setBackgroundResource(0);
        if(simpleLoginDialog != null)
            simpleLoginDialog = null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            KeyBoardUtils.closeKeybord(et, getContext());
        }
        return super.dispatchTouchEvent(ev);
    }

    private void showLoginDilaog() {
        if (simpleLoginDialog == null) {
            simpleLoginDialog = new SimpleLoginDialog(getContext());
        }
        simpleLoginDialog.show();
    }
    public void setEditView(EditText et){
        this.et = et;
    }
}
