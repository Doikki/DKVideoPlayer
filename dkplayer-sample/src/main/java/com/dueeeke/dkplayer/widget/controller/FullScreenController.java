package com.dueeeke.dkplayer.widget.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

public class FullScreenController extends StandardVideoController {
    public FullScreenController(@NonNull Context context) {
        super(context);
    }

    public FullScreenController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        adjustLandscape();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lock) {
            doLockUnlock();
        } else if (i == R.id.iv_play || i == R.id.iv_replay) {
            doPauseResume();
        } else if (i == R.id.back) {
            PlayerUtils.scanForActivity(getContext()).finish();
        }
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            mFullScreenButton.setVisibility(GONE);
        }
    }


    @Override
    protected void adjustReserveLandscape() {
        super.adjustReserveLandscape();
        mBottomContainer.setPadding(0, 0, (int) (mPadding + getResources().getDimension(R.dimen.default_spacing)), 0);
    }

    @Override
    protected void adjustLandscape() {
        super.adjustLandscape();
        mBottomContainer.setPadding(mPadding, 0, (int) getResources().getDimension(R.dimen.default_spacing), 0);
    }
}
