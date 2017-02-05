/*
 * Copyright (c) 2015 Meizu Technology Co., Ltd.
 * All rights reserved.
 */

package com.jrue.appframe.lib.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.jrue.appframe.lib.R;
import com.jrue.appframe.lib.base.BaseFragmentActivity;
import com.jrue.appframe.lib.util.MLog;
import com.jrue.appframe.lib.util.MUtils;

import java.lang.reflect.Field;

/**
 * 通用标题栏，默认灰色靠左显示。
 * <p>
 * 在Activity或者Fragment中的使用方法：
 * <pre>
 * TitleBarLayout bar = getTitleBarLayout();
 * bar.setTitleBackground(TitleBarLayout.TITLE_BACKGROUND_GRAY);
 * bar.setTitleGravity(TitleBarLayout.TITLE_GRAVITY_START);
 * bar.setTitleText(getString(R.string.home_list_title));
 * bar.setTitleEndButton(mLoadingDrawable, null, mLoadingOnClickListener);
 * </pre>
 * </p>
 */
@SuppressWarnings("ResourceType")
public class TitleBarLayout extends LinearLayout {
    private static final String TAG = "TitleBarLayout";
    private static final boolean DEBUG = false;

    private static final int SHIFT_PRIVATE_START = 0;
    private static final int SHIFT_PRIVATE_COUNT = 6;
    private static final int SHIFT_BACKGROUND_START = SHIFT_PRIVATE_START + SHIFT_PRIVATE_COUNT;
    private static final int SHIFT_BACKGROUND_COUNT = 6; // 背景允许使用的6位，也就是63种。（2^6-1）
    private static final int SHIFT_GRAVITY_START = SHIFT_BACKGROUND_START + SHIFT_BACKGROUND_COUNT;
    private static final int SHIFT_GRAVITY_COUNT = 4;
    private static final int SHIFT_FLAG_START = SHIFT_GRAVITY_START + SHIFT_GRAVITY_COUNT;
    private static final int SHIFT_FLAG_COUNT = 32 - SHIFT_FLAG_START;

    private static final int MASK_BACKGROUND = ((1 << SHIFT_BACKGROUND_COUNT) - 1) << SHIFT_BACKGROUND_START;
    private static final int MASK_GRAVITY = ((1 << SHIFT_GRAVITY_COUNT) - 1) << SHIFT_GRAVITY_START;
    private static final int MASK_FLAG = ((1 << SHIFT_FLAG_COUNT) - 1) << SHIFT_FLAG_START;

    private static final int TITLE_PRIVATE_CUSTOM = 1 << (SHIFT_PRIVATE_START);
    private static final int TITLE_PRIVATE_START_AS_BACK = 1 << (SHIFT_PRIVATE_START + 1);

    public static final int TITLE_BACKGROUND_GRAY = 1 << SHIFT_BACKGROUND_START;
    public static final int TITLE_BACKGROUND_BLUE = 2 << SHIFT_BACKGROUND_START;
    public static final int TITLE_BACKGROUND_DARK_BLUE = 3 << SHIFT_BACKGROUND_START;
    public static final int TITLE_BACKGROUND_TRANSLUCENT = 4 << SHIFT_BACKGROUND_START;
    public static final int TITLE_BACKGROUND_LIGHT_BLUE = 5 << SHIFT_BACKGROUND_START;
    public static final int TITLE_BACKGROUND_DEEP_BLUE = 6 << SHIFT_BACKGROUND_START;
    public static final int TITLE_BACKGROUND_DEEP_GRAY = 7 << SHIFT_BACKGROUND_START;
    public static final int TITLE_BACKGROUND_WHITE = 8 << SHIFT_BACKGROUND_START;

    public static final int TITLE_GRAVITY_START = 1 << SHIFT_GRAVITY_START;
    public static final int TITLE_GRAVITY_CENTER = 2 << SHIFT_GRAVITY_START;
    public static final int TITLE_GRAVITY_END = 3 << SHIFT_GRAVITY_START;

