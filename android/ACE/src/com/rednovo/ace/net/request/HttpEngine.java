/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.net.request;

import com.rednovo.libs.net.okhttp.OkHttpEngine;

/**
 * @author Zhen.Li
 * @fileNmae HttpEngine
 * @since 2016-03-04
 */
public class HttpEngine {


    /**
     * 取消当前Activity的请求
     *
     * @param obj
     */
    public static void cancelTag(Object obj) {
        OkHttpEngine.getInstance().cancelTag(obj);
    }
}
