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

import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.parser.MyFansListResult;
import com.rednovo.ace.net.parser.MySubscribeListResult;
import com.rednovo.ace.net.parser.SearchResult;
import com.rednovo.ace.net.request.HttpBase;
import com.rednovo.ace.net.request.HttpMethodPost;
import com.rednovo.ace.net.request.RequestCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 关系相关的请求
 *
 * @author Zhen.Li
 * @fileNmae HttpReqRelation
 * @since 2016-03-04
 */
public class ReqRelationApi {

    private static final String url_suffix = "service/";

    private static final String KEY_USER_ID = "userId";
    private static final String KEY_STAR_ID = "starId";
    private static final String KEY_SHOW_ID = "showId";
    private static final String KEY_CONTACK_INFO = "contactInfo";
    private static final String KEY_PAGE = "page";
    private static final String KEY_PAGE_SIZE = "pageSize";

    /**
     * 请求订阅列表
     *
     * @param activity
     * @param userId
     * @param page
     * @param pageSize
     * @param callback
     * @return
     */
    public static HttpBase reqSubscribeList(Activity activity, String userId, String page, String pageSize, RequestCallback<MySubscribeListResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_PAGE, page);
        params.put(KEY_PAGE_SIZE, pageSize);
        return new HttpMethodPost<MySubscribeListResult>(MySubscribeListResult.class)
                .addUrlArgument(ReqConfig.getUrl() + url_suffix + ReqConfig.KEY_SUBSCRIBE_LIST)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 请求粉丝列表
     *
     * @param activity
     * @param userId
     * @param page
     * @param pageSize
     * @param callback
     * @return
     */
    public static HttpBase reqFansList(Activity activity, String userId, String page, String pageSize, RequestCallback<MyFansListResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_PAGE, page);
        params.put(KEY_PAGE_SIZE, pageSize);
        return new HttpMethodPost<MyFansListResult>(MyFansListResult.class)
                .addUrlArgument(ReqConfig.getUrl() + url_suffix + ReqConfig.KEY_FANS_LIST)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 请求订阅
     *
     * @param activity
     * @param userId
     * @param anchorId
     * @param callback
     * @return
     */
    public static HttpBase reqSubscibe(Object activity, String userId, String anchorId, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("starId", anchorId);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + url_suffix + ReqConfig.KEY_SUBSCRIBE)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 取消订阅
     *
     * @param object
     * @param userId
     * @param anchorId
     * @param callback
     * @return
     */
    public static HttpBase reqCancelSubscibe(Object object, String userId, String anchorId, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("starId", anchorId);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + url_suffix + ReqConfig.KEY_CANCEL_SUBSCRIBE)
                .addArguments(params)
                .addTag(object)
                .execute(callback);
    }

    /**
     * 举报
     *
     * @param activity
     * @param userId
     * @param anchorId
     * @param callback
     * @return
     */
    public static HttpBase reqReport(Activity activity, String userId, String anchorId, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("starId", anchorId);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + url_suffix + ReqConfig.KEY_REPORT)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 禁言
     *
     * @param activity
     * @param showId
     * @param userId
     * @param callback
     * @return
     */
    public static HttpBase reqShutup(Activity activity, String showId, String userId, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_SHOW_ID, showId);
        params.put(KEY_USER_ID, userId);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + url_suffix + ReqConfig.KEY_SHUP_UP)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    /**
     * 意见反馈
     *
     * @param activity
     * @param userId
     * @param suggestion
     * @param contactWay
     * @param callback
     * @return
     */
    public static HttpBase reqSuggestion(Activity activity, String userId, String suggestion, String contactWay, RequestCallback<BaseResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KEY_USER_ID, userId);
        params.put(KEY_STAR_ID, suggestion);
        params.put(KEY_CONTACK_INFO, contactWay);
        return new HttpMethodPost<BaseResult>(BaseResult.class)
                .addUrlArgument(ReqConfig.getUrl() + url_suffix + ReqConfig.KEY_SUGGEST)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

    public static HttpBase reqSearchResult(Activity activity, String searcheTxt, int page, int pageSize, RequestCallback<SearchResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", searcheTxt);
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");

        return new HttpMethodPost<SearchResult>(SearchResult.class)
                .addUrlArgument(ReqConfig.getUrl() + url_suffix + ReqConfig.KEY_SEARCH)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }
}
