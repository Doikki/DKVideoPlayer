package com.dueeeke.dkplayer.activity.pip;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.BaseActivity;
import com.dueeeke.dkplayer.adapter.FloatRecyclerViewAdapter;
import com.dueeeke.dkplayer.bean.VideoBean;
import com.dueeeke.dkplayer.util.DataUtil;
import com.dueeeke.dkplayer.util.PIPManager;
import com.dueeeke.dkplayer.util.Tag;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

/**
 * 悬浮播放终极版
 * Created by Devlin_n on 2017/5/31.
 */

public class PIPListActivity extends BaseActivity implements FloatRecyclerViewAdapter.OnChildViewClickListener {

    private FrameLayout mPlayer, mThumb;
    private PIPManager mPIPManager;
    private VideoView mVideoView;
    private StandardVideoController mController;
    private List<VideoBean> mVideoList;

    @Override
    protected int getTitleResId() {
        return R.string.str_pip_in_list;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initView() {

        mPIPManager = PIPManager.getInstance();
        mVideoView = getVideoViewManager().get(Tag.PIP);
        mController = new StandardVideoController(this);
        mController.addDefaultControlComponent(getString(R.string.str_pip_in_list), false);

        initRecyclerView();
    }

    private void initRecyclerView() {
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
                    mVideoView.setVideoController(mController);
                    mController.setPlayState(mVideoView.getCurrentPlayState());
                    mController.setPlayerState(mVideoView.getCurrentPlayerState());
                    mVideoView.setVideoController(mController);
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
        ImageView iv = mController.findViewById(R.id.thumb);
        Glide.with(this).load(videoBean.getThumb()).into(iv);
        mVideoView.setVideoController(mController);
        mVideoView.start();
        player.addView(mVideoView);
        thumb.setVisibility(View.GONE);
        mPIPManager.setPlayingPosition(position);
        mPlayer = player;
        mThumb = thumb;

    }
}
