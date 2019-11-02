package com.dueeeke.dkplayer.widget.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.interf.ControllerListener;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.controller.MediaPlayerControlWrapper;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

public class AdControlView extends FrameLayout implements IControlComponent, View.OnClickListener {

    protected TextView adTime, adDetail;
    protected ImageView back, volume, fullScreen, playButton;
    protected ControllerListener listener;

    private MediaPlayerControlWrapper mMediaPlayer;

    public AdControlView(@NonNull Context context) {
        super(context);
    }

    public AdControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_ad_control_view, this, true);
        adTime = findViewById(R.id.ad_time);
        adDetail = findViewById(R.id.ad_detail);
        adDetail.setText("了解详情>");
        back = findViewById(R.id.back);
        back.setVisibility(GONE);
        volume = findViewById(R.id.iv_volume);
        fullScreen = findViewById(R.id.fullscreen);
        playButton = findViewById(R.id.iv_play);
        playButton.setOnClickListener(this);
        adTime.setOnClickListener(this);
        adDetail.setOnClickListener(this);
        back.setOnClickListener(this);
        volume.setOnClickListener(this);
        fullScreen.setOnClickListener(this);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onAdClick();
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
            case VideoView.STATE_PLAYING:
                post(mShowProgress);
                playButton.setSelected(true);
                break;
            case VideoView.STATE_PAUSED:
                playButton.setSelected(false);
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                back.setVisibility(GONE);
                fullScreen.setSelected(false);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                back.setVisibility(VISIBLE);
                fullScreen.setSelected(true);
                break;
        }
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back | id == R.id.fullscreen) {
            toggleFullScreen();
        } else if (id == R.id.iv_volume) {
            doMute();
        } else if (id == R.id.ad_detail) {
            if (listener != null) listener.onAdClick();
        } else if (id == R.id.ad_time) {
            if (listener != null) listener.onSkipAd();
        } else if (id == R.id.iv_play) {
            mMediaPlayer.togglePlay();
        }
    }

    private void doMute() {
        mMediaPlayer.setMute(!mMediaPlayer.isMute());
        volume.setImageResource(mMediaPlayer.isMute() ? R.drawable.dkplayer_ic_action_volume_up : R.drawable.dkplayer_ic_action_volume_off);
    }

    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null) {
            mMediaPlayer.toggleFullScreen(activity);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            post(mShowProgress);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(mShowProgress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mShowProgress);
    }

    /**
     * 刷新进度Runnable
     */
    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mMediaPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    /**
     * 重写此方法实现刷新进度功能
     */
    private int setProgress() {
        int position = (int) mMediaPlayer.getCurrentPosition();
        setProgress(position);
        return position;
    }

    private void setProgress(int position) {
        if (mMediaPlayer == null) {
            return;
        }
        int duration = (int) mMediaPlayer.getDuration();
        if (adTime != null)
            adTime.setText(String.format("%s | 跳过", (duration - position) / 1000));
    }


    public void setControllerListener(ControllerListener listener) {
        this.listener = listener;
    }


}
