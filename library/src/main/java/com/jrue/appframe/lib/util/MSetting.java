package com.jrue.appframe.lib.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.text.TextUtils;

import com.google.common.collect.ImmutableSet;
import com.jrue.appframe.lib.base.BaseApp;
import com.jrue.appframe.lib.base.BaseCursor;
import com.jrue.appframe.lib.event.OnSettingChangedEvent;
import com.jrue.appframe.lib.provider.BaseSQLiteTable;
import com.jrue.appframe.lib.provider.SettingTable;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置项存取，需要自定义键值以及存取方法。实例获取请使用{@code getInstance}
 * <p>
 * 例如：新增保存一个属性值，需先添加一个TAG类型的key,然后增加TAG用于界面接收变化广播，实现get和set方法
 * Created by jrue on 17/2/7.
 */
public class MSetting {
    private static final String TAG = "MSetting";

    /**
     * 保存是否是第一次使用
     */
    public static final String KEY_FIRST_USE_FLAG = "first_use_flag";

    public static final String[] PROJECTIONS = {
            SettingTable.Columns._ID,
            SettingTable.Columns.KEY,
            SettingTable.Columns.VALUE,
    };

    private Context mContext;

    private BaseCursor mCursor;

    private final Object mLock = new Object();

    private final Map<String, String> mCursorMap = new HashMap<>();


    private MSetting() {

    }

    private static Cursor findCursor(Cursor cursor, String columnName, String columnValue) {
        if (cursor != null && cursor.moveToFirst()) {
            final int columnIndex = cursor.getColumnIndex(columnName);
            do {
                if (TextUtils.equals(columnValue, cursor.getString(columnIndex))) {
                    return cursor;
                }
            } while (cursor.moveToNext());
        }
        return null;
    }

    public boolean init(Context context) {
        mContext = context.getApplicationContext();
        mCursor = new BaseCursor(new Handler()) {
            @Override
            protected void notifyDataSetChanged() {
                ImmutableSet<String> set;
                synchronized (mLock) {
                    set = ImmutableSet.copyOf(mCursorMap.keySet());
                    mCursorMap.clear();
                }
                for (String key : set) {
                    MEvent.post(OnSettingChangedEvent.newInstance(key));
                }
            }

            @Override
            protected void onContentChanged() {
                synchronized (mLock) {
                    // 解决异步设置变量时，同时获取报错问题
                    super.onContentChanged();
                }
            }
        };
        mCursor.setCursor(mContext.getContentResolver().query(
                BaseSQLiteTable.makeContentUri(SettingTable.TABLE_NAME), PROJECTIONS, null, null, null));
        return mCursor.isValid();
    }

    /**
     * 获取是否是第一次使用
     * @return
     */
    public Boolean getFirstUse(){
        return getBoolean(KEY_FIRST_USE_FLAG, false);
    }

    /**
     * 设置是否是第一次使用的标志位
     * @param isFirst
     * @return
     */
    public boolean setFirstUse(Boolean isFirst) {
        return putBoolean(KEY_FIRST_USE_FLAG, isFirst);
    }

    /**
     * 获取字符串value
     */
    private String getString(String key) {
        synchronized (mLock) {
            if (mCursorMap.containsKey(key)) {
                return mCursorMap.get(key);
            }
            Cursor cursor = findCursor(mCursor.getCursor(), SettingTable.Columns.KEY, key);
            return SettingTable.getString(cursor, SettingTable.Columns.VALUE, null);
        }
    }

    /**
     * 更新字符串value
     */
    private boolean putString(String key, String value) {
        if (MLog.DEBUG) MLog.out.d(TAG, "putString: key=" + key + " val=" + value);

        boolean success;
        synchronized (mLock) {
            if (TextUtils.equals(value, getString(key))) {
                return true;
            }

            if (MLog.DEBUG) MLog.out.d(TAG, "putStringLocked: key=" + key + " value=" + value);
            ContentValues values = new ContentValues();
            values.put(SettingTable.Columns.KEY, key);
            values.put(SettingTable.Columns.VALUE, value);

            ContentResolver cr = mContext.getContentResolver();
            Cursor cursor = findCursor(mCursor.getCursor(), SettingTable.Columns.KEY, key);
            if (cursor == null) {
                success = cr.insert(BaseSQLiteTable.makeContentUri(SettingTable.TABLE_NAME), values) != null;
            } else {
                success = cr.update(ContentUris.withAppendedId(BaseSQLiteTable.makeContentUri(SettingTable.TABLE_NAME),
                        SettingTable.getLong(cursor, SettingTable.Columns._ID, 0)), values, null, null) > 0;
            }

            if (success) mCursorMap.put(key, value);
        }
        if (success) {
            MEvent.post(OnSettingChangedEvent.newInstance(key));
        } else {
            MLog.out.e(TAG, "putStringLocked: Fail key=" + key + " value=" + value);
        }
        return success;
    }

    /**
     * 获取布尔变量value
     */
    private boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    /**
     * 获取布尔变量value
     */
    private boolean getBoolean(String key, boolean defaultValue) {
        String result = getString(key);
        if (TextUtils.isEmpty(result)) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(result);
        }
    }

    /**
     * 更新布尔变量value
     */
    private boolean putBoolean(String key, boolean value) {
        if (MLog.DEBUG) MLog.out.d(TAG, "putBoolean: key=" + key + " old=" + value);
        return putString(key, String.valueOf(value));
    }


    /**
     * 获取int变量value
     */
    private int getInt(String key) {
        return MUtils.parseInt(getString(key), 0);
    }

    /**
     * 更新int变量value
     */
    private boolean putInt(String key, int value) {
        if (MLog.DEBUG) MLog.out.d(TAG, "putInt: key=" + key + " old=" + value);
        return putString(key, String.valueOf(value));
    }

    private static volatile MSetting sInstance;

    public static MSetting getInstance() {
        if (sInstance == null) {
            synchronized (MSetting.class) {
                if (sInstance == null) {
                    sInstance = new MSetting();
                    sInstance.init(BaseApp.getInstance());
                }
            }
        }
        return sInstance;
    }
}
