package com.rednovo.ace.data.pay;

import android.app.Activity;
import android.content.Context;

/**
 * Created by lilong on 16/3/5.
 */
public class PayApiFactory {

    public static final int WX_PAY = 1;
    public static final int IALIX_PAY = 2;

    public static BasePayApi getPayApi(Context cxt, int payType){
        BasePayApi api = null;
        switch (payType){
            case WX_PAY:
                api = WXPayApi.getInstance(cxt);
                break;
            case IALIX_PAY:
                api = IAlixPayApi.getInstance((Activity) cxt);
                break;
        }

        return api;
    }
}
