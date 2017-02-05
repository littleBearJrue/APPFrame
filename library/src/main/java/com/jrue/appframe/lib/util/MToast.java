package com.jrue.appframe.lib.util;

import android.content.Context;
import android.widget.Toast;

import com.jrue.appframe.lib.base.BaseApp;


/**
 * Toast 公共方法
 * <p/>
 * Created by jrue on 2/5/17.
 */
public class MToast {
    private static final String TAG = "MToast";

    private MToast() {
        throw new AssertionError("Don't create " + TAG);
    }

    private static CharSequence getText(Context context, int textResId) {
        CharSequence text;
        if (context == null) {
            text = BaseApp.getInstance().getString(textResId);
        } else {
            text = context.getString(textResId);
        }
        return text;
    }

    public static void show(int textResId) {
        show(BaseApp.getInstance(), getText(BaseApp.getInstance(), textResId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int textResId) {
        show(context, getText(context, textResId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, CharSequence text) {
        if (context == null) {
            MLog.out.w(TAG, "show: " + text);
        } else {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public static Toast showToast(Context context, int textResId, int duration) {

        Toast toast = Toast.makeText(context, getText(context, textResId), duration);
        toast.show();
        return toast;
    }

    public static void show(Context context, int textResId, int duration) {
        show(context, getText(context, textResId), duration);
    }

    public static void show(Context context, CharSequence text, int duration) {
        if (context == null) {
            MLog.out.w(TAG, "show: " + text);
        } else {
            Toast.makeText(context, text, duration).show();
        }
    }

    public static void error(Context context, int textResId) {
        error(context, getText(context, textResId), Toast.LENGTH_SHORT);
    }

    public static void error(Context context, CharSequence text) {
        if (context == null) {
            MLog.out.w(TAG, "error: " + text);
        } else {
            error(context, text, Toast.LENGTH_SHORT);
        }
    }

    public static void error(Context context, int textResId, int duration) {
        error(context, getText(context, textResId), duration);
    }

    public static void error(Context context, CharSequence text, int duration) {
        if (context == null) {
            MLog.out.w(TAG, "error: " + text);
        } else {
            Toast.makeText(context, text, duration).show();
        }
    }
}
