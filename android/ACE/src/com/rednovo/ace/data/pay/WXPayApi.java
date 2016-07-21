package com.rednovo.ace.data.pay;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.net.parser.NewOrderResult;
import com.rednovo.libs.common.StringUtils;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import com.alibaba.fastjson.JSONObject;

import java.security.MessageDigest;
import java.util.Map;

/**
 * Created by lilong on 16/3/5.
 */
public class WXPayApi extends BasePayApi implements IWXAPIEventHandler {

    private static WXPayApi mInstance = null;
    private IWXAPI iwxapi;
    private static final String WX_UNION_LOGIN_APPID = "wx743d2275c0a223e3";
    private Context mContext;
    private WXPayApi(Context cxt){
        mContext = cxt;
        iwxapi = WXAPIFactory.createWXAPI(cxt, WX_UNION_LOGIN_APPID, true);
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        // iwxapi.registerApp(WX_UNION_LOGIN_APPID);
    }

    public static WXPayApi getInstance(Context cxt){
        if(mInstance == null){
            mInstance = new WXPayApi(cxt);
        }

        return mInstance;
    }

    @Override
    public void pay(Map<String, Object> orderInfo) {
        NewOrderResult.Order order = (NewOrderResult.Order) orderInfo.get("orderInfo");
        PayReq req = new PayReq();
        req.appId = order.getAppid();
        req.partnerId = order.getPartnerid();
        req.prepayId = order.getPrepayid();
        req.nonceStr = order.getNoncestr();
        req.timeStamp = order.getTimestamp() + "";
        req.packageValue = "Sign=WXPay";
        req.sign = order.getSign();
        req.extData	= "app data"; // optional
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        boolean result = iwxapi.sendReq(req);
    }

    /**
     * 获取时间戳
     *
     * @return System.currentTimeMillis() / 1000
     */
    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    @Override
    public boolean isSupportPay() {
        return iwxapi.isWXAppSupportAPI();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        switch (baseReq.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp baseResp) {

        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
//            Toast.makeText(this, "code = " + ((SendAuth.Resp) resp).code, Toast.LENGTH_SHORT).show();
        }

        int result = 0;

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
//                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                result = R.string.errcode_deny;
                break;
            default:
//                result = R.string.errcode_unknown;
                break;
        }
    }

    public final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static class MD5 {

        private MD5() {}

        public final static String getMessageDigest(byte[] buffer) {
            char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            try {
                MessageDigest mdTemp = MessageDigest.getInstance("MD5");
                mdTemp.update(buffer);
                byte[] md = mdTemp.digest();
                int j = md.length;
                char str[] = new char[j * 2];
                int k = 0;
                for (int i = 0; i < j; i++) {
                    byte byte0 = md[i];
                    str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                    str[k++] = hexDigits[byte0 & 0xf];
                }
                return new String(str);
            } catch (Exception e) {
                return null;
            }
        }
    }

}
