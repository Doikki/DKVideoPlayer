package com.dueeeke.dkplayer.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class DouYinAdapter extends PagerAdapter {

    private static final String TAG = "DouYinAdapter";

    private List<View> mViews;

    public DouYinAdapter(List<View> views) {
        this.mViews = views;
    }


    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem: called");
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Log.d(TAG, "destroyItem: ");
//        IjkVideoView ijkVideoView = (IjkVideoView) mViews.get(position);
        container.removeView(mViews.get(position));
    }
}