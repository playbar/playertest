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
import com.rednovo.ace.net.request.RequestCallback;

/**
 *
 */
public class ReqUtils {
    /**
     * 请求取消订阅
     * @param activity
     * @param userId
     * @param otherId
     */
    public static void reqCancelSubscibe(Object activity,String userId,String otherId){
        ReqRelationApi.reqCancelSubscibe(activity,userId,otherId, new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
            }

            @Override
            public void onRequestFailure(BaseResult error) {
            }
        });

    }

    /**
     * 请求订阅
     * @param activity
     * @param userId
     * @param otherId
     */
    public static void reqSubscibe(Object activity,String userId,String otherId){
        ReqRelationApi.reqSubscibe(activity,userId,otherId, new RequestCallback<BaseResult>() {
            @Override
            public void onRequestSuccess(BaseResult object) {
            }

            @Override
            public void onRequestFailure(BaseResult error) {
            }
        });

    }


}
