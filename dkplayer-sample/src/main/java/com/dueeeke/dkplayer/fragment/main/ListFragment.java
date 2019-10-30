package com.dueeeke.dkplayer.fragment.main;

import androidx.viewpager.widget.ViewPager;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.MyPagerAdapter;
import com.dueeeke.dkplayer.fragment.BaseFragment;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

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
        viewPager.addOnPageChangeListener(this);

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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == mCurrentPosition) return;
        mNeedRelease = true;
        mCurrentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE && mNeedRelease) {
            //左右滑动ViewPager释放正在播放的播放器
            VideoViewManager.instance().release();
            mNeedRelease = false;
        }
    }
}
