package com.rednovo.ace.ui.activity.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.parser.UpdatePortraitResult;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.view.dialog.live.CreateLiveDialog;
import com.rednovo.ace.view.dialog.ReNameDialog;
import com.rednovo.ace.view.dialog.SimpleDialog;
import com.rednovo.libs.common.CacheKey;
import com.rednovo.libs.common.CacheUtils;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;
import com.rednovo.libs.ui.base.BaseActivity;

/**
 * 资料
 * Created by Dk on 16/2/25.
 */
public class UserDataActivity extends BaseActivity implements View.OnClickListener, SimpleDialog.OnSimpleDialogBtnClickListener {

    public static final int MSG_UPDATE_NICKNAME = 0;

    public static final int MSG_UPDATE_SEX = 1;

    private SimpleDraweeView imgPortrait;

    private GenericDraweeHierarchy hierarchy;


    private TextView tvNickName;

    private TextView tvID;

    private TextView tvAutograph;

    private TextView tvSex;
    private String uploadImageUrl;

    //   private TextView tvAge;
//
//    private TextView tvConstellation;
//
//    private TextView tvLocation;
//
//    private TextView tvEmotion;
//
//    private TextView tvLable1;
//
//    private TextView tvLable2;
//
//    private TextView tvLable3;
//
//    private TextView tvLable4;
//
//    private TextView tvLable5;
//
//    private TextView tvLable6;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_user_data);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.edit_my_data);
        findViewById(R.id.back_btn).setOnClickListener(this);

        imgPortrait = (SimpleDraweeView) findViewById(R.id.img_user_data_portrait);
        tvNickName = (TextView) findViewById(R.id.tv_user_data_nick_name);
        tvID = (TextView) findViewById(R.id.tv_user_data_id);
        tvAutograph = (TextView) findViewById(R.id.tv_user_data_autograph);
        tvSex = (TextView) findViewById(R.id.tv_user_data_sex);

        hierarchy = imgPortrait.getHierarchy();
//        tvAge = (TextView) findViewById(R.id.tv_user_data_nick_name);
//        tvConstellation = (TextView) findViewById(R.id.tv_user_data_nick_name);
//        tvLocation = (TextView) findViewById(R.id.tv_user_data_nick_name);
//        tvEmotion = (TextView) findViewById(R.id.tv_user_data_nick_name);
//        tvLable1 = (TextView) findViewById(R.id.tv_user_data_nick_name);
//        tvLable2 = (TextView) findViewById(R.id.tv_user_data_nick_name);
//        tvLable3 = (TextView) findViewById(R.id.tv_user_data_nick_name);
//        tvLable4 = (TextView) findViewById(R.id.tv_user_data_nick_name);
//        tvLable5 = (TextView) findViewById(R.id.tv_user_data_nick_name);
//        tvLable6 = (TextView) findViewById(R.id.tv_user_data_nick_name);

        findViewById(R.id.rl_user_data_portrait).setOnClickListener(this);
        findViewById(R.id.rl_user_data_nickname).setOnClickListener(this);
        findViewById(R.id.rl_user_data_autograph).setOnClickListener(this);
        findViewById(R.id.rl_user_data_sex).setOnClickListener(this);
//        findViewById(R.id.rl_user_data_age).setOnClickListener(this);
//        findViewById(R.id.rl_user_data_location).setOnClickListener(this);
//        findViewById(R.id.rl_user_data_emotion).setOnClickListener(this);
//        findViewById(R.id.rl_user_data_lable).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ((GenericDraweeHierarchy) imgPortrait.getHierarchy()).setFailureImage(getResources().getDrawable(R.drawable.head_offline));
        if(UserInfoUtils.getUserInfo() != null){
            UserInfoResult.UserEntity userInfo = UserInfoUtils.getUserInfo();
            FrescoEngine.setSimpleDraweeView(imgPortrait, userInfo.getProfile(), ImageRequest.ImageType.SMALL);
            tvNickName.setText(userInfo.getNickName());
            tvID.setText(userInfo.getUserId());
            tvAutograph.setText(userInfo.getSignature());
            String sex = userInfo.getSex();
            if("0".equals(sex)){
                tvSex.setText(R.string.woman);
            }else if("1".equals(sex)){
                tvSex.setText(R.string.man);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 0x100 && data != null) {
            uploadImageUrl = data.getStringExtra("image");
            updatePortrait("image", uploadImageUrl);

        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_NICKNAME:
                    tvNickName.setText(msg.obj.toString());
                    break;
                case MSG_UPDATE_SEX:
                    tvSex.setText(msg.obj.toString().equals("0") ? R.string.woman : R.string.man);
                    break;
                default:

                    break;
            }
        }

        ;
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.rl_user_data_portrait:
                CreateLiveDialog.getDialog(this, 0.3f).show();
                break;
            case R.id.rl_user_data_nickname:
                ReNameDialog reNameDialog = new ReNameDialog(this, handler);
                reNameDialog.show();
                break;
            case R.id.rl_user_data_autograph:
                startActivity(new Intent(UserDataActivity.this, AutographActivity.class));
                break;
            case R.id.rl_user_data_sex:
                if (UserInfoUtils.getUserInfo().getSex().equals("")) {
                    SimpleDialog updateAutographDialog = new SimpleDialog(this, this, R.string.please_choose, R.string.woman, R.string.man);
                    updateAutographDialog.show();
                }
                break;
//            case R.id.rl_user_data_age:
//
//                break;
//            case R.id.rl_user_data_location:
//
//                break;
//            case R.id.rl_user_data_emotion:
//
//                break;
//            case R.id.rl_user_data_lable:
//
//                break;
            default:

                break;
        }
    }

    private void updatePortrait(String fileName, final String filePath) {
        ReqUserApi.requestUpdateProfile(this, UserInfoUtils.getUserInfo().getUserId(), fileName, filePath, new RequestCallback<UpdatePortraitResult>() {
            @Override
            public void onRequestSuccess(UpdatePortraitResult object) {
                UserInfoUtils.getUserInfo().setProfile(object.getVisitUrl());
                FrescoEngine.setSimpleDraweeView(imgPortrait, Globle.PREFIX_FILE + filePath, object.getVisitUrl(), ImageRequest.ImageType.SMALL);
                ShowUtils.showToast(R.string.update_portrait_success);
            }

            @Override
            public void onRequestFailure(UpdatePortraitResult error) {
                error.getErrMsg();
                ShowUtils.showToast(R.string.update_portrait_failed);
            }
        });
    }

    @Override
    public void onSimpleDialogLeftBtnClick() {
        updateSex(UserInfoUtils.getUserInfo().getUserId(), "0");
    }

    @Override
    public void onSimpleDialogRightBtnClick() {
        updateSex(UserInfoUtils.getUserInfo().getUserId(), "1");
    }

    private void updateSex(String userId, final String sex) {
        ReqUserApi.requestUpdateSex(this, userId, sex, new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
                ShowUtils.showToast(R.string.update_sex_success);
                ((UserInfoResult) CacheUtils.getObjectCache().get(CacheKey.USER_INFO)).getUser().setSex(sex);
                Message msg = new Message();
                msg.what = UserDataActivity.MSG_UPDATE_SEX;
                msg.obj = sex;
                handler.sendMessage(msg);
            }

            @Override
            public void onRequestFailure(BaseResult error) {
                ShowUtils.showToast(R.string.update_sex_failed);
            }
        });
    }
}
