/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.common;

import com.rednovo.libs.common.StringUtils;

import org.json.JSONObject;

import java.io.InputStream;

/**
 * @author Zhen.Li
 * @fileNmae JsonUtils
 * @since 2016-03-05
 */
public class JsonUtils {
//    private static Gson mGson = new GsonBuilder().create();
//
//    /**
//     * 获取GSON
//     *
//     * @return GSON实例
//     */
//    public static Gson gsonInstance() {
//        return mGson;
//    }
//
//    /**
//     * 从json字符串构造 clazz 的实例
//     *
//     * @param jsonString json字符串
//     * @param clazz      目标转换对象的class类型
//     * @param <T>        转换完成的类型实例
//     * @return <p>json字符串解析成功，返回SplashScreenItemsResult</p>
//     * <p>json字符串解析失败时，返回null</p>
//     */
//    public static <T> T fromJsonString(String jsonString, Class<T> clazz) {
//        try {
//            return gsonInstance().fromJson(jsonString, clazz);
//        } catch (com.google.gson.JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    /**
//     * 从对象转换为json字符串
//     *
//     * @param object 对象实例
//     * @return json字符串
//     */
//    public static String toJsonString(Object object) {
//        try {
//            return gsonInstance().toJson(object);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    /**
     * InputSteam 转换到 JSONObject
     *
     * @param inputStream 输入流
     * @return JSONObject
     */
    public static JSONObject jsonObjectFromInputStream(InputStream inputStream) {
        try {
            return new JSONObject(StringUtils.stringFromInputStream(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从对象转换为json数组字符串
     *
     * @param object 对象实例
     * @return json数组字符串
     */
    public static String toJsonArrayString(Object object) {
        String joinString = StringUtils.join(",", object);
        StringBuilder stringBuilder = new StringBuilder(joinString.length() + 2);
        return stringBuilder.append('[').append(joinString).append(']').toString();
    }
}
