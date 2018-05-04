package com.dueeeke.dkplayer.widget.controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.interf.ControllerListener;
import com.dueeeke.dkplayer.interf.ListMediaPlayerControl;
import com.dueeeke.videoplayer.controller.GestureVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.util.WindowUtil;

/**
 * 广告控制器
 * Created by Devlin_n on 2017/4/12.
 */

public class AdController extends GestureVideoController implements View.OnClickListener {
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
        adTime = controllerView.findViewById(R.id.ad_time);
        adDetail = controllerView.findViewById(R.id.ad_detail);
        adDetail.setText("了解详情>");
        back = controllerView.findViewById(R.id.back);
        back.setVisibility(GONE);
        volume = controllerView.findViewById(R.id.iv_volume);
        fullScreen = controllerView.findViewById(R.id.fullscreen);
        playButton = controllerView.findViewById(R.id.iv_play);
        playButton.setOnClickListener(this);
        adTime.setOnClickListener(this);
        adDetail.setOnClickListener(this);
        back.setOnClickListener(this);
        volume.setOnClickListener(this);
        fullScreen.setOnClickListener(this);
        mShowing = true;
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
            ((ListMediaPlayerControl) mediaPlayer).skipToNext();
        } else if (id == R.id.iv_play) {
            doPauseResume();
        }
    }

    private void doMute() {
        mediaPlayer.setMute();
        if (mediaPlayer.isMute()) {
            volume.setImageResource(R.drawable.ic_action_volume_up);
        } else {
            volume.setImageResource(R.drawable.ic_action_volume_off);
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            case IjkVideoView.STATE_PLAYING:
                post(mShowProgress);
                playButton.setSelected(true);
                break;
            case IjkVideoView.STATE_PAUSED:
                playButton.setSelected(false);
                break;
        }
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        switch (playerState) {
            case IjkVideoView.PLAYER_NORMAL:
                back.setVisibility(GONE);
                break;
            case IjkVideoView.PLAYER_FULL_SCREEN:
                back.setVisibility(VISIBLE);
                break;
        }
    }

    @Override
    protected int setProgress() {
        if (mediaPlayer == null) {
            return 0;
        }
        int position = (int) mediaPlayer.getCurrentPosition();
        int duration = (int) mediaPlayer.getDuration();

        if (adTime != null)
            adTime.setText(String.format("%s | 跳过", (duration - position) / 1000));
        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (listener != null) listener.onAdClick();
                break;
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        if (mediaPlayer.isFullScreen()) {
            WindowUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mediaPlayer.stopFullScreen();
            setPlayerState(IjkVideoView.PLAYER_NORMAL);
            return true;
        }
        return super.onBackPressed();
    }

    public void setControllerListener(ControllerListener listener) {
        this.listener = listener;
    }
}
