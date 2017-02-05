package com.jrue.appframe.lib.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;

import com.jrue.appframe.lib.R;
import com.jrue.appframe.lib.util.MEvent;
import com.jrue.appframe.lib.util.MLog;
import com.jrue.appframe.lib.util.ViewServer;
import com.jrue.appframe.lib.widget.BottomBarView;
import com.jrue.appframe.lib.widget.TitleBarLayout;

/**
 *  Activity基类，只用于提供外部调用接口。
 * Created by meizu on 17/2/5.
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
    private static final String TAG = "BaseFragmentActivity";

    public interface OnBackdoorListener {
        boolean onBackdoor(int code);
    }

    private TitleBarLayout mTitleBar;

    private BottomBarView mBottomBar;

    private boolean mAutoRegisterEvent = MEvent.DEFAULT_AUTO_REGISTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base_fragment);

        mTitleBar = (TitleBarLayout) findViewById(R.id.titleBarFrame);
        mTitleBar.setTitleBackground(TitleBarLayout.TITLE_BACKGROUND_GRAY);
        mTitleBar.setTitleGravity(TitleBarLayout.TITLE_GRAVITY_CENTER);
        mTitleBar.setTitleStartButtonAsBackKey();
        mTitleBar.setTitleText(getTitle());

        findViewById(R.id.bottomContainer).setVisibility(View.GONE);

        if (savedInstanceState != null) {
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStackImmediate(null, 1);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);
            if (fragment == null) {
                fragment = onCreateFragment(getIntent());
                if (fragment != null) {
                    fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
                }
            }
        }

        ViewServer.get(this).addWindow(this);
        BaseApp.getInstance().onActivityCreate();
    }

    @Override
    protected void onDestroy() {
        BaseApp.getInstance().onActivityDestroy();
        ViewServer.get(this).removeWindow(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        if (MLog.DEBUG) MLog.out.d(TAG, "onStart:" +
                " mAutoRegisterEvent=" + mAutoRegisterEvent);
        super.onStart();
        BaseApp.getInstance().onActivityStart();
        if (mAutoRegisterEvent) {
            MEvent.register(this);
        }
    }

    @Override
    protected void onStop() {
        if (MLog.DEBUG) MLog.out.d(TAG, "onStop:" +
                " mAutoRegisterEvent=" + mAutoRegisterEvent);
        if (mAutoRegisterEvent) {
            MEvent.unregister(this);
        }
        BaseApp.getInstance().onActivityStop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Fragment fragment = onCreateFragment(intent);
        if (fragment == null) return;

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.fragment_container) == null) {
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        } else {
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.dock_right_enter, R.anim.dock_hold);
    }

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        super.startActivityFromFragment(fragment, intent, requestCode);
        overridePendingTransition(R.anim.dock_right_enter, R.anim.dock_hold);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.dock_right_enter, R.anim.dock_hold);
    }

    /**
     * 生成显示窗口的fragment
     */
    protected abstract BaseFragment onCreateFragment(Intent intent);

    public final void setAutoRegisterEvent(boolean enable) {
        if (MLog.DEBUG) MLog.out.d(TAG, "setAutoRegisterEvent: enable=" + enable);
        mAutoRegisterEvent = enable;
    }

    public final void mzFinish() {
        if (Build.VERSION.SDK_INT >= 21) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    /**
     * 获取标题栏
     */
    public final TitleBarLayout getTitleBarLayout() {
        return mTitleBar;
    }

    /**
     * 获取底栏
     */
    public final BottomBarView getBottomBarView() {
        if (mBottomBar == null) {
            View container = findViewById(R.id.bottomContainer);
            if (container != null) {
                container.setVisibility(View.VISIBLE);
                mBottomBar = (BottomBarView) container.findViewById(R.id.bottomBarFrame);
            }
        }
        return mBottomBar;
    }

    protected final void setOnBackdoorListener(OnBackdoorListener listener) {
        getTitleBarLayout().setOnBackdoorListener(listener);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}
