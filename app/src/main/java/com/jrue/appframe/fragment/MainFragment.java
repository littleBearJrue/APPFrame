package com.jrue.appframe.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jrue.appframe.R;
import com.jrue.appframe.lib.base.BaseFragment;
import com.jrue.appframe.lib.widget.TitleBarLayout;

/**
 * Created by jrue on 17/2/5.
 */
public class MainFragment extends BaseFragment {

    public static final String TAG = "MainFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View mzOnCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void mzOnViewCreated(View view) {
        super.mzOnViewCreated(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        TitleBarLayout bar = getTitleBarLayout();
        if (bar != null) {
            bar.setTitleBackground(TitleBarLayout.TITLE_BACKGROUND_DARK_BLUE);
            bar.setTitleGravity(TitleBarLayout.TITLE_GRAVITY_CENTER);
            bar.setTitleText("首页");
            bar.setVisibility(View.VISIBLE);
            bar.setTitleEndButtonDrawable(getResources().getDrawable(R.drawable.ic_add_devices));
            bar.setTitleEndButtonVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
