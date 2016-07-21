package com.rednovo.ace.data.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.rednovo.ace.R;
import com.rednovo.ace.net.parser.ShareInfoResult;
import com.rednovo.libs.common.ThirdPartyAPI;
import com.rednovo.libs.common.Constant;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Dk on 16/3/1.
 */
public class SinaShareApi extends BaseShareApi implements IWeiboHandler.Response, WeiboAuthListener {

    private static final int SIZE_IMAGE_SHARE = 32 * 1024;

    private boolean hasText = true;

    private boolean hasImage = true;

    private boolean hasWebpage = true;

    private boolean hasMusic = false;

    private boolean hasVideo = false;

    private boolean hasVoice = false;

    public SinaShareApi(Activity activity, ShareInfoResult shareInfo) {
        super(shareInfo);
        this.activity = activity;
        this.shareInfo = shareInfo;
    }

    public SinaShareApi(Activity activity, ShareInfoResult shareInfo, boolean hasText, boolean hasImage, boolean hasWebpage, boolean hasMusic, boolean hasVideo, boolean hasVoice){
        this(activity, shareInfo);
        this.hasText = hasText;
        this.hasImage = hasImage;
        this.hasWebpage = hasWebpage;
        this.hasMusic = hasMusic;
        this.hasVideo = hasVideo;
        this.hasVoice = hasVoice;

    }

    @Override
    public void doShare() {
        super.doShare();
        getShareImg(shareInfo.getImgSrc());
    }

    public void onNewIntent(Intent intent) {
        ThirdPartyAPI.mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        if(baseResponse!= null){
            switch (baseResponse.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    ShowUtils.showToast(R.string.share_success);
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    ShowUtils.showToast(R.string.share_cancle);
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    ShowUtils.showToast(R.string.share_fail);
                    break;
            }
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     * @param hasVoice   分享的内容是否有声音
     */
    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
                                  boolean hasMusic, boolean hasVideo, boolean hasVoice) {

        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        //设置分享消息的文本内容
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }
        //设置分享消息的图片内容
        if (hasImage) {
            weiboMessage.imageObject = getImageObj();
        }

        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj();
        }
        if (hasMusic) {
            weiboMessage.mediaObject = getMusicObj();
        }
        if (hasVideo) {
            weiboMessage.mediaObject = getVideoObj();
        }
        if (hasVoice) {
            weiboMessage.mediaObject = getVoiceObj();
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        if (ThirdPartyAPI.isWeiboAppSupportAPI()) {
            ThirdPartyAPI.mWeiboShareAPI.sendRequest(activity, request);
        } else {
            AuthInfo authInfo = new AuthInfo(activity, Constant.SINA_APPID, Constant.SINA_REDIRECT_URL, Constant.SINA_SCOPE);
            Oauth2AccessToken accessToken = readAccessToken(activity);
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }
            ThirdPartyAPI.mWeiboShareAPI.sendRequest(activity, request, authInfo, token, this);
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     */
    private void sendSingleMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
                                   boolean hasMusic, boolean hasVideo/*, boolean hasVoice*/) {

        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        if (hasText) {
            weiboMessage.mediaObject = getTextObj();
        }
        if (hasImage) {
            weiboMessage.mediaObject = getImageObj();
        }
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj();
        }
        if (hasMusic) {
            weiboMessage.mediaObject = getMusicObj();
        }
        if (hasVideo) {
            weiboMessage.mediaObject = getVideoObj();
        }
        /*if (hasVoice) {
            weiboMessage.mediaObject = getVoiceObj();
        }*/

        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        ThirdPartyAPI.mWeiboShareAPI.sendRequest(activity, request);
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = shareInfo.getTitle();
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        //设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        Bitmap bitmap = null;
        if (shareInfo.image != null) {
            bitmap = shareInfo.image;
        } else {
            bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
        }
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = shareInfo.getSumy();
        mediaObject.description = shareInfo.getSumy();

