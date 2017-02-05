package com.jrue.appframe.service;

import android.content.Context;

import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import com.jrue.appframe.lib.base.BaseService;
import com.jrue.appframe.lib.event.OnApplicationCreateCtrlEvent;
import com.jrue.appframe.lib.event.OnApplicationDestroyCtrlEvent;
import com.jrue.appframe.lib.event.OnApplicationStartCtrlEvent;
import com.jrue.appframe.lib.event.OnApplicationStopCtrlEvent;
import com.jrue.appframe.lib.util.MEvent;
import com.jrue.appframe.lib.util.MLog;

/**
 * app唯一启动服务
 * Created by jrue on 17/2/5.
 */
public final class LocalManagerService extends BaseService implements Handler.Callback {
    private static final String TAG = "LocalManagerService";

    private static final long MINUTE = 60 * 1000L;

    private static final int MSG_FIRST = 0x15091100;


    private static final int MSG_APP_STOP = MSG_FIRST + 1;

    private static final int MSG_LAST = MSG_APP_STOP;

    private Handler mHandler;

    private PowerManager.WakeLock mWakeLock;


    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(this);
        MEvent.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onApplicationStop();
        MEvent.unregister(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_APP_STOP: {
                onApplicationStop();
                break;
            }
            default: {
                MLog.out.w(TAG, "handleMessage: Unknown" +
                        " msg=" + Integer.toHexString(msg.what));
                return false;
            }
        }
        return true;
    }


    public void onEvent(OnApplicationCreateCtrlEvent event) {
        if (MLog.DEBUG) MLog.zlx.d(TAG, "onEvent: " + event);
        onApplicationCreate();
    }

    public void onEvent(OnApplicationDestroyCtrlEvent event) {
        if (MLog.DEBUG) MLog.zlx.d(TAG, "onEvent: " + event);
        if (mHandler.hasMessages(MSG_APP_STOP)) {
            onApplicationStop();
        }
        onApplicationDestroy();
    }

    public void onEvent(OnApplicationStartCtrlEvent event) {
        if (MLog.DEBUG) MLog.zlx.d(TAG, "onEvent: " + event);
        releaseWakeLock();
        if (mHandler.hasMessages(MSG_APP_STOP)) {
            mHandler.removeMessages(MSG_APP_STOP);
        } else {
            onApplicationStart();
        }
    }

    public void onEvent(OnApplicationStopCtrlEvent event) {
        if (MLog.DEBUG) MLog.zlx.d(TAG, "onEvent: " + event);
        acquireWakeLock();
        mHandler.removeMessages(MSG_APP_STOP);
        mHandler.sendEmptyMessageDelayed(MSG_APP_STOP, 3 * MINUTE);
    }


    private void onApplicationCreate() {
        if (MLog.DEBUG) MLog.zlx.d(TAG, "onApplicationCreate:");
    }

    private void onApplicationDestroy() {
        if (MLog.DEBUG) MLog.zlx.d(TAG, "onApplicationDestroy:");
        synchronized (this) {
            for (int msg = MSG_FIRST; msg <= MSG_LAST; ++msg) {
                mHandler.removeMessages(msg);
            }
        }
    }

    private void onApplicationStart() {
        if (MLog.DEBUG) MLog.zlx.d(TAG, "onApplicationStart:");
        synchronized (this) {

        }
    }

    private void onApplicationStop() {
        if (MLog.DEBUG) MLog.zlx.d(TAG, "onApplicationStop:");
        releaseWakeLock();
        synchronized (this) {

        }
    }

    // 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock() {
        synchronized (this) {
            if (mWakeLock != null) return;
            if (MLog.DEBUG) MLog.zlx.d(TAG, "acquireWakeLock:");
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            if (mWakeLock != null) {
                mWakeLock.acquire();
            }
        }
    }

    // 释放设备电源锁
    private void releaseWakeLock() {
        synchronized (this) {
            if (mWakeLock == null) return;
            if (MLog.DEBUG) MLog.zlx.d(TAG, "releaseWakeLock:");
            mWakeLock.release();
            mWakeLock = null;
        }
    }

}
