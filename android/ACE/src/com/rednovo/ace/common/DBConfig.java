package com.rednovo.ace.common;

/**
 * Created by Dk on 16/3/15.
 */
public class DBConfig {

//版本2:增加了signature字段

    /**
     * 数据库版本号
     **/
    public final static int DATABASE_VERSION = 2;
    /**
     * 数据库名
     **/
    public final static String DATABASE_NAME = "aceData";
    /**
     * 浏览历史表名
     */
    public static final String BROWSING_HISTORY_TABLE = "browsing_history_table";

    public static final String BROWSING_HISTORY_TABLE_USER_ID_KEY = "user_id";
    public static final String BROWSING_HISTORY_TABLE_STAR_ID_KEY = "star_id";
    public static final String BROWSING_HISTORY_TABLE_STAR_NAME_KEY = "star_name";
    public static final String BROWSING_HISTORY_TABLE_STAR_IMG_URL_KEY = "star_img_url";
    public static final String BROWSING_HISTORY_TABLE_STAR_LEVEL = "star_level";
    public static final String BROWSING_HISTORY_TABLE_STAR_SIGNATURE = "signature";
}
