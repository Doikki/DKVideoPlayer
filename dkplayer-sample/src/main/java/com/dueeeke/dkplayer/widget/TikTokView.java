package com.dueeeke.dkplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.dkplayer.R;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.controller.MediaPlayerControl;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;

public class TikTokView extends FrameLayout implements IControlComponent {

    private ImageView thumb;
    private ImageView mPlayBtn;

    private MediaPlayerControl mMediaPlayer;

    public TikTokView(@NonNull Context context) {
        super(context);
    }

    public TikTokView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TikTokView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_tiktok_controller, this, true);
        thumb = findViewById(R.id.iv_thumb);
        mPlayBtn = findViewById(R.id.play_btn);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
            }
        });
    }


    private void togglePlay() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
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
            case VideoView.STATE_IDLE:
                L.e("STATE_IDLE " + hashCode());
                thumb.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PLAYING:
                L.e("STATE_PLAYING " + hashCode());
                thumb.setVisibility(GONE);
                mPlayBtn.setVisibility(GONE);
                break;
            case VideoView.STATE_PAUSED:
                L.e("STATE_PAUSED " + hashCode());
                thumb.setVisibility(GONE);
                mPlayBtn.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PREPARED:
                L.e("STATE_PREPARED " + hashCode());
                break;
            case VideoView.STATE_ERROR:
                L.e("STATE_ERROR " + hashCode());
                Toast.makeText(getContext(), R.string.dkplayer_error_message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void setPlayerState(int playerState) {

    }

    @Override
    public void attach(MediaPlayerControl mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    @Override
    public View getView() {
        return this;
    }
}
