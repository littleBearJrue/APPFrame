/*
 * Copyright (c) 2015 Meizu Technology Co., Ltd.
 * All rights reserved.
 */

package com.jrue.appframe.lib.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.input.InputManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;

import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 无法分类的公用方法，请尽量使用单独的类！！！
 * <p/>
 * Created by yangrui on 3/5/15.
 */
public class MUtils {
    private static final String TAG = "MUtils";

    private static final boolean DEBUG = false;

    private final static Pattern NUM_PATTERN = Pattern.compile("^\\d+$");

    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static final String SPECIAL_CHARACTERS_FILTER_STRING = "'";
    private static final Pattern SPECIAL_CHARACTERS_FILTER = Pattern.compile("'");

    /**
     * 获取屏幕宽高
     */
    private static final Point sScreenSize = new Point();

    /**
     * Prevents this class from being instantiated.
     */
    private MUtils() {
        throw new AssertionError("Don't create " + TAG);
    }

    /**
     * 获取文件MD5
     */
    public static String getFileMd5(String path) {
        String checksum = null;
        FileInputStream fis = null;
        try {
            MessageDigest mdEnc = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(path);
            byte[] buffer = new byte[4096];
            int numOfBytesRead;
            while ((numOfBytesRead = fis.read(buffer)) > 0) {
                mdEnc.update(buffer, 0, numOfBytesRead);
            }
            byte[] hash = mdEnc.digest();
            checksum = new BigInteger(1, hash).toString(16);
        } catch (Exception ex) {
            MLog.out.e(TAG, "getFileMd5: " + ex);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    MLog.out.e(TAG, "getFileMd5: " + e);
                }
            }
        }
        return checksum;
    }

    /**
     * 获取用户可读格式的字节数目字符串，数字和单位之间使用空格分割
     *
     * @param bytes 字节数目
     * @param si    使用1000为1K
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        float density = 1;
        if (context != null) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        float density = 1;
        if (context != null) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context （DisplayMetrics类中属性scaledDensity）
     */
    public static int px2sp(Context context, float pxValue) {
        float density = 1;
        if (context != null) {
            density = context.getResources().getDisplayMetrics().scaledDensity;
        }
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context （DisplayMetrics类中属性scaledDensity）
     */
    public static int sp2px(Context context, float spValue) {
        float density = 1;
        if (context != null) {
            density = context.getResources().getDisplayMetrics().scaledDensity;
        }
        return (int) (spValue * density + 0.5f);
    }

    public static Point getScreenSize(Context context) {
        if (context == null) {
            return sScreenSize;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(sScreenSize);
        return sScreenSize;
    }

    /**
     * 获取sd卡目录下本应用的文件夹
     */
    public static File getExternalStorageDirectory() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File dir = new File(Environment.getExternalStorageDirectory(), "meizu/meijia");
            if (dir.isDirectory() || dir.mkdirs()) {
                return dir;
            }
        }
        return null;
    }

    /**
     * 弹出软键盘
     * FIXME： 不要使用这种方法弹出键盘
     */
    public static void popUpSoftKeyboard(final EditText et, final Context context) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 300);
    }

    /**
     * 显示软键盘
     */
    public static void showSoftInput(View v) {
        if (v != null && v.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) v.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(v, 0, null);
        }
    }

    /**
     * 显示软键盘
     */
    public static void hideSoftInput(View v) {
        if (v != null && v.hasFocus()) {
            InputMethodManager imm = (InputMethodManager) v.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0, null);
        }
    }

    /**
     * 获取IMEI
     */
    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * 判断当前是否可以联网 context 是null 返回false!
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null) {
                    status = netInfo.isAvailable() && netInfo.isConnected();
                }
            }
        } catch (Exception ex) {
            MLog.out.e(TAG, "isNetworkAvailable: " + ex);
        }
        return status;
    }

    public static boolean isWifiConnected(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (netInfo != null) {
                    status = netInfo.isAvailable() && netInfo.isConnected();
                }
            }
        } catch (Exception ex) {
            MLog.out.e(TAG, "isNetworkAvailable: " + ex);
        }
        return status;
    }

    public static String getGatewayIp(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String gatewayStr = int2ip(wm.getDhcpInfo().gateway);//网关地址
        if (DEBUG) MLog.out.d(TAG, "getGatewayIp: " + gatewayStr);
        return gatewayStr;
    }

    public static String int2ip(int ip) {
        return String.valueOf(ip & 0xff) + '.'
                + ((ip >>> 8) & 0xff) + '.'
                + ((ip >>> 16) & 0xff) + '.'
                + ((ip >>> 24) & 0xff);
    }


    public static boolean hasSpecialCharacters(CharSequence charSequence) {
        return SPECIAL_CHARACTERS_FILTER.matcher(charSequence).find();
    }

    /**
     * 判断是否连接外网
     */
    public static boolean ping(Context context) {
        return ping(context, 3, 60);
    }

    public static boolean ping(Context context, int times, int timeout) {
        try {
            if (isNetworkAvailable(context)) {
                String ip = "www.baidu.com";// ping 的地址
                Process p = Runtime.getRuntime().exec("ping -c " + times + " -w " + timeout + " " + ip);// ping网址3次
                if (p.waitFor() == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            MLog.out.e(TAG, "ping: " + e);
        }

        return false;
    }

    /**
     * 编码base64字符串
     * <p/>
     *
     * @param data 需要编码的字符串
     */
    public static String encodeBase64Str(String data) throws Exception {
        byte[] buffer = data.getBytes(Charsets.UTF_8);
        return Base64.encodeToString(buffer, Base64.NO_WRAP);
    }

    /**
     * 解码base64字符串
     * <p/>
     *
     * @param base64Code 编码后的字串
     */
    public static String decoderBase64Str(String base64Code) throws Exception {
        byte[] buffer = Base64.decode(base64Code, Base64.NO_WRAP);
        return new String(buffer, Charsets.UTF_8);
    }


    public static int verCompare(String ver1, String ver2) {
        if (MString.equalsIgnoreCase(ver1, ver2)) {
            return 0;
        }

        String[] arr1 = ver1.split("\\.");
        String[] arr2 = ver2.split("\\.");
        final int N = Math.min(arr1.length, arr2.length);
        for (int i = 0; i < N; ++i) {
            int diff = parseIntSkipNonDigit(arr1[i], 0) - parseIntSkipNonDigit(arr2[i], 0);
            if (diff != 0) return diff;
        }

        return arr1.length - arr2.length;
    }

    public static int parseIntSkipNonDigit(CharSequence text, int defaultValue) {
        int index;
        final int N = text.length();
        for (index = 0; index < N; ++index) {
            if (Character.isDigit(text.charAt(index))) {
                break;
            }
        }
        if (index > 0) {
            return parseInt(text.subSequence(index, N), defaultValue);
        } else {
            return parseInt(text, defaultValue);
        }
    }

    public static int parseInt(CharSequence text, int defaultValue) {
        int value = defaultValue;
        if (!TextUtils.isEmpty(text)) {
            try {
                value = Integer.parseInt(text.toString());
            } catch (NumberFormatException e) {
                if (DEBUG) MLog.zlx.w(TAG, "parseInt: " + e);
            }
        }
        return value;
    }

    public static int parseInt(CharSequence text) {
        return parseInt(text, 0);
    }

    public static float parseFloat(CharSequence text, float defaultValue) {
        float value = defaultValue;
        if (!TextUtils.isEmpty(text)) {
            try {
                value = Float.parseFloat(text.toString());
            } catch (NumberFormatException e) {
                if (DEBUG) MLog.zlx.w(TAG, "parseFloat: " + e);
            }
        }
        return value;
    }

    public static float parseFloat(CharSequence text) {
        return parseFloat(text, 0);
    }

    /**
     * 是否设置状态栏图标为黑色主题
     *
     * @param dark true-设置为黑色主题；false-恢复白色主题
     */
    public static boolean setDarkStatusBarIcon(Window win, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        WindowManager.LayoutParams winParams = win.getAttributes();
        try {
            final int MEIZU_FLAG_DARK_STATUS_BAR_ICON = WindowManager.LayoutParams.class
                    .getField("MEIZU_FLAG_DARK_STATUS_BAR_ICON").getInt(null);
            final Field meizuFlagsField = WindowManager.LayoutParams.class.getField("meizuFlags");
            final int meizuFlags = meizuFlagsField.getInt(winParams);
            if (dark == ((meizuFlags & MEIZU_FLAG_DARK_STATUS_BAR_ICON) != 0)) {
                return true;
            }
            if (dark) {
                meizuFlagsField.setInt(winParams, meizuFlags | MEIZU_FLAG_DARK_STATUS_BAR_ICON);
            } else {
                meizuFlagsField.setInt(winParams, meizuFlags & ~MEIZU_FLAG_DARK_STATUS_BAR_ICON);
            }
        } catch (Exception e) {
            if (DEBUG) MLog.out.w(TAG, "setDarkStatusBarIcon: " + e.toString());
            return false;
        }
        win.setAttributes(winParams);
        return true;
    }

    /**
     * 状态栏高度
     */
    public static Rect getStatusBarRect(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame;
    }

    public static boolean simulateKeyEvent(int keyCode) {
        try {
            Method getInstance = InputManager.class.getMethod("getInstance");
            InputManager im = (InputManager) getInstance.invoke(null);
            if (im == null) return false;

            long now = SystemClock.uptimeMillis();
            KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0);
            KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0);
            Method injectInputEvent = InputManager.class.getMethod(
                    "injectInputEvent", InputEvent.class, int.class);
            int mode = InputManager.class.getField("INJECT_INPUT_EVENT_MODE_ASYNC").getInt(null);
            injectInputEvent.invoke(im, down, mode);
            injectInputEvent.invoke(im, up, mode);
            return true;
        } catch (Exception e) {
            MLog.out.e(TAG, "simulateKeyEvent: " + e.toString());
        }
        return false;
    }

    public static int genRandomNum(int min, int max) {
        Random random = new Random();

        int randomNum = random.nextInt(max) % (max - min + 1) + min;
        return randomNum;
    }

    private static int sAppverion;

    /**
     * 获取客户端的版本号
     *
     * @param context
     * @return 客户端的版本号
     */
    public static int getAppVerion(Context context) {
        if (sAppverion > 0) {
            return sAppverion;
        }
        if (null == context) {
            return 0;
        }
        PackageInfo packageInfo = null;
        try {
            PackageManager pm = context.getPackageManager();
            if (null == pm) {
                return 0;
            }
            packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            if (null != packageInfo) {
                sAppverion = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return sAppverion;
    }

    /**
     * 安装应用
     */
    public static void startInstallApp(Uri uri, Context context) {
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");//设置intent的数据类型
        context.startActivity(installIntent);
    }

    /**
     * 根据传递进来的bytes的大小 返回对应的数据字符串
     *
     * @param bytes
     * @return
     */
    public static String getDataSize(long bytes) {
        DecimalFormat format = new DecimalFormat("###.00");
        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < 1024 * 1024) {
            return format.format(bytes / 1024f) + "KB";
        } else if (bytes < 1024 * 1024 * 1024) {
            return format.format(bytes / 1024f / 1024f) + "M";
        } else if (bytes < 1024L * 1024 * 1024 * 1024) {
            return format.format(bytes / 1024f / 1024f / 1024f) + "G";
        } else {
            return "";
        }
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机号码
     *
     * @param mobileNumber
     * @return
     */
    public static boolean checkMobileNumber(String mobileNumber) {
        boolean flag;
        try {
            Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static int compareRomVersionString(String version1, String version2) {
        if (TextUtils.equals(version1, version2)) {
            return 0;
        }
        if (null == version1) {
            return -1;
        }
        if (null == version2) {
            return 1;
        }
        if (!version1.matches(MConst.VERSION_REGULAR) ||
                !version2.matches(MConst.VERSION_REGULAR)) {
//            throw new IllegalArgumentException("Version number format errors");
            return -1;
        }
        String[] verArray1 = version1.split("\\.");
        String[] verArray2 = version2.split("\\.");
        int N;
        if (verArray1.length <= verArray2.length) {
            N = verArray1.length;
        } else {
            N = verArray2.length;
        }
        for (int i = 0; i < N; i++) {
            int part1 = Integer.valueOf(verArray1[i]);
            int part2 = Integer.valueOf(verArray2[i]);
            if (part1 > part2) {
                return 1;
            } else if (part1 < part2) {
                return -1;
            }
        }
        if (verArray1.length > verArray2.length) {
            for (int i = N; i < verArray1.length; i++) {
                if (Integer.valueOf(verArray1[i]) > 0) {
                    return 1;
                }
            }
            return 0;
        } else if (verArray1.length == verArray2.length) {
            return 0;
        } else {
            for (int i = N; i < verArray2.length; i++) {
                if (Integer.valueOf(verArray2[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        }
    }

    public static void setKeyboardFocus(final EditText primaryTextField, Handler handler) {
        handler.postDelayed(new Runnable() {
            public void run() {
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_DOWN, 0, 0, 0));
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP, 0, 0, 0));
            }
        }, 100);
    }

    public static boolean checkStrIsNum(CharSequence value) {
        return NUM_PATTERN.matcher(value).matches();
    }

    public static String getWayIp(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo di = wm.getDhcpInfo();
        long getewayIpL = di.gateway;
        return long2ip(getewayIpL);//网关地址

    }

    public static String long2ip(long ip) {
        return String.valueOf((int) (ip & 0xff)) + '.' +
                ((int) ((ip >> 8) & 0xff)) + '.' +
                ((int) ((ip >> 16) & 0xff)) + '.' +
                ((int) ((ip >> 24) & 0xff));
    }

    /**
     * 获取网络状态
     */
    public static int getNetWorkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return networkInfo.getType();
        }
        return -1;
    }

    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && context != null &&
                context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
            return BluetoothAdapter.getDefaultAdapter();
        }
        return null;
    }

    public static <T> boolean arrayContains(T[] array, T elem) {
        if (array != null && array.length > 0) {
            for (T item : array) {
                if (item == elem) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean arrayContains(int[] array, int elem) {
        if (array == null || array.length <= 0) {
            return false;
        }
        for (int item : array) {
            if (item == elem) {
                return true;
            }
        }
        return false;
    }

    public static boolean arrayContains(byte[] array, byte elem) {
        if (array == null || array.length <= 0) {
            return false;
        }
        for (byte item : array) {
            if (item == elem) {
                return true;
            }
        }
        return false;
    }

    public static boolean arrayEquals(byte[] src, int srcPos,
                                      byte[] dst, int dstPos, int length) {
        for (int i = 0; i < length; ++i) {
            if (src[srcPos + i] != dst[dstPos + i]) {
                return false;
            }
        }
        return true;
    }

}
