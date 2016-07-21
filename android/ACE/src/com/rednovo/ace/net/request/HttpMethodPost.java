/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.net.request;

import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.libs.net.okhttp.OkHttpEngine;
import com.rednovo.libs.net.okhttp.builder.PostFormBuilder;
import com.rednovo.libs.net.okhttp.callback.Callback;
import com.rednovo.libs.net.okhttp.request.RequestCall;

import java.io.File;
import java.util.Map;

/**
 * @author Zhen.Li
 * @fileNmae HttpMethodPost
 * @since 2016-03-04
 */
public final class HttpMethodPost<T extends BaseResult> extends HttpBase<T> {
    private PostFormBuilder postFormBuilder;
    private RequestCall mRequestCall;
    private Class<T> clazz;
    private long sConnTime = 0;
    private long sReadTime = 0;
    private long sWriteTime = 0;

    public HttpMethodPost(Class<T> mClazz) {
        super();
        this.clazz = mClazz;
        postFormBuilder = OkHttpEngine.post();
    }

    @Override
    public HttpBase<T> execute(RequestCallback<T> requestCallback) {
        mRequestCall = postFormBuilder.build();
        if (sConnTime != 0)
            mRequestCall.connTimeOut(sConnTime);

        if (sReadTime != 0)
            mRequestCall.readTimeOut(sReadTime);

        if (sWriteTime != 0)
            mRequestCall.writeTimeOut(sWriteTime);

        mRequestCall.execute(new CallbackBase<T>(clazz, requestCallback));
        return this;

    }

    @Override
    public HttpBase<T> execute(Callback requestCallback) {
        mRequestCall = postFormBuilder.build();
        if (sConnTime != 0)
            mRequestCall.connTimeOut(sConnTime);

        if (sReadTime != 0)
            mRequestCall.readTimeOut(sReadTime);

        if (sWriteTime != 0)
            mRequestCall.writeTimeOut(sWriteTime);

        mRequestCall.execute(requestCallback);
        return null;
    }

    @Override
    public HttpBase addArguments(Map<String, String> arguments) {
        postFormBuilder.params(arguments);
        return this;
    }

    @Override
    public HttpBase<T> addArguments(String key, String value) {
        postFormBuilder.addParams(key, value);
        return this;
    }

    @Override
    public HttpBase<T> addUrlArgument(String url) {
        postFormBuilder.url(url);
        return this;
    }

    @Override
    public HttpBase<T> addHeader(String key, String header) {
        postFormBuilder.addHeader(key, header);
        return this;
    }

    /**
     * @param name
     * @param filename
     * @param file
     */
    @Override
    public HttpBase addFile(String name, String filename, File file) {
        postFormBuilder.addFile(name, filename, file);
        return this;
    }

    @Override
    public HttpBase<T> addTag(Object tag) {
        postFormBuilder.tag(tag);
        return this;
    }

    @Override
    public HttpBase<T> cancelReq() {
        mRequestCall.cancel();
        return this;
    }

    @Override
    public HttpBase<T> connTimeOut(long connTime) {
        this.sConnTime = connTime;
        return this;
    }

    @Override
    public HttpBase<T> readTimeOut(long redTime) {
        this.sReadTime = redTime;
        return this;
    }

    @Override
    public HttpBase<T> writeTimeOut(long writeTime) {
        this.sWriteTime = writeTime;
        return this;
    }
}
