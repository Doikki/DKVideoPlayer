package com.devlin_n.videoplayer.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.devlin_n.videoplayer.R;
import com.devlin_n.videoplayer.player.IjkVideoView;
import com.devlin_n.videoplayer.widget.PlayProgressButton;

/**
 * 悬浮播放控制器
 * Created by Devlin_n on 2017/6/1.
 */

public class FloatController extends BaseVideoController implements View.OnClickListener {

    private PlayProgressButton playProgressButton;


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
        playProgressButton = (PlayProgressButton) controllerView.findViewById(R.id.play_progress_btn);
        playProgressButton.setPlayButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doPauseResume();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_close) {
            mediaPlayer.stopFloatWindow();
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            case IjkVideoView.STATE_IDLE:
                break;
            case IjkVideoView.STATE_PLAYING:
                playProgressButton.setState(PlayProgressButton.STATE_PLAYING);
                hide();
                break;
            case IjkVideoView.STATE_PAUSED:
                playProgressButton.setState(PlayProgressButton.STATE_PAUSE);
                show(0);
                break;
            case IjkVideoView.STATE_PREPARING:
                playProgressButton.setState(PlayProgressButton.STATE_LOADING);
                break;
            case IjkVideoView.STATE_PREPARED:
                playProgressButton.setVisibility(GONE);
                break;
            case IjkVideoView.STATE_ERROR:
                break;
            case IjkVideoView.STATE_BUFFERING:
                playProgressButton.setState(PlayProgressButton.STATE_LOADING);
                playProgressButton.setVisibility(VISIBLE);
                break;
            case IjkVideoView.STATE_BUFFERED:
                playProgressButton.setState(PlayProgressButton.STATE_LOADING_END);
                if (!mShowing) playProgressButton.setVisibility(GONE);
                break;
            case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                playProgressButton.setState(PlayProgressButton.STATE_PAUSE);
                show(0);
                break;
        }
    }


    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    private void show(int timeout) {
        if (!mShowing) {
            playProgressButton.show();
            mShowing = true;
        }
        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }


    @Override
    public void hide() {
        if (mShowing) {
            playProgressButton.hide();
            mShowing = false;
        }
    }
}
