package com.jrue.appframe.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.jrue.appframe.R;
import com.jrue.appframe.fragment.MainFragment;
import com.jrue.appframe.fragment.SecondFragment;
import com.jrue.appframe.fragment.ThirdFragment;
import com.jrue.appframe.lib.base.BaseFragment;
import com.jrue.appframe.lib.base.BaseFragmentActivity;
import com.jrue.appframe.lib.util.MUtils;
import com.jrue.appframe.lib.widget.BottomBarView;

/**
 * 程序主窗口
 * Created by jrue on 17/2/5.
 */
public class MainActivity extends BaseFragmentActivity implements BottomBarView.OnItemChangeListener{

    private static final String TAG = "MainActivity";

    private BottomBarView mBottomBarView;
    private BottomBarView.MenuEdit mMenuEdit;
    private View.OnClickListener mHomeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getBottomBarView().setCurrentItem(indexOfFragment(MainFragment.class));
        }
    };
    private View.OnClickListener mMarketOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getBottomBarView().setCurrentItem(indexOfFragment(SecondFragment.class));
        }
    };
    private View.OnClickListener mUserOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getBottomBarView().setCurrentItem(indexOfFragment(ThirdFragment.class));
        }
    };

    private final TabItem[] mTabs = {
            new TabItem(MainFragment.class, "首页", R.drawable.tab_item_home, mHomeOnClickListener),
            new TabItem(SecondFragment.class, "第二页", R.drawable.tab_item_market, mMarketOnClickListener),
            new TabItem(ThirdFragment.class, "第三页", R.drawable.tab_item_home, mUserOnClickListener),
    };

    private TabItem mLastTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTitleBarLayout().setVisibility(View.GONE);

        mBottomBarView = getBottomBarView();
        mBottomBarView.setDividerPaintColor(getResources().getColor(R.color.transparent_background));
        mBottomBarView.setBackgroundResource(R.color.tab_background);
        mBottomBarView.setShowDividers(BottomBarView.SHOW_DIVIDER_NONE);

        mMenuEdit = mBottomBarView.beginMenuEdit();

        for (TabItem item : mTabs) {
            mMenuEdit.addItem(R.layout.tab_item_main, item.text, item.icon, item.onClick);
        }
        mMenuEdit.commit();

        int padding = (MUtils.getScreenSize(this).x - (MUtils.dip2px(this, 120) * mTabs.length)) / ((mTabs.length + 1) * 3);
        mBottomBarView.setPadding(padding, 0, padding, 0);

        ViewGroup.LayoutParams lp = mBottomBarView.getLayoutParams();
        lp.height = MUtils.dip2px(this, 50.5f);
        mBottomBarView.setLayoutParams(lp);

        mBottomBarView.setOnItemChangeListener(this);
        mBottomBarView.setCurrentItem(indexOfFragment(MainFragment.class));

    }

    @Override
    public void onItemChanged(int index) {
        FragmentTransaction ft = doTabChanged(mTabs[index].getTag(), null);
        if (ft != null) {
            ft.commit();
        }
    }

    private FragmentTransaction doTabChanged(String tabId, FragmentTransaction ft) {
        TabItem newTab = null;
        for (TabItem tab : mTabs) {
            if (TextUtils.equals(tabId, tab.getTag())) {
                newTab = tab;
            }
        }
        if (newTab == null) {
            throw new IllegalStateException("No tab known for tag " + tabId);
        }
        if (mLastTab != newTab) {
            if (ft == null) {
                ft = getSupportFragmentManager().beginTransaction();
            }
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }
            if (newTab.fragment == null) {
                newTab.fragment = (BaseFragment) BaseFragment.instantiate(
                        this, newTab.clazz.getName(), null);
                ft.add(R.id.fragment_container, newTab.fragment, newTab.getTag());
            } else {
                ft.attach(newTab.fragment);
            }

            mLastTab = newTab;
        }
        return ft;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected BaseFragment onCreateFragment(Intent intent) {
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private int indexOfFragment(Class<? extends BaseFragment> fragment) {
        for (int i = 0; i < mTabs.length; ++i) {
            if (mTabs[i].clazz == fragment) {
                return i;
            }
        }
        throw new RuntimeException("indexOfFragment: Unable to find " + fragment);
    }

    private static class TabItem {
        final Class clazz;
        final String text;
        final int icon;
        final View.OnClickListener onClick;
        BaseFragment fragment;

        public TabItem(Class clazz, String text, int icon, View.OnClickListener onClick) {
            this.clazz = clazz;
            this.text = text;
            this.icon = icon;
            this.onClick = onClick;
        }

        public String getTag() {
            if (clazz == null) {
                return null;
            }
            return clazz.getSimpleName();
        }
    }
}
