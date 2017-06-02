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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devlin_n.magic_player.R;
import com.devlin_n.magic_player.player.MagicVideoView;
import com.devlin_n.magic_player.util.WindowUtil;

/**
 * 直播/点播控制器
 * Created by Devlin_n on 2017/4/7.
 */

public class MagicVideoController extends BaseVideoController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = MagicVideoController.class.getSimpleName();
    protected TextView totalTime, currTime;
    protected ImageView fullScreenButton;
    protected LinearLayout bottomContainer, topContainer;
    protected SeekBar videoProgress;
    protected ImageView floatScreen;
    protected ImageView backButton;
    protected ImageView lock;
    protected TextView title;
    protected ImageView playButton;
    private boolean isLive;
    private boolean isDragging;
    private View statusHolder;

    private ProgressBar bufferProgress;
    private ImageView thumb;
    private ImageView rePlayButton;
    private LinearLayout completeContainer;


    public MagicVideoController(@NonNull Context context) {
        this(context, null);
    }

    public MagicVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
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
        thumb = (ImageView) controllerView.findViewById(R.id.thumb);
        thumb.setOnClickListener(this);
        bufferProgress = (ProgressBar) controllerView.findViewById(R.id.buffering);
        rePlayButton = (ImageView) controllerView.findViewById(R.id.iv_replay);
        rePlayButton.setOnClickListener(this);
        completeContainer = (LinearLayout) controllerView.findViewById(R.id.complete_container);
        completeContainer.setOnClickListener(this);
        title = (TextView) controllerView.findViewById(R.id.title);
        statusHolder = controllerView.findViewById(R.id.status_holder);
        statusHolder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) WindowUtil.getStatusBarHeight(getContext())));
        statusHolder.setVisibility(GONE);
        topContainer.setVisibility(GONE);
        bottomContainer.setVisibility(GONE);
        lock.setVisibility(GONE);
        bufferProgress.setVisibility(GONE);
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
        } else if (i == R.id.iv_play || i == R.id.thumb || i == R.id.iv_replay) {
            doPauseResume();
        }
    }

    @Override
    public void setPlayerState(int playerState) {
        switch (playerState) {
            case MagicVideoView.PLAYER_NORMAL:
                if (isLocked) return;
                fullScreenButton.setSelected(false);
                backButton.setVisibility(INVISIBLE);
                lock.setVisibility(INVISIBLE);
                topContainer.setVisibility(INVISIBLE);
                statusHolder.setVisibility(GONE);
                break;
            case MagicVideoView.PLAYER_FULL_SCREEN:
                if (isLocked) return;
                fullScreenButton.setSelected(true);
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
                break;
        }
    }

    @Override
    public void setPlayState(int playState) {

        switch (playState) {
            case MagicVideoView.STATE_IDLE:
                thumb.setVisibility(VISIBLE);
                break;
            case MagicVideoView.STATE_PLAYING:
                playButton.setSelected(true);
                thumb.setVisibility(GONE);
                completeContainer.setVisibility(GONE);
                hide();
                break;
            case MagicVideoView.STATE_PAUSED:
                playButton.setSelected(false);
                show(0);
                break;
            case MagicVideoView.STATE_PREPARING:
                bufferProgress.setVisibility(VISIBLE);
                playButton.setVisibility(GONE);
                thumb.setVisibility(GONE);
                completeContainer.setVisibility(GONE);
                break;
            case MagicVideoView.STATE_PREPARED:
                bufferProgress.setVisibility(GONE);
                if (mShowing) {
                    playButton.setVisibility(VISIBLE);
                }
                break;
            case MagicVideoView.STATE_ERROR:
                break;
            case MagicVideoView.STATE_BUFFERING:
                bufferProgress.setVisibility(VISIBLE);
                playButton.setVisibility(GONE);
                break;
            case MagicVideoView.STATE_BUFFERED:
                bufferProgress.setVisibility(GONE);
                if (mShowing && !isLocked) {
                    playButton.setVisibility(VISIBLE);
                }
                break;
            case MagicVideoView.STATE_PLAYBACK_COMPLETED:
                hide();
                thumb.setVisibility(VISIBLE);
                completeContainer.setVisibility(VISIBLE);
                isLocked = false;
                mediaPlayer.setLock(false);
                break;
        }
    }

    private void doLockUnlock() {
        if (isLocked) {
            isLocked = false;
            mShowing = false;
            show();
            lock.setSelected(false);
            Toast.makeText(getContext(), R.string.unlocked, Toast.LENGTH_SHORT).show();
        } else {
            hide();
            isLocked = true;
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
                if (bufferProgress.getVisibility() != VISIBLE) {
                    playButton.setVisibility(GONE);
                    playButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_out));
                }
            }
            mShowing = false;
        }
    }

    private void hideAllViews() {
        if (bufferProgress.getVisibility() != VISIBLE) {
            playButton.setVisibility(GONE);
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
                if (bufferProgress.getVisibility() != VISIBLE) {
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

        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }

    private void showAllViews() {
        if (bufferProgress.getVisibility() != VISIBLE) {
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
        hide();
        isLocked = false;
        lock.setSelected(false);
        mediaPlayer.setLock(false);
        playButton.setSelected(false);
        completeContainer.setVisibility(GONE);
        playButton.setVisibility(VISIBLE);
        thumb.setVisibility(VISIBLE);
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

    public ImageView getThumb(){
        return thumb;
    }
}
