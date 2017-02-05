package com.jrue.appframe.lib.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.base.Utf8;
import com.google.common.primitives.UnsignedBytes;

import java.nio.charset.Charset;

/**
 * 字符处理
 * <p/>
 * Created by xiaobo.lin on 2015/3/12.
 */
public final class MString {
    private static final String TAG = "MString";

    private static final String GB18030 = "GB18030";

    public static final int REMOTE_BYTE_NAME = 32;
    public static final String UTF_8 = "UTF-8";

    private static final byte[] EMPTY_BYTES = new byte[0];

    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /**
     * 格式化字符串
     */
    public static SpannableString getFormatString(Context context, int textResId, String str, int colorResId, float fontSize) {
        SpannableString ss = new SpannableString(context.getString(textResId, str));
        //用颜色标记文本
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorResId)), str.length(), ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        AbsoluteSizeSpan span = new AbsoluteSizeSpan((int) fontSize);
        ss.setSpan(span, str.length(), ss.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ss;
    }

    public static SpannableString getFormatSocketString(Context context, int textResId, int colorResId, String str1, String str2, float fontSize1, float fontSize2) {
        SpannableString ss = new SpannableString(context.getString(textResId, str1, str2));
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorResId)), 0, ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan((int) fontSize1), 0, str1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan((int) fontSize2), ss.length() - str2.length(), ss.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ss;
    }

    /**
     * 格式化字符串
     */
    public static SpannableString getFormatScenceString(Context context, int textResId, String str1, String str2, float fontSize) {
        SpannableString ss = new SpannableString(context.getString(textResId, str1, str2));
        ss.setSpan(new AbsoluteSizeSpan((int) fontSize), 0, str1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ss;
    }


    /**
     * 格式化字符串
     */
    public static SpannableString getFormatScenceTipsString(Context context, int textResId, int colorResId, String str1, String str2, float fontSize) {
        SpannableString ss = new SpannableString(context.getString(textResId, str1, str2));
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorResId)), 0, str1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan((int) fontSize), 0, str1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ss;
    }

    /**
     * 格式化字符串
     */
    public static SpannableString getFormatString(Context context, int textResId, int colorResId, String str1, String str2, float fontSize) {
        SpannableString ss = new SpannableString(context.getString(textResId, str1, str2));
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorResId)), 0, str1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan((int) fontSize), 0, str1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ss;
    }

    /**
     * 格式化字符串
     */
    public static SpannableString getFormatRemoteSceneValueString(Context context, int textResId, int str1ColorResId, int str2ColorResId, String str1, String str2, float str1FontSize, float str2FontSize) {
        SpannableString ss = new SpannableString(context.getString(textResId, str1, str2));
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(str1ColorResId)), 0, str1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan((int) str1FontSize), 0, str1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(str2ColorResId)), str1.length(), ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan((int) str2FontSize), str1.length(), ss.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ss;
    }

    /**
     * 格式化字符串
     */
    public static SpannableString getFormatSceneValueString(Context context, int textResId, String str, int colorResId, float fontSize) {
        SpannableString ss = new SpannableString(context.getString(textResId, str));
        int len = ss.length() - str.length();
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorResId)), len, ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        AbsoluteSizeSpan span = new AbsoluteSizeSpan((int) fontSize);
        ss.setSpan(span, len, ss.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ss;
    }

    /**
     * 格式化字符串
     */
    public static SpannableString getFormatSceneValueString(Context context, int textResId, int colorResId, String str1, String str2, float fontSize) {
        SpannableString ss = new SpannableString(context.getString(textResId, str1, str2));
        int len = ss.length() - str2.length();
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorResId)), len, ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan((int) fontSize), len, ss.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ss;
    }

    public static SpannableString getFormatSceneValueString(Context context, int textResId, String str1, String str2) {
        SpannableString ss = new SpannableString(context.getString(textResId, str1, str2));
        int len = ss.length() - str2.length() - 2;
        ss.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 2, str1.length() + 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), len, ss.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ss;
    }

    /**
     * 格式化字符串
     */
    public static SpannableString getFormatDoorSensorString(Context context, int textResId, String str1, String str2, float fontSize) {
        SpannableString ss = new SpannableString(context.getString(textResId, str1, str2));
        int len = ss.length() - str2.length();
        ss.setSpan(new AbsoluteSizeSpan((int) fontSize), len, ss.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ss;
    }


    /**
     * 转换字符串到byte数组，max为数组的最大长度，默认使用UTF-8，超长时使用GBK
     */
    public static byte[] toByteArray(String text, int max) {
        if (max <= 0 || TextUtils.isEmpty(text)) {
            return EMPTY_BYTES;
        }

        for (int inCount = Math.min(max, text.length()); inCount > 0; --inCount) {
            if (Character.isHighSurrogate(text.charAt(inCount - 1))) {
                continue;
            }

            String input = text;
            if (inCount < input.length()) {
                input = input.substring(0, inCount);
            }

            byte[] utf8 = input.getBytes(Charsets.UTF_8);
            if (utf8.length <= max) {
                return utf8;
            }

            // 包含2个char的code point，最好使用UTF-8编码
            if (Character.codePointCount(text, 0, inCount) == inCount) {
                byte[] bytes = input.getBytes(Charset.forName(GB18030));
                if (bytes.length <= max) {
                    return bytes;
                }
            }
        }
        return EMPTY_BYTES;
    }

    /**
     * 转化字节数组为字符串，使用UTF8编码
     */
    public static String fromByteArray(byte[] data, int start, int count) {
        if (start + count > data.length) {
            return null;
        }

        while (count > 0 && data[start + (count - 1)] == 0) {
            --count; // 删除结尾的无效字节
        }

        if (count <= 0) return null;
        // 首选使用UTF8编码，否则使用GB18030
        if (Utf8.isWellFormed(data, start, count)) {
            return new String(data, start, count, Charsets.UTF_8);
        } else {
            return new String(data, start, count, Charset.forName(GB18030));
        }
    }

    /**
     * 转化字节数组为字符串，使用UTF8编码
     */
    public static String fromByteArray(byte[] data) {
        return fromByteArray(data, 0, data.length);
    }

    /**
     * 将字节数组显示成16进制字符串
     */
    public static String toHexString(byte[] array) {
        if (array == null) {
            return toHexString(null, 0, 0);
        }
        return toHexString(array, 0, array.length);
    }

    /**
     * 将字节数组显示成16进制字符串
     */
    public static String toHexString(byte[] array, int start, int count) {
        if (array == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder(count * 4);
        sb.append('[');
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(", ");
            appendHexString(sb, array[start + i]);
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * 将字节数组显示成16进制字符串
     */
    public static String toRawHexString(byte[] array) {
        if (array == null) array = EMPTY_BYTES;
        return toRawHexString(array, 0, array.length);
    }

    /**
     * 将字节数组显示成16进制字符串
     */
    public static String toRawHexString(byte[] array, int start, int count) {
        if (array == null || count == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(count * 2);
        for (int i = 0; i < count; i++) {
            appendHexString(sb, array[start + i]);
        }
        return sb.toString();
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式
     * 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
     */
    public static byte[] bytesFromRawHexString(String raw) {
        if (TextUtils.isEmpty(raw)) {
            return EMPTY_BYTES;
        }
        byte[] bytes = raw.getBytes();
        byte[] result = new byte[bytes.length / 2];
        for (int i = 0; i < bytes.length / 2; i++) {
            result[i] = byteFromHexString(bytes, i * 2);
        }
        return result;
    }

    private static void appendHexString(StringBuilder sb, byte num) {
        sb.append(DIGITS[(num >>> 4) & 0xF]);
        sb.append(DIGITS[num & 0xF]);
    }

    /**
     * 将字节数据转换成两个字节的字符串，如0x0E转换成字符串"0E"
     */
    public static String toHexString(byte num) {
        return String.valueOf(DIGITS[(num >>> 4) & 0xF]) + DIGITS[num & 0xF];
    }

    /**
     * 将字符数组前两个字节转化为数据，例如"0EFE"将转为0x0E
     */
    private static byte byteFromHexString(byte[] text, int st) {
        int num = 0;
        int count = Math.min(2, text.length - st);
        for (int i = 0; i < count; ++i) {
            int c = UnsignedBytes.toInt(text[st + i]);
            num <<= 4;
            if (c >= '0' && c <= '9') {
                num += c - '0';
            } else if (c >= 'A' && c <= 'F') {
                num += c - 'A' + 0xA;
            } else if (c >= 'a' && c <= 'f') {
                num += c - 'a' + 0xA;
            } else {
                break;
            }
        }
        return UnsignedBytes.checkedCast(num);
    }

    /**
     * 将6位字节数组显示MAC字符串
     */
    public static String toMacReverseString(byte[] data, int st, int len) {
        if (data == null || data.length < st + len) {
            throw new IllegalArgumentException("Unable to convert mac");
        }
        StringBuilder sb = new StringBuilder(len * 3 - 1);
        for (int i = len; i > 0; --i) {
            if (i < len) sb.append(':');
            appendHexString(sb, data[st + i - 1]);
        }
        return sb.toString();
    }

    /**
     * 获得数据除以100
     */
    public static String getDataDivideByHundred(String s, boolean isGetInteger) {
        int num = MUtils.parseInt(s, 0);
        if (isGetInteger || (num % 100) == 0) {
            return Integer.toString(num / 100);
        } else {
            return String.format("%.1f", ((float) num / 100));
        }
    }

    /**
     * 获得数据除以10
     */
    public static String getDataDivideByTen(String s, boolean isGetInteger) {
        int num = MUtils.parseInt(s, 0);
        if (isGetInteger || (num % 10) == 0) {
            return Integer.toString(num / 10);
        } else {
            return String.format("%.1f", ((float) num / 10));
        }
    }

    /**
     * 获得数据除以100
     */
    public static String getDataDivideByHundredLimit(String s, boolean isGetInteger) {
        int num = MUtils.parseInt(s, 0);
        if (isGetInteger || (num % 100) == 0) {
            num = num / 100;
            if (num > 100) {
                num = 100;
            }
            return Integer.toString(num);
        } else {
            return String.format("%.1f", ((float) num / 100));
        }
    }

    private static int min(int one, int two, int three) {
        int min = one;
        if (two < min) {
            min = two;
        }
        if (three < min) {
            min = three;
        }
        return min;
    }

    private static int ld(String str1, String str2) {
        final int LEN1 = str1.length();
        final int LEN2 = str2.length();
        if (LEN1 == 0 || LEN2 == 0) {
            return Math.max(LEN1, LEN2);
        }

        int[][] matrix = new int[LEN1 + 1][LEN2 + 1];
        for (int row = 0; row <= LEN1; row++) { //初始化第一列
            matrix[row][0] = row;
        }
        for (int col = 0; col <= LEN2; col++) { //初始化第一行
            matrix[0][col] = col;
        }
        for (int row = 0; row < LEN1; ++row) { //遍历str1
            char ch = str1.charAt(row); //去匹配str2
            for (int col = 0; col < LEN2; ++col) {
                // 记录相同字符，在某个矩阵位置值的增量，不是0就是1
                int temp = (ch == str2.charAt(col)) ? 0 : 1;
                // 左边+1，上边+1，左上角+temp取最小
                matrix[row + 1][col + 1] = min(
                        matrix[row][col + 1] + 1,
                        matrix[row + 1][col] + 1,
                        matrix[row][col] + temp);
            }
        }
        return matrix[LEN1][LEN2];
    }

    /**
     * 计算两个字符串的相似性，接近于1相似度高
     */
    public static float sim(String str1, String str2) {
        str1 = Strings.nullToEmpty(str1);
        str2 = Strings.nullToEmpty(str2);
        float result = 1 - (float) ld(str1, str2) / Math.max(str1.length(), str2.length());
        if (MLog.DEBUG) MLog.out.d(TAG, "sim: " + result + " s1='" + str1 + "' s2='" + str2 + '\'');
        return result;
    }

    /**
     * 忽略大小写判断两个字符串是否相等
     */
    public static boolean equalsIgnoreCase(CharSequence a, CharSequence b) {
        int length;
        if (a == b) return true;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return ((String) a).equalsIgnoreCase((String) b);
            }

            for (int i = 0; i < length; i++) {
                char c1 = a.charAt(i);
                char c2 = b.charAt(i);
                if (c1 != c2 && foldCase(c1) != foldCase(c2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static char foldCase(char ch) {
        if (ch < 128) {
            if ('A' <= ch && ch <= 'Z') {
                return (char) (ch + ('a' - 'A'));
            }
            return ch;
        }
        return Character.toLowerCase(Character.toUpperCase(ch));
    }

    /**
     * 移除mac地址分隔符
     */
    public static String removeFormatMac(String mac) {
        if (TextUtils.isEmpty(mac)) {
            return "";
        }
        String[] str = mac.split(":");
        StringBuilder sb = new StringBuilder();
        for (int i = str.length - 1; i >= 0; i--) {
            sb.append(str[i]);
        }
        return sb.toString();
    }

    /**
     * 将mac地址以每两个字符分割转换为16进制形式
     */
    public static byte[] bytesFromRawHexStringByMac(String mac) {
        if (TextUtils.isEmpty(mac)) {
            return EMPTY_BYTES;
        }
        return bytesFromRawHexString(removeFormatMac(mac));
    }
}
