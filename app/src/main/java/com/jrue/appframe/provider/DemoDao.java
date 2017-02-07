package com.jrue.appframe.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jrue.appframe.lib.provider.BaseSQLiteTable;

import java.util.ArrayList;
import java.util.List;

/**
 * 对保存的数据库操作类
 * Created by jrue on 17/2/7.
 */
public class DemoDao {
    /**
     * 查找全部
     */
    public static List<DemoObject> loadAllItems(Context context) {
        Cursor cursor = null;
        List<DemoObject> recordList = new ArrayList<DemoObject>();
        try {
            cursor = context.getContentResolver().query(
                    BaseSQLiteTable.makeContentUri(DemoTable.TABLE_NAME),
                    null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                DemoObject demoObject = new DemoObject();
                demoObject.setName(cursor.getString(cursor
                        .getColumnIndex(DemoTable.Columns.NAME)));
                demoObject.setDeviceId(cursor.getString(cursor
                        .getColumnIndex(DemoTable.Columns.DEVICE_ID)));
                demoObject.setMac(cursor.getString(cursor
                        .getColumnIndex(DemoTable.Columns.MAC)));
                demoObject.setType(cursor.getInt(cursor
                        .getColumnIndex(DemoTable.Columns.TYPE)));
                demoObject.setTime(cursor.getLong(cursor
                        .getColumnIndex(DemoTable.Columns.TIME)));
                recordList.add(demoObject);
            }
        } catch (OutOfMemoryError e) {
            System.gc();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return recordList;
    }

    /**
     * 查找 by id
     */
    public static boolean loadSingleItems(String id, Context context) {
        String where = String.format("%s=\"%s\"",
                DemoTable.Columns.DEVICE_ID, id);
        Cursor cursor = context.getContentResolver().query(
                BaseSQLiteTable.makeContentUri(DemoTable.TABLE_NAME),
                null, where, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    /**
     * 插入
     */
    public static void insertItem(DemoObject demoObject, Context context) {
        if (loadSingleItems(demoObject.getDeviceId(), context)) {
            updateTime(demoObject.getTime(), demoObject.getDeviceId(), demoObject.getName(), context);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DemoTable.Columns.DEVICE_ID, demoObject.getDeviceId());
            contentValues.put(DemoTable.Columns.TIME, demoObject.getTime());
            contentValues.put(DemoTable.Columns.MAC, demoObject.getMac());
            contentValues.put(DemoTable.Columns.NAME, demoObject.getName());
            contentValues.put(DemoTable.Columns.TYPE, demoObject.getType());
            context.getContentResolver().insert(BaseSQLiteTable.makeContentUri(DemoTable.TABLE_NAME), contentValues);
        }
    }

    /**
     * 更新操作 by id
     */
    public static boolean updateTime(long time, String id, String name, Context context) {
        String where = String.format("%s=\"%s\"",
                DemoTable.Columns.DEVICE_ID, id);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DemoTable.Columns.TIME, time);
        contentValues.put(DemoTable.Columns.NAME, name);
        return 0 < context.getContentResolver().update(
                BaseSQLiteTable.makeContentUri(DemoTable.TABLE_NAME), contentValues, where, null);
    }

    /**
     * 删除 by id
     */
    public static boolean deleteItem(String id, Context context) {
        String where = String.format("%s=\"%s\"",
                DemoTable.Columns.DEVICE_ID, id);
        return 0 < context.getContentResolver().delete(BaseSQLiteTable.makeContentUri(DemoTable.TABLE_NAME), where, null);
    }

    /**
     * 清空
     */
    public static boolean clearItem(Context context) {
        return 0 < context.getContentResolver().delete(BaseSQLiteTable.makeContentUri(DemoTable.TABLE_NAME), null, null);
    }

    public static class DemoObject {
        /**
         * 设备标识
         */
        private String deviceId;
        /**
         * 设备名称
         */
        private String name;
        /**
         * mac地址
         */
        private String mac;
        /**
         * 设备类型
         */
        private int type;
        /**
         * 设备状态
         */
        private boolean isSwitchOn;
        /**
         * 操作时间
         */
        private long time;


        public DemoObject() {

        }
        public DemoObject(String deviceId, String name, String mac, int type, long time) {
            this.deviceId = deviceId;
            this.name = name;
            this.mac = mac;
            this.type = type;
            this.time = time;
        }
        public DemoObject(String deviceId, String name, String mac, int type, long time,boolean isSwitchOn) {
            this.deviceId = deviceId;
            this.name = name;
            this.mac = mac;
            this.type = type;
            this.time = time;
            this.isSwitchOn=isSwitchOn;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public boolean isSwitchOn() {
            return isSwitchOn;
        }

        public void setSwitchOn(boolean isSwitchOn) {
            this.isSwitchOn = isSwitchOn;
        }
    }
}