    public static final int TITLE_FLAG_BOTTOM_LINE = 1 << (SHIFT_FLAG_START);

    private static final SparseIntArray sGravityMap;
    private static final SparseIntArray sBackgroundMap;

    static {
        sGravityMap = new SparseIntArray();
        sGravityMap.put(TITLE_GRAVITY_START, Gravity.START | Gravity.CENTER_VERTICAL);
        sGravityMap.put(TITLE_GRAVITY_CENTER, Gravity.CENTER);
        sGravityMap.put(TITLE_GRAVITY_END, Gravity.END | Gravity.CENTER_VERTICAL);

        sBackgroundMap = new SparseIntArray();
        sBackgroundMap.put(TITLE_BACKGROUND_GRAY, R.color.window_background_color_gray);
        sBackgroundMap.put(TITLE_BACKGROUND_DARK_BLUE, R.color.window_background_color_dark_blue);
        sBackgroundMap.put(TITLE_BACKGROUND_TRANSLUCENT, R.color.window_background_color_translucent);
        sBackgroundMap.put(TITLE_BACKGROUND_LIGHT_BLUE, R.color.window_background_color_light_blue);
        sBackgroundMap.put(TITLE_BACKGROUND_DEEP_BLUE, R.color.window_background_color_deep_blue);
        sBackgroundMap.put(TITLE_BACKGROUND_DEEP_GRAY, R.color.window_background_color_deep_gray);
        sBackgroundMap.put(TITLE_BACKGROUND_WHITE, R.color.window_background_color_white);
        sBackgroundMap.put(TITLE_BACKGROUND_BLUE, R.color.window_background_color_blue);
    }

    private int mTitleFlags;
    private View mDividerView;
    private View mCustomTitleView;
    private TextView mStartTextView, mEndTextView, mLeftTextView, mRightTextView;
    private TextView mStartHintTextView, mEndHintTextView, mLeftHintTextView, mRightHintTextView; //幽灵控件用来计算大小
    private FrameLayout mStartFrame, mCenterFrame, mEndFrame, mLeftFrame, mRightFrame;
    private int mTitlePadding, mTitleGap;

    private int mBackdoorCode;

    private long mLastTouchTime;

    private BaseFragmentActivity.OnBackdoorListener mOnBackdoorListener;

    public TitleBarLayout(Context context) {
        this(context, null);
    }

