package com.dueeeke.videocontroller.component;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videocontroller.R;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.controller.MediaPlayerControlWrapper;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

import static com.dueeeke.videoplayer.util.PlayerUtils.stringForTime;

/**
 * 点播底部控制栏
 */
public class VodControlView extends FrameLayout implements IControlComponent, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    
    private MediaPlayerControlWrapper mMediaPlayer;

    private TextView mTotalTime, mCurrTime;
    private ImageView mFullScreen;
    private LinearLayout mBottomContainer;
    private SeekBar mVideoProgress;
    private ProgressBar mBottomProgress;
    private ImageView mPlayButton;

    private boolean mIsDragging;
    private boolean mIsShowing;

    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;

    private boolean mIsShowBottomProgress = true;

    public VodControlView(@NonNull Context context) {
        super(context);
    }

    public VodControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VodControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    
    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.dkplayer_layout_vod_control_view, this, true);
        mFullScreen = findViewById(R.id.fullscreen);
        mFullScreen.setOnClickListener(this);
        mBottomContainer = findViewById(R.id.bottom_container);
        mVideoProgress = findViewById(R.id.seekBar);
        mVideoProgress.setOnSeekBarChangeListener(this);
        mTotalTime = findViewById(R.id.total_time);
        mCurrTime = findViewById(R.id.curr_time);
        mPlayButton = findViewById(R.id.iv_play);
        mPlayButton.setOnClickListener(this);
        mBottomProgress = findViewById(R.id.bottom_progress);

        mShowAnimator = ObjectAnimator.ofFloat(mBottomContainer, "alpha", 0, 1);
        mShowAnimator.setDuration(300);
        mHideAnimator = ObjectAnimator.ofFloat(mBottomContainer, "alpha", 1, 0);
        mHideAnimator.setDuration(300);
        mHideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mBottomContainer.setVisibility(GONE);
                if (mIsShowBottomProgress) {
                    mBottomProgress.setVisibility(VISIBLE);
                }
            }
        });

    }

    /**
     * 是否显示底部进度条
     */
    public void showBottomProgress(boolean isShow) {
        mIsShowBottomProgress = isShow;
    }

    @Override
    public void show() {
        mIsShowing = true;
        setVisibility(VISIBLE);
        mBottomContainer.setVisibility(VISIBLE);
        if (mIsShowBottomProgress) {
            mBottomProgress.setVisibility(GONE);
        }
        mHideAnimator.cancel();
        mShowAnimator.start();
    }

    @Override
    public void hide() {
        mIsShowing = false;
        setVisibility(VISIBLE);
        mShowAnimator.cancel();
        mHideAnimator.start();
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
                setVisibility(GONE);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mVideoProgress.setProgress(0);
                mVideoProgress.setSecondaryProgress(0);
                break;
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
                setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                post(mShowProgress);
                mPlayButton.setSelected(true);
                if (mIsShowBottomProgress) {
                    if (mIsShowing) {
                        mBottomProgress.setVisibility(GONE);
                        mBottomContainer.setVisibility(VISIBLE);
                    } else {
                        mBottomProgress.setVisibility(VISIBLE);
                        mBottomContainer.setVisibility(GONE);
                    }
                    setVisibility(VISIBLE);
                }
                break;
            case VideoView.STATE_PAUSED:
                mPlayButton.setSelected(false);
                break;
            case VideoView.STATE_BUFFERING:
            case VideoView.STATE_BUFFERED:
                mPlayButton.setSelected(mMediaPlayer.isPlaying());
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                mFullScreen.setSelected(false);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                mFullScreen.setSelected(true);
                break;
        }
    }

    @Override
    public void attach(MediaPlayerControlWrapper mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void adjustPortrait(int space) {
        mBottomContainer.setPadding(0, 0, 0, 0);
        mBottomProgress.setPadding(0, 0, 0, 0);
    }

    @Override
    public void adjustLandscape(int space) {
        mBottomContainer.setPadding(space, 0, 0, 0);
        mBottomProgress.setPadding(space, 0, 0, 0);
    }

    @Override
    public void adjustReserveLandscape(int space) {
        mBottomContainer.setPadding(0, 0, space, 0);
        mBottomProgress.setPadding(0, 0, space, 0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fullscreen) {
            toggleFullScreen();
        } else if (id == R.id.iv_play) {
            mMediaPlayer.togglePlay();
        }
    }

    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null || activity.isFinishing()) return;
        mMediaPlayer.toggleFullScreen(activity);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        removeCallbacks(mShowProgress);
        mMediaPlayer.stopFadeOut();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mMediaPlayer.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / mVideoProgress.getMax();
        mMediaPlayer.seekTo((int) newPosition);
        mIsDragging = false;
        post(mShowProgress);
        mMediaPlayer.startFadeOut();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        long duration = mMediaPlayer.getDuration();
        long newPosition = (duration * progress) / mVideoProgress.getMax();
        if (mCurrTime != null)
            mCurrTime.setText(stringForTime((int) newPosition));
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            post(mShowProgress);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(mShowProgress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mShowProgress);
    }

    /**
     * 刷新进度Runnable
     */
    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mMediaPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    /**
     * 重写此方法实现刷新进度功能
     */
    private int setProgress() {
        int position = (int) mMediaPlayer.getCurrentPosition();
        setProgress(position);
        return position;
    }

    private void setProgress(int position) {
        if (mMediaPlayer == null || mIsDragging) {
            return;
        }

        int duration = (int) mMediaPlayer.getDuration();
        if (mVideoProgress != null) {
            if (duration > 0) {
                mVideoProgress.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * mVideoProgress.getMax());
                mVideoProgress.setProgress(pos);
                mBottomProgress.setProgress(pos);
            } else {
                mVideoProgress.setEnabled(false);
            }
            int percent = mMediaPlayer.getBufferedPercentage();
            if (percent >= 95) { //解决缓冲进度不能100%问题
                mVideoProgress.setSecondaryProgress(mVideoProgress.getMax());
                mBottomProgress.setSecondaryProgress(mBottomProgress.getMax());
            } else {
                mVideoProgress.setSecondaryProgress(percent * 10);
                mBottomProgress.setSecondaryProgress(percent * 10);
            }
        }

        if (mTotalTime != null)
            mTotalTime.setText(stringForTime(duration));
        if (mCurrTime != null)
            mCurrTime.setText(stringForTime(position));
    }
}
