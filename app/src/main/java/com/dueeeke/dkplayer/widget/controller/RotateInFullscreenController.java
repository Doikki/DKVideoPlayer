package com.dueeeke.dkplayer.widget.controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

public class RotateInFullscreenController extends StandardVideoController {

    private boolean isLandscape;

    public RotateInFullscreenController(@NonNull Context context) {
        super(context);
    }

    public RotateInFullscreenController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateInFullscreenController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();

        mGestureDetector = new GestureDetector(getContext(), new MyGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (!mMediaPlayer.isFullScreen()) {
                    mMediaPlayer.startFullScreen();
                    return true;
                }
                if (mShowing) {
                    hide();
                } else {
                    show();
                }
                return true;
            }
        });
    }

    @Override
    protected void doStartStopFullScreen() {
        if (isLandscape) {
            PlayerUtils.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isLandscape = false;
        } else {
            PlayerUtils.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isLandscape = true;
        }
        mFullScreenButton.setSelected(isLandscape);
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        switch (playerState) {
            case IjkVideoView.PLAYER_FULL_SCREEN:
                mFullScreenButton.setSelected(false);
                getThumb().setVisibility(GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.fullscreen) {
            doStartStopFullScreen();
        } else if (i == R.id.lock) {
            doLockUnlock();
        } else if (i == R.id.iv_play) {
            doPauseResume();
        } else if (i == R.id.back) {
            if (isLandscape)
                PlayerUtils.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mMediaPlayer.stopFullScreen();
        } else if (i == R.id.thumb) {
            mMediaPlayer.start();
            mMediaPlayer.startFullScreen();
        } else if (i == R.id.iv_replay) {
            mMediaPlayer.replay(true);
            mMediaPlayer.startFullScreen();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mIsLocked) {
            show();
            Toast.makeText(getContext(), R.string.lock_tip, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (mMediaPlayer.isFullScreen()) {
            if (isLandscape)
                PlayerUtils.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mMediaPlayer.stopFullScreen();
            return true;
        }
        return super.onBackPressed();
    }
}