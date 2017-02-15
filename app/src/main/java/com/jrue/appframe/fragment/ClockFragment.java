package com.jrue.appframe.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jrue.appframe.R;
import com.jrue.appframe.lib.base.BaseFragment;
import com.jrue.appframe.lib.widget.TitleBarLayout;

/**
 * Created by jrue on 17/2/8.
 */
public class ClockFragment extends BaseFragment {
    public static final String TAG = "ClockFragment";

    static final String FROM_PRE_DATA = "pre_data";

    private String pre_str;

    public static ClockFragment newInstance(String fromPre) {
        Bundle bundle = new Bundle();
        bundle.putString(FROM_PRE_DATA, fromPre);
        ClockFragment fragment = new ClockFragment();
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
        return inflater.inflate(R.layout.clock_layout, container, false);
    }

    @Override
    public void mzOnViewCreated(View view) {
        super.mzOnViewCreated(view);
        TextView tv = (TextView)view.findViewById(R.id.next_text);
        tv.setText(pre_str);
    }

    @Override
    public void onResume() {
        super.onResume();
        TitleBarLayout bar = getTitleBarLayout();
        if (bar != null) {
            bar.setTitleBackground(TitleBarLayout.TITLE_BACKGROUND_DARK_BLUE);
            bar.setTitleGravity(TitleBarLayout.TITLE_GRAVITY_CENTER);
            bar.setTitleText("Clock View");
            bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
