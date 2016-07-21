package com.rednovo.ace.common;

import android.text.TextUtils;

import com.rednovo.ace.net.parser.SystemResult;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.CacheKey;
import com.rednovo.libs.common.CacheUtils;
import com.rednovo.libs.common.SPUtils;
import com.rednovo.libs.common.SharedPreferenceKey;
import com.rednovo.libs.common.Utils;

import java.util.Map;

/**
 * 缓存当前的登陆用户信息
 *
 * @author Xd/2015年4月21日
 */
public class CacheUserInfoUtils {
    public static String USERID = "userId";
    public static String NICKNAME = "nickName";
    public static String SEX = "sex";
    public static String GIFT_VERSION = "gift_version";


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public static void put(String key, Object object) {
        SPUtils.put(BaseApplication.getApplication().getApplicationContext(), key, object);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(String key, Object defaultObject) {
        return SPUtils.get(BaseApplication.getApplication().getApplicationContext(), key, defaultObject);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public static void remove(String key) {
        SPUtils.remove(BaseApplication.getApplication().getApplicationContext(), key);
    }

    /**
     * 清除所有数据
     */
    public static void clear() {
        SPUtils.clear(BaseApplication.getApplication().getApplicationContext());
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        return SPUtils.contains(BaseApplication.getApplication().getApplicationContext(), key);
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public static Map<String, ?> getAll() {
        return SPUtils.getAll(BaseApplication.getApplication().getApplicationContext());
    }

    /**
     * @author Zhen.Li
     * @since 16/5/13 下午5:14
     */
    public static boolean isVersionChanged() {
        String oldVersion = (String) get(SharedPreferenceKey.KEY_OLD_APP_VERSION, "");
        if (TextUtils.isEmpty(oldVersion)) {
            return true;
        }
        return !oldVersion.equals(Utils.getAPPVersionCode());
    }

    public static void addVersionCode() {

        put(SharedPreferenceKey.KEY_OLD_APP_VERSION, Utils.getAPPVersionCode());
    }


    public static void putBootPic(String bootPic) {
        if (bootPic != null) {
            put(SharedPreferenceKey.KEY_BOOT_PIC, bootPic);
        }
    }

    public static void putShowTips(String showTips) {
        if (showTips != null) {
            put(SharedPreferenceKey.KEY_SHOW_TIPS, showTips);
        }
    }

    /**
     * 获取启动页url
     *
     * @return
     */
    public static String getBootPic() {
        return (String) get(SharedPreferenceKey.KEY_BOOT_PIC, "");
    }

    /**
     * 获取直播间提示信息
     *
     * @return
     */
    public static String getShowTips() {
        return (String) get(SharedPreferenceKey.KEY_SHOW_TIPS, "");
    }

    /**
     * 获取总控参数对象
     *
     * @return
     */
    public static SystemResult getSystemVersion() {
        if (CacheUtils.getObjectCache().contain(CacheKey.KEY_SYSTEM)) {
            return (SystemResult) CacheUtils.getObjectCache().get(CacheKey.KEY_SYSTEM);
        }
        return null;
    }

    /**
     * 开播实名认证
     * 1、需要认证，0不需要认证
     *
     * @return
     */
    public static boolean isLiveAuthentication() {
        SystemResult result = getSystemVersion();
        if (result != null) {
            int nVerify = result.getIsVerify();
            if (nVerify == 1) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
