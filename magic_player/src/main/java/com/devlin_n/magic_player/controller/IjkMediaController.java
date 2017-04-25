package com.devlin_n.magic_player.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.devlin_n.magic_player.R;
import com.devlin_n.magic_player.player.IjkVideoView;
import com.devlin_n.magic_player.util.WindowUtil;

/**
 * 直播/点播控制器
 * Created by Devlin_n on 2017/4/7.
 */

public class IjkMediaController extends BaseMediaController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "IjkMediaController";
    protected TextView totalTime, currTime;
    protected ImageView fullScreenButton;
    protected LinearLayout bottomContainer, topContainer;
    protected SeekBar videoProgress;
    protected ImageView floatScreen;
    protected ImageView backButton;
    protected ImageView lock;
    protected TextView title;
    protected ImageView playButton;
    private int sDefaultTimeout = 4000;
    private boolean isLive;
    private boolean isDragging;
    private View statusHolder;


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
        playButton = (ImageView) controllerView.findViewById(R.id.iv_play);
        playButton.setOnClickListener(this);
        title = (TextView) controllerView.findViewById(R.id.title);
        statusHolder = controllerView.findViewById(R.id.status_holder);
        statusHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) WindowUtil.getStatusBarHeight(getContext())));
        statusHolder.setVisibility(GONE);
        topContainer.setVisibility(GONE);
        bottomContainer.setVisibility(GONE);
        lock.setVisibility(GONE);
        playButton.setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.float_screen) {
            mediaPlayer.startFloatWindow();
        } else if (i == R.id.fullscreen || i == R.id.back) {
            doStartStopFullScreen();
        } else if (i == R.id.lock) {
            doLockUnlock();
        } else if (i == R.id.iv_play) {
            doPauseResume();
        }
    }

    private void doPauseResume() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.ic_play);
        } else {
            mediaPlayer.start();
            playButton.setImageResource(R.drawable.ic_pause);
        }
    }

    private void doLockUnlock() {
        if (isLocked) {
            isLocked = false;
            mShowing = false;
            show();
            lock.setImageResource(R.drawable.ic_lock);
        } else {
            hide();
            isLocked = true;
            lock.setImageResource(R.drawable.ic_unlock);
        }
        mediaPlayer.setLock(isLocked);
    }


    @Override
    public void updateFullScreen() {
        if (isLocked) return;
        if (mediaPlayer != null && mediaPlayer.isFullScreen()) {
            fullScreenButton.setImageResource(R.drawable.ic_stop_fullscreen);
            statusHolder.setVisibility(VISIBLE);
            backButton.setVisibility(VISIBLE);
            if (isShowing()) {
                lock.setVisibility(VISIBLE);
                topContainer.setVisibility(VISIBLE);
                WindowUtil.showNavKey(getContext());
                WindowUtil.showStatusBar(getContext());
            } else {
                lock.setVisibility(INVISIBLE);
                topContainer.setVisibility(INVISIBLE);
            }
        } else {
            fullScreenButton.setImageResource(R.drawable.ic_start_fullscreen);
            backButton.setVisibility(INVISIBLE);
            lock.setVisibility(INVISIBLE);
            topContainer.setVisibility(INVISIBLE);
            statusHolder.setVisibility(GONE);
        }
    }

    @Override
    public void updatePlayButton() {
        super.updatePlayButton();
        if (mediaPlayer.getCurrentState() == IjkVideoView.STATE_PREPARING) {
            playButton.setVisibility(GONE);
            playButton.setImageResource(R.drawable.ic_pause);
        } else {
            if (mediaPlayer.isPlaying()) {
                playButton.setImageResource(R.drawable.ic_pause);
            } else {
                playButton.setImageResource(R.drawable.ic_play);
            }
        }

        if (isShowing() && !isLocked) {
            playButton.setVisibility(VISIBLE);
        }

    }

    @Override
    public void startFullScreenDirectly() {
        fullScreenButton.setVisibility(GONE);
        backButton.setVisibility(VISIBLE);
        statusHolder.setVisibility(VISIBLE);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowUtil.getAppCompActivity(getContext()).finish();
            }
        });
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
        if (mShowing) {
            if (mediaPlayer.isFullScreen()) {
                lock.setVisibility(GONE);
                lock.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out));
                if (!isLocked) {
                    hideAllViews();
                }
            } else {
                bottomContainer.setVisibility(GONE);
                bottomContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_bottom_out));
                playButton.setVisibility(GONE);
                if (mediaPlayer.getCurrentState() != IjkVideoView.STATE_BUFFERING) {
                    playButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out));
                }

            }
            mShowing = false;
        }
    }

    private void hideAllViews() {
        playButton.setVisibility(GONE);
        if (mediaPlayer.getCurrentState() != IjkVideoView.STATE_BUFFERING) {
            playButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out));
        }
        bottomContainer.setVisibility(GONE);
        bottomContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_bottom_out));
        topContainer.setVisibility(GONE);
        topContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_top_out));
        WindowUtil.hideStatusBar(getContext());
        WindowUtil.hideNavKey(getContext());
    }

    private void show(int timeout) {
        if (!mShowing) {
            if (mediaPlayer.isFullScreen()) {
                lock.setVisibility(VISIBLE);
                if (!isLocked) {
                    showAllViews();
                }
            } else {
                if (mediaPlayer.getCurrentState() != IjkVideoView.STATE_BUFFERING) {
                    playButton.setVisibility(VISIBLE);
                    playButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in));
                }
                bottomContainer.setVisibility(VISIBLE);
                bottomContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_bottom_in));
                topContainer.setVisibility(GONE);
                if (isLive) {
                    videoProgress.setVisibility(INVISIBLE);
                    totalTime.setVisibility(INVISIBLE);
                }
            }
            mShowing = true;
        }
        post(mShowProgress);

        if (timeout != 0) {
            removeCallbacks(mFadeOut);
            postDelayed(mFadeOut, timeout);
        }
    }

    private void showAllViews() {
        if (mediaPlayer.getCurrentState() != IjkVideoView.STATE_BUFFERING) {
            playButton.setVisibility(VISIBLE);
            playButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in));
        }
        bottomContainer.setVisibility(VISIBLE);
        bottomContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_bottom_in));
        topContainer.setVisibility(VISIBLE);
        topContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_top_in));
        WindowUtil.showStatusBar(getContext());
        WindowUtil.showNavKey(getContext());
        if (isLive) {
            videoProgress.setVisibility(INVISIBLE);
            totalTime.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    @Override
    public void reset() {
        videoProgress.setProgress(0);
        playButton.setImageResource(R.drawable.ic_play);
        show();
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
        if (title != null)
            title.setText(mediaPlayer.getTitle());
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
