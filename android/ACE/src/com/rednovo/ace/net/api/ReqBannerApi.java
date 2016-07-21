package com.rednovo.ace.net.api;

import android.app.Activity;

import com.rednovo.ace.net.parser.BannerResult;
import com.rednovo.ace.net.request.HttpBase;
import com.rednovo.ace.net.request.HttpMethodPost;
import com.rednovo.ace.net.request.RequestCallback;

/**
 * Created by lilong on 16/3/13.
 */
public class ReqBannerApi {
    private static String url_suffix = "service/";

    public static HttpBase reqBanner(Activity activity, RequestCallback<BannerResult> callback) {
        return new HttpMethodPost<BannerResult>(BannerResult.class)
                .addUrlArgument(ReqConfig.getUrl() + url_suffix + ReqConfig.KEY_BANNER)
                .addTag(activity)
                .execute(callback);
    }
}
