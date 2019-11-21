package com.dueeeke.dkplayer.widget.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.VodControlView;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

public class PortraitWhenFullScreenController extends StandardVideoController {

    @Nullable
    private Activity mActivity;
    private View mFullScreen;

    public PortraitWhenFullScreenController(@NonNull Context context) {
        this(context, null);
    }

    public PortraitWhenFullScreenController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PortraitWhenFullScreenController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = PlayerUtils.scanForActivity(context);
    }

    @Override
    protected void initView() {
        super.initView();
        VodControlView vodControlView = new VodControlView(getContext());
        vodControlView.showBottomProgress(false);
        mFullScreen = vodControlView.findViewById(R.id.fullscreen);
        mFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullScreen();
            }
        });
        addControlComponent(vodControlView);
    }

    @Override
    protected void toggleFullScreen() {
        if (mActivity == null) return;
        int o = mActivity.getRequestedOrientation();
        if (o == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        mFullScreen.setSelected(o != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (!mMediaPlayerWrapper.isFullScreen()) {
            mMediaPlayerWrapper.startFullScreen();
            return true;
        }
        mMediaPlayerWrapper.toggleShowState();
        return true;
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        mOrientationHelper.disable();
        switch (playerState) {
            case VideoView.PLAYER_FULL_SCREEN:
                if (mActivity != null) {
                    int o = mActivity.getRequestedOrientation();
                    mFullScreen.setSelected(o == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    int space = getAdaptCutout() ? (int) PlayerUtils.getStatusBarHeight(getContext()) : 0;
                    adjustView(o, space);
                }
                break;
            case VideoView.PLAYER_NORMAL:
                hideInner();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fullscreen) {
            toggleFullScreen();
        } else if (i == R.id.lock) {
            mMediaPlayerWrapper.toggleLockState();
        } else if (i == R.id.iv_play) {
            togglePlay();
        } else if (i == R.id.back) {
            stopFullScreen();
        } else if (i == R.id.thumb) {
            mMediaPlayerWrapper.start();
            mMediaPlayerWrapper.startFullScreen();
        } else if (i == R.id.iv_replay) {
            mMediaPlayerWrapper.replay(true);
            mMediaPlayerWrapper.startFullScreen();
        }
    }

    @Override
    public void adjustView(int orientation, int space) {
        super.adjustView(orientation, space);
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setPadding(0, space, 0, 0);
        } else {
            setPadding(0, 0, 0, 0);
        }
    }
}