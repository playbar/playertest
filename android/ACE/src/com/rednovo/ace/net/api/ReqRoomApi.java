/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.net.api;

import android.app.Activity;
import android.text.TextUtils;

import com.rednovo.ace.net.parser.AudienceResult;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.parser.BeginLiveResult;
import com.rednovo.ace.net.parser.GiftListResult;
import com.rednovo.ace.net.parser.HallSubscribeResult;
import com.rednovo.ace.net.parser.HotResult;
import com.rednovo.ace.net.request.HttpBase;
import com.rednovo.ace.net.request.HttpMethodGet;
import com.rednovo.ace.net.request.HttpMethodPost;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.libs.net.okhttp.callback.Callback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * -- responseType: 1 常规请求,返回文本内容 responseType: 2 流请求,返回文件流 responseType: 房间操作的请求
 *
 * @author Zhen.Li
 * @fileNmae HttpRquestRoom
 * @since 2016-03-04
 */
public final class ReqRoomApi {

    private static final String URL_SUFFIX = "service/";
    private static final String URL_SUFFIX_S = "services/";
    private static final String KEY_PAGE = "page";
    private static final String KEY_PAGESIZE = "pageSize";
    private static final String KEY_USERID = "userId";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSITION = "position";
    private static final String KEY_SHOWIMG = "showImg";
    private static final String KEY_SHOWID = "showId";

    private static final String KEY_GIFTCNT = "giftCnt";
    private static final String KEY_GIFTID = "giftId";
    private static final String KEY_RECEIVERID = "receiverId";
    private static final String KEY_SENDERID = "senderId";

    private static final String KEY_WS_URL = "WS_URL";
    private static final String KEY_WS_RETIP_NUM = "WS_RETIP_NUM";
    private static final String KEY_WS_URL_TYPE = "WS_URL_TYPE";
    private static final String KEY_UIP = "UIP";


    /**
     * 请求热门直播列表
     *
     * @param page     当前页
     * @param pageSize 每页的数量
     * @param callback 回调数据
     */
    public static HttpBase<HotResult> reqLiveList(Activity activity, String page, String pageSize, RequestCallback<HotResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_PAGE, page);
        params.put(KEY_PAGESIZE, pageSize);
        return new HttpMethodPost<HotResult>(HotResult.class)
                .addUrlArgument(ReqConfig.getUrl() + URL_SUFFIX + ReqConfig.KEY_LIVE_LIST)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 创建直播
     *
     * @param activity
     * @param userId
     * @param title
     * @param position
     * @param fileName
     * @param filePath
     * @param requestCallback
     * @return
     */
    public static HttpBase<BeginLiveResult> reqCreateLive(Activity activity, String userId, String title, String position, String fileName, String filePath, RequestCallback<BeginLiveResult> requestCallback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USERID, userId);
        params.put(KEY_TITLE, title);
        params.put(KEY_POSITION, position);
        HttpBase<BeginLiveResult> httpBase = new HttpMethodPost<BeginLiveResult>(BeginLiveResult.class).addUrlArgument(ReqConfig.getUrl() + URL_SUFFIX + ReqConfig.KEY_BEGIN_LIVE).addArguments(params).addTag(activity);
        if (!TextUtils.isEmpty(filePath)) {
            httpBase.connTimeOut(10000);
            httpBase.readTimeOut(10000);
            httpBase.writeTimeOut(10000);
            httpBase.addFile(KEY_SHOWIMG, fileName, new File(filePath));
        }

        httpBase.execute(requestCallback);
        return httpBase;
    }

    /**
     * 请求房间观众列表
     *
     * @param activity
     * @param showId
     * @param page
     * @param pageSize
     * @param requestCallback
     * @return
     */
    public static HttpBase<AudienceResult> reqAudienceList(Object activity, String showId, String page, String pageSize, RequestCallback<AudienceResult> requestCallback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_SHOWID, showId);
        params.put(KEY_PAGE, page);
        params.put(KEY_PAGESIZE, pageSize);
        return new HttpMethodPost<AudienceResult>(AudienceResult.class).addUrlArgument(ReqConfig.getUrl() + URL_SUFFIX + ReqConfig.KEY_AUDIENCE_LIVE).addArguments(params).addTag(activity).execute(requestCallback);
    }

    /**
     * 赠送礼物
     *
     * @param activity
     * @param showId
     * @param giftCnt
     * @param giftId
     * @param receiverId
     * @param senderId
     * @param requestCallback
     * @return
     */
    public static HttpBase<BaseResult> reqSendGif(Activity activity, String showId, String giftCnt, String giftId, String receiverId, String senderId, RequestCallback<BaseResult> requestCallback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_SHOWID, showId);
        params.put(KEY_GIFTCNT, giftCnt);
        params.put(KEY_GIFTID, giftId);
        params.put(KEY_RECEIVERID, receiverId);
        params.put(KEY_SENDERID, senderId);
        return new HttpMethodPost<BaseResult>(BaseResult.class).addUrlArgument(ReqConfig.getUrl() + URL_SUFFIX + ReqConfig.KEY_SEND_GIF).addArguments(params).addTag(activity).execute(requestCallback);
    }

    /**
     * 获取礼物列表
     *
     * @param
     * @param requestCallback
     * @return
     */
    public static HttpBase<GiftListResult> reqGiftList(RequestCallback<GiftListResult> requestCallback) {
        Map<String, String> params = new HashMap<String, String>();
        return new HttpMethodPost<GiftListResult>(GiftListResult.class).addUrlArgument(ReqConfig.getUrl() + URL_SUFFIX + ReqConfig.KEY_GIFT_LIST).addArguments(params).execute(requestCallback);
    }


    public static HttpBase<HallSubscribeResult> reqHallSubscribeList(Object object, String userId, RequestCallback<HallSubscribeResult> requestCallback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);

        return new HttpMethodPost<HallSubscribeResult>(HallSubscribeResult.class)
                .addUrlArgument(ReqConfig.getUrl() + URL_SUFFIX + ReqConfig.KEY_HALL_SUBSCRIBE)
                .addArguments(params)
                .addTag(object)
                .execute(requestCallback);
    }


    /**
     * 值为 1 时,返回 IP:1.1.1.1
     * 值为 2 时,返回格式为:http://1.1.1.1/www.wstest.com/live/channel?wsiphost=ipdb
     * 值为 3 时,返回格式为:rtmp://1.1.1.1/live/channel?wsiphost=ip db&wsHost=www.wstest.com
     *
     * @param wsUrl           WS_URL 必须为完成的推拉流 url,也即需要带上 完整的域名/发布点/流名
     * @param obj
     * @param requestCallback
     * @return
     */
    public static HttpBase<BaseResult> reqNGBUrl(String wsUrl, Object obj, Callback requestCallback) {
        return new HttpMethodGet<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getNgbUrl())
                .addHeader(KEY_WS_URL, wsUrl)
                .addHeader(KEY_WS_RETIP_NUM, "1")
                .addHeader(KEY_WS_URL_TYPE, "3")
                .addTag(obj)
                .execute(requestCallback);

    }
}
