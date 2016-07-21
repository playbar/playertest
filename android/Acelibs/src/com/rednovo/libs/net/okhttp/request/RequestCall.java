package com.rednovo.libs.net.okhttp.request;

import com.rednovo.libs.net.okhttp.OkHttpEngine;
import com.rednovo.libs.net.okhttp.callback.Callback;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhy on 15/12/15.
 */
public class RequestCall {
    private OkHttpRequest okHttpRequest;
    private Request request;
    private Call call;

    private long readTimeOut;
    private long writeTimeOut;
    private long connTimeOut;

    private OkHttpClient clone;


    public RequestCall(OkHttpRequest request) {
        this.okHttpRequest = request;
    }

    public RequestCall readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public RequestCall writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public RequestCall connTimeOut(long connTimeOut) {
        this.connTimeOut = connTimeOut;
        return this;
    }


    public Call generateCall(Callback callback) {
        request = generateRequest(callback);

        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0) {
            readTimeOut = readTimeOut > 0 ? readTimeOut : OkHttpEngine.DEFAULT_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : OkHttpEngine.DEFAULT_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut : OkHttpEngine.DEFAULT_MILLISECONDS;

//            clone = OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
//                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
//                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
//                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
//                    .build();
            clone = OkHttpEngine.getInstance().getOkHttpClient();
            clone.setReadTimeout(readTimeOut, TimeUnit.MILLISECONDS);
            clone.setWriteTimeout(writeTimeOut, TimeUnit.MILLISECONDS);
            clone.setConnectTimeout(connTimeOut, TimeUnit.MILLISECONDS);

            call = clone.newCall(request);
        } else {
            call = OkHttpEngine.getInstance().getOkHttpClient().newCall(request);
        }
        return call;
    }

    private Request generateRequest(Callback callback) {
        return okHttpRequest.generateRequest(callback);
    }

    public void execute(Callback callback) {
        generateCall(callback);

        if (callback != null) {
            callback.onBefore(request);
        }

        OkHttpEngine.getInstance().execute(this, callback);
    }

    public Call getCall() {
        return call;
    }

    public Request getRequest() {
        return request;
    }

    public OkHttpRequest getOkHttpRequest() {
        return okHttpRequest;
    }

    public Response execute() throws IOException {
        generateCall(null);
        return call.execute();
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }


}
