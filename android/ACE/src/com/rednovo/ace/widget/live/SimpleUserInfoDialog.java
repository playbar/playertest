package com.rednovo.ace.widget.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.common.LiveInfoUtils;
import com.rednovo.ace.core.session.SendUtils;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqRelationApi;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.UserZoneActivity;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;
import com.rednovo.libs.widget.dialog.BaseDialog;

/**
 * 个人简单信息弹框
 */
public class SimpleUserInfoDialog extends BaseDialog implements View.OnClickListener {
//    private static SimpleUserInfoDialog mInstance;
    private SimpleDraweeView ivAnchor;
    private TextView tvReport, tvHome, tvSubscribe, tvName, tvSignature, tvFans, tvPosition, tvID;
    private Context mContext;
    private UserInfoResult.UserEntity currentUser;
    private boolean isSubscribe;
    private int fansCnt;
    private OnNoLoginListener listener;

    public void setIsAnchor(boolean isAnchor) {
        this.isAnchor = isAnchor;
        if (isAnchor) {
            tvReport.setText(getContext().getString(R.string.tv_silent));
            tvHome.setVisibility(View.GONE);
        } else {
            tvHome.setVisibility(View.VISIBLE);
        }
    }

    private boolean isAnchor;

    /*public static SimpleUserInfoDialog getSimpleUserInfoDialog(Context context, int resId) {
        if (mInstance == null) {
            mInstance = new SimpleUserInfoDialog(context, resId);
        }
        return mInstance;
    }*/

    public SimpleUserInfoDialog(Context context, int resId) {
        super(context, R.layout.dialog_simple_userinfo, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        this.getWindow().setWindowAnimations(R.style.dialogCenterWindowAnim);
        ivAnchor = (SimpleDraweeView) findViewById(R.id.icon_anchor);
        tvReport = (TextView) findViewById(R.id.tv_report);
        tvHome = (TextView) findViewById(R.id.tv_home);
        tvSubscribe = (TextView) findViewById(R.id.tv_subscribe);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvFans = (TextView) findViewById(R.id.tv_fans);
        tvID = (TextView) findViewById(R.id.tv_id);
        tvPosition = (TextView) findViewById(R.id.tv_position);
        tvSignature = (TextView) findViewById(R.id.tv_signature);
        tvReport.setOnClickListener(this);
        tvHome.setOnClickListener(this);
        tvSubscribe.setOnClickListener(this);

//        mInstance = this;

        mContext = context;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_report:
                if(!UserInfoUtils.isAlreadyLogin()){
                    if(listener != null)
                        listener.loginNotice();
                }else if(currentUser != null){
                    if (isAnchor) {
//                        SendUtils.kickOut(UserInfoUtils.getUserInfo().getUserId(), currentUser.getUserId(), UserInfoUtils.getUserInfo().getUserId());
//                        SendUtils.shutup(UserInfoUtils.getUserInfo().getUserId(), currentUser.getUserId(), UserInfoUtils.getUserInfo().getUserId());
                        shutup(currentUser.getUserId());
                    } else {
                        report(currentUser.getUserId());
                    }
                }
                break;
            case R.id.tv_home:
                if(!UserInfoUtils.isAlreadyLogin()){
                    if(listener != null)
                        listener.loginNotice();
                }else if (currentUser != null) {
                    Intent intent = new Intent(mContext, UserZoneActivity.class);
                    intent.putExtra(UserZoneActivity.USER, currentUser);
                    intent.putExtra(Globle.KEY_SHOW_LIVEING_BTN, false);
                    mContext.startActivity(intent);
                    this.dismiss();
                }
                break;
            case R.id.tv_subscribe:
                if(!UserInfoUtils.isAlreadyLogin()){
                    if(listener != null)
                        listener.loginNotice();
                }else if (currentUser != null) {
                    if (isSubscribe) {
                        //已订阅
                        cancelSub(currentUser.getUserId());
                        tvSubscribe.setText(mContext.getString(R.string.hall_subscribe_text));
                        tvSubscribe.setTextColor(mContext.getResources().getColor(R.color.color_19191a));
                        tvSubscribe.setBackgroundResource(R.drawable.tv_subscribe_bg);
                        isSubscribe = false;
                        fansCnt--;
                        if (fansCnt < 0)
                            fansCnt = 0;
                        tvFans.setText(getContext().getString(R.string.tv_fans, fansCnt + ""));
                    } else {
                        subscribe(currentUser.getUserId());
                        tvSubscribe.setText(mContext.getString(R.string.subscribe_already));
                        tvSubscribe.setTextColor(mContext.getResources().getColor(R.color.color_868686));
                        tvSubscribe.setBackgroundResource(R.drawable.tv_subscribed_bg);
                        isSubscribe = true;
                        fansCnt++;
                        tvFans.setText(getContext().getString(R.string.tv_fans, fansCnt + ""));
                        if(listener != null && LiveInfoUtils.getStartId() != null && LiveInfoUtils.getStartId().equals(currentUser.getUserId()))
                            listener.followNotice();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 订阅
     */
    private void subscribe(String userId) {
        ReqRelationApi.reqSubscibe((Activity) mContext, UserInfoUtils.getUserInfo().getUserId(),userId, new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
            }

            @Override
            public void onRequestFailure(BaseResult error) {
            }
        });
    }

    /**
     * 取消订阅
     */
    private void cancelSub(String userId) {
        ReqRelationApi.reqCancelSubscibe((Activity) mContext, UserInfoUtils.getUserInfo().getUserId(),userId, new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
            }

            @Override
            public void onRequestFailure(BaseResult error) {
            }
        });
    }

