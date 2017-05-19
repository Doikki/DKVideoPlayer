package com.devlin_n.magic_player.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devlin_n.magic_player.R;

/**
 * 广告控制器
 * Created by Devlin_n on 2017/4/12.
 */

public class AdController extends BaseVideoController implements View.OnClickListener {
    private static final String TAG = AdController.class.getSimpleName();
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
            volume.setImageResource(R.drawable.ic_volume_on);
        } else {
            volume.setImageResource(R.drawable.ic_volume_off);
        }
    }

    @Override
    public void updatePlayButton() {
        playButton.post(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    playButton.setSelected(false);
                } else {
                    playButton.setSelected(true);
                }
            }
        });
        updateProgress();
    }

    @Override
    public void updateFullScreen() {
        super.updateFullScreen();
        if (mediaPlayer != null) {
            if (mediaPlayer.isFullScreen()) {
                back.setVisibility(VISIBLE);
                fullScreen.setImageResource(R.drawable.ic_stop_fullscreen);
            } else {
                fullScreen.setImageResource(R.drawable.ic_start_fullscreen);
                back.setVisibility(GONE);
            }
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
