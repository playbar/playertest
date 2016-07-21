package com.rednovo.ace.data.login;

import com.rednovo.ace.R;
import com.rednovo.libs.common.ThirdPartyAPI;
import com.rednovo.libs.common.ShowUtils;
import com.tencent.mm.sdk.modelmsg.SendAuth;

/**
 * 使用微信登录
 * Created by Dk on 16/3/6.
 */
public class LoginForWechat {

    public LoginForWechat() {

    }

    public void login() {
        if (ThirdPartyAPI.isWXAppInstalled()) {
            if (ThirdPartyAPI.isWXAppSupportAPI()) {
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "ace";
                ThirdPartyAPI.mIWXAPI.sendReq(req);
            } else {
                ShowUtils.showToast(R.string.wechat_support_api_to_less);
            }
        } else {
            ShowUtils.showToast(R.string.wechat_uninstalled);
        }
    }
}
