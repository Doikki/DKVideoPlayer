package com.dueeeke.videoplayer.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dueeeke.videoplayer.R;
import com.dueeeke.videoplayer.player.IjkVideoView;

/**
 * 悬浮播放控制器
 * Created by Devlin_n on 2017/6/1.
 */

public class FloatController extends BaseVideoController implements View.OnClickListener {


    private ProgressBar proLoading;
    private ImageView playButton;


    public FloatController(@NonNull Context context) {
        super(context);
    }

    public FloatController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_float_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        this.setOnClickListener(this);
        controllerView.findViewById(R.id.btn_close).setOnClickListener(this);
        proLoading = controllerView.findViewById(R.id.loading);
        playButton = controllerView.findViewById(R.id.start_play);
        playButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_close) {
            mediaPlayer.stopFloatWindow();
        } else if (id == R.id.start_play) {
            doPauseResume();
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            case IjkVideoView.STATE_IDLE:
                playButton.setSelected(false);
                playButton.setVisibility(VISIBLE);
                proLoading.setVisibility(GONE);
                break;
            case IjkVideoView.STATE_PLAYING:
                playButton.setSelected(true);
                playButton.setVisibility(GONE);
                proLoading.setVisibility(GONE);
                hide();
                break;
            case IjkVideoView.STATE_PAUSED:
                playButton.setSelected(false);
                playButton.setVisibility(VISIBLE);
                proLoading.setVisibility(GONE);
                show(0);
                break;
            case IjkVideoView.STATE_PREPARING:
                playButton.setVisibility(GONE);
                proLoading.setVisibility(VISIBLE);
                break;
            case IjkVideoView.STATE_PREPARED:
                playButton.setVisibility(GONE);
                proLoading.setVisibility(GONE);
                break;
            case IjkVideoView.STATE_ERROR:
                break;
            case IjkVideoView.STATE_BUFFERING:
                playButton.setVisibility(GONE);
                proLoading.setVisibility(VISIBLE);
                break;
            case IjkVideoView.STATE_BUFFERED:
                playButton.setVisibility(GONE);
                proLoading.setVisibility(GONE);
                break;
            case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                show(0);
                break;
        }
    }


    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    private void show(int timeout) {
        if (currentPlayState == IjkVideoView.STATE_BUFFERING) return;
        if (!mShowing) {
            playButton.setVisibility(VISIBLE);
        }
        mShowing = true;

        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }


    @Override
    public void hide() {
        if (currentPlayState == IjkVideoView.STATE_BUFFERING) return;
        if (mShowing) {
            playButton.setVisibility(GONE);
            mShowing = false;
        }
    }
}
