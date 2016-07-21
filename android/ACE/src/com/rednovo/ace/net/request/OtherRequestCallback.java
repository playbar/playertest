package com.rednovo.ace.net.request;

/**
 * Created by Dk on 16/3/18.
 */
public interface OtherRequestCallback {
    void onRequestSuccess(Object object);

    void onRequestFailure(Object error);
}
