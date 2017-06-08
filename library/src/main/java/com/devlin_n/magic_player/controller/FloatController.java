package com.devlin_n.magic_player.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.devlin_n.magic_player.R;
import com.devlin_n.magic_player.player.MagicVideoView;

/**
 * 悬浮播放控制器
 * Created by Devlin_n on 2017/6/1.
 */

public class FloatController extends BaseVideoController implements View.OnClickListener {

    private ImageView playButton;
    private ProgressBar bufferProgress;


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
        playButton = (ImageView) controllerView.findViewById(R.id.iv_play);
        playButton.setOnClickListener(this);
        bufferProgress = (ProgressBar) controllerView.findViewById(R.id.buffering);
        this.setOnClickListener(this);
        controllerView.findViewById(R.id.btn_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_play) {
            doPauseResume();
        } else if (id == R.id.btn_close) {
            mediaPlayer.stopFloatWindow();
        }
    }

    @Override
    public void setPlayState(int playState) {
        switch (playState) {
            case MagicVideoView.STATE_IDLE:
                break;
            case MagicVideoView.STATE_PLAYING:
                playButton.setSelected(true);
                hide();
                break;
            case MagicVideoView.STATE_PAUSED:
                playButton.setSelected(false);
                show(0);
                break;
            case MagicVideoView.STATE_PREPARING:
                playButton.setVisibility(GONE);
                bufferProgress.setVisibility(VISIBLE);
                break;
            case MagicVideoView.STATE_PREPARED:
                bufferProgress.setVisibility(GONE);
                if (mShowing) {
                    playButton.setVisibility(VISIBLE);
                }
                break;
            case MagicVideoView.STATE_ERROR:
                break;
            case MagicVideoView.STATE_BUFFERING:
                playButton.setVisibility(GONE);
                bufferProgress.setVisibility(VISIBLE);
                break;
            case MagicVideoView.STATE_BUFFERED:
                bufferProgress.setVisibility(GONE);
                if (mShowing && !isLocked) {
                    playButton.setVisibility(VISIBLE);
                }
                break;
            case MagicVideoView.STATE_PLAYBACK_COMPLETED:
                playButton.setSelected(false);
                show(0);
                break;
        }
    }


    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    private void show(int timeout){
        if (!mShowing) {
            if (bufferProgress.getVisibility() != VISIBLE) {
                playButton.setVisibility(VISIBLE);
                playButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in));
            }
            mShowing = true;
        }
        post(mShowProgress);

        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }


    @Override
    public void hide() {
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
        if (mShowing) {
            if (bufferProgress.getVisibility() != VISIBLE) {
                playButton.setVisibility(GONE);
                playButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out));
            }
            mShowing = false;
        }
    }
}
