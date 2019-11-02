package com.dueeeke.videocontroller.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videocontroller.R;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.controller.MediaPlayerControlWrapper;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;

/**
 * 准备播放界面
 */
public class PrepareView extends FrameLayout implements IControlComponent {

    private MediaPlayerControlWrapper mMediaPlayer;
    
    private ImageView mThumb;
    private ImageView mStartPlay;
    private ProgressBar mLoading;
    private FrameLayout mNetWarning;

    public PrepareView(@NonNull Context context) {
        super(context);
    }

    public PrepareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PrepareView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    {
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_prepare_view, this, true);
        mThumb = findViewById(R.id.thumb);
        mStartPlay = findViewById(R.id.start_play);
        mLoading = findViewById(R.id.loading);
        mNetWarning = findViewById(R.id.net_warning_layout);
        findViewById(R.id.status_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                VideoViewManager.instance().setPlayOnMobileNetwork(true);
                mMediaPlayer.start();
            }
        });
    }

    /**
     * 设置点击此界面开始播放
     */
    public void setClickStart() {
        setOnClickListener(new OnClickListener() {
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
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_PREPARING:
                bringToFront();
                setVisibility(VISIBLE);
                mStartPlay.setVisibility(View.GONE);
                mNetWarning.setVisibility(GONE);
                mLoading.setVisibility(View.VISIBLE);
                break;
            case VideoView.STATE_PLAYING:
            case VideoView.STATE_ERROR:
                setVisibility(GONE);
                break;
            case VideoView.STATE_IDLE:
                setVisibility(VISIBLE);
                bringToFront();
                mLoading.setVisibility(View.GONE);
                mNetWarning.setVisibility(GONE);
                mStartPlay.setVisibility(View.VISIBLE);
                mThumb.setVisibility(View.VISIBLE);
                break;
            case VideoView.STATE_START_ABORT:
                setVisibility(VISIBLE);
                mNetWarning.setVisibility(VISIBLE);
                mNetWarning.bringToFront();
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void attach(MediaPlayerControlWrapper mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void adjustPortrait(int space) {

    }

    @Override
    public void adjustLandscape(int space) {

    }

    @Override
    public void adjustReserveLandscape(int space) {

    }
}
