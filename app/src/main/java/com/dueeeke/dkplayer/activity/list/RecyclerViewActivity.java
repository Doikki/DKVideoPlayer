package com.dueeeke.dkplayer.activity.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.VideoRecyclerViewAdapter;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;

/**
 * Created by Devlin_n on 2017/5/31.
 */

public class RecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_recycler_view);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new VideoRecyclerViewAdapter(DataUtil.getVideoList()));
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                IjkVideoView ijkVideoView = view.findViewById(R.id.video_player);
                if (ijkVideoView != null && !ijkVideoView.isFullScreen()) {
//                    Log.d("@@@@@@", "onChildViewDetachedFromWindow: called");
//                    int tag = (int) ijkVideoView.getTag();
//                    Log.d("@@@@@@", "onChildViewDetachedFromWindow: position: " + tag);
                    ijkVideoView.release();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoViewManager.instance().releaseVideoPlayer();
    }

    @Override
    public void onBackPressed() {
        if (!VideoViewManager.instance().onBackPressed()){
            super.onBackPressed();
        }
    }
}
