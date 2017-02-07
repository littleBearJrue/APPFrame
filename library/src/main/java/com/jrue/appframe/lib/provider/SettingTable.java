package com.jrue.appframe.lib.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


/**
 * 数据库demo展示(保存设置信息内容)
 * Created by jrue on 17/2/7.
 */
public final class SettingTable extends BaseSQLiteTable {
    private static final String TAG = "SettingTable";

    public static final String TABLE_NAME = "SettingTable";

    public SettingTable(Context context) {
        super(context, TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + Columns._ID + " INTEGER PRIMARY KEY,"
                + Columns.KEY + " TEXT,"
                + Columns.VALUE + " TEXT"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public interface Columns extends BaseColumns {
        /**
         * 键
         * <P>Type: TEXT</P>
         */
        String KEY = "key";

        /**
         * 值
         * <P>Type: TEXT</P>
         */
        String VALUE = "value";
    }
}
