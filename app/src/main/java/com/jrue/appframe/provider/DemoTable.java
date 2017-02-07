package com.jrue.appframe.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.jrue.appframe.lib.provider.BaseSQLiteTable;

/**
 * 保存数据库演示（保存对象类型）
 * Created by jrue on 17/2/7.
 */
public class DemoTable extends BaseSQLiteTable{
    private static final String TAG = "DemoTable";

    public static final String TABLE_NAME = "demo_table";

    public static final String DEFAULT_SORT_ORDER = Columns.TIME + " DESC";

    /* package */ DemoTable(Context context) {
        super(context, TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + Columns._ID + " INTEGER PRIMARY KEY,"
                + Columns.DEVICE_ID + " TEXT,"
                + Columns.TIME + " LONG,"
                + Columns.MAC + " TEXT,"
                + Columns.TYPE + " INTEGER,"
                + Columns.SWITCH + " INTEGER,"
                + Columns.NAME + " TEXT"
                + ");");
    }

    @Override
    protected String defaultSortOrder() {
        return DEFAULT_SORT_ORDER;
    }

    public interface Columns extends BaseColumns {
        /**
         * 名字
         * <P>Type: TEXT</P>
         */
        String NAME = "name";
        /**
         * 设备标识
         * <P>Type: TEXT</P>
         */
        String DEVICE_ID = "device_id";
        /**
         * mac地址
         * <P>Type: TEXT</P>
         */
        String MAC = "mac";
        /**
         * 设备类型
         * <P>Type: INTEGER</P>
         */
        String TYPE = "type";
        /**
         * 设备状态
         * <P>Type: INTEGER</P>
         */
        String SWITCH = "switch";
        /**
         * 操作时间
         * <P>Type: LONG</P>
         */
        String TIME = "time";
    }
}
