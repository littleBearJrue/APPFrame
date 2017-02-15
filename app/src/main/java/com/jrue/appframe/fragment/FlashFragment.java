package com.jrue.appframe.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jrue.appframe.R;
import com.jrue.appframe.lib.base.BaseFragment;
import com.jrue.appframe.lib.widget.TitleBarLayout;

import butterknife.Bind;

/**
 * Created by jrue on 17/2/15.
 */
public class FlashFragment extends BaseFragment {
    public static final String TAG = "ClockFragment";

    static final String FROM_PRE_DATA = "pre_data";

    private String pre_str;

    @Bind(R.id.flash_text)
    TextView flashText;

    public static FlashFragment newInstance(String fromPre) {
        Bundle bundle = new Bundle();
        bundle.putString(FROM_PRE_DATA, fromPre);
        FlashFragment fragment = new FlashFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        pre_str = bundle.getString(FROM_PRE_DATA);
    }

    @Override
    public View mzOnCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.flash_layout, container, false);
    }

    @Override
    public void mzOnViewCreated(View view) {
        super.mzOnViewCreated(view);
        flashText.setText(pre_str);
    }

    @Override
    public void onResume() {
        super.onResume();
        TitleBarLayout bar = getTitleBarLayout();
        if (bar != null) {
            bar.setTitleBackground(TitleBarLayout.TITLE_BACKGROUND_DARK_BLUE);
            bar.setTitleGravity(TitleBarLayout.TITLE_GRAVITY_CENTER);
            bar.setTitleText("Flash View");
            bar.setVisibility(View.VISIBLE);
        }
    }
}
