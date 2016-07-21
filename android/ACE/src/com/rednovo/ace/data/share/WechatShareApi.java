package com.rednovo.ace.data.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.rednovo.ace.R;
import com.rednovo.ace.net.parser.ShareInfoResult;
import com.rednovo.libs.common.ThirdPartyAPI;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.Utils;
import com.rednovo.libs.net.fresco.FrescoEngine;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

/**
 * Created by Dk on 16/3/1.
 */
public class WechatShareApi extends BaseShareApi {

    private static final int SIZE_IMAGE_SHARE = 32 * 1024;


    /**
     * ShareFactory.WECHAT_SHARE 分享给微信好友
     * ShareFactory.WECHAT_FRIEND_SHARE 分享到朋友圈
     */
    private int type;

    private String tag;

    public WechatShareApi(Activity activity, int type, ShareInfoResult shareInfo) {
        super(shareInfo);
        this.type = type;
        this.activity = activity;
        this.shareInfo = shareInfo;
    }

    @Override
    public void doShare() {
        super.doShare();
        if (!ThirdPartyAPI.isWXAppInstalled()) {
            ShowUtils.showToast(R.string.wechat_uninstalled);
            return;
        }
        if (!ThirdPartyAPI.isWXAppSupportAPI()) {
            ShowUtils.showToast(R.string.wechat_support_api_to_less);
            return;
        }
        getShareImg(shareInfo.getImgSrc());
    }

    /**
     * 分享文字
     *
     * @return 是否分享成功
     */
    private boolean shareText() {

        //初始化一个WXTextObject对象，填写分享的文本内容
        WXTextObject object = new WXTextObject();
        object.text = shareInfo.getSumy();

        //用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = object;
        msg.description = shareInfo.getSumy();

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        if (type == ShareFactory.WECHAT_SHARE) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else if (type == ShareFactory.WECHAT_FRIEND_SHARE) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }

        //发送数据，返回分享结果
        return ThirdPartyAPI.mIWXAPI.sendReq(req);
    }

    /**
     * 分享图片
     *
     * @return 是否分享成功
     */
    private boolean shareImage() {

        //初始化WXImageObject和WXMediaMessage对象
        WXImageObject object = new WXImageObject(shareInfo.image);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = object;
        msg.description = shareInfo.getSumy();


        //设置缩略图
        Bitmap thumbBmp = zoomImageForWX(shareInfo.image);
        shareInfo.image.recycle();
        msg.thumbData = Utils.bmpToByteArray(thumbBmp, true);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        if (type == ShareFactory.WECHAT_SHARE) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else if (type == ShareFactory.WECHAT_FRIEND_SHARE) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }

        //发送数据，返回分享结果
        return ThirdPartyAPI.mIWXAPI.sendReq(req);
    }

    /**
     * 分享音乐
     *
     * @return 是否分享成功
     */
    private boolean shareMusic() {

        //初始化一个WXMusicObject对象，填写url
        WXMusicObject object = new WXMusicObject();
        object.musicUrl = shareInfo.musicUrl;

        //用一个WXMusicObject对象初始化一个WXMediaMessage对象，填写标题、描述
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = object;
        msg.title = shareInfo.getTitle();
        msg.description = shareInfo.getSumy();

        //设置缩略图
        Bitmap thumbBmp = null;
        if (shareInfo.image != null) {
            thumbBmp = zoomImageForWX(shareInfo.image);
            shareInfo.image.recycle();
        } else {
            thumbBmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
        }
        msg.thumbData = Utils.bmpToByteArray(thumbBmp, true);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("music");
        req.message = msg;
        if (type == ShareFactory.WECHAT_SHARE) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else if (type == ShareFactory.WECHAT_FRIEND_SHARE) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }

        //发送数据，返回分享结果
        return ThirdPartyAPI.mIWXAPI.sendReq(req);
    }

    /**
     * 分享视频
     *
     * @return 是否分享成功
     */
    private boolean shareVideo() {

        //初始化一个WXVideoObject对象，填写url
        WXVideoObject object = new WXVideoObject();
        object.videoUrl = shareInfo.videoUrl;

        //用一个WXVideoObject对象初始化一个WXMediaMessage对象，填写标题、描述
        WXMediaMessage msg = new WXMediaMessage(object);
        msg.title = shareInfo.getTitle();
        msg.description = shareInfo.getSumy();

        //设置缩略图
        Bitmap thumbBmp = null;
        if (shareInfo.image != null) {
            thumbBmp = zoomImageForWX(shareInfo.image);
            shareInfo.image.recycle();
        } else {
            thumbBmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
        }
        msg.thumbData = Utils.bmpToByteArray(thumbBmp, true);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("video");
        req.message = msg;
        if (type == ShareFactory.WECHAT_SHARE) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else if (type == ShareFactory.WECHAT_FRIEND_SHARE) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }

        //发送数据，返回分享结果
        return ThirdPartyAPI.mIWXAPI.sendReq(req);
    }

    /**
     * 分享网页
     *
     * @return 是否分享成功
     */
    private boolean shareWebPage() {

        //初始化一个WXVideoObject对象，填写url
        WXWebpageObject object = new WXWebpageObject();
        object.webpageUrl = shareInfo.getUrl();

        //用一个WXVideoObject对象初始化一个WXMediaMessage对象，填写标题、描述
        WXMediaMessage msg = new WXMediaMessage(object);
        msg.title = shareInfo.getTitle();
        msg.description = shareInfo.getSumy();

        //设置缩略图
        Bitmap thumbBmp = null;
        if (shareInfo.image != null) {
            thumbBmp = zoomImageForWX(shareInfo.image);
        } else {
            thumbBmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
        }
        msg.thumbData = Utils.bmpToByteArray(thumbBmp, true);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        if (type == ShareFactory.WECHAT_SHARE) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else if (type == ShareFactory.WECHAT_FRIEND_SHARE) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }

        //发送数据，返回分享结果
        return ThirdPartyAPI.mIWXAPI.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void getShareImg(String url){
        FrescoEngine.setSimpleDraweeView(activity, url, new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                Bitmap mBitmap = Bitmap.createBitmap(bitmap);
                shareInfo.image = mBitmap;
                if (tag != null) {
                    if (tag.equals("text")) {
                        shareText();
                    } else if (tag.equals("image")) {
                        shareImage();
                    } else if (tag.equals("music")) {
                        shareMusic();
                    } else if (tag.equals("video")) {
                        shareVideo();
                    } else if (tag.equals("web")) {
                        shareWebPage();
                    } else {
                        shareWebPage();
                    }
                } else {
                    shareWebPage();
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                if (tag != null) {
                    if (tag.equals("text")) {
                        shareText();
                    } else if (tag.equals("image")) {
                        shareImage();
                    } else if (tag.equals("music")) {
                        shareMusic();
                    } else if (tag.equals("video")) {
                        shareVideo();
                    } else if (tag.equals("web")) {
                        shareWebPage();
                    } else {
                        shareWebPage();
                    }
                } else {
                    shareWebPage();
                }
            }
        });
    }

    private Bitmap zoomImageForWX(Bitmap bitmap) {
        int area = bitmap.getWidth() * bitmap.getHeight() * 4;
        double scale = Math.sqrt(((double) area) / SIZE_IMAGE_SHARE);
        if (scale > 1) {
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale), true);
        }
        return bitmap;
    }
}
