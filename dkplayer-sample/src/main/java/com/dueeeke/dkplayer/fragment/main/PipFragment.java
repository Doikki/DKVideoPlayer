package com.dueeeke.dkplayer.fragment.main;

import android.content.Intent;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.pip.AndroidOPiPActivity;
import com.dueeeke.dkplayer.activity.pip.PIPActivity;
import com.dueeeke.dkplayer.activity.pip.PIPListActivity;
import com.dueeeke.dkplayer.activity.pip.TinyScreenListActivity;
import com.dueeeke.dkplayer.fragment.BaseFragment;

public class PipFragment extends BaseFragment implements View.OnClickListener {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_pip;
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.btn_pip).setOnClickListener(this);
        findViewById(R.id.btn_pip_in_list).setOnClickListener(this);
        findViewById(R.id.btn_pip_android_o).setOnClickListener(this);
        findViewById(R.id.btn_tiny_screen).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pip:
                startActivity(new Intent(getActivity(), PIPActivity.class));
                break;
            case R.id.btn_pip_in_list:
                startActivity(new Intent(getActivity(), PIPListActivity.class));
                break;
            case R.id.btn_pip_android_o:
                startActivity(new Intent(getActivity(), AndroidOPiPActivity.class));
                break;
            case R.id.btn_tiny_screen:
                startActivity(new Intent(getActivity(), TinyScreenListActivity.class));
                break;
        }
    }
}
