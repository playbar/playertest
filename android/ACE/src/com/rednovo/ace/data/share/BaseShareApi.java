package com.rednovo.ace.data.share;

import android.app.Activity;

import com.rednovo.ace.net.parser.ShareInfoResult;

/**
 * Created by Dk on 16/3/1.
 */
public class BaseShareApi {

    public interface ShareResultCallback{
        void shareComplet();
        void shareError();
        void shareCancle();
    };

    protected ShareResultCallback shareResultCallback;

    protected ShareInfoResult shareInfo;

    protected Activity activity;

    public BaseShareApi(ShareInfoResult shareInfo) {
        this.shareInfo = shareInfo;
    }

    public void setShareResultCallback(ShareResultCallback shareResultCallback){
        this.shareResultCallback = shareResultCallback;
    }

    public void doShare(){}

}
