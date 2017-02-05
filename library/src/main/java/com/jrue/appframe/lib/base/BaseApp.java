package com.jrue.appframe.lib.base;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.util.SparseArray;

import com.jrue.appframe.lib.BuildConfig;
import com.jrue.appframe.lib.event.OnApplicationCreateCtrlEvent;
import com.jrue.appframe.lib.event.OnApplicationDestroyCtrlEvent;
import com.jrue.appframe.lib.event.OnApplicationStartCtrlEvent;
import com.jrue.appframe.lib.event.OnApplicationStopCtrlEvent;
import com.jrue.appframe.lib.util.MEvent;
import com.jrue.appframe.lib.util.MLog;

/**
 * 进程入口
 * <p/>
 * Created by jrue on 2/5/17.
 */
public abstract class BaseApp extends MultiDexApplication {
    private static final String TAG = "BaseApp";

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private int mVersionCode;
    private String mVersionName;

    private int mActivityCount;

    private int mForegroundActivityCount;

    /**
     * Map used to store app's tags.
     */
    private final SparseArray<Object> mKeyedTags = new SparseArray<>(2);

    public void onCreate() {
        super.onCreate();

        mzOnPreCreate();

        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            if (pi != null) {
                mVersionCode = pi.versionCode;
                mVersionName = pi.versionName;
            }
        } catch (Exception e) {
            MLog.out.e(TAG, "getAppVersion: " + e);
        }

        System.setProperty("http.keepAlive", String.valueOf(
                Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO));
    }

    public final void onActivityCreate() {
        if (mActivityCount <= 0) {
            mActivityCount = 0;
            onApplicationCreate();
        }
        mActivityCount++;
    }

    public final void onActivityDestroy() {
        mActivityCount--;
        if (mActivityCount <= 0) {
            mActivityCount = 0;
            onApplicationDestroy();
        }
    }

    public final void onActivityStart() {
        if (mForegroundActivityCount <= 0) {
            mForegroundActivityCount = 0;
            onApplicationStart();
        }
        mForegroundActivityCount++;
    }

    public final void onActivityStop() {
        mForegroundActivityCount--;
        if (mForegroundActivityCount <= 0) {
            mForegroundActivityCount = 0;
            onApplicationStop();
        }
    }

    protected void onApplicationCreate() {
        if (DEBUG) MLog.out.d(TAG, "onApplicationCreate:");
        MEvent.postSticky(OnApplicationCreateCtrlEvent.INSTANCE);
    }

    protected void onApplicationDestroy() {
        if (DEBUG) MLog.out.d(TAG, "onApplicationDestroy:");
        MEvent.post(OnApplicationDestroyCtrlEvent.INSTANCE);
    }

    protected void onApplicationStart() {
        if (DEBUG) MLog.out.d(TAG, "onApplicationStart:");
        MEvent.postSticky(OnApplicationStartCtrlEvent.INSTANCE);
    }

    protected void onApplicationStop() {
        if (DEBUG) MLog.out.d(TAG, "onApplicationStop:");
        MEvent.post(OnApplicationStopCtrlEvent.INSTANCE);
    }

    protected void mzOnPreCreate() {
        sInstance = this;
    }

    public <T> T getTag(int key) {
        return getTag(key, null);
    }

    public <T> T getTag(int key, T defValue) {
        if (mKeyedTags != null) {
            return (T) mKeyedTags.get(key, defValue);
        }
        return defValue;
    }

    public final int getVersionCode() {
        return mVersionCode;
    }

    public final String getVersionName() {
        return mVersionName;
    }


    private String getMetaData(String name) {
        try {
            ApplicationInfo appInfo = getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
            if (appInfo != null && appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (Exception e) {
            MLog.out.e(TAG, "getMetaData: " + e);
        }
        return "";
    }

    private static BaseApp sInstance;

    public static BaseApp getInstance() {
        return sInstance;
    }
}


