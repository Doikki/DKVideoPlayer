package com.dueeeke.dkplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dueeeke.dkplayer.R;

/**
 * List相关Demo
 * Created by xinyu on 2018/1/3.
 */

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    public void list(View view) {
        startActivity(new Intent(this, ListViewActivity.class));
    }

    public void recyclerAutoPlay(View view) {
        startActivity(new Intent(this, AutoPlayRecyclerViewActivity.class));
    }

    public void listFragmentViewPager(View view) {
        startActivity(new Intent(this, ListFragmentViewPagerActivity.class));
    }

    public void recycler(View view) {
        startActivity(new Intent(this, RecyclerViewActivity.class));
    }

    public void douYin(View view) {
        startActivity(new Intent(this, DouYinActivity.class));
    }
}
