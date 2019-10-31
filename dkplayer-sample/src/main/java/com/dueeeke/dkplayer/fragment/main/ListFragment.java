package com.dueeeke.dkplayer.fragment.main;

import androidx.viewpager.widget.ViewPager;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.MyPagerAdapter;
import com.dueeeke.dkplayer.fragment.BaseFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends BaseFragment {

    private int mCurrentPosition;
    private boolean mNeedRelease;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list;
    }

    @Override
    protected void initViews() {
        super.initViews();

        ViewPager viewPager = findViewById(R.id.view_pager);

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.str_list_view));
        titles.add(getString(R.string.str_recycler_view));
        titles.add(getString(R.string.str_auto_play_recycler_view));
        titles.add(getString(R.string.str_tiktok_2));
        titles.add(getString(R.string.str_rotate_in_fullscreen));
        titles.add(getString(R.string.str_seamless_play));

        viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager(), titles));

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }
}