    public TitleBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 是否设置状态栏图标为黑色主题
     *
     * @param on true-设置为黑色主题；false-恢复白色主题
     */
    private static boolean setDarkStatusBarIcon(Window win, boolean on) {
        WindowManager.LayoutParams winParams = win.getAttributes();
        try {
            final int MEIZU_FLAG_DARK_STATUS_BAR_ICON = WindowManager.LayoutParams.class
                    .getField("MEIZU_FLAG_DARK_STATUS_BAR_ICON").getInt(null);
            final Field meizuFlagsField = WindowManager.LayoutParams.class.getField("meizuFlags");
            final int meizuFlags = meizuFlagsField.getInt(winParams);
            if (on == ((meizuFlags & MEIZU_FLAG_DARK_STATUS_BAR_ICON) != 0)) {
                return true;
            }
            if (on) {
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
     * 获取标题栏中对应id的控件，如果不存在则使用layoutId生成。
     */
    private static View getTopBarView(FrameLayout frame, int viewId, int layoutId) {
        View myView = frame.findViewById(viewId);
        if (myView == null) {
            myView = LayoutInflater.from(frame.getContext()).inflate(layoutId, frame, false);
            if (myView == null) return null;
            myView = myView.findViewById(viewId);
        }

        boolean found = false;
        final int N = frame.getChildCount();
        for (int i = 0; i < N; i++) {
            View child = frame.getChildAt(i);
            if (child.findViewById(viewId) != null) {
                found = true;
                child.setVisibility(View.VISIBLE);
            } else {
                child.setVisibility(View.INVISIBLE);
            }
        }
        if (!found) frame.addView(myView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        return myView;
    }

    /**
     * 判断背景是否需要深色的图标
     */
    private static boolean needDarkResource(int background) {
        boolean dark = (background == TITLE_BACKGROUND_GRAY ||
                background == TITLE_BACKGROUND_DEEP_GRAY ||
                background == TITLE_BACKGROUND_WHITE);
        if (DEBUG) MLog.zlx.d(TAG, "needDarkResource: " + dark);
        return dark;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTitlePadding = getResources().getDimensionPixelOffset(R.dimen.spacing_horizontal_normal);
        mTitleGap = getResources().getDimensionPixelOffset(R.dimen.spacing_horizontal_large);

        mStartFrame = (FrameLayout) findViewById(R.id.widgetTitleStartFrame);
        mStartTextView = (TextView) mStartFrame.findViewById(R.id.widgetTitleTextView);
        mStartHintTextView = (TextView) mStartFrame.findViewById(R.id.widgetTitleHintTextView);

        mLeftFrame = (FrameLayout) findViewById(R.id.widgetTitleLeftFrame);
        mLeftTextView = (TextView) mLeftFrame.findViewById(R.id.widgetTitleTextView);
        mLeftHintTextView = (TextView) mLeftFrame.findViewById(R.id.widgetTitleHintTextView);

        mCenterFrame = (FrameLayout) findViewById(R.id.widgetTitleCenterFrame);

        mEndFrame = (FrameLayout) findViewById(R.id.widgetTitleEndFrame);
        mEndTextView = (TextView) mEndFrame.findViewById(R.id.widgetTitleTextView);
        mEndHintTextView = (TextView) mEndFrame.findViewById(R.id.widgetTitleHintTextView);


        mRightFrame = (FrameLayout) findViewById(R.id.widgetTitleRightFrame);
        mRightTextView = (TextView) mRightFrame.findViewById(R.id.widgetTitleTextView);
        mRightHintTextView = (TextView) mRightFrame.findViewById(R.id.widgetTitleHintTextView);

        mDividerView = findViewById(R.id.widgetTitleDividerView);

        updateBackground();
        updateGravity();
        updateDividerVisibility();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handled = super.dispatchTouchEvent(ev);
        if (!handled && mOnBackdoorListener != null) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                long now = SystemClock.elapsedRealtime();
                if (now > mLastTouchTime + 800L) {
                    mBackdoorCode = 0;
                }

                mBackdoorCode <<= 1;
                if (ev.getX() > (getWidth() >> 1)) {
                    mBackdoorCode |= 1;
                }

                if (mOnBackdoorListener.onBackdoor(mBackdoorCode)) {
                    mLastTouchTime = mBackdoorCode = 0;
                } else {
                    mLastTouchTime = now;
                }
            }
            handled = true;
        }
        return handled;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateBackground();
    }

    /**
     * 设置标题对齐方式
     * <p>
     * 标题的对齐方式：
     * <ul>
     * <li>{@link #TITLE_GRAVITY_START}标题靠左，此时只能显示右侧按钮</li>
     * <li>{@link #TITLE_GRAVITY_CENTER}标题居中，可以显示两侧按键</li>
     * <li>{@link #TITLE_GRAVITY_END}标题靠右，此时只能显示左侧按钮</li>
     * </ul>
     * </p>
     *
     * @param gravity 对齐方式
     */
    public void setTitleGravity(int gravity) {
        setFlagsInner(gravity, MASK_GRAVITY);
        updateGravity();
    }

    /**
     * 设置标题标识位
     */
    public void setTitleFlags(int flags) {
        setFlagsInner(flags, MASK_FLAG);
        setFlagsInner(TITLE_PRIVATE_CUSTOM, TITLE_PRIVATE_CUSTOM);
        updateDividerVisibility();
    }

    /**
     * 获取标题标识位
     */
    public int getTitleFlags() {
        return mTitleFlags & MASK_FLAG;
    }

    /**
     * 设置标题栏标题
     *
     * @param text 标题文本
     */
    public void setTitleText(CharSequence text) {
        if (mCustomTitleView != null) {
            MLog.out.e(TAG, "setTitleText: Please use your custom view!!!");
            return;
        }
        TextView textView = (TextView) getTopBarView(
                mCenterFrame, R.id.widgetTitleTextView,
                R.layout.top_bar_item_text_view);
        if (textView == null) return;
        textView.setGravity(sGravityMap.get((mTitleFlags & MASK_GRAVITY), Gravity.CENTER));
        textView.setTextColor(getResources().getColorStateList(
                needDarkResource(getTitleBackground()) ?
                        R.color.title_bar_text_color_gray :
                        R.color.title_bar_text_color));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(
                        R.dimen.title_bar_text_size_title));
        textView.setText(text);
    }

    /**
     * 设置用户自定义标题栏显示
     *
     * @param view 用户自定义控件
     */
    public void setTitleView(View view) {
        if (mCustomTitleView == view) {
            return;
        }
        mCustomTitleView = view;
        mCenterFrame.removeAllViews();
        if (view != null) mCenterFrame.addView(view, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    /**
     * 设置用户自定义标题栏显示
     */
    public void setTitleView(int resource) {
        mCustomTitleView = LayoutInflater.from(getContext())
                .inflate(resource, mCenterFrame, false);

        ViewGroup.LayoutParams lp = mCustomTitleView.getLayoutParams();
        if (!(lp instanceof FrameLayout.LayoutParams)) {
            lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        }

        mCenterFrame.removeAllViews();
        mCenterFrame.addView(mCustomTitleView, lp);
    }

    /**
     * 更新标题栏左侧侧按键的显示文本
     *
     * @param text 按键显示文本
     */
    public void setTitleStartButtonText(String text) {
        setFlagsInner(0, TITLE_PRIVATE_START_AS_BACK);
        mStartTextView.setText(text);
        mEndHintTextView.setText(text);
    }

    /**
     * 更新标题栏左侧按键的显示文本
     *
     * @param text 按键显示文本
     */
    public void setTitleLeftButtonText(String text) {
        mLeftTextView.setText(text);
        mRightHintTextView.setText(text);
    }

    /**
     * 更新标题栏右侧按键的显示文本
     *
     * @param text 按键显示文本
     */
    public void setTitleEndButtonText(String text) {
        mEndTextView.setText(text);
        mStartHintTextView.setText(text);
    }

    /**
     * 更新标题栏右侧按键的显示文本
     *
     * @param text 按键显示文本
     */
    public void setTitleRightButtonText(String text) {
        mRightTextView.setText(text);
        mLeftHintTextView.setText(text);
    }

    public final void setOnBackdoorListener(BaseFragmentActivity.OnBackdoorListener listener) {
        mOnBackdoorListener = listener;
    }

    private void setButton(FrameLayout frame, Drawable background, String text,
                           OnClickListener listener) {
        if (listener == null) {
            frame.setVisibility((mTitleFlags & MASK_GRAVITY) == TITLE_GRAVITY_CENTER ? INVISIBLE : GONE);
        } else {
            frame.setOnClickListener(listener);
            frame.setVisibility(VISIBLE);
            TextView textView = (TextView) getTopBarView(
                    frame, R.id.widgetTitleTextView,
                    R.layout.top_bar_item_text_view);
            if (textView == null) return;
            textView.setTextColor(getResources()
                    .getColorStateList(R.color.title_bar_text_color));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimensionPixelSize(
                            R.dimen.title_bar_text_size_button));
            textView.setBackground(background);
            textView.setText(text);
        }
    }

    /**
     * 设置顶栏的开始按键为BACK
     */
    public void setTitleStartButtonAsBackKey() {
        mStartTextView.setText(null);
        mEndHintTextView.setText(null);

        final int resource = needDarkResource(getTitleBackground()) ?
                R.drawable.ic_arrow_left : R.drawable.ic_arrow_left_white;
        mStartTextView.setBackgroundResource(resource);
        mEndHintTextView.setBackgroundResource(resource);

        mStartFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MUtils.simulateKeyEvent(KeyEvent.KEYCODE_BACK);
            }
        });

