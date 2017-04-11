package com.devlin_n.magic_player;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;
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

public class IjkMediaController extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final int ALERT_WINDOW_PERMISSION_CODE = 1;
    private static final String TAG = "IjkMediaController";
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    protected TextView totalTime, currTime;
    protected MediaPlayerControlInterface mediaPlayer;
    protected ImageView startButton;
    protected ImageView fullScreenButton;
    protected LinearLayout bottomContainer, topContainer;
    protected View controllerView;
    protected SeekBar videoProgress;
    protected ImageView floatScreen;
    protected ImageView backButton;
    protected TextView lock;
    private boolean mShowing;
    private int sDefaultTimeout = 3000;
    private boolean isLive;
    private boolean isLocked;

    private OrientationEventListener orientationEventListener = new OrientationEventListener(getContext()) {

        private int tag = 0;

        @Override
        public void onOrientationChanged(int orientation) {
            Log.d(TAG, "onOrientationChanged: " + orientation);
            if (orientation >= 330) {
                if (tag == 1) return;
                if ((tag == 2 || tag == 3) && !mediaPlayer.isFullScreen()) {
                    tag = 1;
                    return;
                }
                tag = 1;
                WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mediaPlayer.stopFullScreen();
            } else if (orientation >= 250 && orientation <= 270) {
                if (tag == 2) return;
                if (tag == 1 && mediaPlayer.isFullScreen()) {
                    tag = 2;
                    return;
                }
                tag = 2;
                if (!mediaPlayer.isFullScreen()) {
                    mediaPlayer.startFullScreen();
                }
                WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (orientation >= 70 && orientation <= 90) {
                if (tag == 3) return;
                if (tag == 1 && mediaPlayer.isFullScreen()) {
                    tag = 3;
                    return;
                }
                tag = 3;
                if (!mediaPlayer.isFullScreen()) {
                    mediaPlayer.startFullScreen();
                }
                WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
            updateFullScreen();
        }
    };

    public IjkMediaController(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public IjkMediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    protected void initView(Context context) {
        controllerView = LayoutInflater.from(context).inflate(R.layout.layout_media_controller, this);
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
        lock = (TextView) controllerView.findViewById(R.id.lock);
        lock.setOnClickListener(this);
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
            topContainer.setVisibility(VISIBLE);
            bottomContainer.setVisibility(VISIBLE);
            orientationEventListener.enable();
            lock.setText("锁定");
            isLocked = false;
        } else {
            topContainer.setVisibility(INVISIBLE);
            bottomContainer.setVisibility(INVISIBLE);
            orientationEventListener.disable();
            lock.setText("解锁");
            isLocked = true;
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
            lock.setVisibility(VISIBLE);
        } else {
            fullScreenButton.setImageResource(R.drawable.ic_start_fullscreen);
            backButton.setVisibility(INVISIBLE);
            lock.setVisibility(INVISIBLE);
        }
    }

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

    protected void startFloatScreen() {
        mediaPlayer.startFloatScreen();
    }

    protected void doStartStopFullScreen() {
        if (mediaPlayer.isFullScreen()) {
            WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mediaPlayer.stopFullScreen();
        } else {
            WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mediaPlayer.startFullScreen();
        }
        updateFullScreen();
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
        if (!fromUser) {
            return;
        }

        long duration = mediaPlayer.getDuration();
        long newPosition = (duration * progress) / videoProgress.getMax();
        if (currTime != null)
            currTime.setText(stringForTime((int) newPosition));
    }

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
        if (isLocked) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                show(0); // show until hide is called
                break;
            case MotionEvent.ACTION_UP:
                show(sDefaultTimeout); // start timeout
                break;
            case MotionEvent.ACTION_CANCEL:
                hide();
                break;
            default:
                break;
        }
        return true;
    }

    protected interface MediaPlayerControlInterface {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(int pos);

        boolean isPlaying();

        int getBufferPercentage();

        void startFloatScreen();

        void startFullScreen();

        void stopFullScreen();

        boolean isFullScreen();
    }

    public void setMediaPlayer(MediaPlayerControlInterface mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
