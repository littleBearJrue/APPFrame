package com.jrue.appframe.lib.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.google.common.collect.ImmutableList;
import com.jrue.appframe.lib.util.MLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据访问的基类
 * Created by jrue on 17/2/7.
 */
public class BaseSQLiteProvider extends ContentProvider {
    private static final String TAG = "BaseSQLiteProvider";

    private static final boolean DEBUG = false;

    private final String mName;

    private final int mVersion;

    private MySQLiteHelper mHelper;

    private List<BaseSQLiteTable> mTableList;

    public BaseSQLiteProvider(String name, int version) {
        mName = name;
        mVersion = version;
        mTableList = new ArrayList<>();
    }

    @Override
    public boolean onCreate() {
        mTableList = ImmutableList.copyOf(mTableList);
        mHelper = new MySQLiteHelper(getContext(), mName, mVersion, mTableList);
        return !mTableList.isEmpty();
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        List<String> segments = uri.getPathSegments();
        BaseSQLiteTable table = mHelper.findSQSqLiteTable(segments.get(0));
        if (table == null) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (segments.size() > 1) {
            selection = BaseColumns._ID + "=" + segments.get(1)
                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
        }

        Cursor c = table.query(projection, selection, selectionArgs, sortOrder);
        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        List<String> segments = uri.getPathSegments();
        BaseSQLiteTable table = mHelper.findSQSqLiteTable(segments.get(0));
        if (table == null) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        long rowId = table.insert(values);
        if (rowId <= 0) {
            throw new SQLException("Failed to insert row into " + uri);
        }

        Uri noteUri = ContentUris.withAppendedId(uri, rowId);
        getContext().getContentResolver().notifyChange(noteUri, null);
        return noteUri;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        List<String> segments = uri.getPathSegments();
        BaseSQLiteTable table = mHelper.findSQSqLiteTable(segments.get(0));
        if (table == null) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (segments.size() > 1) {
            where = BaseColumns._ID + "=" + segments.get(1)
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : "");
        }

        int count = table.delete(where, whereArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        List<String> segments = uri.getPathSegments();
        BaseSQLiteTable table = mHelper.findSQSqLiteTable(segments.get(0));
        if (table == null) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (segments.size() > 1) {
            where = BaseColumns._ID + "=" + segments.get(1)
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : "");
        }

        int count = table.update(values, where, whereArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    protected final void addSQLiteTable(BaseSQLiteTable table) {
        // 如果已经调用了onCreate则mTableList会变为不可变列表，add操作会导致异常
        if (table != null) {
            mTableList.add(table);
        }
    }

    static final class MySQLiteHelper extends SQLiteOpenHelper {
        private final List<BaseSQLiteTable> mTableList;

        /**
         * Prevents this class from being instantiated.
         */
        protected MySQLiteHelper(Context context, String name, int version, List<BaseSQLiteTable> tables) {
            super(context, name, null, version);
            mTableList = ImmutableList.copyOf(tables);
            for (BaseSQLiteTable table : mTableList) {
                table.onBind(this);
            }

            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                for (BaseSQLiteTable table : mTableList) {
                    sb.append(table.getName());
                    sb.append(',');
                }
                sb.append("]");
                MLog.out.d(TAG, "SQL TABLES=" + sb.toString());
            }
        }

        @Override
        public final void onCreate(SQLiteDatabase db) {
            for (BaseSQLiteTable table : mTableList) {
                table.onCreate(db);
            }
        }

        @Override
        public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            for (BaseSQLiteTable table : mTableList) {
                table.onUpgrade(db, oldVersion, newVersion);
            }
        }

        /**
         * 根据表名称搜索数据库访问表对象
         */
        public final BaseSQLiteTable findSQSqLiteTable(String name) {
            for (BaseSQLiteTable table : mTableList) {
                if (TextUtils.equals(name, table.getName())) {
                    return table;
                }
            }
            return null;
        }
    }
}
