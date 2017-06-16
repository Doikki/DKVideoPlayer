package com.devlin_n.yinyangplayer.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devlin_n.yinyangplayer.R;
import com.devlin_n.yinyangplayer.player.YinYangPlayer;

/**
 * 广告控制器
 * Created by Devlin_n on 2017/4/12.
 */

public class AdController extends BaseVideoController implements View.OnClickListener {
    protected TextView adTime, adDetail;
    protected ImageView back, volume, fullScreen, playButton;

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
        adTime = (TextView) controllerView.findViewById(R.id.ad_time);
        adDetail = (TextView) controllerView.findViewById(R.id.ad_detail);
        adDetail.setText("了解详情>");
        back = (ImageView) controllerView.findViewById(R.id.back);
        back.setVisibility(GONE);
        volume = (ImageView) controllerView.findViewById(R.id.iv_volume);
        fullScreen = (ImageView) controllerView.findViewById(R.id.fullscreen);
        playButton = (ImageView) controllerView.findViewById(R.id.iv_play);
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
            Toast.makeText(getContext(), "施工中~", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.ad_time) {
            mediaPlayer.skipToNext();
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
            case YinYangPlayer.STATE_PLAYING:
                post(mShowProgress);
                playButton.setSelected(true);
                break;
            case YinYangPlayer.STATE_PAUSED:
                playButton.setSelected(false);
                break;
        }
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        switch (playerState) {
            case YinYangPlayer.PLAYER_NORMAL:
                back.setVisibility(GONE);
                break;
            case YinYangPlayer.PLAYER_FULL_SCREEN:
                back.setVisibility(VISIBLE);
                break;
        }
    }

    @Override
    protected int setProgress() {
        if (mediaPlayer == null) {
            return 0;
        }
        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();


        if (adTime != null)
            adTime.setText(String.format("%s | 跳过", (duration - position) / 1000));
        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Toast.makeText(getContext(), "施工中~", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }
}
