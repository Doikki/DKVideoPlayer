package com.devlin_n.magic_player.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.devlin_n.magic_player.R;

/**
 * 竖屏时的控制器
 * Created by Devlin_n on 2017/4/24.
 */

public class NormalScreenController extends BaseMediaController implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private TextView currTime, totalTime;
    private SeekBar videoProgress;
    private ImageView startFullScreenBtn;
    private LinearLayout bottomContainer;
    private boolean isDragging;


    public NormalScreenController(@NonNull Context context) {
        super(context);
    }

    public NormalScreenController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_controller_normal;
    }

    @Override
    protected void initView() {
        super.initView();
        currTime = (TextView) controllerView.findViewById(R.id.curr_time);
        totalTime = (TextView) controllerView.findViewById(R.id.total_time);
        videoProgress = (SeekBar) controllerView.findViewById(R.id.seekBar);
        bottomContainer = (LinearLayout) controllerView.findViewById(R.id.bottom_container);
        bottomContainer.setVisibility(GONE);
        startFullScreenBtn = (ImageView) controllerView.findViewById(R.id.fullscreen);
        videoProgress.setOnSeekBarChangeListener(this);
        startFullScreenBtn.setOnClickListener(this);
    }

    @Override
    protected int setProgress() {

        if (mediaPlayer == null || isDragging) {
            return 0;
        }
        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        if (videoProgress != null) {
            if (duration > 0) {
                int pos = (int) (position * 1.0 / duration * videoProgress.getMax());
                videoProgress.setProgress(pos);
            }
            int percent = mediaPlayer.getBufferPercentage();
            if (percent >= 95) { //修复第二进度不能100%问题
                videoProgress.setSecondaryProgress(videoProgress.getMax());
            } else {
                videoProgress.setSecondaryProgress(percent * 10);
            }
        }

        if (totalTime != null)
            totalTime.setText(stringForTime(duration));
        if (currTime != null)
            currTime.setText(stringForTime(position));
        return position;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragging = true;
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        long duration = mediaPlayer.getDuration();
        long newPosition = (duration * progress) / videoProgress.getMax();
        if (currTime != null)
            currTime.setText(stringForTime((int) newPosition));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mediaPlayer.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / videoProgress.getMax();
        mediaPlayer.seekTo((int) newPosition);
        isDragging = false;
        setProgress();
        post(mShowProgress);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fullscreen) {
            doStartStopFullScreen();
        }
    }

    @Override
    public void show() {
        super.show();
        bottomContainer.setVisibility(VISIBLE);
        bottomContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_bottom_in));
        mediaPlayer.updatePlayButton(VISIBLE);
        mShowing = true;
        post(mShowProgress);

        removeCallbacks(mFadeOut);
        postDelayed(mFadeOut, 4000);
    }

    @Override
    public void hide() {
        super.hide();
        bottomContainer.setVisibility(GONE);
        bottomContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_bottom_out));
        mediaPlayer.updatePlayButton(GONE);
        mShowing = false;
    }
}
