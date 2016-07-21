package com.rednovo.ace.net.request;

import com.rednovo.libs.net.okhttp.OkHttpEngine;
import com.rednovo.libs.net.okhttp.builder.GetBuilder;
import com.rednovo.libs.net.okhttp.request.RequestCall;

import java.io.File;
import java.util.Map;

/**
 * Created by Dk on 16/3/18.
 */
public class OtherHttpMethodGet {
    private OkHttpEngine okHttpEngine;
    private GetBuilder getBuilder;
    private RequestCall requestCall;
    private Class clazz;
    private long sConnTime = 0;
    private long sReadTime = 0;
    private long sWriteTime = 0;

    public OtherHttpMethodGet(Class clazz) {
        okHttpEngine = OkHttpEngine.getInstance();
        this.clazz = clazz;
        getBuilder = OkHttpEngine.get();
    }

    public OtherHttpMethodGet execute(OtherRequestCallback requestCallback) {
        requestCall = getBuilder.build();
        if (sConnTime != 0)
            requestCall.connTimeOut(sConnTime);
        if (sReadTime != 0)
            requestCall.readTimeOut(sReadTime);
        if (sWriteTime != 0)
            requestCall.writeTimeOut(sWriteTime);

        requestCall.execute(new OtherCallback(clazz, requestCallback));
        return this;
    }

    public OtherHttpMethodGet addArguments(Map<String, String> arguments) {
        getBuilder.params(arguments);
        return this;
    }

    public OtherHttpMethodGet addArguments(String key, String value) {
        getBuilder.addParams(key, value);
        return this;
    }

    public OtherHttpMethodGet addUrlArgument(String url) {
        getBuilder.url(url);
        return this;
    }

    public OtherHttpMethodGet addHeader(String key, String header) {
        getBuilder.addHeader(key, header);
        return this;
    }

    public OtherHttpMethodGet addFile(String name, String filename, File file) {
        return this;
    }

    public OtherHttpMethodGet addTag(Object tag) {
        getBuilder.tag(tag);
        return this;
    }

    public OtherHttpMethodGet cancelReq() {
        requestCall.cancel();
        return this;
    }

    public OtherHttpMethodGet connTimeOut(long timeOut) {
        this.sConnTime = timeOut;
        return this;
    }

    public OtherHttpMethodGet readTimeOut(long timeOut) {
        this.sReadTime = timeOut;
        return this;
    }

    public OtherHttpMethodGet writeTimeOut(long timeOut) {
        this.sWriteTime = timeOut;
        return this;
    }
}
