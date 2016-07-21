package com.rednovo.ace.widget.live;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.R;
import com.rednovo.ace.core.session.SessionEngine;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.events.LiveEndEvent;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.DateUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by lizhen on 16/3/15.
 */
public class CloseLiveView extends RelativeLayout implements View.OnClickListener {

    private TextView tvNickName;
    private TextView tvMoney;
    private TextView tvFans;
    private TextView tvAudience;
    private TextView tvLiveTime;
    private TextView tvPraise;
    private SimpleDraweeView simpleDraweeView;
    private int drawable;

    public CloseLiveView(Context context) {
        super(context);
        initView(context);
    }

    public CloseLiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CloseLiveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        UserInfoResult.UserEntity user = UserInfoUtils.getUserInfo();
        drawable = R.drawable.head_offline;
        Context cxt = BaseApplication.getApplication().getApplicationContext();
        ((GenericDraweeHierarchy) simpleDraweeView.getHierarchy()).setFailureImage(cxt.getResources().getDrawable(drawable));

        FrescoEngine.setSimpleDraweeView(simpleDraweeView, user.getProfile(), ImageRequest.ImageType.SMALL);

    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onRecvLiveEndEvent(LiveEndEvent liveEndEvent) {
        if(UserInfoUtils.isAlreadyLogin()){
            //接到账号封停后，用户已经被退出登录
            String nickName = UserInfoUtils.getUserInfo().getNickName();
            tvNickName.setText(nickName);
            int coinsLenght = liveEndEvent.conins.length();
            if (coinsLenght > 5) {
                tvMoney.setTextSize(28);

            } else if (coinsLenght > 8) {
                tvMoney.setTextSize(24);
            } else {
                tvMoney.setTextSize(36);
            }
            tvMoney.setText(liveEndEvent.conins);
            tvFans.setText(liveEndEvent.fans);
            String coverTime = null;
            try {
                coverTime = DateUtils.converTime(Integer.parseInt(liveEndEvent.length));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            tvLiveTime.setText(coverTime);
            tvAudience.setText(liveEndEvent.memberCnt);
            tvPraise.setText(liveEndEvent.support);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
        drawable = 0;
    }

    protected void initView(Context mContext) {
        inflate(mContext, R.layout.activity_live_close, this);
        tvNickName = (TextView) findViewById(R.id.tv_nickName);
        tvMoney = (TextView) findViewById(R.id.tv_money);
        tvFans = (TextView) findViewById(R.id.tv_fans);
        tvAudience = (TextView) findViewById(R.id.tv_audience);
        tvLiveTime = (TextView) findViewById(R.id.tv_live_time);
        tvPraise = (TextView) findViewById(R.id.tv_praise);
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.iv_anchor);
        findViewById(R.id.tv_sure).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SessionEngine.getSessionEngine().close();
                    }
                }).start();
                ((Activity) getContext()).finish();
                break;
            default:
                break;
        }

    }

    public boolean isVisible() {
        if (getVisibility() == View.VISIBLE)
            return true;
        else
            return false;
    }

    public void setVisible(boolean isEndView) {
        if (isEndView) {
            this.setVisibility(View.VISIBLE);
        }else{
            this.setVisibility(View.GONE);
        }
    }


}
