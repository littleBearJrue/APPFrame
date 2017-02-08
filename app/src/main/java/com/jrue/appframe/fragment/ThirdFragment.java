package com.jrue.appframe.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jrue.appframe.R;
import com.jrue.appframe.lib.base.BaseFragment;
import com.jrue.appframe.lib.event.OnBackPressedCtrlEvent;
import com.jrue.appframe.lib.widget.TitleBarLayout;

/**
 * Created by jrue on 17/2/7.
 */
public class ThirdFragment extends BaseFragment {
    public static final String TAG = "ThirdFragment";

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

        Button nextBtn = (Button)view.findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentToBackStack(NextFragment.newInstance("这是下一个界面了！！！"));
            }
        });

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

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onEvent(OnBackPressedCtrlEvent event) {
        getBaseActivity().mzFinish();
    }
}
