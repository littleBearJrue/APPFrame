package com.jrue.appframe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jrue.appframe.R;
import com.jrue.appframe.lib.base.BaseFragment;
import com.jrue.appframe.lib.event.OnBackPressedCtrlEvent;
import com.jrue.appframe.lib.widget.TitleBarLayout;
import com.jrue.appframe.main.HomeCustomActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by jrue on 17/2/7.
 */
public class ThirdFragment extends BaseFragment {
    public static final String TAG = "ThirdFragment";

    @Bind(R.id.next_btn)
    Button nextBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAutoRegisterEvent(true);
    }

    @Override
    public View mzOnCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.third_layout, container, false);
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
            bar.setTitleBackground(TitleBarLayout.TITLE_BACKGROUND_GRAY);
            bar.setTitleGravity(TitleBarLayout.TITLE_GRAVITY_CENTER);
            bar.setTitleText("第三页");
            bar.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.next_btn)
    void onNextClick() {
        Intent intent = new Intent(getActivity(), HomeCustomActivity.class);
        intent.putExtra("textActivity", "进入另一个Activity载体!");
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onEvent(OnBackPressedCtrlEvent event) {
        getBaseActivity().mzFinish();
    }
}
