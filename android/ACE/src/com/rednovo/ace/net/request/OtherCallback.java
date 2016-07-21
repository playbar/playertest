package com.rednovo.ace.net.request;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.libs.common.LogUtils;
import com.rednovo.libs.net.okhttp.callback.Callback;
import com.squareup.okhttp.Response;

/**
 * Created by Dk on 16/3/18.
 */
public class OtherCallback extends Callback {

    private static final String LOG_TAG = "CallbackBase";

    private Class clazz;

    private OtherRequestCallback otherRequestCallback;

    public OtherCallback(Class clazz, OtherRequestCallback otherRequestCallback) {
        this.clazz = clazz;
        this.otherRequestCallback = otherRequestCallback;
    }

    @Override
    public Object parseNetworkResponse(Response response) throws Exception {
        Object object = null;
        try {
            String body = response.body().string();
            LogUtils.v(LOG_TAG, response.code() + "" + body);
            object = JSON.parseObject(body, clazz);
        } catch (Exception ex) {

        }
        return object;
    }

    @Override
    public void onError(Exception e) {
        Object object = newResultInstance();
        if (object != null) {
            // LogUtils.v(LOG_TAG, e.getMessage());
            if (otherRequestCallback != null)
                otherRequestCallback.onRequestFailure(object);
        }
    }

    @Override
    public void onResponse(Object response) {
        if (response != null) {
            if (clazz != null) {
                invokeResultCheckMethod(clazz, response);
            }
            otherRequestCallback.onRequestSuccess(response);
        } else {
            otherRequestCallback.onRequestFailure(response);
        }
    }

    private void invokeResultCheckMethod(Class<?> clazz, Object object) {
        try {
            java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
            for (java.lang.reflect.Method method : methods) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0] == BaseResult.class) {
                    method.setAccessible(true);
                    method.invoke(null, object);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object newResultInstance() {
        Object data = null;
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
