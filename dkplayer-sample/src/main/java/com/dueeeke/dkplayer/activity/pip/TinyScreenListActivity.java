package com.dueeeke.dkplayer.activity.pip;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.FloatRecyclerViewAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.widget.controller.FloatController;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.List;

/**
 * Created by Devlin_n on 2017/5/31.
 */

public class TinyScreenListActivity extends AppCompatActivity implements FloatRecyclerViewAdapter.OnChildViewClickListener {

    private VideoView mVideoView;
    private StandardVideoController mStandardVideoController;
    private FloatController mFloatController;
    private List<VideoBean> mVideoList;

    private int mCurrentPlayingPosition = -1;
    private FrameLayout mPlayer, mThumb;

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

        mVideoView = new VideoView(this);
        mStandardVideoController = new StandardVideoController(this);
        mVideoView.setVideoController(mStandardVideoController);
        mFloatController = new FloatController(this);
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
        mVideoList = DataUtil.getVideoList();
        FloatRecyclerViewAdapter floatRecyclerViewAdapter = new FloatRecyclerViewAdapter(mVideoList);
        floatRecyclerViewAdapter.setOnChildViewClickListener(this);
        recyclerView.setAdapter(floatRecyclerViewAdapter);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                FrameLayout player = view.findViewById(R.id.player_container);
                FrameLayout thumb = view.findViewById(R.id.layout_thumb);
                if (player == null || thumb == null) return;
                int position = (int) player.getTag(R.id.key_position);
                if (position == mCurrentPlayingPosition) {
                    mVideoView.stopTinyScreen();
                    thumb.setVisibility(View.GONE);
                    mStandardVideoController.setPlayState(mVideoView.getCurrentPlayState());
                    mStandardVideoController.setPlayerState(mVideoView.getCurrentPlayerState());
                    mVideoView.setVideoController(mStandardVideoController);
                    if (mVideoView.getParent() instanceof ViewGroup) {
                        ((ViewGroup) mVideoView.getParent()).removeView(mVideoView);
                    }
                    player.addView(mVideoView);
                    mThumb = thumb;
                    mPlayer = player;
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                FrameLayout player = view.findViewById(R.id.player_container);
                FrameLayout thumb = view.findViewById(R.id.layout_thumb);
                if (player == null || thumb == null) return;
                int position = (int) player.getTag(R.id.key_position);
                if (position == mCurrentPlayingPosition && !mVideoView.isFullScreen()) {
                    if (mThumb != null) mThumb.setVisibility(View.VISIBLE);
                    mVideoView.startTinyScreen();
                    mFloatController.setPlayState(mVideoView.getCurrentPlayState());
                    mFloatController.setPlayerState(mVideoView.getCurrentPlayerState());
                    mVideoView.setVideoController(mFloatController);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (!mVideoView.onBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    public void onChildViewClick(View itemView, View childView, int position) {
        if (mCurrentPlayingPosition == position) return;
        if (mVideoView.isTinyScreen()) mVideoView.stopTinyScreen();
        if (mPlayer != null) mPlayer.removeAllViews();
        if (mThumb != null) mThumb.setVisibility(View.VISIBLE);
        FrameLayout player = itemView.findViewById(R.id.player_container);
        FrameLayout thumb = itemView.findViewById(R.id.layout_thumb);
        VideoBean videoBean = mVideoList.get(position);
        mVideoView.release();
        mVideoView.setUrl(videoBean.getUrl());
        Glide.with(this).load(videoBean.getThumb()).into(mStandardVideoController.getThumb());
        mVideoView.setVideoController(mStandardVideoController);
        mVideoView.start();
        player.addView(mVideoView);
        thumb.setVisibility(View.GONE);
        mCurrentPlayingPosition = position;
        mPlayer = player;
        mThumb = thumb;
    }
}
