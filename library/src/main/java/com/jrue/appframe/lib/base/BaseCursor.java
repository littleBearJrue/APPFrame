package com.jrue.appframe.lib.base;

import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;

import com.jrue.appframe.lib.util.MLog;


/**
 * Cursor包装类
 * <p/>
 * Created by jrue on 2/5/17.
 */
public class BaseCursor {
    private static final String TAG = "BaseCursor";

    private final ContentObserver mContentObserver;
    private final DataSetObserver mDataSetObserver;
    private Cursor mCursor;
    private boolean mDataValid;

    public BaseCursor(Handler handler) {
        mContentObserver = new ContentObserver(handler) {
            @Override
            public boolean deliverSelfNotifications() {
                return true;
            }

            @Override
            public void onChange(boolean selfChange) {
                if (MLog.DEBUG) MLog.out.d(TAG, "onCharacteristicChange: selfChange=" + selfChange);
                onContentChanged();
            }
        };

        mDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                if (MLog.DEBUG) MLog.out.d(TAG, "onChanged:");
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                if (MLog.DEBUG) MLog.out.d(TAG, "onInvalidated:");
                notifyDataSetInvalidated();
            }
        };
    }

    public final boolean isValid() {
        return mDataValid;
    }

    public final Cursor getCursor() {
        if (mDataValid && mCursor != null) {
            return mCursor;
        }
        return null;
    }

    public final Cursor setCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        if (MLog.DEBUG) MLog.out.d(TAG, "setCursor: old=" + mCursor + " new=" + newCursor);
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            oldCursor.unregisterContentObserver(mContentObserver);
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            newCursor.registerContentObserver(mContentObserver);
            newCursor.registerDataSetObserver(mDataSetObserver);
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyDataSetInvalidated();
        }
        return oldCursor;
    }

    @SuppressWarnings("deprecation")
    protected void onContentChanged() {
        if (mCursor != null && !mCursor.isClosed()) {
            if (MLog.DEBUG) MLog.out.d(TAG, "onContentChanged: Auto requery");
            mDataValid = mCursor.requery();
        }
    }

    protected void notifyDataSetChanged() {
    }

    protected void notifyDataSetInvalidated() {
    }
}
