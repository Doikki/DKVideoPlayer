package com.dueeeke.dkplayer.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dueeeke.dkplayer.fragment.RecyclerViewFragment;

import java.util.List;

/**
 * MyPagerAdapter
 * Created by xinyu on 2018/1/3.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {

    private List<RecyclerViewFragment> mFragmentList;
    private List<String> titles;

    public MyPagerAdapter(FragmentManager fm, List<RecyclerViewFragment> fragmentList, List<String> titles) {
        super(fm);
        mFragmentList = fragmentList;
        this.titles = titles;
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
