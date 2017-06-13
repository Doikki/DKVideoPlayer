package com.devlin_n.yin_yang_player.controller;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devlin_n.yin_yang_player.R;
import com.devlin_n.yin_yang_player.player.YinYangPlayer;
import com.devlin_n.yin_yang_player.util.L;
import com.devlin_n.yin_yang_player.util.WindowUtil;
import com.devlin_n.yin_yang_player.widget.PlayProgressButton;

/**
 * 直播/点播控制器
 * Created by Devlin_n on 2017/4/7.
 */

public class StandardVideoController extends BaseVideoController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    protected TextView totalTime, currTime;
    protected ImageView fullScreenButton;
    protected LinearLayout bottomContainer, topContainer;
    protected SeekBar videoProgress;
    protected ImageView floatScreen;
    protected ImageView backButton;
    protected ImageView lock;
    protected TextView title;
    private boolean isLive;
    private boolean isDragging;
    private View statusHolder;

    private ProgressBar bottomProgress;
    private PlayProgressButton playProgressButton;
    private ImageView thumb;
    private LinearLayout completeContainer;
    private boolean showTopContainer;
    private Animation showAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in);
    private Animation hideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out);


    public StandardVideoController(@NonNull Context context) {
        this(context, null);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_standard_controller;
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
        backButton.setOnClickListener(this);
        lock = (ImageView) controllerView.findViewById(R.id.lock);
        lock.setOnClickListener(this);
        thumb = (ImageView) controllerView.findViewById(R.id.thumb);
        thumb.setOnClickListener(this);
        playProgressButton = (PlayProgressButton) controllerView.findViewById(R.id.play_progress_btn);
        playProgressButton.setPlayButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doPauseResume();
            }
        });
        bottomProgress = (ProgressBar) controllerView.findViewById(R.id.bottom_progress);
        ImageView rePlayButton = (ImageView) controllerView.findViewById(R.id.iv_replay);
        rePlayButton.setOnClickListener(this);
        completeContainer = (LinearLayout) controllerView.findViewById(R.id.complete_container);
        completeContainer.setOnClickListener(this);
        title = (TextView) controllerView.findViewById(R.id.title);
        statusHolder = controllerView.findViewById(R.id.status_holder);
        statusHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) WindowUtil.getStatusBarHeight(getContext())));
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
        } else if (i == R.id.thumb || i == R.id.iv_replay) {
            doPauseResume();
        }
    }

    public void showTopContainer() {
        this.showTopContainer = true;
        topContainer.setVisibility(VISIBLE);
    }

    @Override
    public void setPlayerState(int playerState) {
        switch (playerState) {
            case YinYangPlayer.PLAYER_NORMAL:
                if (isLocked) return;
                setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                gestureEnabled = false;
                fullScreenButton.setSelected(false);
                backButton.setVisibility(GONE);
                lock.setVisibility(GONE);
                statusHolder.setVisibility(GONE);
                if (mShowing) topContainer.setVisibility(showTopContainer ? VISIBLE : GONE);
                break;
            case YinYangPlayer.PLAYER_FULL_SCREEN:
                if (isLocked) return;
                setLayoutParams(new FrameLayout.LayoutParams(
                        screenHeight,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                gestureEnabled = true;
                fullScreenButton.setSelected(true);
                statusHolder.setVisibility(VISIBLE);
                backButton.setVisibility(VISIBLE);
                if (mShowing) {
                    lock.setVisibility(VISIBLE);
                    topContainer.setVisibility(VISIBLE);
                    WindowUtil.showNavKey(getContext());
                    WindowUtil.showStatusBar(getContext());
                } else {
                    lock.setVisibility(GONE);
                }
                break;
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            case YinYangPlayer.STATE_IDLE:
                L.e("STATE_IDLE");
                hide();
                isLocked = false;
                lock.setSelected(false);
                mediaPlayer.setLock(false);
                completeContainer.setVisibility(GONE);
                bottomProgress.setVisibility(GONE);
                thumb.setVisibility(VISIBLE);
                playProgressButton.setVisibility(VISIBLE);
                playProgressButton.reset();
                if (!mediaPlayer.isFullScreen() && showTopContainer) {
                    topContainer.setVisibility(VISIBLE);
                } else {
                    topContainer.setVisibility(GONE);
                }
                break;
            case YinYangPlayer.STATE_PLAYING:
                L.e("STATE_PLAYING");
                post(mShowProgress);
                playProgressButton.setState(PlayProgressButton.STATE_PLAYING);
                thumb.setVisibility(GONE);
                completeContainer.setVisibility(GONE);
                topContainer.setVisibility(GONE);
                hide();
                break;
            case YinYangPlayer.STATE_PAUSED:
                L.e("STATE_PAUSED");
                playProgressButton.setState(PlayProgressButton.STATE_PAUSE);
                show(0);
                break;
            case YinYangPlayer.STATE_PREPARING:
                L.e("STATE_PREPARING");
                completeContainer.setVisibility(GONE);
                topContainer.setVisibility(GONE);
                playProgressButton.setState(PlayProgressButton.STATE_LOADING);
                break;
            case YinYangPlayer.STATE_PREPARED:
                L.e("STATE_PREPARED");
                if (!isLive) bottomProgress.setVisibility(VISIBLE);
                playProgressButton.setVisibility(GONE);
                break;
            case YinYangPlayer.STATE_ERROR:
                L.e("STATE_ERROR");
                break;
            case YinYangPlayer.STATE_BUFFERING:
                L.e("STATE_BUFFERING");
                playProgressButton.setState(PlayProgressButton.STATE_LOADING);
                playProgressButton.setVisibility(VISIBLE);
                break;
            case YinYangPlayer.STATE_BUFFERED:
                L.e("STATE_BUFFERED");
                playProgressButton.setState(PlayProgressButton.STATE_LOADING_END);
                if (!mShowing || isLocked) playProgressButton.setVisibility(GONE);
                break;
            case YinYangPlayer.STATE_PLAYBACK_COMPLETED:
                L.e("STATE_PLAYBACK_COMPLETED");
                hide();
                thumb.setVisibility(VISIBLE);
                completeContainer.setVisibility(VISIBLE);
                bottomProgress.setProgress(0);
                bottomProgress.setSecondaryProgress(0);
                if (mediaPlayer.isFullScreen()) {
                    topContainer.setVisibility(GONE);
                } else {
                    topContainer.setVisibility(showTopContainer ? VISIBLE : GONE);
                }
                isLocked = false;
                mediaPlayer.setLock(false);
                break;
        }
    }

    private void doLockUnlock() {
        if (isLocked) {
            isLocked = false;
            mShowing = false;
            gestureEnabled = true;
            show();
            lock.setSelected(false);
            Toast.makeText(getContext(), R.string.unlocked, Toast.LENGTH_SHORT).show();
        } else {
            hide();
            isLocked = true;
            gestureEnabled = false;
            lock.setSelected(true);
            Toast.makeText(getContext(), R.string.locked, Toast.LENGTH_SHORT).show();
        }
        mediaPlayer.setLock(isLocked);
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

    /**
     * 设置是否为直播视频
     */
    public void setLive(boolean live) {
        isLive = live;
        bottomProgress.setVisibility(GONE);
        videoProgress.setVisibility(INVISIBLE);
        totalTime.setVisibility(INVISIBLE);
        floatScreen.setVisibility(VISIBLE);
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
        post(mShowProgress);
        show();
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
        if (mShowing) {
            if (mediaPlayer.isFullScreen()) {
                lock.setVisibility(GONE);
                if (!isLocked) {
                    hideAllViews();
                    WindowUtil.hideStatusBar(getContext());
                    WindowUtil.hideNavKey(getContext());
                    topContainer.setVisibility(GONE);
                    topContainer.startAnimation(hideAnim);
                }
            } else {
                if (showTopContainer) {
                    topContainer.setVisibility(GONE);
                    topContainer.startAnimation(hideAnim);
                }
                hideAllViews();
            }
            if (!isLive && !isLocked) {
                bottomProgress.setVisibility(VISIBLE);
                bottomProgress.startAnimation(showAnim);
            }
            mShowing = false;
        }
    }

    private void hideAllViews() {
        playProgressButton.hide();
        bottomContainer.setVisibility(GONE);
        bottomContainer.startAnimation(hideAnim);
    }

    private void show(int timeout) {
        if (!mShowing) {
            if (mediaPlayer.isFullScreen()) {
                lock.setVisibility(VISIBLE);
                if (!isLocked) {
                    showAllViews();
                    topContainer.setVisibility(VISIBLE);
                    topContainer.startAnimation(showAnim);
                }
            } else {
                showAllViews();
                if (showTopContainer) {
                    topContainer.setVisibility(VISIBLE);
                    topContainer.startAnimation(showAnim);
                }
            }
            if (!isLocked && !isLive) {
                bottomProgress.setVisibility(GONE);
                bottomProgress.startAnimation(hideAnim);
            }
            mShowing = true;
        }
        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }

    private void showAllViews() {
        playProgressButton.show();
        bottomContainer.setVisibility(VISIBLE);
        bottomContainer.startAnimation(showAnim);
        WindowUtil.showStatusBar(getContext());
        WindowUtil.showNavKey(getContext());
    }

    @Override
    public void show() {
        show(sDefaultTimeout);
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
                videoProgress.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * videoProgress.getMax());
                videoProgress.setProgress(pos);
                bottomProgress.setProgress(pos);
            } else {
                videoProgress.setEnabled(false);
            }
            int percent = mediaPlayer.getBufferPercentage();
            if (percent >= 95) { //修复第二进度不能100%问题
                videoProgress.setSecondaryProgress(videoProgress.getMax());
                bottomProgress.setSecondaryProgress(bottomProgress.getMax());
            } else {
                videoProgress.setSecondaryProgress(percent * 10);
                bottomProgress.setSecondaryProgress(percent * 10);
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
        if (isLive) {
            mNeedSeek = false;
        } else {
            super.slideToChangePosition(deltaX);
        }
    }

    public ImageView getThumb() {
        return thumb;
    }
}
