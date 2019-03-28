package com.dueeeke.dkplayer.activity.pip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.dueeeke.videoplayer.player.IjkVideoView;

import java.util.List;

/**
 * Created by Devlin_n on 2017/5/31.
 */

public class TinyScreenListActivity extends AppCompatActivity implements FloatRecyclerViewAdapter.OnChildViewClickListener {

    private IjkVideoView mIjkVideoView;
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

        mIjkVideoView = new IjkVideoView(this);
        mStandardVideoController = new StandardVideoController(this);
        mIjkVideoView.setVideoController(mStandardVideoController);
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
                    mIjkVideoView.stopTinyScreen();
                    thumb.setVisibility(View.GONE);
                    mStandardVideoController.setPlayState(mIjkVideoView.getCurrentPlayState());
                    mStandardVideoController.setPlayerState(mIjkVideoView.getCurrentPlayerState());
                    mIjkVideoView.setVideoController(mStandardVideoController);
                    if (mIjkVideoView.getParent() instanceof ViewGroup) {
                        ((ViewGroup) mIjkVideoView.getParent()).removeView(mIjkVideoView);
                    }
                    player.addView(mIjkVideoView);
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
                if (position == mCurrentPlayingPosition) {
                    if (mThumb != null) mThumb.setVisibility(View.VISIBLE);
                    mIjkVideoView.startTinyScreen();
                    mFloatController.setPlayState(mIjkVideoView.getCurrentPlayState());
                    mFloatController.setPlayerState(mIjkVideoView.getCurrentPlayerState());
                    mIjkVideoView.setVideoController(mFloatController);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIjkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIjkVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIjkVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (!mIjkVideoView.onBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    public void onChildViewClick(View itemView, View childView, int position) {
        if (mCurrentPlayingPosition == position) return;
        if (mIjkVideoView.isTinyScreen()) mIjkVideoView.stopTinyScreen();
        if (mPlayer != null) mPlayer.removeAllViews();
        if (mThumb != null) mThumb.setVisibility(View.VISIBLE);
        FrameLayout player = itemView.findViewById(R.id.player_container);
        FrameLayout thumb = itemView.findViewById(R.id.layout_thumb);
        VideoBean videoBean = mVideoList.get(position);
        mIjkVideoView.release();
        mIjkVideoView.setUrl(videoBean.getUrl());
        Glide.with(this).load(videoBean.getThumb()).into(mStandardVideoController.getThumb());
        mIjkVideoView.setVideoController(mStandardVideoController);
        mIjkVideoView.start();
        player.addView(mIjkVideoView);
        thumb.setVisibility(View.GONE);
        mCurrentPlayingPosition = position;
        mPlayer = player;
        mThumb = thumb;
    }
}
