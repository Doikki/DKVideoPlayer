package com.dueeeke.dkplayer.fragment.main;

import androidx.viewpager.widget.ViewPager;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.ListPagerAdapter;
import com.dueeeke.dkplayer.fragment.BaseFragment;
import com.dueeeke.dkplayer.util.Tag;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 注意：RecyclerView demo 我采用继承的方式实现，
 * ${@link com.dueeeke.dkplayer.fragment.list.RecyclerViewPortraitFragment} 我甚至使用了三重继承😂，
 * 实际开发中可以不需要这样。
 * 我这样做仅仅只为代码复用，方便维护
 */
public class ListFragment extends BaseFragment {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list;
    }

    @Override
    protected void initView() {
        super.initView();

        ViewPager viewPager = findViewById(R.id.view_pager);

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.str_list_view));
        titles.add(getString(R.string.str_recycler_view));
        titles.add(getString(R.string.str_auto_play_recycler_view));
        titles.add(getString(R.string.str_tiktok));
        titles.add(getString(R.string.str_portrait_when_fullscreen));
        titles.add(getString(R.string.str_seamless_play));

        viewPager.setAdapter(new ListPagerAdapter(getChildFragmentManager(), titles));

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注意：在这里release的目的是为了在退出App时释放正在列表上播放的视频
        getVideoViewManager().releaseByTag(Tag.LIST);
    }
}
