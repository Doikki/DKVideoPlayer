package com.devlin_n.magic_player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Devlin_n on 2017/4/7.
 */

public class IjkMediaController extends BaseIjkMediaController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    protected TextView totalTime, currTime;
    private boolean mShowing;
    private int sDefaultTimeout = 3000;
    private boolean isLive;


    public IjkMediaController(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public IjkMediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    protected void initView(Context context) {
        super.initView(context);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        startButton = (ImageView) controllerView.findViewById(R.id.play);
        floatScreen = (ImageView) controllerView.findViewById(R.id.float_screen);
        startButton.setOnClickListener(this);
        fullScreenButton = (ImageView) controllerView.findViewById(R.id.fullscreen);
        fullScreenButton.setOnClickListener(this);
        bottomContainer = (LinearLayout) controllerView.findViewById(R.id.bottom_container);
        topContainer = (LinearLayout) controllerView.findViewById(R.id.top_container);
        videoProgress = (SeekBar) controllerView.findViewById(R.id.seekBar);
        videoProgress.setOnSeekBarChangeListener(this);
        totalTime = (TextView) controllerView.findViewById(R.id.total_time);
        currTime = (TextView) controllerView.findViewById(R.id.curr_time);
        floatScreen.setOnClickListener(this);
        backButton = (ImageView) controllerView.findViewById(R.id.back);
        backButton.setVisibility(INVISIBLE);
        backButton.setOnClickListener(this);
    }

    @Override
    protected int getControllerViewLayout() {
        return R.layout.layout_media_controller;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.play) {
            startPlayLogic();
        } else if (i == R.id.float_screen) {
            startFloatScreen();
        } else if (i == R.id.fullscreen || i == R.id.back) {
            startFullScreen();
        }
    }

    @Override
    protected void updateFullScreen() {

        if (mediaPlayer.isFullScreen()) {
            fullScreenButton.setImageResource(R.drawable.ic_stop_fullscreen);
            backButton.setVisibility(VISIBLE);
        } else {
            fullScreenButton.setImageResource(R.drawable.ic_start_fullscreen);
            backButton.setVisibility(INVISIBLE);
        }
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        removeCallbacks(mShowProgress);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mediaPlayer.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / videoProgress.getMax();
        mediaPlayer.seekTo((int) newPosition);
        setProgress();
        post(mShowProgress);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "onProgressChanged: " + progress);
        if (!fromUser) {
            return;
        }

        long duration = mediaPlayer.getDuration();
        long newPosition = (duration * progress) / videoProgress.getMax();
        if (currTime != null)
            currTime.setText(stringForTime((int) newPosition));
    }

    @Override
    public void hide() {
        if (mShowing) {
            removeCallbacks(mShowProgress);
            bottomContainer.setVisibility(GONE);
            topContainer.setVisibility(GONE);
            startButton.setVisibility(GONE);
            mShowing = false;
        }
    }

    private void show(int timeout) {
        if (!mShowing) {
            setProgress();
            bottomContainer.setVisibility(VISIBLE);
            topContainer.setVisibility(VISIBLE);
            startButton.setVisibility(VISIBLE);
            if (isLive) {
                videoProgress.setVisibility(INVISIBLE);
                totalTime.setVisibility(INVISIBLE);
            }
            mShowing = true;
        }

        post(mShowProgress);

        if (timeout != 0) {
            removeCallbacks(mFadeOut);
            postDelayed(mFadeOut, timeout);
        }
    }

    public void show() {
        show(sDefaultTimeout);
    }

    @Override
    protected void reset() {
        startButton.setImageResource(R.drawable.ic_play);
        videoProgress.setProgress(0);
        show(0);
    }

    public boolean isShowing() {
        return mShowing;
    }

    private final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mShowing && mediaPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private int setProgress() {
        if (mediaPlayer == null) {
            return 0;
        }
        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        if (videoProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = videoProgress.getMax() * position / duration;
                videoProgress.setProgress((int) pos);
            }
            int percent = mediaPlayer.getBufferPercentage();
            videoProgress.setSecondaryProgress(percent * 10);
        }

        if (totalTime != null)
            totalTime.setText(stringForTime(duration));
        if (currTime != null)
            currTime.setText(stringForTime(position));

        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                show(0); // show until hide is called
                Log.d(TAG, "onTouch: ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                show(sDefaultTimeout); // start timeout
                Log.d(TAG, "onTouch: ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                hide();
                Log.d(TAG, "onTouch: ACTION_CANCEL");
                break;
            default:
                break;
        }
        return true;
    }
}