    /**
     * 禁言
     */
    private void shutup(String userId) {
        ReqRelationApi.reqShutup((Activity) mContext, UserInfoUtils.getUserInfo().getUserId(),userId, new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                ShowUtils.showToast(mContext.getString(R.string.tv_shutup_success));
            }

            @Override
            public void onRequestFailure(BaseResult error) {

            }
        });
        dismiss();
    }

    /**
     * 举报
     */
    private void report(String userId) {
        ReqRelationApi.reqReport((Activity) mContext, UserInfoUtils.getUserInfo().getUserId(),userId, new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                ShowUtils.showToast(mContext.getString(R.string.tv_reprot_success));
            }

            @Override
            public void onRequestFailure(BaseResult error) {

            }
        });
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        mInstance = null;
    }

    public void setUserId(String id) {
        String userId = "";
        if (UserInfoUtils.isAlreadyLogin()) {
            userId = UserInfoUtils.getUserInfo().getUserId();
        }
        tvID.setText(getContext().getString(R.string.ID, id));
        ReqUserApi.requestUserInfo((Activity) mContext, id, userId, new RequestCallback<UserInfoResult>() {
            @Override
            public void onRequestSuccess(UserInfoResult object) {
                if (object != null) {
                    setData(object.getUser());
                    currentUser = object.getUser();
                }
            }

            @Override
            public void onRequestFailure(UserInfoResult error) {

            }
        });
    }

    private void setData(UserInfoResult.UserEntity user) {
        if (user != null) {
            Context cxt = BaseApplication.getApplication().getApplicationContext();
            ((GenericDraweeHierarchy) ivAnchor.getHierarchy()).setPlaceholderImage(R.drawable.head_online);
            ((GenericDraweeHierarchy) ivAnchor.getHierarchy()).setFailureImage(cxt.getResources().getDrawable(R.drawable.head_offline));
            FrescoEngine.setSimpleDraweeView(ivAnchor, user.getProfile(), ImageRequest.ImageType.SMALL);
            tvName.setText(user.getNickName() == null ? "" : user.getNickName());
            String signature = "";
            if (user.getSignature() != null)
                signature = user.getSignature();
            if (signature.length() > 10) {
                String substring = signature.substring(0, 10);
                String substring1 = signature.substring(10, signature.length());
                signature = substring + "\n" + substring1;
            }

            if(StringUtils.isEmpty(signature)) {
                signature = mContext.getString(R.string.signature_default_text);
            }
            tvSignature.setText(signature);
            String fans = user.getExtendData().getFansCnt();
            if (TextUtils.isEmpty(fans)) {
                fansCnt = 0;
            } else {
                fansCnt = Integer.parseInt(fans);
            }
            tvFans.setText(getContext().getString(R.string.tv_fans, fansCnt + ""));
            String position = "";
            if(StringUtils.isEmpty(user.getExtendData().getPostion())) {
                position = mContext.getString(R.string.position_default_text);
            } else {
                position = user.getExtendData().getPostion();
            }
            tvPosition.setText(position);
            if (UserInfoUtils.isAlreadyLogin() && UserInfoUtils.getUserInfo().getUserId().equals(user.getUserId())) {
                //自己
                tvReport.setVisibility(View.GONE);
                tvSubscribe.setText(mContext.getString(R.string.hall_subscribe_text));
                tvSubscribe.setTextColor(mContext.getResources().getColor(R.color.color_19191a));
                tvSubscribe.setBackgroundResource(R.drawable.tv_subscribe_bg);
                tvSubscribe.setEnabled(false);
            } else {
                tvReport.setVisibility(View.VISIBLE);
                tvSubscribe.setEnabled(true);
                if (user.getExtendData().getRelatoin() != null) {
                    tvSubscribe.setTextColor(mContext.getResources().getColor(user.getExtendData().getRelatoin().equals("1") ? R.color.color_868686 : R.color.color_19191a));
                    tvSubscribe.setBackgroundResource(user.getExtendData().getRelatoin().equals("1") ? R.drawable.tv_subscribed_bg : R.drawable.tv_subscribe_bg);
                    tvSubscribe.setText(user.getExtendData().getRelatoin().equals("1") ? mContext.getString(R.string.subscribe_already) : mContext.getString(R.string.hall_subscribe_text));
                    isSubscribe = user.getExtendData().getRelatoin().equals("1");
                } else {
                    tvSubscribe.setText(mContext.getString(R.string.hall_subscribe_text));
                    tvSubscribe.setTextColor(mContext.getResources().getColor(R.color.color_19191a));
                    tvSubscribe.setBackgroundResource(R.drawable.tv_subscribe_bg);
                }
            }
        }
    }
    public interface OnNoLoginListener {
        /**
         * 登陆提醒
         */
        void loginNotice();

        /**
         * 关注监听
         */
        void followNotice();
    }
    public void setOnNoLoginListener(OnNoLoginListener listener) {
        this.listener = listener;
    }


}
