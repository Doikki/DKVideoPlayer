package com.dueeeke.dkplayer.activity.pip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.adapter.FloatRecyclerViewAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.util.PIPManager;
import com.dueeeke.dkplayer.widget.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;

import java.util.List;

/**
 * 悬浮播放终极版
 * Created by Devlin_n on 2017/5/31.
 */

public class PIPListActivity extends AppCompatActivity implements FloatRecyclerViewAdapter.OnChildViewClickListener {

    private FrameLayout mPlayer, mThumb;
    private PIPManager mPIPManager;
    private IjkVideoView mIjkVideoView;
    private StandardVideoController mStandardVideoController;
    private List<VideoBean> mVideoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.str_pip_in_list);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPIPManager = PIPManager.getInstance();
        mIjkVideoView = mPIPManager.getIjkVideoView();
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
        FloatRecyclerViewAdapter floatRecyclerViewAdapter = new FloatRecyclerViewAdapter(mVideoList, this);
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
                    mStandardVideoController.setPlayState(mIjkVideoView.getCurrentPlayState());
                    mStandardVideoController.setPlayerState(mIjkVideoView.getCurrentPlayerState());
                    mIjkVideoView.setVideoController(mStandardVideoController);
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
                if (position == mPIPManager.getPlayingPosition()) {
                    if (mThumb != null) mThumb.setVisibility(View.VISIBLE);
                    startFloatWindow();
                }
            }
        });
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == FloatWindowManager.PERMISSION_REQUEST_CODE) {
//            if (FloatWindowManager.getInstance().checkPermission(this)) {
//                mPIPManager.startFloatWindow();
//            } else {
//                Toast.makeText(PIPListActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void startFloatWindow() {
        if (FloatWindowManager.getInstance().checkPermission(this)) {
            mPIPManager.startFloatWindow();
        } else {
            mPIPManager.reset();
            FloatWindowManager.getInstance().applyPermission(this);
        }
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
        mIjkVideoView.release();
        mIjkVideoView.setUrl(videoBean.getUrl());
        Glide.with(this).load(videoBean.getThumb()).into(mStandardVideoController.getThumb());
        mIjkVideoView.setVideoController(mStandardVideoController);
        mIjkVideoView.start();
        player.addView(mIjkVideoView);
        thumb.setVisibility(View.GONE);
        mPIPManager.setPlayingPosition(position);
        mPlayer = player;
        mThumb = thumb;

    }
}
