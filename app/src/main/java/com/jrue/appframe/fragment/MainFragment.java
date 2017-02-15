package com.jrue.appframe.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jrue.appframe.R;
import com.jrue.appframe.lib.base.BaseFragment;
import com.jrue.appframe.lib.event.OnBackPressedCtrlEvent;
import com.jrue.appframe.lib.util.MSetting;
import com.jrue.appframe.lib.widget.TitleBarLayout;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by jrue on 17/2/5.
 */
public class MainFragment extends BaseFragment {

    public static final String TAG = "MainFragment";

    @Bind(R.id.useBtn)
    Button useBtn;
    @Bind(R.id.textView)
    TextView text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAutoRegisterEvent(true);
    }

    @Override
    public View mzOnCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void mzOnViewCreated(View view) {
        super.mzOnViewCreated(view);
        text = (TextView) view.findViewById(R.id.textView);
        if (MSetting.getInstance().getFirstUse()) {
            text.setText("欢迎第一次使用！");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        TitleBarLayout bar = getTitleBarLayout();
        if (bar != null) {
            bar.setTitleBackground(TitleBarLayout.TITLE_BACKGROUND_GRAY);
            bar.setTitleGravity(TitleBarLayout.TITLE_GRAVITY_CENTER);
            bar.setTitleText("首页");
            bar.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.useBtn)
    void onUseClick() {
        MSetting.getInstance().setFirstUse(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onEvent(OnBackPressedCtrlEvent event) {
        getBaseActivity().mzFinish();
    }
}
