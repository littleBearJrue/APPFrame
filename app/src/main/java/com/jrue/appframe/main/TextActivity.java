package com.jrue.appframe.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.jrue.appframe.fragment.NextFragment;
import com.jrue.appframe.lib.base.BaseFragment;
import com.jrue.appframe.lib.base.BaseFragmentActivity;

/**
 * Created by jrue on 17/2/15.
 */
public class TextActivity extends BaseFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected BaseFragment onCreateFragment(Intent intent) {
        return NextFragment.newInstance("这是下一个界面了！！！");
    }
}