        setFlagsInner(TITLE_PRIVATE_START_AS_BACK, TITLE_PRIVATE_START_AS_BACK);
        setTitleStartButtonVisibility(VISIBLE);
    }

    /**
     * 更新标题栏右侧按键的显示文本
     *
     * @param color 设置文本颜色
     */
    public void setTitleLeftButtonTextColor(int color) {
        mLeftTextView.setTextColor(color);
        mRightHintTextView.setTextColor(color);
    }

    /**
     * 更新标题栏右侧按键的显示文本
     *
     * @param color 设置文本颜色
     */
    public void setTitleEndButtonTextColor(int color) {
        mEndTextView.setTextColor(color);
        mStartHintTextView.setTextColor(color);
    }

    /**
     * 更新标题栏右侧按键的显示文本大小
     *
     * @param size 文字大小
     */
    public void setTitleEndButtonTextSize(int size) {
        mEndTextView.setTextSize(size);
        mStartHintTextView.setTextSize(size);
    }

    /**
     * 更新标题栏右侧按键的显示文本
     *
     * @param color 设置文本颜色
     */
    public void setTitleRightButtonTextColor(int color) {
        mRightTextView.setTextColor(color);
        mLeftHintTextView.setTextColor(color);
    }

    /**
     * 更新标题栏左侧侧按键的显示背景
     *
     * @param drawable 按键背景
     */
    public void setTitleStartButtonDrawable(Drawable drawable) {
        setFlagsInner(0, TITLE_PRIVATE_START_AS_BACK);
        mStartTextView.setBackground(drawable);
        mEndHintTextView.setBackground(drawable);
    }

    /**
     * 更新标题栏左侧侧按键的显示背景
     *
     * @param resourceId 按键背景
     */
    public void setTitleStartButtonDrawable(int resourceId) {
        setFlagsInner(0, TITLE_PRIVATE_START_AS_BACK);
        mStartTextView.setBackgroundResource(resourceId);
        mEndHintTextView.setBackgroundResource(resourceId);
    }

    /**
     * 更新标题栏左侧侧按键的显示背景
     *
     * @param drawable 按键背景
     */
    public void setTitleLeftButtonDrawable(Drawable drawable, boolean isSetPadding) {
        mLeftTextView.setBackground(drawable);
        mRightHintTextView.setBackground(drawable);
    }

    /**
     * 增加标题栏左侧显示背景动画
     *
     * @param context 上下文
     */
    public void setTitleLeftButtonAnim(Context context) {
        clearAnimation();
        mLeftTextView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.ir_rotate_animation));
    }

    /**
     * 更新标题栏右侧按键的显示背景
     *
     * @param drawable 按键背景
     */
    public void setTitleEndButtonDrawable(Drawable drawable) {
        mEndTextView.setBackground(drawable);
        //  mStartHintTextView.setBackground(drawable);
    }

    /**
     * 更新标题栏右侧按键的显示背景
     *
     * @param resourceId 资源id
     */
    public void setTitleEndButtonDrawable(int resourceId) {
        mEndTextView.setBackgroundResource(resourceId);
        //  mStartHintTextView.setBackgroundResource(resourceId);
    }

    /**
     * 更新标题栏右侧按键的显示背景
     *
     * @param drawable 按键背景
     */
    public void setTitleRightButtonDrawable(Drawable drawable) {
        mRightTextView.setBackground(drawable);
        mLeftHintTextView.setBackground(drawable);
    }

    /**
     * 更新标题栏左侧侧按键的点击响应方法
     *
     * @param listener 点击响应方法
     */
    public void setTitleStartButtonOnClickListener(OnClickListener listener) {
        setFlagsInner(0, TITLE_PRIVATE_START_AS_BACK);
        mStartFrame.setOnClickListener(listener);
    }

    /**
     * 更新标题栏左侧按键的点击响应方法
     *
     * @param listener 点击响应方法
     */
    public void setTitleLeftButtonOnClickListener(OnClickListener listener) {
        mLeftFrame.setOnClickListener(listener);
    }

    /**
     * 更新标题栏右侧按键的点击响应方法
     *
     * @param listener 点击响应方法
     */
    public void setTitleEndButtonOnClickListener(OnClickListener listener) {
        mEndFrame.setOnClickListener(listener);
    }

    /**
     * 更新标题栏右侧按键的点击响应方法
     *
     * @param listener 点击响应方法
     */
    public void setTitleRightButtonOnClickListener(OnClickListener listener) {
        mRightFrame.setOnClickListener(listener);
    }

    /**
     * 更新标题栏左侧按键的可见状态
     *
     * @param visibility 控件可见状态
     */
    public void setTitleStartButtonVisibility(int visibility) {
        mStartFrame.setVisibility(visibility);
        updateFrameVisibility(mStartFrame);
        updateFramePadding();
    }

    /**
     * 更新标题栏左侧按键的可见状态
     *
     * @param visibility 控件可见状态
     */
    public void setTitleLeftButtonVisibility(int visibility) {
        mLeftFrame.setVisibility(visibility);
        updateFrameVisibility(mLeftFrame);
        updateFramePadding();
    }

    /**
     * 更新标题栏右侧按键的可见状态
     *
     * @param visibility 控件可见状态
     */
    public void setTitleEndButtonVisibility(int visibility) {
        mEndFrame.setVisibility(visibility);
        updateFrameVisibility(mEndFrame);
        updateFramePadding();
    }

    /**
     * 更新标题栏右侧按键的可见状态
     *
     * @param visibility 控件可见状态
     */
    public void setTitleRightButtonVisibility(int visibility) {
        mRightFrame.setVisibility(visibility);
        updateFrameVisibility(mRightFrame);
        updateFramePadding();
    }

    private void setButtonVisibility(FrameLayout frame, int flag) {
        if (flag == VISIBLE) {
            frame.setVisibility(VISIBLE);
        } else if (flag == INVISIBLE) {
            frame.setVisibility(INVISIBLE);
        } else if (flag == GONE) {
            frame.setVisibility(GONE);
        }
    }

    private int getTitleBackground() {
        return mTitleFlags & MASK_BACKGROUND;
    }

    /**
     * 设置标题栏背景
     * <p>
     * 状态栏和窗口的背景：
     * <ul>
     * <li>{@link #TITLE_BACKGROUND_GRAY}灰色</li>
     * <li>{@link #TITLE_BACKGROUND_BLUE}蓝色</li>
     * </ul>
     * </p>
     *
     * @param background 状态栏和窗口的背景
     */
    public void setTitleBackground(int background) {
        setFlagsInner(background & MASK_BACKGROUND, MASK_BACKGROUND);
        setTitleBackgroundDrawable(null);
        updateBackground();
    }

    private Drawable mTitleBackgroundDrawable;

    public void setTitleBackgroundDrawable(Drawable drawable) {
        Window w = getWindow();
        if (w != null) {
            ImageView imageView = (ImageView) w.findViewById(R.id.baseBackgroundImageView);
            if (imageView != null) {
                imageView.setImageDrawable(drawable);
                if (drawable == null) {
                    imageView.setVisibility(GONE);
                } else {
                    imageView.setVisibility(VISIBLE);
                }
            }
        }
        mTitleBackgroundDrawable = drawable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mTitleBackgroundDrawable != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int wImage = mTitleBackgroundDrawable.getIntrinsicWidth();
            if (width > 0 && wImage > 0) {
                int hImage = mTitleBackgroundDrawable.getIntrinsicHeight();
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(width * hImage / wImage - getTop(), MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setFlagsInner(int flags, int mask) {
        final int oldFlags = mTitleFlags;
        mTitleFlags = (oldFlags & ~mask) | (flags & mask);
        if (DEBUG && oldFlags != mTitleFlags) MLog.zlx.d(TAG, "setFlagsInner:" +
                " flags=" + Integer.toHexString(flags) +
                " mask=" + Integer.toHexString(flags) +
                " old=" + Integer.toHexString(oldFlags) +
                " new=" + Integer.toHexString(mTitleFlags));
    }

    private Window getWindow() {
        Context context = getContext();
        if (context != null && context instanceof Activity) {
            return ((Activity) context).getWindow();
        }
        return null;
    }

    private void updateFrameVisibility(FrameLayout frame) {
        if (frame.getVisibility() != VISIBLE) {
            if ((mTitleFlags & MASK_GRAVITY) == TITLE_GRAVITY_CENTER) {
                frame.setVisibility(INVISIBLE);
            } else {
                frame.setVisibility(GONE);
            }
        }
    }

    private void updateGravity() {
        final int titleGravity = mTitleFlags & MASK_GRAVITY;
        final int N = mCenterFrame.getChildCount();
        for (int i = 0; i < N; i++) {
            View child = mCenterFrame.getChildAt(i);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
            lp.gravity = sGravityMap.get(titleGravity, Gravity.START | Gravity.CENTER);
            child.setLayoutParams(lp);
        }
        mCenterFrame.setVisibility(VISIBLE);
        updateFrameVisibility(mStartFrame);
        updateFrameVisibility(mLeftFrame);
        updateFrameVisibility(mEndFrame);
        updateFrameVisibility(mRightFrame);
        updateFramePadding();
    }

    private void updateBackground() {
        Window window = getWindow();
        if (window == null) return;

        int titleBackground = getTitleBackground();
        int color = sBackgroundMap.get(titleBackground,
                R.color.window_background_color_blue);
        window.setBackgroundDrawableResource(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        boolean dark = needDarkResource(titleBackground);
        setDarkStatusBarIcon(window, dark);
        if ((mTitleFlags & TITLE_PRIVATE_CUSTOM) == 0) {
            setFlagsInner(dark ? TITLE_FLAG_BOTTOM_LINE : 0, TITLE_FLAG_BOTTOM_LINE);
            updateDividerVisibility();
        }
        if ((mTitleFlags & TITLE_PRIVATE_START_AS_BACK) != 0) {
            int resource = dark ? R.drawable.ic_arrow_left : R.drawable.ic_arrow_left_white;
            mStartTextView.setBackgroundResource(resource);
            mEndHintTextView.setBackgroundResource(resource);
        }
    }

    private void updateDividerVisibility() {
        if ((mTitleFlags & TITLE_FLAG_BOTTOM_LINE) == 0) {
            mDividerView.setVisibility(View.GONE);
        } else {
            mDividerView.setVisibility(View.VISIBLE);
        }
    }

    private void setFramePadding(FrameLayout frame, int paddingLeft, int paddingRight) {
        if (frame.getPaddingLeft() == paddingLeft &&
                frame.getPaddingRight() == paddingRight) {
            return;
        }
        int top = frame.getPaddingTop();
        int bottom = frame.getPaddingBottom();
        frame.setPadding(paddingLeft, top, paddingRight, bottom);
    }

    private void updateFramePadding() {
        boolean startGone = mStartFrame.getVisibility() == GONE;
        boolean leftGone = mLeftFrame.getVisibility() == GONE;
        boolean endGone = mEndFrame.getVisibility() == GONE;
        boolean rightGone = mRightFrame.getVisibility() == GONE;
        if (startGone && endGone && leftGone && rightGone) {
            setFramePadding(mCenterFrame, mTitlePadding, mTitlePadding);
        } else if (startGone) {
            setFramePadding(mCenterFrame, mTitlePadding, 0);
            setFramePadding(mEndFrame, mTitleGap, mTitlePadding);
        } else if (endGone) {
            setFramePadding(mStartFrame, mTitlePadding, mTitleGap);
            setFramePadding(mCenterFrame, 0, mTitlePadding);
        } else {
            setFramePadding(mStartFrame, mTitlePadding, mTitleGap);
            setFramePadding(mCenterFrame, 0, 0);
            setFramePadding(mEndFrame, mTitleGap, mTitlePadding);
        }
    }
}
