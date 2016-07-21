package com.rednovo.ace.common;

import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.SPUtils;
import com.rednovo.libs.common.SharedPreferenceKey;

/**
 * Created by lizhen on 16/3/23.
 */
public class LiveInfoUtils {

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    private static void put(String key, Object object) {
        SPUtils.put(BaseApplication.getApplication().getApplicationContext(), key, object);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    private static Object get(String key, Object defaultObject) {
        return SPUtils.get(BaseApplication.getApplication().getApplicationContext(), key, defaultObject);
    }

    public static void remove(String key) {
        SPUtils.remove(BaseApplication.getApplication().getApplicationContext(), key);
    }

    public static String getStartId() {
        return (String) get(SharedPreferenceKey.KEY_START_ID, "");
    }


    public static void putStartId(String startId) {
        put(SharedPreferenceKey.KEY_START_ID, startId);
    }

    public static void putShowId(String startId) {
        put(SharedPreferenceKey.KEY_SHOW_ID, startId);
    }

    public static String getShowId() {
        return (String) get(SharedPreferenceKey.KEY_SHOW_ID, "");
    }

    public static void putNickName(String nickName) {
        put(SharedPreferenceKey.KEY_NICK_NAME, nickName);
    }

    public static String getNickName() {
        return (String) get(SharedPreferenceKey.KEY_NICK_NAME, "");
    }

    public static void putProFile(String proFile) {
        put(SharedPreferenceKey.KEY_PRO_FILE, proFile);
    }

    public static String getProFile() {
        return (String) get(SharedPreferenceKey.KEY_PRO_FILE, "");
    }

    public static void putAudienceCnt(String audienceCnt) {
        put(SharedPreferenceKey.KEY_AUDIENCE_CNT, audienceCnt);
    }

    public static String getAudienceCnt() {
        return (String) get(SharedPreferenceKey.KEY_AUDIENCE_CNT, "");
    }

    public static String getShowImg() {
        return (String) get(SharedPreferenceKey.KEY_SHOW_IMG, "");
    }

    public static void putShowImg(String showImg) {
        put(SharedPreferenceKey.KEY_SHOW_IMG, showImg);
    }

    public static void clearLiveInfo() {
        remove(SharedPreferenceKey.KEY_START_ID);
        remove(SharedPreferenceKey.KEY_SHOW_ID);
        remove(SharedPreferenceKey.KEY_NICK_NAME);
        remove(SharedPreferenceKey.KEY_PRO_FILE);
        remove(SharedPreferenceKey.KEY_AUDIENCE_CNT);
        remove(SharedPreferenceKey.KEY_SHOW_IMG);
    }

    /**
     * 是否在直播
     * @param isShow
     */
    public static void putIsShow(boolean isShow){
        put(SharedPreferenceKey.KEY_IS_SHOW,isShow);
    }
    public static boolean getIsShow(){
        if(SPUtils.contains(BaseApplication.getApplication().getApplicationContext(),SharedPreferenceKey.KEY_IS_SHOW)){
            return (Boolean)get(SharedPreferenceKey.KEY_IS_SHOW,false);
        }else{
            return false;
        }
    }

}
