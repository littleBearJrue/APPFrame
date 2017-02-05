package com.jrue.appframe.lib.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import com.jrue.appframe.lib.R;
import com.jrue.appframe.lib.util.ViewServer;

/**
 * 基础Activity类
 * Created by meizu on 17/2/5.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

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
        super.onStart();
        BaseApp.getInstance().onActivityStart();
    }

    @Override
    protected void onStop() {
        BaseApp.getInstance().onActivityStop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        // 对Activity的界面切换增加动画
        overridePendingTransition(R.anim.dock_right_enter, R.anim.dock_hold);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        // 对Activity的界面切换增加动画
        overridePendingTransition(R.anim.dock_right_enter, R.anim.dock_hold);
    }

    public final void mzFinish() {
        if (Build.VERSION.SDK_INT >= 21) {
            finishAfterTransition();
        } else {
            finish();
        }
    }
}
