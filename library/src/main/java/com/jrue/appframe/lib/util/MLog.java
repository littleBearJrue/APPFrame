/*
 * Copyright (C) 2015 Meizu Technology Co., Ltd.
 *
 * All rights reserved.
 */

package com.jrue.appframe.lib.util;

import android.os.Build;
import android.util.Log;

import com.jrue.appframe.lib.BuildConfig;


/**
 * 统一处理日志打印。
 * <p>
 * 现只定义了三个级别：DEBUG、WARN、ERROR
 * <ul>
 * <li>DEBUG用于普通日志输出，最好在各个类里面定义DEBUG开关控制输出</li>
 * <li>WARN用于警告信息，是否由类中的DEBUG开关控制输出由开发者自由决定</li>
 * <li>ERROR用于错误信息，始终显示</li>
 * </ul>
 * <p/>
 * 每个类最好在开头定义TAG和DEBUG两个常量用于日志输出，例如：
 * <pre class="prettyprint">
 * public class Hello {
 * private static final String TAG = Hello.class.getSimpleName();
 * private static final boolean DEBUG = MLog.DEBUG;
 * }
 * </pre>
 * </p>
 * Created by yangrui on 2015/2/15.
 */
public final class MLog {
    public static boolean DEBUG = BuildConfig.DEBUG;

    static final boolean MEIZU = MString.equalsIgnoreCase("Meizu", Build.BRAND);

    public static final MLog out = new MLog("out@mzsz");
    public static final MLog zlx = new MLog("zlx@mzsz");

    private final String mUserText;

    /**
     * Prevents this class from being instantiated.
     */
    private MLog(String text) {
        mUserText = text + ' ';
    }

    private String getMessage(String msg) {
        return mUserText + msg;
    }

    public int d(String tag, String msg) {
        if (DEBUG && MEIZU) {
            return Log.d(tag, getMessage(msg));
        } else if (DEBUG) {
            return Log.w(tag, getMessage(msg));
        } else {
            return 0;
        }
    }

    public int d(String tag, String msg, Throwable tr) {
        if (DEBUG && MEIZU) {
            return Log.d(tag, getMessage(msg), tr);
        } else if (DEBUG) {
            return Log.w(tag, getMessage(msg), tr);
        } else {
            return 0;
        }
    }

    public int w(String tag, String msg) {
        return Log.w(tag, getMessage(msg));
    }

    public int w(String tag, String msg, Throwable tr) {
        return Log.w(tag, getMessage(msg), tr);
    }

    public int e(String tag, String msg) {
        return Log.e(tag, getMessage(msg));
    }

    public int e(String tag, String msg, Throwable tr) {
        return Log.e(tag, getMessage(msg), tr);
    }
}
