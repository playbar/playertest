package com.rednovo.ace.data.share;

import android.app.Activity;
import android.os.Bundle;

import com.rednovo.ace.net.parser.ShareInfoResult;
import com.rednovo.libs.common.ThirdPartyAPI;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

/**
 * Created by Dk on 16/3/1.
 */
public class QQShareApi extends BaseShareApi {

    /**
     * ShareFactory.QQ_SHARE 分享给QQ好友
     * ShareFactory.QQ_ZONE_SHARE 分享到QQ空间
     */
    private int type;

    private String tag;

    public QQShareApi(Activity activity, int type, ShareInfoResult shareInfo) {
        super(shareInfo);
        this.type = type;
        this.activity = activity;
        this.shareInfo = shareInfo;
    }

    @Override
    public void doShare() {
        super.doShare();
        switch (type) {
            case ShareFactory.QQ_SHARE:
                if (tag == null) {
                    shareTextImageView();
                } else {
                    if (tag.equals("img")) {
                        shareImage();
                    } else if (tag.equals("default")) {
                        shareTextImageView();
                    } else if (tag.equals("app")) {
                        shareAPP();
                    } else if (tag.equals("music")) {
                        shareMusic();
                    } else {
                        shareTextImageView();
                    }
                }
                break;
            case ShareFactory.QQ_ZONE_SHARE:
                shareToQZone();
                break;
        }
    }

    /**
     * 分享图文
     */
    private void shareTextImageView() {

        Bundle params = new Bundle();
        //必填   Int  分享的类型。图文分享(普通分享)填Tencent.SHARE_TO_QQ_TYPE_DEFAULT
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        //必填	String	分享的标题, 最长30个字符。
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareInfo.getTitle());
        //可选	String	分享的消息摘要，最长40个字。
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.getSumy());
        //必填	String	这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareInfo.getUrl());
        //可选	String	分享图片的URL或者本地路径
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareInfo.getImgSrc());
        //可选	String	手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "ACE");
        //可选	Int	分享额外选项，两种类型可选(默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框):
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);

        ThirdPartyAPI.mTencent.shareToQQ(activity, params, iUiListener);
    }

    /**
     * 分享纯图片
     */
    private void shareImage() {

        Bundle params = new Bundle();
        //必选	String	需要分享的本地图片路径。
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareInfo.getImgSrc());
        //可选	String	手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替。
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "ACE");
        //QQShare.SHARE_TO_QQ_KEY_TYPE	必选	Int	分享类型，分享纯图片时填写QQShare.SHARE_TO_QQ_TYPE_IMAGE
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        //可选	Int	分享额外选项，两种类型可选(默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框):
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        ThirdPartyAPI.mTencent.shareToQQ(activity, params, iUiListener);
    }

    /**
     * 分享音乐
     */
    private void shareMusic() {
        Bundle params = new Bundle();
        //必填	Int	分享的类型。分享音乐填Tencent.SHARE_TO_QQ_TYPE_AUDIO。
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
        //必填	String	分享的标题, 最长30个字符。
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareInfo.getTitle());
        //可选	String	分享的消息摘要，最长40个字符。
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.getSumy());
        //必填	String	这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareInfo.getUrl());
        //可选	String	分享图片的URL或者本地路径。
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareInfo.getImgSrc());
        //必填	String	音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐。
        params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, shareInfo.musicUrl);
        //可选	String	手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替。
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "ACE");
        //可选	Int	分享额外选项，两种类型可选(默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框):
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        ThirdPartyAPI.mTencent.shareToQQ(activity, params, iUiListener);
    }

    /**
     * 分享APP
     */
    private void shareAPP() {
        Bundle params = new Bundle();
        //必填	Int	分享的类型。分享APP填Tencent.SHARE_TO_QQ_TYPE_APP。
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
        //必填	String	分享的标题, 最长30个字符。
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareInfo.getTitle());
        //可选	String	分享的消息摘要，最长40个字符。
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.getSumy());
        //可选	String	分享图片的URL或者本地路径。
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareInfo.getImgSrc());
        //可选	String	手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替。
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "ACE");
        //可选	Int	分享额外选项，两种类型可选(默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框):
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        ThirdPartyAPI.mTencent.shareToQQ(activity, params, iUiListener);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone() {
        Bundle params = new Bundle();
        //选填	Int	SHARE_TO_QZONE_TYPE_IMAGE_TEXT（图文）
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        //必填	String	分享的标题，最多200个字符。
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareInfo.getTitle());
        //选填	String	分享的摘要，最多600字符。
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareInfo.getSumy());
        //必填	String	需要跳转的链接，URL字符串。
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareInfo.getUrl());
        //选填	String	分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）。
        //QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持。
        ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add(shareInfo.getImgSrc());
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        ThirdPartyAPI.mTencent.shareToQzone(activity, params, iUiListener);
    }

    IUiListener iUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            shareResultCallback.shareComplet();
        }

        @Override
        public void onError(UiError uiError) {
            shareResultCallback.shareError();
        }

        @Override
        public void onCancel() {
            shareResultCallback.shareCancle();
        }
    };
}
