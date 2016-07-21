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
import com.rednovo.libs.net.okhttp.builder.GetBuilder;
import com.rednovo.libs.net.okhttp.callback.Callback;
import com.rednovo.libs.net.okhttp.request.RequestCall;

import java.io.File;
import java.util.Map;

/**
 * @author Zhen.Li
 * @fileNmae HttpMethodGet
 * @since 2016-03-04
 */
public final class HttpMethodGet<T extends BaseResult> extends HttpBase<T> {

    private GetBuilder getBuilder;
    private RequestCall requestCall;
    private Class<T> mResultClass;
    private long sConnTime = 0;
    private long sReadTime = 0;
    private long sWriteTime = 0;

    public HttpMethodGet(Class<T> resultClass) {
        super();
        this.mResultClass = resultClass;
        getBuilder = OkHttpEngine.get();
    }

    @Override
    public HttpBase<T> execute(RequestCallback<T> requestCallback) {
        requestCall = getBuilder.build();
        if (sConnTime != 0)
            requestCall.connTimeOut(sConnTime);
        if (sReadTime != 0)
            requestCall.readTimeOut(sReadTime);
        if (sWriteTime != 0)
            requestCall.writeTimeOut(sWriteTime);

        requestCall.execute(new CallbackBase<T>(mResultClass, requestCallback));
        return this;
    }

    @Override
    public HttpBase<T> execute(Callback requestCallback) {
        requestCall = getBuilder.build();
        if (sConnTime != 0)
            requestCall.connTimeOut(sConnTime);
        if (sReadTime != 0)
            requestCall.readTimeOut(sReadTime);
        if (sWriteTime != 0)
            requestCall.writeTimeOut(sWriteTime);

        requestCall.execute(requestCallback);
        return null;
    }


    @Override
    public HttpBase<T> addArguments(Map<String, String> arguments) {
        getBuilder.params(arguments);
        return this;
    }

    @Override
    public HttpBase<T> addArguments(String key, String value) {
        getBuilder.addParams(key, value);
        return this;
    }

    @Override
    public HttpBase<T> addUrlArgument(String url) {
        getBuilder.url(url);
        return this;
    }

    @Override
    public HttpBase<T> addHeader(String key, String header) {
        getBuilder.addHeader(key, header);
        return this;
    }

    @Override
    public HttpBase<T> addFile(String name, String filename, File file) {
        return this;
    }

    @Override
    public HttpBase<T> addTag(Object tag) {
        getBuilder.tag(tag);
        return this;
    }

    @Override
    public HttpBase<T> cancelReq() {
        requestCall.cancel();
        return this;
    }

    @Override
    public HttpBase<T> connTimeOut(long timeOut) {
        this.sConnTime = timeOut;
        return this;
    }

    @Override
    public HttpBase<T> readTimeOut(long timeOut) {
        this.sReadTime = timeOut;
        return this;
    }

    @Override
    public HttpBase<T> writeTimeOut(long timeOut) {
        this.sWriteTime = timeOut;
        return this;
    }

}
