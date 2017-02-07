package com.jrue.appframe.lib.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.jrue.appframe.lib.base.BaseApp;
import com.jrue.appframe.lib.util.MLog;

/**
 * 数据库表基类，使用Provider进行访问
 * Created by jrue on 17/2/7.
 */
public abstract class BaseSQLiteTable {
    private static final String TAG = "BaseSQLiteTable";

    private static final boolean DEBUG = false;

    private final String mTableName;

    private SQLiteOpenHelper mOpenHelper;

    protected BaseSQLiteTable(Context context, String tableName) {
        mTableName = tableName;
    }

    public static String getString(Cursor cursor, String key, String defaultString) {
        int index;
        if (cursor == null || (index = cursor.getColumnIndex(key)) < 0) {
            return defaultString;
        }
        return cursor.getString(index);
    }

    public static int getInt(Cursor cursor, String key, int defaultInt) {
        int index;
        if (cursor == null || (index = cursor.getColumnIndex(key)) < 0) {
            return defaultInt;
        }
        return cursor.getInt(index);
    }

    public static long getLong(Cursor cursor, String key, long defaultLong) {
        int index;
        if (cursor == null || (index = cursor.getColumnIndex(key)) < 0) {
            return defaultLong;
        }
        return cursor.getLong(index);
    }

    public abstract void onCreate(SQLiteDatabase db);

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DEBUG) MLog.zlx.w(TAG, "Upgrading Database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + mTableName);
        onCreate(db);
    }

    /* package */ void onBind(SQLiteOpenHelper helper) {
        if (mOpenHelper != null || helper == null) {
            MLog.out.e(TAG, "setOpenHelper: old=" + mOpenHelper + " new=" + helper);
        }
        mOpenHelper = helper;
    }

    /**
     * 获取数据库表的名称
     */
    public final String getName() {
        return mTableName;
    }

    /**
     * 返回数据默认的排列顺序
     */
    protected String defaultSortOrder() {
        if (DEBUG) MLog.out.d(TAG, "defaultSortOrder: null");
        return null;
    }

    /**
     * Perform a query by combining all current settings and the
     * information passed into this method.
     *
     * @param projection    A list of which columns to return. Passing
     *                      null will return all columns, which is discouraged to prevent
     *                      reading data from storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return,
     *                      formatted as an SQL WHERE clause (excluding the WHERE
     *                      itself). Passing null will return all rows for the given URL.
     * @param selectionArgs You may include ?s in selection, which
     *                      will be replaced by the values from selectionArgs, in order
     *                      that they appear in the selection. The values will be bound
     *                      as Strings.
     * @param sortOrder     How to order the rows, formatted as an SQL
     *                      ORDER BY clause (excluding the ORDER BY itself). Passing null
     *                      will use the default sort order, which may be unordered.
     * @return a cursor over the result set
     * @see android.content.ContentResolver#query(android.net.Uri, String[],
     * String, String[], String)
     */
    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = defaultSortOrder();
        }
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(mTableName);
        return qb.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    /**
     * Convenience method for inserting a row into the database.
     *
     * @param values this map contains the initial column values for the
     *               row. The keys should be the column names and the values the
     *               column values
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insert(ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.insert(mTableName, null, values);
    }

    /**
     * Convenience method for deleting rows in the database.
     *
     * @param where     the optional WHERE clause to apply when deleting.
     *                  Passing null will delete all rows.
     * @param whereArgs You may include ?s in the where clause, which
     *                  will be replaced by the values from whereArgs. The values
     *                  will be bound as Strings.
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise. To remove all rows and get a count pass "1" as the
     * whereClause.
     */
    public int delete(String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.delete(mTableName, where, whereArgs);
    }

    /**
     * Convenience method for updating rows in the database.
     *
     * @param values    a map from column names to new column values. null is a
     *                  valid value that will be translated to NULL.
     * @param where     the optional WHERE clause to apply when updating.
     *                  Passing null will update all rows.
     * @param whereArgs You may include ?s in the where clause, which
     *                  will be replaced by the values from whereArgs. The values
     *                  will be bound as Strings.
     * @return the number of rows affected
     */
    public int update(ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.update(mTableName, values, where, whereArgs);
    }

    public static Uri makeContentUri(String tableName) {
        return Uri.parse("content://" + BaseApp.getInstance().getPackageName() + ".provider/" + tableName);
    }
}
