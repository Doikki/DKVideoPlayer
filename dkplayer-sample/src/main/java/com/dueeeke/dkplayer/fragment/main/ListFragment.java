package com.dueeeke.dkplayer.fragment.main;

import android.content.Intent;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.list.AutoPlayRecyclerViewActivity;
import com.dueeeke.dkplayer.activity.list.ListFragmentViewPagerActivity;
import com.dueeeke.dkplayer.activity.list.ListViewActivity;
import com.dueeeke.dkplayer.activity.list.RecyclerViewActivity;
import com.dueeeke.dkplayer.activity.list.RotateRecyclerViewActivity;
import com.dueeeke.dkplayer.activity.list.SeamlessPlayActivity;
import com.dueeeke.dkplayer.activity.list.tiktok.TikTok2Activity;
import com.dueeeke.dkplayer.activity.list.tiktok.TikTokActivity;
import com.dueeeke.dkplayer.fragment.BaseFragment;

public class ListFragment extends BaseFragment implements View.OnClickListener {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list;
    }

    @Override
    protected void initViews() {
        super.initViews();
        findViewById(R.id.btn_list_view).setOnClickListener(this);
        findViewById(R.id.btn_recycler_view).setOnClickListener(this);
        findViewById(R.id.btn_auto_recycler_view).setOnClickListener(this);
        findViewById(R.id.btn_list_fragment_viewpager).setOnClickListener(this);
        findViewById(R.id.btn_rotate_fullscreen).setOnClickListener(this);
        findViewById(R.id.btn_tiktok_recyclerview).setOnClickListener(this);
        findViewById(R.id.btn_tiktok_verticalviewpager).setOnClickListener(this);
        findViewById(R.id.btn_seamless_play).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_list_view:
                startActivity(new Intent(getActivity(), ListViewActivity.class));
                break;
            case R.id.btn_recycler_view:
                startActivity(new Intent(getActivity(), RecyclerViewActivity.class));
                break;
            case R.id.btn_auto_recycler_view:
                startActivity(new Intent(getActivity(), AutoPlayRecyclerViewActivity.class));
                break;
            case R.id.btn_list_fragment_viewpager:
                startActivity(new Intent(getActivity(), ListFragmentViewPagerActivity.class));
                break;
            case R.id.btn_rotate_fullscreen:
                startActivity(new Intent(getActivity(), RotateRecyclerViewActivity.class));
                break;
            case R.id.btn_tiktok_recyclerview:
                startActivity(new Intent(getActivity(), TikTokActivity.class));
                break;
            case R.id.btn_tiktok_verticalviewpager:
                startActivity(new Intent(getActivity(), TikTok2Activity.class));
                break;
            case R.id.btn_seamless_play:
                startActivity(new Intent(getActivity(), SeamlessPlayActivity.class));
                break;
        }
    }
}
