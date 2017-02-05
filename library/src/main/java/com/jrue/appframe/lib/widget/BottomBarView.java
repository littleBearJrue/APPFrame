package com.jrue.appframe.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.jrue.appframe.lib.R;
import com.jrue.appframe.lib.base.BaseFragment;
import com.jrue.appframe.lib.util.MLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 底部显示栏
 */
public class BottomBarView extends LinearLayout {
    private static final String TAG = "BottomBarView";

    /**
     * Interface definition for a callback to be invoked when tab changed
     */
    public interface OnItemChangeListener {
        void onItemChanged(int index);
    }

    private int mDividerHeight;

    private MenuItem mCurrentItem;

    private OnItemChangeListener mOnItemChangeListener;

    private int mDividerPaintColor = 0xFFCFCFCF;

    private final Paint mDividerPaint;

    private final List<MenuItem> mMenuItemList = new ArrayList<>();

    public BottomBarView(Context context) {
        this(context, null);
    }

    public BottomBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(HORIZONTAL);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomBarView, defStyle, 0);

        mDividerPaint = new Paint();
        mDividerPaint.setColor(typedArray.getColor(R.styleable.BottomBarView_bbvDividerColor, mDividerPaintColor));
        mDividerPaint.setStrokeWidth(typedArray.getDimensionPixelSize(R.styleable.BottomBarView_bbvDividerWidth, 4));
        mDividerHeight = typedArray.getDimensionPixelSize(R.styleable.BottomBarView_bbvDividerHeight, 108);

        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int N = getChildCount();
        if (N <= 0 || isInEditMode()) {
            return;
        }

        final int top = (getHeight() - mDividerHeight) / 2;
        for (int i = 0; i < N; i++) {
            final int x = getChildAt(i).getRight();
            canvas.drawLine(x, top, x, top + mDividerHeight, mDividerPaint);
        }
    }

    public void setDividerPaintColor(int defValue) {
        mDividerPaint.setColor(defValue);
    }

    public final void setOnItemChangeListener(OnItemChangeListener listener) {
        mOnItemChangeListener = listener;
    }

    public final int getCurrentItem() {
        if (mCurrentItem == null) return -1;
        return mMenuItemList.indexOf(mCurrentItem);
    }

    public final void setCurrentItem(int index) {
        MenuItem current = null;
        if (index >= 0 && index < mMenuItemList.size()) {
            current = mMenuItemList.get(index);
        }
        if (mCurrentItem != current) {
            setCurrentItem(mCurrentItem, false);
            setCurrentItem(current, true);
            mCurrentItem = current;
            if (mOnItemChangeListener != null) {
                mOnItemChangeListener.onItemChanged(index);
            }
        }
    }

    public final MenuEdit beginMenuEdit() {
        return new MenuEdit(this);
    }

    private void commitMenu(MenuEdit edit) {
        if (mMenuItemList.equals(edit.mItemList)) {
            return;
        }

        removeAllViews();
        mMenuItemList.clear();
        for (MenuItem item : edit.mItemList) {
            addMenuItem(item);
        }
    }


    public void setTextSize(float fontSize, MenuEdit edit) {
        removeAllViews();
        mMenuItemList.clear();
        for (MenuItem item : edit.mItemList) {
            addMenuItem(setTextViewSize(fontSize, item));
        }
    }

    public void setImageSize(int width, int height, MenuEdit edit) {
        removeAllViews();
        mMenuItemList.clear();
        for (MenuItem item : edit.mItemList) {
            addMenuItem(setImageViewSize(width, height, item));
        }
    }

    public void setTextColor(int color, MenuEdit edit) {
        removeAllViews();
        mMenuItemList.clear();
        for (MenuItem item : edit.mItemList) {
            addMenuItem(setTextViewColor(color, item));
        }
    }

    private MenuItem setImageViewSize(int width, int height, MenuItem item) {
        View view;
        if (item.view == null) {
            view = LayoutInflater.from(getContext()).inflate(item.layout, null);
        } else {
            view = item.view;
        }

        ImageView iv = (ImageView) view.findViewById(R.id.tabItemImageView);
        iv.setLayoutParams(new LayoutParams(width, height));
        return item;
    }

    private MenuItem setTextViewSize(float fontSize, MenuItem item) {
        View view;
        if (item.view == null) {
            view = LayoutInflater.from(getContext()).inflate(item.layout, null);
        } else {
            view = item.view;
        }

        TextView tv = (TextView) view.findViewById(R.id.tabItemTextView);
        tv.setTextSize(fontSize);
        return item;
    }

    private MenuItem setTextViewColor(int color, MenuItem item) {
        View view;
        if (item.view == null) {
            view = LayoutInflater.from(getContext()).inflate(item.layout, null);
        } else {
            view = item.view;
        }

        TextView tv = (TextView) view.findViewById(R.id.tabItemTextView);
        tv.setTextColor(color);
        return item;
    }

    public View getItemView(MenuEdit edit, int position) {
        if (edit != null && edit.mItemList.size() > position && position >= 0) {
            MenuItem item = edit.mItemList.get(position);
            if (item != null && item.view != null)
                return item.view;
        }
        return null;
    }

    private void addMenuItem(MenuItem item) {
        View view;
        if (item.view == null) {
            view = LayoutInflater.from(getContext()).inflate(item.layout, null);
        } else {
            view = item.view;
        }

        TextView tv = (TextView) view.findViewById(R.id.tabItemTextView);
        if (tv == null) {
            if (MLog.DEBUG) MLog.out.w(TAG,
                    "addMenuItem: tabItemTextView is null");
        } else if (TextUtils.isEmpty(item.text)) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(item.text);
        }
        ImageView iv = (ImageView) view.findViewById(R.id.tabItemImageView);
        if (iv == null) {
            if (MLog.DEBUG) MLog.out.w(TAG,
                    "addMenuItem: tabItemImageView is null");
        } else if (item.icon == 0) {
            iv.setVisibility(View.GONE);
        } else {
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(item.icon);
        }

        view.setFocusable(true);
        view.setOnClickListener(item.listener);

        item.view = view;
        mMenuItemList.add(item);

        addView(view, -1, new LayoutParams(0,
                FrameLayout.LayoutParams.MATCH_PARENT, 1.0f));
    }

    private void setCurrentItem(MenuItem item, boolean focused) {
        if (item != null && item.view != null) {
            item.view.setSelected(focused);
        }
    }

    private static final class MenuItem {
        final int icon;
        final int layout;
        final CharSequence text;
        final OnClickListener listener;
        View view;

        /* package */ MenuItem(int layout, CharSequence text, int iconResId, OnClickListener l) {
            this.text = text;
            this.icon = iconResId;
            this.listener = l;
            this.layout = layout;
        }

        /* package */ MenuItem(int layout, CharSequence text, int iconResId, Class<? extends BaseFragment> clazz) {
            this.text = text;
            this.icon = iconResId;
            this.listener = null;
            this.layout = layout;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MenuItem)) return false;
            MenuItem menuItem = (MenuItem) o;
            return Objects.equal(icon, menuItem.icon) &&
                    Objects.equal(layout, menuItem.layout) &&
                    Objects.equal(text, menuItem.text) &&
                    Objects.equal(listener, menuItem.listener);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(icon, layout, text, listener);
        }
    }

    public static final class MenuEdit {
        private final BottomBarView mView;
        private final List<MenuItem> mItemList;

        /* package */ MenuEdit(BottomBarView bbv) {
            mView = bbv;
            mItemList = new ArrayList<>(5);
        }

        public final MenuEdit addItem(int textResId, int iconResId, OnClickListener l) {
            return addItem(mView.getResources().getText(textResId), iconResId, l);
        }

        public final MenuEdit addItem(CharSequence text, int iconResId, OnClickListener l) {
            return addItem(R.layout.bottom_bar_item, text, iconResId, l);
        }

        public final MenuEdit addItem(int layout, CharSequence text, int iconResId, OnClickListener l) {
            mItemList.add(new MenuItem(layout, text, iconResId, l));
            return this;
        }

        public final MenuEdit addItem(int layout, CharSequence text, int iconResId, Class<? extends BaseFragment> clazz) {
            mItemList.add(new MenuItem(layout, text, iconResId, clazz));
            return this;
        }

        public final MenuEdit clearItem() {
            if (mItemList != null && mItemList.size() > 0)
                mItemList.clear();
            return this;
        }

        public final void commit() {
            mView.commitMenu(this);
        }
    }
}
