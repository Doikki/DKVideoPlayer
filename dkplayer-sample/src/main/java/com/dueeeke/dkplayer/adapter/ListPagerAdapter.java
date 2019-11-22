package com.dueeeke.dkplayer.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dueeeke.dkplayer.fragment.list.ListViewFragment;
import com.dueeeke.dkplayer.fragment.list.RecyclerViewAutoPlayFragment;
import com.dueeeke.dkplayer.fragment.list.RecyclerViewFragment;
import com.dueeeke.dkplayer.fragment.list.RecyclerViewPortraitFragment;
import com.dueeeke.dkplayer.fragment.list.SeamlessPlayFragment;
import com.dueeeke.dkplayer.fragment.list.TikTokListFragment;

import java.util.List;

/**
 * List主页适配器
 * Created by dueeeke on 2018/1/3.
 */
public class ListPagerAdapter extends FragmentPagerAdapter {

    private List<String> mTitles;
    private SparseArrayCompat<Fragment> mFragments = new SparseArrayCompat<>();

    public ListPagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mTitles = titles;
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        Fragment fragment = mFragments.get(position);
        if (fragment == null) {
            switch (position) {
                default:
                case 0:
                    fragment = new ListViewFragment();
                    break;
                case 1:
                    fragment = new RecyclerViewFragment();
                    break;
                case 2:
                    fragment = new RecyclerViewAutoPlayFragment();
                    break;
                case 3:
                    fragment = new TikTokListFragment();
                    break;
                case 4:
                    fragment = new SeamlessPlayFragment();
                    break;
                case 5:
                    fragment = new RecyclerViewPortraitFragment();
                    break;
            }
            mFragments.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
