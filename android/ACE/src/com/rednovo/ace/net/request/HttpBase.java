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
import com.rednovo.libs.net.okhttp.callback.Callback;

import java.io.File;
import java.util.Map;

/**
 * @author Zhen.Li
 * @fileNmae HttpBase
 * @since 2016-03-04
 */
public abstract class HttpBase<T extends BaseResult> {
    protected OkHttpEngine okHttpEngine;

    public HttpBase() {
        okHttpEngine = OkHttpEngine.getInstance();
    }

    public abstract HttpBase<T> execute(RequestCallback<T> requestCallback);

    public abstract HttpBase<T> execute(Callback requestCallback);

    public abstract HttpBase<T> addArguments(Map<String, String> arguments);

    public abstract HttpBase<T> addArguments(String key, String value);

    public abstract HttpBase<T> addUrlArgument(String url);

    public abstract HttpBase<T> addHeader(String key, String header);

    public abstract HttpBase<T> addFile(String name, String filename, File file);

    /**
     * 将当前Activity加入tag
     *
     * @param tag
     */
    public abstract HttpBase<T> addTag(Object tag);


    public abstract HttpBase<T> cancelReq();

    public abstract HttpBase<T> connTimeOut(long timeOut);

    public abstract HttpBase<T> readTimeOut(long timeOut);

    public abstract HttpBase<T> writeTimeOut(long timeOut);

}