        Bitmap bitmap = null;
        if(shareInfo.image != null){
            bitmap = zoomImageForSina(shareInfo.image);
        }else{
            bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
        }
        ///设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = shareInfo.getUrl();
        return mediaObject;
    }

    /**
     * 创建多媒体（音乐）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private MusicObject getMusicObj() {
        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
        musicObject.identify = Utility.generateGUID();
        musicObject.title = shareInfo.getTitle();
        musicObject.description = shareInfo.getSumy();

        Bitmap bitmap = null;
        if(shareInfo.image != null){
            bitmap = shareInfo.image;
        }else{
            bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
        }


        // 设置 Bitmap 类型的图片到视频对象里        设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        musicObject.setThumbImage(bitmap);
        musicObject.actionUrl = shareInfo.getUrl();
        musicObject.dataUrl = shareInfo.videoUrl;
        musicObject.dataHdUrl = shareInfo.videoUrl;
        musicObject.duration = 10;
        return musicObject;
    }

    /**
     * 创建多媒体（视频）消息对象。
     *
     * @return 多媒体（视频）消息对象。
     */
    private VideoObject getVideoObj() {
        // 创建媒体消息
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = shareInfo.getTitle();
        videoObject.description = shareInfo.getSumy();

        Bitmap bitmap = null;
        if(shareInfo.image != null){
            bitmap = shareInfo.image;
        }else{
            bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
        }

        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, os);
            System.out.println("kkkkkkk    size  "+ os.toByteArray().length );
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("Weibo.BaseMediaObject", "put thumb failed");
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        videoObject.setThumbImage(bitmap);
        videoObject.actionUrl = shareInfo.getUrl();
        videoObject.dataUrl = "www.weibo.com";
        videoObject.dataHdUrl = "www.weibo.com";
        videoObject.duration = 10;
        videoObject.defaultText = "Vedio 默认文案";
        return videoObject;
    }

    /**
     * 创建多媒体（音频）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private VoiceObject getVoiceObj() {
        // 创建媒体消息
        VoiceObject voiceObject = new VoiceObject();
        voiceObject.identify = Utility.generateGUID();
        voiceObject.title = shareInfo.getTitle();
        voiceObject.description = shareInfo.getSumy();

        Bitmap bitmap = null;
        if(shareInfo.image != null){
            bitmap = shareInfo.image;
        }else{
            bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
        }

        voiceObject.setThumbImage(bitmap);
        voiceObject.actionUrl = shareInfo.getUrl();
        voiceObject.dataUrl = "www.weibo.com";
        voiceObject.dataHdUrl = "www.weibo.com";
        voiceObject.duration = 10;
        voiceObject.defaultText = "Voice 默认文案";
        return voiceObject;
    }

    private Oauth2AccessToken readAccessToken(Context context) {
        if (null == context) {
            return null;
        }

        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences pref = context.getSharedPreferences("com_weibo_sdk_android", Context.MODE_APPEND);
        token.setUid(pref.getString("uid", ""));
        token.setToken(pref.getString("access_token", ""));
        token.setRefreshToken(pref.getString("expires_in", ""));
        token.setExpiresTime(pref.getLong("refresh_token", 0));

        return token;
    }

    /**
     * 保存 Token 对象到 SharedPreferences。
     *
     * @param context 应用程序上下文环境
     * @param token   Token 对象
     */
    public static void writeAccessToken(Context context, Oauth2AccessToken token) {
        if (null == context || null == token) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences("com_weibo_sdk_android", Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("uid", token.getUid());
        editor.putString("access_token", token.getToken());
        editor.putString("expires_in", token.getRefreshToken());
        editor.putLong("refresh_token", token.getExpiresTime());
        editor.commit();
    }

    private void getShareImg(String url){
        FrescoEngine.setSimpleDraweeView(activity, url, new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                Bitmap mBitmap = Bitmap.createBitmap(bitmap);
                shareInfo.image = mBitmap;
                if (!ThirdPartyAPI.isWeiboAppInstalled() || !ThirdPartyAPI.isWeiboAppSupportAPI()) {
                    sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
                } else {
                    if (ThirdPartyAPI.isMultipleShare()) {
                        sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
                    } else {
                        sendSingleMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo);
                    }
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                if (!ThirdPartyAPI.isWeiboAppInstalled() || !ThirdPartyAPI.isWeiboAppSupportAPI()) {
                    sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
                } else {
                    if (ThirdPartyAPI.isMultipleShare()) {
                        sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
                    } else {
                        sendSingleMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo);
                    }
                }
            }
        });
    }

    private Bitmap zoomImageForSina(Bitmap bitmap) {
        int area = bitmap.getWidth() * bitmap.getHeight() * 4;
        double scale = Math.sqrt(((double) area) / SIZE_IMAGE_SHARE);
        if (scale > 1) {
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale), true);
        }
        return bitmap;
    }

    @Override
    public void onComplete(Bundle bundle) {
        Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
        writeAccessToken(activity, newToken);
        ShowUtils.showToast(R.string.share_success);
    }

    @Override
    public void onWeiboException(WeiboException e) {
        ShowUtils.showToast(R.string.share_fail);
    }

    @Override
    public void onCancel() {
        ShowUtils.showToast(R.string.share_cancle);
    }
}
