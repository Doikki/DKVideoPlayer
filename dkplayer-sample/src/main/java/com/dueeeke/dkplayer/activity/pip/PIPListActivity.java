package com.dueeeke.dkplayer.activity.pip;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.FloatRecyclerViewAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.util.PIPManager;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

/**
 * 悬浮播放终极版
 * Created by Devlin_n on 2017/5/31.
 */

public class PIPListActivity extends AppCompatActivity implements FloatRecyclerViewAdapter.OnChildViewClickListener {

    private FrameLayout mPlayer, mThumb;
    private PIPManager mPIPManager;
    private VideoView mVideoView;
    private StandardVideoController mStandardVideoController;
    private List<VideoBean> mVideoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycler_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_pip_in_list);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPIPManager = PIPManager.getInstance();
        mVideoView = mPIPManager.getVideoView();
        mStandardVideoController = new StandardVideoController(this);
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
                if (position == mPIPManager.getPlayingPosition()) {
                    mPIPManager.stopFloatWindow();
                    thumb.setVisibility(View.GONE);
                    mStandardVideoController.setPlayState(mVideoView.getCurrentPlayState());
                    mStandardVideoController.setPlayerState(mVideoView.getCurrentPlayerState());
                    mVideoView.setVideoController(mStandardVideoController);
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
                if (position == mPIPManager.getPlayingPosition()) {
                    if (mThumb != null) mThumb.setVisibility(View.VISIBLE);
                    startFloatWindow();
                }
            }
        });
    }

    private void startFloatWindow() {
        AndPermission
                .with(this)
                .overlay()
                .onGranted(data -> {
                    mPIPManager.startFloatWindow();
                })
                .onDenied(data -> {

                })
                .start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPIPManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPIPManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPIPManager.reset();
    }

    @Override
    public void onBackPressed() {
        if (mPIPManager.onBackPress()) return;
        super.onBackPressed();

    }

    @Override
    public void onChildViewClick(View itemView, View childView, int position) {
        if (mPIPManager.getPlayingPosition() == position) return;
        if (mPIPManager.isStartFloatWindow()) mPIPManager.stopFloatWindow();
        if (mPlayer != null) mPlayer.removeAllViews();
        if (mThumb != null) mThumb.setVisibility(View.VISIBLE);
        FrameLayout player = itemView.findViewById(R.id.player_container);
        FrameLayout thumb = itemView.findViewById(R.id.layout_thumb);
        VideoBean videoBean = mVideoList.get(position);
        mVideoView.release();
        mVideoView.setUrl(videoBean.getUrl());
        ImageView iv = mStandardVideoController.findViewById(R.id.thumb);
        Glide.with(this).load(videoBean.getThumb()).into(iv);
        mVideoView.setVideoController(mStandardVideoController);
        mVideoView.start();
        player.addView(mVideoView);
        thumb.setVisibility(View.GONE);
        mPIPManager.setPlayingPosition(position);
        mPlayer = player;
        mThumb = thumb;

    }
}
