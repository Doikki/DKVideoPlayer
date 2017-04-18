package com.devlin_n.magic_player.controller;

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
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devlin_n.magic_player.R;
import com.devlin_n.magic_player.util.WindowUtil;

/**
 * 直播/点播控制器
 * Created by Devlin_n on 2017/4/7.
 */

public class IjkMediaController extends BaseMediaController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final int ALERT_WINDOW_PERMISSION_CODE = 1;
    private static final String TAG = "IjkMediaController";
    protected TextView totalTime, currTime;
    protected ImageView fullScreenButton;
    protected LinearLayout bottomContainer, topContainer;
    protected SeekBar videoProgress;
    protected ImageView floatScreen;
    protected ImageView backButton;
    protected ImageView lock;
    protected TextView title;
    private int sDefaultTimeout = 3000;
    private boolean isLive;
    private boolean isDragging;


    public IjkMediaController(@NonNull Context context) {
        this(context, null);
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
        floatScreen = (ImageView) controllerView.findViewById(R.id.float_screen);
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
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.float_screen) {
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
            if (mAutoRotate) orientationEventListener.enable();
            lock.setImageResource(R.drawable.ic_lock);
        } else {
            hide();
            isLocked = true;
            if (mAutoRotate) orientationEventListener.disable();
            lock.setImageResource(R.drawable.ic_unlock);
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

    public void updateFullScreen() {

        if (mediaPlayer != null && mediaPlayer.isFullScreen()) {
            fullScreenButton.setImageResource(R.drawable.ic_stop_fullscreen);
            backButton.setVisibility(VISIBLE);
            if (isShowing()) {
                lock.setVisibility(VISIBLE);
                WindowUtil.showNavKey(getContext());
                WindowUtil.showStatusBar(getContext());
            } else {
                lock.setVisibility(INVISIBLE);
            }
        } else {
            fullScreenButton.setImageResource(R.drawable.ic_start_fullscreen);
            backButton.setVisibility(INVISIBLE);
            lock.setVisibility(INVISIBLE);
        }
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public boolean getLive() {
        return isLive;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragging = true;
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mediaPlayer.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / videoProgress.getMax();
        mediaPlayer.seekTo((int) newPosition);
        isDragging = false;
        setProgress();
        post(mShowProgress);
//        postDelayed(mFadeOut, sDefaultTimeout);
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
        removeCallbacks(mFadeOut);
        if (isShowing()) {
            if (!isLocked) {
                bottomContainer.setVisibility(GONE);
                topContainer.setVisibility(GONE);
                mediaPlayer.updatePlayButton(GONE);
                topContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_top_out));
                bottomContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_bottom_out));
            }
            if (mediaPlayer.isFullScreen()) {
                WindowUtil.hideStatusBar(getContext());
                WindowUtil.hideNavKey(getContext());
                lock.setVisibility(GONE);
                lock.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_right_out));
            }
        }
        mShowing = false;
    }

    private void show(int timeout) {
        if (mediaPlayer.isFullScreen()) {
            lock.setVisibility(VISIBLE);
            lock.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_right_in));
            WindowUtil.showStatusBar(getContext());
            WindowUtil.showNavKey(getContext());
        }
        if (!isLocked) {
            setProgress();
            bottomContainer.setVisibility(VISIBLE);
            topContainer.setVisibility(VISIBLE);
            mediaPlayer.updatePlayButton(VISIBLE);
            topContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_top_in));
            bottomContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_bottom_in));
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
        videoProgress.setProgress(0);
        show();
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

    private int setProgress() {
        if (mediaPlayer == null || isDragging) {
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
    protected void slideToChangePosition(float deltaX) {
        if (!isLive) {
            super.slideToChangePosition(deltaX);
        } else {
            mSliding = false;
        }
    }
}
