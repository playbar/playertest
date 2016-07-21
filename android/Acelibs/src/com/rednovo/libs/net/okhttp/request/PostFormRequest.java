package com.rednovo.libs.net.okhttp.request;

import android.text.TextUtils;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.rednovo.libs.net.okhttp.OkHttpEngine;
import com.rednovo.libs.net.okhttp.builder.PostFormBuilder;
import com.rednovo.libs.net.okhttp.callback.Callback;
import com.rednovo.libs.net.okhttp.requestbody.FormBody;
import com.rednovo.libs.net.okhttp.requestbody.MultipartBody;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;


public class PostFormRequest extends OkHttpRequest {
    private List<PostFormBuilder.FileInput> files;

    public PostFormRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, List<PostFormBuilder.FileInput> files) {
        super(url, tag, params, headers);
        this.files = files;
    }

    @Override
    protected RequestBody buildRequestBody() {
        if (files == null || files.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            addParams(builder);
            return builder.build();
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            addParams(builder);

            for (int i = 0; i < files.size(); i++) {
                PostFormBuilder.FileInput fileInput = files.get(i);
                RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file);
                builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
            }
            return builder.build();
        }
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody, final Callback callback) {
        if (callback == null) return requestBody;
        CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {

                OkHttpEngine.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        callback.inProgress(bytesWritten * 1.0f / contentLength);
                    }
                });

            }
        });
        return countingRequestBody;
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.post(requestBody).build();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void addParams(MultipartBody.Builder builder) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                if (!TextUtils.isEmpty(key)) {
                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                            RequestBody.create(null, params.get(key)));
                }
            }
        }
    }

    private void addParams(FormBody.Builder builder) {
        if (params == null || params.isEmpty()) {
            //builder.add("1", "1");
            return;
        }

        for (String key : params.keySet()) {
            String value = "";
            if (params.get(key) != null) {
                value = params.get(key);
            }
            builder.add(key, value);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        if (files != null) {
            for (PostFormBuilder.FileInput file : files) {
                sb.append(file.toString() + "  ");
            }
        }
        return sb.toString();
    }
}
