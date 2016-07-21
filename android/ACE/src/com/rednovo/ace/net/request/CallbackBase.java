/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.net.request;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.libs.common.LogUtils;
import com.rednovo.libs.net.okhttp.callback.Callback;
import com.squareup.okhttp.Response;

/**
 * @author Zhen.Li
 * @fileNmae RequestCallback
 * @since 2016-03-04
 */
public final class CallbackBase<T extends BaseResult> extends Callback {
    private static final String LOG_TAG = "CallbackBase";
    private Class<T> clazz;
    private RequestCallback<T> requestCallback;

    public CallbackBase(Class<T> mClazz, RequestCallback<T> mRequestCallback) {
        this.clazz = mClazz;
        this.requestCallback = mRequestCallback;
    }

    //此方法为异步方法
    @Override
    public Object parseNetworkResponse(Response response) {
        T t = null;
        try {
            String body = response.body().string();
            LogUtils.v(LOG_TAG, response.code() + "" + body);
            t = JSON.parseObject(body, clazz);
        } catch (Exception ex) {

        }
        return t;
    }

    @Override
    public void onError(Exception e) {
        T t = newResultInstance();
        if (t != null) {
            t.setErrCode(BaseResult.ERROR);
            t.setErrMsg(e.getMessage());
           // LogUtils.v(LOG_TAG, e.getMessage());
            if (requestCallback != null)
                requestCallback.onRequestFailure(t);
        }
    }

    @Override
    public void onResponse(Object response) {
        if (response != null) {
            T t = (T) response;
            if (null == t) {
                t = newResultInstance();
                error(t);
                return;
            }
            if (t.isSuccess()) {
                if (clazz != null) {
                    invokeResultCheckMethod(clazz, t);
                }
                requestCallback.onRequestSuccess(t);
            } else {
                requestCallback.onRequestFailure(t);
            }
        } else {

            T t = newResultInstance();
            error(t);
        }

    }

    protected void error(T t) {
        if (t != null) {
            t.setErrCode(BaseResult.ERROR);
            t.setErrMsg("parser error ");
            requestCallback.onRequestFailure(t);
        }
    }


    private void invokeResultCheckMethod(Class<?> clazz, T r) {
        try {
            java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
            for (java.lang.reflect.Method method : methods) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0] == BaseResult.class) {
                    method.setAccessible(true);
                    method.invoke(null, r);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private T newResultInstance() {
        T data = null;
        try {
            data = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        // 为了保证上层调用安全，这里需要保证data永远不可能为null，result class必须要有默认的构造方法
        if (data == null) {
        }
        return data;
    }

}
