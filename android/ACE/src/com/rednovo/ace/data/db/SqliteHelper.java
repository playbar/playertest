/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rednovo.ace.common.DBConfig;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.cell.LiveInfo;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhen.Li
 * @fileNmae SqliteHelper
 * @since 2016-03-02
 */
public class SqliteHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "SqliteHelper";

    private static SqliteHelper mSqliteHelper = null;

//    private static Context mContext = BaseApplication.getApplication().getApplicationContext();

    private static SQLiteDatabase database;

    public static synchronized SqliteHelper getInstance() {
        if (mSqliteHelper == null) {
            mSqliteHelper = new SqliteHelper();
        }
        return mSqliteHelper;
    }

    public SqliteHelper() {
        super(BaseApplication.getApplication().getApplicationContext(), DBConfig.DATABASE_NAME, null, DBConfig.DATABASE_VERSION);
        database = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.v(LOG_TAG, "onCreate");
        createHisttoryTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            updateTable(db, DBConfig.BROWSING_HISTORY_TABLE);
        }
    }

    /**
     * 创建浏览历史表
     * stat_id、user_id为联合主键, 字段有 star_id、user_id、star_name、star_img_url
     */
    private void createHisttoryTable(SQLiteDatabase db) {
        String str = "create table if not exists " + DBConfig.BROWSING_HISTORY_TABLE
                + " (" + DBConfig.BROWSING_HISTORY_TABLE_USER_ID_KEY + " integer, "
                + DBConfig.BROWSING_HISTORY_TABLE_STAR_ID_KEY + " Integer,"
                + DBConfig.BROWSING_HISTORY_TABLE_STAR_NAME_KEY + " varchar(200), "
                + DBConfig.BROWSING_HISTORY_TABLE_STAR_IMG_URL_KEY + " varchar(500), "
                + DBConfig.BROWSING_HISTORY_TABLE_STAR_LEVEL + " varchar(3), "
                + DBConfig.BROWSING_HISTORY_TABLE_STAR_SIGNATURE + " varchar(50), "
                + "primary key ("
                + DBConfig.BROWSING_HISTORY_TABLE_USER_ID_KEY + ","
                + DBConfig.BROWSING_HISTORY_TABLE_STAR_ID_KEY + ") ON CONFLICT REPLACE );";
        db.execSQL(str);
    }

    /**
     * 向浏览历史表增加数据
     */
    public synchronized void insertHistory(LiveInfo info) {
        if (UserInfoUtils.isAlreadyLogin()) {
            String user_id = UserInfoUtils.getUserInfo().getUserId();
            String star_id = info.getStarId();
            String star_name = info.getNickName();
            String star_profile = info.getProfile();
            String star_level = info.getRank();
            String signature = info.getSignature();
            String sex = "";

            ContentValues cv = new ContentValues();
            cv.put(DBConfig.BROWSING_HISTORY_TABLE_USER_ID_KEY, user_id);
            cv.put(DBConfig.BROWSING_HISTORY_TABLE_STAR_ID_KEY, star_id);
            cv.put(DBConfig.BROWSING_HISTORY_TABLE_STAR_NAME_KEY, star_name);
            cv.put(DBConfig.BROWSING_HISTORY_TABLE_STAR_IMG_URL_KEY, star_profile);
            cv.put(DBConfig.BROWSING_HISTORY_TABLE_STAR_LEVEL, star_level);
            cv.put(DBConfig.BROWSING_HISTORY_TABLE_STAR_SIGNATURE, signature);
            database.insert(DBConfig.BROWSING_HISTORY_TABLE, null, cv);
        }
    }

    /**
     * 删除某位用户的浏览记录
     *
     * @param userId 用户ID
     */
    public synchronized void deleteHistoryForUserId(String userId) {
        String whereClause = DBConfig.BROWSING_HISTORY_TABLE_USER_ID_KEY + "=?";
        String[] whereArgs = {userId};
        database.delete(DBConfig.BROWSING_HISTORY_TABLE, whereClause, whereArgs);
    }

    /**
     * 删除某位用户下的某条记录
     *
     * @param userId 用户ID
     * @param starId 主播ID
     */
    public synchronized void deleteHistoryForStarId(String userId, String starId) {
        String whereClause = DBConfig.BROWSING_HISTORY_TABLE_USER_ID_KEY
                + "=? And " + DBConfig.BROWSING_HISTORY_TABLE_USER_ID_KEY + "=?";
        String[] whereArgs = {userId, starId};
        database.delete(DBConfig.BROWSING_HISTORY_TABLE, whereClause, whereArgs);
    }

    /**
     * 获取历史记录
     *
     * @param userId
     * @return
     */
    public List<LiveInfo> getHistory(String userId) {
        List<LiveInfo> list = new ArrayList<LiveInfo>();
        String[] colums = {
                DBConfig.BROWSING_HISTORY_TABLE_USER_ID_KEY,
                DBConfig.BROWSING_HISTORY_TABLE_STAR_ID_KEY,
                DBConfig.BROWSING_HISTORY_TABLE_STAR_NAME_KEY,
                DBConfig.BROWSING_HISTORY_TABLE_STAR_IMG_URL_KEY,
                DBConfig.BROWSING_HISTORY_TABLE_STAR_LEVEL,
                DBConfig.BROWSING_HISTORY_TABLE_STAR_SIGNATURE};
        String selection = DBConfig.BROWSING_HISTORY_TABLE_USER_ID_KEY + "=?";
        String[] selectionArgs = {userId};
        Cursor cursor = database.query(DBConfig.BROWSING_HISTORY_TABLE, colums, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                LiveInfo info = new LiveInfo();
                info.setStarId(cursor.getString(cursor.getColumnIndex(DBConfig.BROWSING_HISTORY_TABLE_STAR_ID_KEY)));
                info.setNickName(cursor.getString(cursor.getColumnIndex(DBConfig.BROWSING_HISTORY_TABLE_STAR_NAME_KEY)));
                info.setProfile(cursor.getString(cursor.getColumnIndex(DBConfig.BROWSING_HISTORY_TABLE_STAR_IMG_URL_KEY)));
                info.setRank(cursor.getString(cursor.getColumnIndex(DBConfig.BROWSING_HISTORY_TABLE_STAR_LEVEL)));
                info.setSignature(cursor.getString(cursor.getColumnIndex(DBConfig.BROWSING_HISTORY_TABLE_STAR_SIGNATURE)));
                list.add(info);
                cursor.moveToNext();
            }
        }
        return list;
    }

    private void updateTable(SQLiteDatabase db, String tableName) {
        try {
            db.beginTransaction();
            String tempTable = tableName + "texp_temptable";
            String sql = "alter table " + tableName + " rename to " + tempTable;
            db.execSQL(sql);
            String dropString = "drop table if exists " + tableName;
            db.execSQL(dropString);
            createHisttoryTable(db);
            String ins = "insert into " + tableName + " select from " + tempTable;
            db.execSQL(ins);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}
