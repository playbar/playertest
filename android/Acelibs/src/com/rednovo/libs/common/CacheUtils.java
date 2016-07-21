package com.rednovo.libs.common;

import android.content.Context;

import com.rednovo.libs.cache.ObjectCache;


/**
 * 缓存相关
 *
 * @author ll
 * @version 1.0.0
 */
public class CacheUtils {

    private static ObjectCache sObjectCache;

    /**
     * 初始化缓存
     *
     * @param context context
     */
    public static void init(Context context) {
        try {
            sObjectCache = ObjectCache.open(Constant.OBJECT_CACHE_MEM_PERCENT, StorageUtils.getDateCacheFolderPath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("CacheUtils init error !");
        }
    }


    /**
     * 获取对象缓存实例
     *
     * @return 图片缓存实例
     */
    public static ObjectCache getObjectCache() {
        return sObjectCache;
    }

    /**
     * close图片缓存
     */
    public static void close() {
        if (sObjectCache != null) {
            sObjectCache.close();
        }
    }
}
