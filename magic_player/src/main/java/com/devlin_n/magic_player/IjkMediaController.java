package com.devlin_n.magic_player;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

/**
 * 控制器
 * Created by Devlin_n on 2017/4/7.
 */

public class IjkMediaController extends BaseMediaController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final int ALERT_WINDOW_PERMISSION_CODE = 1;
    private static final String TAG = "IjkMediaController";
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    protected TextView totalTime, currTime;
    protected ImageView startButton;
    protected ImageView fullScreenButton;
    protected LinearLayout bottomContainer, topContainer;
    protected SeekBar videoProgress;
    protected ImageView floatScreen;
    protected ImageView backButton;
    protected ImageView lock;
    protected TextView title;
    private boolean mShowing;
    private int sDefaultTimeout = 3000;
    private boolean isLive;
    private boolean isLocked;


    public IjkMediaController(@NonNull Context context) {
        super(context);
    }

    public IjkMediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_media_controller;
    }

    @Override
    protected void initView() {
        super.initView();
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
        lock = (ImageView) controllerView.findViewById(R.id.lock);
        lock.setOnClickListener(this);
        title = (TextView) controllerView.findViewById(R.id.title);
        orientationEventListener.enable();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.play) {
            startPlayLogic();
        } else if (i == R.id.float_screen) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sdk23Permission();
            } else {
                startFloatScreen();
            }
        } else if (i == R.id.fullscreen || i == R.id.back) {
            doStartStopFullScreen();
        } else if (i == R.id.lock) {
            doLockUnlock();
        }
    }

    private void doLockUnlock() {
        if (isLocked) {
            isLocked = false;
            show();
            orientationEventListener.enable();
            lock.setImageResource(R.drawable.ic_unlock);
        } else {
            isLocked = true;
            hide();
            orientationEventListener.disable();
            lock.setImageResource(R.drawable.ic_lock);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sdk23Permission() {
        if (!Settings.canDrawOverlays(WindowUtil.getAppCompActivity(getContext()))) {
            Toast.makeText(WindowUtil.getAppCompActivity(getContext()), R.string.float_window_warning, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + WindowUtil.getAppCompActivity(getContext()).getPackageName()));
            WindowUtil.getAppCompActivity(getContext()).startActivityForResult(intent, ALERT_WINDOW_PERMISSION_CODE);
        } else {
            startFloatScreen();
        }
    }

    protected void updateFullScreen() {

        if (mediaPlayer.isFullScreen()) {
            fullScreenButton.setImageResource(R.drawable.ic_stop_fullscreen);
            backButton.setVisibility(VISIBLE);
            if (!isShowing()) {
                lock.setVisibility(INVISIBLE);
            }
        } else {
            fullScreenButton.setImageResource(R.drawable.ic_start_fullscreen);
            backButton.setVisibility(INVISIBLE);
            lock.setVisibility(INVISIBLE);
        }
    }

    @Override
    public boolean lockBack() {
        return isLocked;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public void startPlay() {
        startPlayLogic();
    }

    protected void startPlayLogic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            startButton.setImageResource(R.drawable.ic_play);
        } else {
            mediaPlayer.start();
            startButton.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
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
        removeCallbacks(mShowProgress);
        bottomContainer.setVisibility(GONE);
        topContainer.setVisibility(GONE);
        lock.setVisibility(GONE);
        mShowing = false;
    }

    private void show(int timeout) {
        if (mediaPlayer.isFullScreen()) lock.setVisibility(VISIBLE);
        if (isLocked) {
            bottomContainer.setVisibility(INVISIBLE);
            topContainer.setVisibility(INVISIBLE);
        } else {
            setProgress();
            bottomContainer.setVisibility(VISIBLE);
            topContainer.setVisibility(VISIBLE);
            if (isLive) {
                videoProgress.setVisibility(INVISIBLE);
                totalTime.setVisibility(INVISIBLE);
            }
        }
        mShowing = true;
        post(mShowProgress);

        if (timeout != 0) {
            removeCallbacks(mFadeOut);
            postDelayed(mFadeOut, timeout);
        }
    }

    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    @Override
    public void reset() {
        startButton.setImageResource(R.drawable.ic_play);
        videoProgress.setProgress(0);
        show();
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
        if (title != null)
            title.setText(mediaPlayer.getTitle());

        Log.d(TAG, "setProgress: " + duration);
        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isShowing()) {
                    hide();
                } else {
                    show();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                hide();
                break;
            default:
                break;
        }
        return true;
    }
}
