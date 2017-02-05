package com.jrue.appframe.main;

import android.content.Intent;

import com.jrue.appframe.BuildConfig;
import com.jrue.appframe.lib.base.BaseApp;
import com.jrue.appframe.lib.util.MLog;
import com.jrue.appframe.service.LocalManagerService;

/**
 * 程序入口
 * Created by meizu on 17/2/5.
 */
public class MainApp extends BaseApp {

    private static final String TAG = "MainApp";
    private static final boolean DEBUG = BuildConfig.DEBUG;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onApplicationStart() {
        super.onApplicationStart();
        if (DEBUG) MLog.zlx.d(TAG, "onApplicationStart: start service");
        startService(new Intent(this, LocalManagerService.class));
    }

    @Override
    protected void onApplicationStop() {
        super.onApplicationStop();
        if (DEBUG) MLog.zlx.d(TAG, "onApplicationStop: save all");
    }
}
