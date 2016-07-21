package com.rednovo.ace.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.data.events.BaseEvent;
import com.rednovo.ace.data.share.ShareFactory;
import com.rednovo.ace.net.api.OtherReqApi;
import com.rednovo.ace.net.parser.WechatInfoResult;
import com.rednovo.ace.net.parser.WechatTokenResult;
import com.rednovo.ace.net.request.OtherRequestCallback;
import com.rednovo.libs.common.ThirdPartyAPI;
import com.rednovo.libs.common.Constant;
import com.rednovo.libs.common.ShowUtils;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.modelmsg.SendAuth.Resp;

import de.greenrobot.event.EventBus;

/**
 * Created by Dk on 16/3/3.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThirdPartyAPI.mIWXAPI.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Bundle bundle = new Bundle();
        baseResp.toBundle(bundle);
        Resp sp = new Resp(bundle);
        if(baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH){
            //微信登录的回调
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    ShowUtils.showProgressDialog(this, R.string.text_loading);
                    if(sp.code!=null) {
                        getWechatAccessToken(sp.code);
                    }else{
                        finish();
                        EventBus.getDefault().post(new BaseEvent(Globle.KEY_EVENT_WECHAT_LOGIN_FAILED));
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    ShowUtils.showToast(R.string.auth_cancle);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    ShowUtils.showToast(R.string.login_failed);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    ShowUtils.showToast(R.string.login_failed);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    ShowUtils.showToast(R.string.login_failed);
                    finish();
                    break;
                default:
                    break;
            }
        }

        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            //微信支付的回调

        }

        if(baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX){
            //微信分享的回调
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    ShowUtils.showToast(R.string.share_success);
                    ShareFactory.shareLater(this, "003");
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    ShowUtils.showToast(R.string.share_cancle);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    ShowUtils.showToast(R.string.share_fail);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    ShowUtils.showToast(R.string.share_fail);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    ShowUtils.showToast(R.string.share_fail);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    private void getWechatAccessToken(String code){
        OtherReqApi.reuqestWechatToken(Constant.WX_APPID, Constant.WX_APPSECRET, code, new OtherRequestCallback() {
            @Override
            public void onRequestSuccess(Object object) {
                WechatTokenResult result = (WechatTokenResult)object;
                getWechatInfo(result.getAccess_token(), result.getOpenid());
            }

            @Override
            public void onRequestFailure(Object error) {
                EventBus.getDefault().post(new BaseEvent(Globle.KEY_EVENT_WECHAT_LOGIN_FAILED));
                finish();
            }
        });
    }
    private void getWechatInfo(String account_token, String openid){
        OtherReqApi.reuqestWechatInfo(account_token, openid, new OtherRequestCallback() {
            @Override
            public void onRequestSuccess(Object object) {
                WechatInfoResult result = (WechatInfoResult) object;
                EventBus.getDefault().post(new BaseEvent(Globle.KEY_EVENT_WECHAT_LOGIN_SUCCESS, result));
                finish();
            }

            @Override
            public void onRequestFailure(Object error) {
                EventBus.getDefault().post(new BaseEvent(Globle.KEY_EVENT_WECHAT_LOGIN_FAILED));
                finish();
            }
        });
    }
}
