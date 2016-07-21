package com.rednovo.ace.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.IncomeBalanceResult;
import com.rednovo.ace.net.parser.UserBalanceResult;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.LoginActivity;
import com.rednovo.ace.ui.activity.RegistActivity;
import com.rednovo.ace.ui.activity.my.AccountCenterActivity;
import com.rednovo.ace.ui.activity.my.BrowsingHistoryActivity;
import com.rednovo.ace.ui.activity.my.MyFansActivity;
import com.rednovo.ace.ui.activity.my.MySubscribeActivity;
import com.rednovo.ace.ui.activity.my.RechargeActivity;
import com.rednovo.ace.ui.activity.my.SettingActivity;
import com.rednovo.ace.ui.activity.my.UserDataActivity;
import com.rednovo.ace.ui.activity.my.WithdrawalsActivity;
import com.rednovo.ace.view.dialog.SimpleLoginDialog;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.CacheKey;
import com.rednovo.libs.common.CacheUtils;
import com.rednovo.libs.common.LevelUtils;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;

/**
 * 我的
 * Created by Dk on 16/2/24.
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {

    private SimpleDraweeView imgPortrait;

    private ImageView imgEditData;

    private Button btnLogin;

    private Button btnRegist;

    private TextView tvName;

    private TextView tvId;

    private TextView tvCoin;

    private TextView tvSubscribe;

    private TextView tvFans;

    private TextView tvInCome;

    private ImageView imgUserLevel;

//    private RelativeLayout rlAccountCenter;

    private RelativeLayout loginView;

    private LinearLayout unLoginView;

    private RelativeLayout rlWithdrawals;

    private RelativeLayout rlRecharge;

    private RelativeLayout rlSetting;

    private boolean isFirst = true;

    public MyFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.layout_my_fragment, null);

        btnLogin = (Button) view.findViewById(R.id.btn_my_fragment_login);
        btnRegist = (Button) view.findViewById(R.id.btn_my_fragment_regist);
        imgPortrait = (SimpleDraweeView) view.findViewById(R.id.img_my_fragment_portrait);
        ((GenericDraweeHierarchy) imgPortrait.getHierarchy()).setPlaceholderImage(getActivity().getResources().getDrawable(R.drawable.head_offine_default));
        imgEditData = (ImageView) view.findViewById(R.id.img_my_fragment_edit_data);
        tvName = (TextView) view.findViewById(R.id.tv_my_fragment_name);
        tvId = (TextView) view.findViewById(R.id.tv_my_fragment_id);
        tvCoin = (TextView) view.findViewById(R.id.tv_coins);
        tvSubscribe = (TextView) view.findViewById(R.id.tv_my_fragment_my_subscribe_num);
        tvFans = (TextView) view.findViewById(R.id.tv_my_fragment_my_fans_num);
        tvInCome = (TextView) view.findViewById(R.id.tv_income);
        imgUserLevel = (ImageView) view.findViewById(R.id.img_my_fragment_user_level);
//        rlAccountCenter = (RelativeLayout) view.findViewById(R.id.rl_my_fragment_account_center);
        loginView = (RelativeLayout) view.findViewById(R.id.rl_my_fragment_login_view);
        unLoginView = (LinearLayout) view.findViewById(R.id.ll_my_fragment_unlogin_view);
        rlWithdrawals = (RelativeLayout) view.findViewById(R.id.rl_my_fragment_withdrawals);
        rlRecharge = (RelativeLayout) view.findViewById(R.id.rl_my_fragment_recharge);
        rlSetting = (RelativeLayout) view.findViewById(R.id.rl_my_fragment_setting);

        btnLogin.setOnClickListener(this);
        btnRegist.setOnClickListener(this);
        imgEditData.setOnClickListener(this);
        imgPortrait.setOnClickListener(this);
        view.findViewById(R.id.ll_my_fragment_my_subscribe).setOnClickListener(this);
        view.findViewById(R.id.ll_my_fragment_my_fans).setOnClickListener(this);
//        view.findViewById(R.id.ll_my_fragment_browsing_history).setOnClickListener(this);
//        rlAccountCenter.setOnClickListener(this);
        rlWithdrawals.setOnClickListener(this);
        rlRecharge.setOnClickListener(this);
        rlSetting.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_my_fragment_login:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.btn_my_fragment_regist:
                startActivity(new Intent(getActivity(), RegistActivity.class));
                break;
            case R.id.img_my_fragment_portrait:
                if(UserInfoUtils.isAlreadyLogin()){
                    startActivity(new Intent(getActivity(), UserDataActivity.class));
                }else{
                    new SimpleLoginDialog(getActivity()).show();
                }
                break;
            case R.id.img_my_fragment_edit_data:
                if(UserInfoUtils.isAlreadyLogin()){
                    startActivity(new Intent(getActivity(), UserDataActivity.class));
                }else{
                    new SimpleLoginDialog(getActivity()).show();
                }
                break;
            case R.id.ll_my_fragment_my_subscribe:
                if (UserInfoUtils.isAlreadyLogin()) {
                    startActivity(new Intent(getActivity(), MySubscribeActivity.class));
                } else {
                    new SimpleLoginDialog(getActivity()).show();
                }
                break;
            case R.id.ll_my_fragment_my_fans:
                if (UserInfoUtils.isAlreadyLogin()) {
                    startActivity(new Intent(getActivity(), MyFansActivity.class));
                } else {
                    new SimpleLoginDialog(getActivity()).show();
                }
                break;
//            case R.id.ll_my_fragment_browsing_history:
//                if (UserInfoUtils.isAlreadyLogin()) {
//                    startActivity(new Intent(getActivity(), BrowsingHistoryActivity.class));
//                } else {
//                    new SimpleLoginDialog(getActivity()).show();
//                }
//                break;
//            case R.id.rl_my_fragment_account_center:
//                if (UserInfoUtils.isAlreadyLogin()) {
//                    startActivity(new Intent(getActivity(), AccountCenterActivity.class));
//                } else {
//                    new SimpleLoginDialog(getActivity()).show();
//                }
//                break;
            case R.id.rl_my_fragment_withdrawals:
                if (UserInfoUtils.isAlreadyLogin()) {
                    startActivity(new Intent(getActivity(), WithdrawalsActivity.class));
                } else {
                    new SimpleLoginDialog(getActivity()).show();
                }
                break;
            case R.id.rl_my_fragment_recharge:
                if (UserInfoUtils.isAlreadyLogin()) {
                    startActivity(new Intent(getActivity(), RechargeActivity.class));
                } else {
                    new SimpleLoginDialog(getActivity()).show();
                }
                break;
            case R.id.rl_my_fragment_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            default:

                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            updateInfo();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirst) {
            updateInfo();
        }
        isFirst = false;
    }

    public void requestUserInfo(Activity activity, String userId) {
        ReqUserApi.requestUserInfo(activity, userId, "", new RequestCallback<UserInfoResult>() {
            @Override
            public void onRequestSuccess(UserInfoResult object) {
                CacheUtils.getObjectCache().add(CacheKey.USER_INFO, object);
                Context cxt = BaseApplication.getApplication().getApplicationContext();
//                ((GenericDraweeHierarchy) imgPortrait.getHierarchy()).setFailureImage(cxt.getResources().getDrawable(R.drawable.head_offline));

                if(StringUtils.isEmpty(object.getUser().getProfile())) {    // 用户没有设置头像，设置登录后默认头像
                    FrescoEngine.setSimpleDraweeView(imgPortrait, "", ImageRequest.ImageType.SMALL);
                    ((GenericDraweeHierarchy) imgPortrait.getHierarchy()).setPlaceholderImage(cxt.getResources().getDrawable(R.drawable.head_online_default));
                }else {     // 用户已设置头像
                    FrescoEngine.setSimpleDraweeView(imgPortrait, object.getUser().getProfile(), ImageRequest.ImageType.SMALL);
                }
                tvName.setText(object.getUser().getNickName());
                tvId.setText(getString(R.string.id_text, object.getUser().getUserId()));
                tvSubscribe.setText(object.getUser().getExtendData().getSubscribeCnt());
                tvFans.setText(object.getUser().getExtendData().getFansCnt());
                imgUserLevel.setImageResource(LevelUtils.getLevelIcon(object.getUser().getRank()));
            }

            @Override
            public void onRequestFailure(UserInfoResult object) {
                updateInfo();
            }
        });
    }

    private void updateInfo() {
        if (UserInfoUtils.isAlreadyLogin()) {
            loginView.setVisibility(View.VISIBLE);
            unLoginView.setVisibility(View.GONE);
            requestUserInfo(getActivity(), UserInfoUtils.getUserInfo().getUserId());
            requestUserBalance(getActivity(), UserInfoUtils.getUserInfo().getUserId());
            getIncomeBalance(getActivity(), UserInfoUtils.getUserInfo().getUserId());

        } else {
            loginView.setVisibility(View.GONE);
            unLoginView.setVisibility(View.VISIBLE);

            // 用户未登录，先清空残留的头像设置，在设置未登录默认头像
            FrescoEngine.setSimpleDraweeView(imgPortrait, "", ImageRequest.ImageType.SMALL);
            ((GenericDraweeHierarchy) imgPortrait.getHierarchy()).setPlaceholderImage(getActivity().getResources().getDrawable(R.drawable.head_offine_default));
            tvCoin.setText("0");
            tvSubscribe.setText("0");
            tvFans.setText("0");
            tvInCome.setText("0");
        }
    }

    private void requestUserBalance(Activity activity, String userId) {
        ReqUserApi.requsetUserBalance(activity, userId, new RequestCallback<UserBalanceResult>() {
            @Override
            public void onRequestSuccess(UserBalanceResult object) {

                CacheUtils.getObjectCache().add(CacheKey.USER_BALANCE, object);
                tvCoin.setText(object.getBlance());
            }

            @Override
            public void onRequestFailure(UserBalanceResult object) {

            }
        });
    }

    private void getIncomeBalance(Activity activity, String userId) {
        ReqUserApi.requsestIncomeBalance(activity, userId, new RequestCallback<IncomeBalanceResult>() {
            @Override
            public void onRequestSuccess(IncomeBalanceResult object) {
                tvInCome.setText(object.getBalance());
            }

            @Override
            public void onRequestFailure(IncomeBalanceResult error) {

            }
        });
    }
}
