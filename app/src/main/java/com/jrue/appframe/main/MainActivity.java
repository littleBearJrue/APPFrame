package com.jrue.appframe.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jrue.appframe.fragment.MainFragment;
import com.jrue.appframe.lib.base.BaseFragment;
import com.jrue.appframe.lib.base.BaseFragmentActivity;

/**
 * 程序主窗口
 * Created by jrue on 17/2/5.
 */
public class MainActivity extends BaseFragmentActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTitleBarLayout().setVisibility(View.GONE);
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
        return new MainFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
