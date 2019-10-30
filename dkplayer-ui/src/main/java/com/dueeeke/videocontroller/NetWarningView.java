package com.dueeeke.videocontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.controller.MediaPlayerControl;
import com.dueeeke.videoplayer.player.VideoViewManager;

public class NetWarningView<T extends MediaPlayerControl> extends FrameLayout implements IControlComponent<T> {

    private T mMediaPlayer;

    public NetWarningView(@NonNull Context context) {
        super(context);
    }

    public NetWarningView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NetWarningView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_net_warning_view, this, true);
        findViewById(R.id.status_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                VideoViewManager.instance().setPlayOnMobileNetwork(true);
                mMediaPlayer.start();
            }
        });
        setClickable(true);
    }

    @Override
    public void show() {
        setVisibility(VISIBLE);
    }

    @Override
    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public void setPlayState(int playState) {

    }

    @Override
    public void setPlayerState(int playerState) {

    }

    @Override
    public void setMediaPlayer(T mediaPlayer) {
        this.mMediaPlayer = mediaPlayer;
    }

    @Override
    public View getView() {
        return this;
    }
}
