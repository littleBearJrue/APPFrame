package com.jrue.appframe.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jrue.appframe.R;
import com.jrue.appframe.lib.base.BaseFragment;
import com.jrue.appframe.lib.widget.TitleBarLayout;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 自定义界面汇总
 * Created by jrue on 17/2/15.
 */
public class CustomFragment extends BaseFragment {
    public static final String TAG = "CustomFragment";

    @Bind(R.id.clockBtn)
    Button clockBtn;
    @Bind(R.id.flashBtn)
    Button flashBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        TitleBarLayout bar = getTitleBarLayout();
        if (bar != null) {
            bar.setTitleBackground(TitleBarLayout.TITLE_BACKGROUND_GRAY);
            bar.setTitleGravity(TitleBarLayout.TITLE_GRAVITY_CENTER);
            bar.setTitleText("自定义View汇总");
            bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected View mzOnCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.custom_layout, container, false);
    }

    @Override
    protected void mzOnViewCreated(View view) {
        super.mzOnViewCreated(view);
    }

    @OnClick(R.id.clockBtn)
    void onClockClick() {
        addFragmentToBackStack(ClockFragment.newInstance("自定义Clock界面"));
    }

    @OnClick(R.id.flashBtn)
    void onFlashClick() {
        addFragmentToBackStack(FlashFragment.newInstance("自定义Flash界面"));
    }
}
