package com.jrue.appframe.lib.base;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jrue.appframe.lib.util.MLog;

/**
 * Service 基类
 * Created by meizu on 17/2/5.
 */
public class BaseService extends Service {
    private static final String TAG = "BaseService";
    private static final boolean DEBUG = false;

    private final LocalBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        if (DEBUG) MLog.out.d(TAG, "onCreate: " + getClass().getName());
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) MLog.out.d(TAG, "onDestroy: " + getClass().getName());
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public BaseService getService() {
            return BaseService.this;
        }
    }
}
