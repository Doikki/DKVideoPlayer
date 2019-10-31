package com.dueeeke.dkplayer.widget.controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.interf.ControllerListener;
import com.dueeeke.videocontroller.ErrorView;
import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 广告控制器
 * Created by Devlin_n on 2017/4/12.
 */

public class AdController extends BaseVideoController implements View.OnClickListener {
    protected TextView adTime, adDetail;
    protected ImageView back, volume, fullScreen, playButton;
    protected ControllerListener listener;

    public AdController(@NonNull Context context) {
        super(context);
    }

    public AdController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_ad_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        adTime = mControllerView.findViewById(R.id.ad_time);
        adDetail = mControllerView.findViewById(R.id.ad_detail);
        adDetail.setText("了解详情>");
        back = mControllerView.findViewById(R.id.back);
        back.setVisibility(GONE);
        volume = mControllerView.findViewById(R.id.iv_volume);
        fullScreen = mControllerView.findViewById(R.id.fullscreen);
        playButton = mControllerView.findViewById(R.id.iv_play);
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

        addControlComponent(new ErrorView(getContext()));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back | id == R.id.fullscreen) {
            doStartStopFullScreen();
        } else if (id == R.id.iv_volume) {
            doMute();
        } else if (id == R.id.ad_detail) {
            if (listener != null) listener.onAdClick();
        } else if (id == R.id.ad_time) {
            if (listener != null) listener.onSkipAd();
        } else if (id == R.id.iv_play) {
            doPauseResume();
        }
    }

    private void doMute() {
        mMediaPlayer.setMute(!mMediaPlayer.isMute());
        volume.setImageResource(mMediaPlayer.isMute() ? R.drawable.dkplayer_ic_action_volume_up : R.drawable.dkplayer_ic_action_volume_off);
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
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
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
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
    protected int setProgress() {
        if (mMediaPlayer == null) {
            return 0;
        }
        int position = (int) mMediaPlayer.getCurrentPosition();
        int duration = (int) mMediaPlayer.getDuration();

        if (adTime != null)
            adTime.setText(String.format("%s | 跳过", (duration - position) / 1000));
        return position;
    }

    @Override
    public boolean onBackPressed() {
        if (mMediaPlayer.isFullScreen()) {
            PlayerUtils.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mMediaPlayer.stopFullScreen();
            setPlayerState(VideoView.PLAYER_NORMAL);
            return true;
        }
        return super.onBackPressed();
    }

    public void setControllerListener(ControllerListener listener) {
        this.listener = listener;
    }
}
