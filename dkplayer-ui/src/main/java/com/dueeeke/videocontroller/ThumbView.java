package com.dueeeke.videocontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.controller.MediaPlayerControl;
import com.dueeeke.videoplayer.player.VideoView;

public class ThumbView<T extends MediaPlayerControl> extends FrameLayout implements IControlComponent<T> {

    private T mMediaPlayer;
    
    private ImageView mThumb;
    private ImageView mStartPlay;
    private ProgressBar mLoading;

    public ThumbView(@NonNull Context context) {
        super(context);
    }

    public ThumbView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ThumbView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    {
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_thumb_view, this, true);
        mThumb = findViewById(R.id.thumb);
        mStartPlay = findViewById(R.id.start_play);
        mLoading = findViewById(R.id.loading);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.start();
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void setPlayState(int playState) {
        switch (playState) {
            case VideoView.STATE_PREPARING:
                setVisibility(VISIBLE);
                mStartPlay.setVisibility(View.GONE);
                mLoading.setVisibility(View.VISIBLE);
                break;
            case VideoView.STATE_PLAYING:
            case VideoView.STATE_ERROR:
                setVisibility(GONE);
                break;
            case VideoView.STATE_IDLE:
                setVisibility(VISIBLE);
                mLoading.setVisibility(View.GONE);
                mStartPlay.setVisibility(View.VISIBLE);
                mThumb.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void setPlayerState(int playerState) {

    }

    @Override
    public void setMediaPlayer(T mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    @Override
    public View getView() {
        return this;
    }
}
