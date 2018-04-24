package com.dueeeke.dkplayer.activity.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.MyPagerAdapter;
import com.dueeeke.dkplayer.fragment.RecyclerViewFragment;
import com.dueeeke.videoplayer.player.VideoViewManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表加ViewPager
 * Created by xinyu on 2018/1/3.
 */

public class ListFragmentViewPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<String> titles = new ArrayList<>();
    private List<RecyclerViewFragment> mFragmentList = new ArrayList<>();
    private VideoViewManager mVideoViewManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_fragment_view_pager);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("LIST FRAGMENT VIEWPAGER");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVideoViewManager = VideoViewManager.instance();
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mTabLayout = findViewById(R.id.tl);
        mViewPager = findViewById(R.id.vp);
        mViewPager.addOnPageChangeListener(this);

        titles.add("List1");
        titles.add("List2");
        titles.add("List3");

        for (int i = 0; i < titles.size(); i++) {
            mFragmentList.add(RecyclerViewFragment.newInstance());
        }

        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mFragmentList, titles));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoViewManager.releaseVideoPlayer();
    }

    @Override
    public void onBackPressed() {
        if (!mVideoViewManager.onBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mVideoViewManager.stopPlayback();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
