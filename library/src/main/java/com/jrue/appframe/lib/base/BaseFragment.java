package com.jrue.appframe.lib.base;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.jrue.appframe.lib.R;
import com.jrue.appframe.lib.util.MEvent;
import com.jrue.appframe.lib.util.MLog;
import com.jrue.appframe.lib.widget.BottomBarView;
import com.jrue.appframe.lib.widget.TitleBarLayout;

import butterknife.ButterKnife;

/**
 *  * 显示模块基类，Activity统一使用fragment显示。
 * <p>
 * 子类一般需要重写{@code onLocalServiceChanged}方法，当服务发生变化时更新窗口显示。
 * 使用{@code getTitleBarLayout}获取标题栏。
 * Created by jrue on 17/2/5.
 */
public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    private View mSavedView;

    private Handler mHandler;

    private BaseCursor mCursor;

    private boolean mAutoRegisterEvent = MEvent.DEFAULT_AUTO_REGISTER;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCursor(onNewCursor());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setCursor(null);
    }

    /**
     * 请优先使用{@link #mzOnCreateView(LayoutInflater, ViewGroup)}
     */
    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = mSavedView;
        if (view == null) {
            view = mzOnCreateView(inflater, container);
            if (view != null) {
                ButterKnife.bind(this, view);
                mzOnViewCreated(view);
            }
        } else {
            ViewParent p = view.getParent();
            if (p instanceof ViewGroup) {
                ((ViewGroup) p).removeView(view);
            }
            ButterKnife.bind(this, view);
        }
        mSavedView = view;
        return view;
    }

    /**
     * 请优先使用{@link #mzOnViewCreated(View)}
     */
    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAutoRegisterEvent) {
            MEvent.register(this);
        }
    }

    @Override
    public void onPause() {
        if (mAutoRegisterEvent) {
            MEvent.unregister(this);
        }
        super.onPause();
    }

    protected View mzOnCreateView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    protected void mzOnViewCreated(View view) {
    }

    protected final void addFragmentToBackStack(Fragment fragment) {
        if (MLog.DEBUG) MLog.out.d(TAG, "addFragmentToBackStack: " + fragment);
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public final BaseFragmentActivity getBaseActivity() {
        return (BaseFragmentActivity) getActivity();
    }

    /**
     * 获取当前窗口的标题栏
     */
    public final TitleBarLayout getTitleBarLayout() {
        return getBaseActivity().getTitleBarLayout();
    }

    /**
     * 获取当前窗口的底栏
     */
    public final BottomBarView getBottomBarView() {
        return getBaseActivity().getBottomBarView();
    }

    protected final void setAutoRegisterEvent(boolean enable) {
        if (MLog.DEBUG) MLog.out.d(TAG, "setAutoRegisterEvent: " + enable);
        mAutoRegisterEvent = enable;
    }

    protected final Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    protected Cursor onNewCursor() {
        return null;
    }

    public final Cursor getCursor() {
        if (mCursor != null) {
            return mCursor.getCursor();
        }
        return null;
    }

    public final void setCursor(Cursor cursor) {
        if (mCursor != null) {
            Cursor cursorPre = mCursor.setCursor(cursor);
            if (cursorPre != null) cursorPre.close();
        } else if (cursor != null) {
            mCursor = new MyCursor();
            mCursor.setCursor(cursor);
        }
    }

    public final Cursor getCursorAt(int position) {
        Cursor cursor = getCursor();
        if (cursor != null && cursor.moveToPosition(position)) {
            return cursor;
        }
        return null;
    }

    protected boolean onCursorContentChanged() {
        return false;
    }

    protected void onCursorDataSetChanged() {
    }

    protected void onCursorDataSetInvalidated() {
    }


    private class MyCursor extends BaseCursor {
        public MyCursor() {
            super(mHandler);
        }

        @Override
        protected void onContentChanged() {
            if (!onCursorContentChanged()) {
                super.onContentChanged();
            }
        }

        @Override
        protected void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            onCursorDataSetChanged();
        }

        @Override
        protected void notifyDataSetInvalidated() {
            super.notifyDataSetInvalidated();
            onCursorDataSetInvalidated();
        }
    }
}
