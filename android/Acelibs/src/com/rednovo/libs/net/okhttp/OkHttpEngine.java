package com.rednovo.libs.net.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.rednovo.libs.net.okhttp.builder.GetBuilder;
import com.rednovo.libs.net.okhttp.builder.PostFileBuilder;
import com.rednovo.libs.net.okhttp.builder.PostFormBuilder;
import com.rednovo.libs.net.okhttp.builder.PostStringBuilder;
import com.rednovo.libs.net.okhttp.callback.Callback;
import com.rednovo.libs.net.okhttp.https.HttpsUtils;
import com.rednovo.libs.net.okhttp.request.RequestCall;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class OkHttpEngine {
    public static final String TAG = "OkHttpEngine";
    public static final long DEFAULT_MILLISECONDS = 10000;
    private static OkHttpEngine mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private boolean debug;
    private String tag;

    private OkHttpEngine() {
        OkHttpClient okHttpClientBuilder = new OkHttpClient();
        mDelivery = new Handler(Looper.getMainLooper());
        if (true) {
            okHttpClientBuilder.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

        }
        mOkHttpClient = okHttpClientBuilder;
        mOkHttpClient.setCookieHandler(CookieManager.getDefault());
        mOkHttpClient.setConnectTimeout(8000L, TimeUnit.MILLISECONDS);//连接超时
        mOkHttpClient.setWriteTimeout(8000L, TimeUnit.MILLISECONDS);//写入超时
        mOkHttpClient.setReadTimeout(8000L, TimeUnit.MILLISECONDS);//响应超时
//        mOkHttpClient.setCache(null);
    }


    public OkHttpEngine debug(String tag) {
        debug = true;
        this.tag = tag;
        return this;
    }


    public static OkHttpEngine getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpEngine.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpEngine();
                }
            }
        }
        return mInstance;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }


    public void execute(final RequestCall requestCall, Callback callback) {
        if (debug) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            Log.d(tag, "{method:" + requestCall.getRequest().method() + ", detail:" + requestCall.getOkHttpRequest().toString() + "}");
        }

        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;

        requestCall.getCall().enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                sendFailResultCallback(e, finalCallback);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() >= 400 && response.code() <= 599) {
                    try {
                        sendFailResultCallback(new RuntimeException(response.body().string()), finalCallback);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                try {
                    Object o = finalCallback.parseNetworkResponse(response);
                    sendSuccessResultCallback(o, finalCallback);
                } catch (Exception e) {
                    sendFailResultCallback(e, finalCallback);
                }
            }
        });
    }


    public void sendFailResultCallback(final Exception e, final Callback callback) {
        if (callback == null) return;

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(e);
                callback.onAfter();
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback) {
        if (callback == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    public void cancelTag(Object tag) {
        mOkHttpClient.getDispatcher().cancel(tag);
    }


    public void setCertificates(InputStream... certificates) {
        mOkHttpClient = getOkHttpClient()
                .setSslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null));
    }


}

