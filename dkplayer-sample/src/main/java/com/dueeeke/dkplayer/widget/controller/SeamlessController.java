package com.dueeeke.dkplayer.widget.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videocontroller.StatusView;
import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.controller.MediaPlayerControl;
import com.dueeeke.videoplayer.player.VideoView;

public class SeamlessController extends BaseVideoController<MediaPlayerControl> {

    private ImageView mMute;

    private StatusView mStatusView;

    public SeamlessController(@NonNull Context context) {
        super(context);
    }

    public SeamlessController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SeamlessController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_seamless_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        setClickable(false);
        setFocusable(false);
        mMute = mControllerView.findViewById(R.id.iv_mute);
        mMute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMute();
            }
        });

        mStatusView = new StatusView(getContext());
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl mediaPlayer) {
        super.setMediaPlayer(mediaPlayer);
        mStatusView.attachMediaPlayer(mMediaPlayer);
    }


    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            case VideoView.STATE_ERROR:
                mStatusView.showErrorView(this);
                break;
            case VideoView.STATE_IDLE:
                mStatusView.dismiss();
                break;
        }
    }

    /**
     * 显示移动网络播放警告
     */
    @Override
    public void showNetWarning() {
        mStatusView.showNetWarning(this);
    }

    @Override
    public void hideNetWarning() {
        mStatusView.dismiss();
    }

    private void toggleMute() {
        if (mMediaPlayer.isMute()) {
            mMediaPlayer.setMute(false);
            mMute.setSelected(true);
        } else {
            mMediaPlayer.setMute(true);
            mMute.setSelected(false);
        }
    }

    public void resetController() {
        mMute.setSelected(false);
    }
}
