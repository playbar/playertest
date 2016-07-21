package com.rednovo.ace.data.pay;

import android.os.Handler;

import java.util.Map;

/**
 * Created by lilong on 16/3/5.
 */
public class BasePayApi {

    protected Handler mCallback;
    protected int mWhat;

    public void pay(Map<String, Object> orderInfo){

    }

    public boolean isSupportPay(){
        return true;
    }

    public BasePayApi setCallback(Handler callback, int what){
        mCallback = callback;
        mWhat = what;

        return this;
    }

    public void destroy(){

    }
}
