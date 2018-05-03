package com.dueeeke.dkplayer.widget.videoview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.util.WindowUtil;

public class RotateIjkVideoView extends IjkVideoView {
    public RotateIjkVideoView(@NonNull Context context) {
        super(context);
    }

    public RotateIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void startFullScreen() {
        setOnClickListener(null);
        setVideoController(mVideoController);
        Activity activity = WindowUtil.scanForActivity(getContext());
        if (activity == null) return;
        if (isFullScreen) return;
        WindowUtil.hideSystemBar(getContext());
        this.removeView(playerContainer);
        ViewGroup contentView = activity
                .findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(playerContainer, params);
        isFullScreen = true;
        setPlayerState(PLAYER_FULL_SCREEN);
    }

    @Override
    public void stopFullScreen() {
        setVideoController(null);
        setOnClickListener(mOnClickListener);
        Activity activity = WindowUtil.scanForActivity(getContext());
        if (activity == null) return;
        if (!isFullScreen) return;
        WindowUtil.showSystemBar(getContext());
        ViewGroup contentView = activity
                .findViewById(android.R.id.content);
        contentView.removeView(playerContainer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(playerContainer, params);
        isFullScreen = false;
        setPlayerState(PLAYER_NORMAL);
    }

    @Override
    public void setVideoController(@Nullable BaseVideoController mediaController) {
        playerContainer.removeView(mVideoController);
        if (mediaController != null) {
            mediaController.setMediaPlayer(this);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            playerContainer.addView(mediaController, params);
            mVideoController = mediaController;
        }
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        if (isFullScreen) return;
        //暂时移除Controller
        setVideoController(null);
        setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            start();
            startFullScreen();
        }
    };

    @Override
    public void release() {
        setVideoController(mVideoController);
        super.release();
    }

    @Override
    public void stopPlayback() {
        setVideoController(mVideoController);
        super.stopPlayback();
    }
}
