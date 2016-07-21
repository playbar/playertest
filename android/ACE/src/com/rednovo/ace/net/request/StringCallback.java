package com.rednovo.ace.net.request;

import com.rednovo.libs.net.okhttp.callback.Callback;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * @author Zhen.Li
 * @fileNmae StringCallback
 * @since 2016-04-13
 */
public abstract class StringCallback extends Callback {
    @Override
    public Object parseNetworkResponse(Response response) {
        String body = null;
        try {
            body = response.body().string();
        } catch (IOException e) {
        }
        return body;
    }

    @Override
    public void onError(Exception e) {
        onRequestFailure(e.getMessage());
    }

    @Override
    public void onResponse(Object response) {
        if (response != null) {
            String body = (String) response;
            onRequestSuccess(body);
        } else {
            onRequestFailure("obj null");
        }
    }

    public abstract void onRequestSuccess(String object);

    public abstract void onRequestFailure(String error);


}
