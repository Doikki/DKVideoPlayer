package com.dueeeke.videocontroller;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.PrepareView;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videoplayer.controller.GestureVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.PlayerUtils;

import static com.dueeeke.videoplayer.util.PlayerUtils.stringForTime;

/**
 * 直播/点播控制器
 * Created by Devlin_n on 2017/4/7.
 */

public class StandardVideoController extends GestureVideoController
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, GestureVideoController.GestureListener {

    protected TextView mTotalTime, mCurrTime;
    protected ImageView mFullScreenButton;
    protected LinearLayout mBottomContainer;
    protected SeekBar mVideoProgress;
    protected ImageView mLockButton;
    private boolean mIsLive;
    private boolean mIsDragging;

    private ProgressBar mBottomProgress;
    private ImageView mPlayButton;
    private ProgressBar mLoadingProgress;
    private Animation mShowAnim;
    private Animation mHideAnim;
    protected ImageView mRefreshButton;

    protected CenterView mCenterView;


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
        return R.layout.dkplayer_layout_standard_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        mFullScreenButton = mControllerView.findViewById(R.id.fullscreen);
        mFullScreenButton.setOnClickListener(this);
        mBottomContainer = mControllerView.findViewById(R.id.bottom_container);
        mVideoProgress = mControllerView.findViewById(R.id.seekBar);
        mVideoProgress.setOnSeekBarChangeListener(this);
        mTotalTime = mControllerView.findViewById(R.id.total_time);
        mCurrTime = mControllerView.findViewById(R.id.curr_time);
        mLockButton = mControllerView.findViewById(R.id.lock);
        mLockButton.setOnClickListener(this);
        mPlayButton = mControllerView.findViewById(R.id.iv_play);
        mPlayButton.setOnClickListener(this);
        mLoadingProgress = mControllerView.findViewById(R.id.loading);
        mBottomProgress = mControllerView.findViewById(R.id.bottom_progress);
        mRefreshButton = mControllerView.findViewById(R.id.iv_refresh);
        mRefreshButton.setOnClickListener(this);

        setGestureListener(this);

        mCenterView = new CenterView(getContext());
        mCenterView.setVisibility(GONE);
        addView(mCenterView);

        mHideAnim = new AlphaAnimation(1f, 0f);
        mHideAnim.setDuration(300);
        mShowAnim = new AlphaAnimation(0f, 1f);
        mShowAnim.setDuration(300);
    }

    public void addDefaultControlComponent() {
        addControlComponent(new CompleteView(getContext()));
        addControlComponent(new ErrorView(getContext()));
        PrepareView prepareView = new PrepareView(getContext());
        prepareView.setClickStart();
        addControlComponent(prepareView);
        addControlComponent(new TitleView(getContext()));
    }

    @Override
    public void adjustPortrait(int space) {
        mBottomContainer.setPadding(0, 0, 0, 0);
        mBottomProgress.setPadding(0, 0, 0, 0);
        FrameLayout.LayoutParams lblp = (LayoutParams) mLockButton.getLayoutParams();
        int dp24 = PlayerUtils.dp2px(getContext(), 24);
        lblp.setMargins(dp24, 0, dp24, 0);
    }

    @Override
    public void adjustLandscape(int space) {
        mBottomContainer.setPadding(space, 0, 0, 0);
        mBottomProgress.setPadding(space, 0, 0, 0);
        FrameLayout.LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
        int dp24 = PlayerUtils.dp2px(getContext(), 24);
        layoutParams.setMargins(dp24 + space, 0, dp24 + space, 0);
    }

    @Override
    public void adjustReserveLandscape(int space) {
        mBottomContainer.setPadding(0, 0, space, 0);
        mBottomProgress.setPadding(0, 0, space, 0);
        FrameLayout.LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
        int dp24 = PlayerUtils.dp2px(getContext(), 24);
        layoutParams.setMargins(dp24, 0, dp24, 0);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fullscreen) {
            doStartStopFullScreen();
        } else if (i == R.id.lock) {
            doLockUnlock();
        }
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
//        mTitle.setText(title);
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                L.e("PLAYER_NORMAL");
                if (mIsLocked) return;
                setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mIsGestureEnabled = false;
                mFullScreenButton.setSelected(false);
                mLockButton.setVisibility(GONE);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                L.e("PLAYER_FULL_SCREEN");
                if (mIsLocked) return;
                mIsGestureEnabled = true;
                mFullScreenButton.setSelected(true);
                if (mShowing) {
                    mLockButton.setVisibility(VISIBLE);
                } else {
                    mLockButton.setVisibility(GONE);
                }
                break;
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            //调用release方法会回到此状态
            case VideoView.STATE_IDLE:
                L.e("STATE_IDLE");
                hide();
                mIsLocked = false;
                mLockButton.setSelected(false);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mVideoProgress.setProgress(0);
                mVideoProgress.setSecondaryProgress(0);
                mBottomProgress.setVisibility(GONE);
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                L.e("STATE_PLAYING");
                //开始刷新进度
                post(mShowProgress);
                mPlayButton.setSelected(true);
                break;
            case VideoView.STATE_PAUSED:
                L.e("STATE_PAUSED");
                mPlayButton.setSelected(false);
                break;
            case VideoView.STATE_PREPARING:
                L.e("STATE_PREPARING");
                break;
            case VideoView.STATE_PREPARED:
                L.e("STATE_PREPARED");
                if (!mIsLive) mBottomProgress.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_ERROR:
                L.e("STATE_ERROR");
                removeCallbacks(mFadeOut);
                hide();
                removeCallbacks(mShowProgress);
                mLoadingProgress.setVisibility(GONE);
                mBottomProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_BUFFERING:
                L.e("STATE_BUFFERING");
                mLoadingProgress.setVisibility(VISIBLE);
                mPlayButton.setSelected(mMediaPlayer.isPlaying());
                break;
            case VideoView.STATE_BUFFERED:
                L.e("STATE_BUFFERED");
                mLoadingProgress.setVisibility(GONE);
                mPlayButton.setSelected(mMediaPlayer.isPlaying());
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                L.e("STATE_PLAYBACK_COMPLETED");
                hide();
                removeCallbacks(mShowProgress);
                mBottomProgress.setVisibility(GONE);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mLoadingProgress.setVisibility(GONE);
                mIsLocked = false;
                break;
        }
    }

    protected void doLockUnlock() {
        if (mIsLocked) {
            mIsLocked = false;
            mShowing = false;
            mIsGestureEnabled = true;
            show();
            mLockButton.setSelected(false);
            Toast.makeText(getContext(), R.string.dkplayer_unlocked, Toast.LENGTH_SHORT).show();
        } else {
            hide();
            mIsLocked = true;
            mIsGestureEnabled = false;
            mLockButton.setSelected(true);
            Toast.makeText(getContext(), R.string.dkplayer_locked, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置是否为直播视频
     */
    public void setLive() {
        mIsLive = true;
        mBottomProgress.setVisibility(GONE);
        mVideoProgress.setVisibility(INVISIBLE);
        mTotalTime.setVisibility(INVISIBLE);
        mCurrTime.setVisibility(INVISIBLE);
        mRefreshButton.setVisibility(VISIBLE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mMediaPlayer.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / mVideoProgress.getMax();
        mMediaPlayer.seekTo((int) newPosition);
        mIsDragging = false;
        post(mShowProgress);
        show();
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
    public void hide() {
        super.hide();
        if (mShowing) {
            if (mMediaPlayer.isFullScreen()) {
                mLockButton.setVisibility(GONE);
                mLockButton.setAnimation(mHideAnim);
                if (!mIsLocked) {
                    hideAllViews();
                }
            } else {
                mBottomContainer.setVisibility(GONE);
                mBottomContainer.startAnimation(mHideAnim);
            }
            if (!mIsLive && !mIsLocked) {
                mBottomProgress.setVisibility(VISIBLE);
                mBottomProgress.startAnimation(mShowAnim);
            }
            mShowing = false;
        }
    }

    private void hideAllViews() {
        mBottomContainer.setVisibility(GONE);
        mBottomContainer.startAnimation(mHideAnim);
    }

    private void show(int timeout) {
        if (!mShowing) {
            if (mMediaPlayer.isFullScreen()) {
                if (mLockButton.getVisibility() != VISIBLE) {
                    mLockButton.setVisibility(VISIBLE);
                    mLockButton.setAnimation(mShowAnim);
                }
                if (!mIsLocked) {
                    showAllViews();
                }
            } else {
                mBottomContainer.setVisibility(VISIBLE);
                mBottomContainer.startAnimation(mShowAnim);
            }
            if (!mIsLocked && !mIsLive) {
                mBottomProgress.setVisibility(GONE);
                mBottomProgress.startAnimation(mHideAnim);
            }
            mShowing = true;
        }
        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }

    private void showAllViews() {
        mBottomContainer.setVisibility(VISIBLE);
        mBottomContainer.startAnimation(mShowAnim);
    }

    @Override
    public void show() {
        super.show();
        show(mDefaultTimeout);
    }

    @Override
    protected void setProgress(int position) {
        if (mMediaPlayer == null || mIsDragging) {
            return;
        }

        if (mIsLive) return;

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


    @Override
    protected void slideToChangePosition(float deltaX) {
        if (mIsLive) {
            mNeedSeek = false;
        } else {
            super.slideToChangePosition(deltaX);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mIsLocked) {
            show();
            Toast.makeText(getContext(), R.string.dkplayer_lock_tip, Toast.LENGTH_SHORT).show();
            return true;
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return super.onBackPressed();

        if (mMediaPlayer.isFullScreen()) {
            stopFullScreen();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onPositionChange(int slidePosition, int currentPosition, int duration) {
        mCenterView.setProVisibility(View.GONE);
        if (slidePosition > currentPosition) {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_fast_forward);
        } else {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_fast_rewind);
        }
        mCenterView.setTextView(stringForTime(slidePosition) + "/" + stringForTime(duration));
    }

    @Override
    public void onBrightnessChange(int percent) {
        mCenterView.setProVisibility(View.VISIBLE);
        mCenterView.setIcon(R.drawable.dkplayer_ic_action_brightness);
        mCenterView.setTextView(percent + "%");
        mCenterView.setProPercent(percent);
    }

    @Override
    public void onVolumeChange(int percent) {
        mCenterView.setProVisibility(View.VISIBLE);
        if (percent <= 0) {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_volume_off);
        } else {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_volume_up);
        }
        mCenterView.setTextView(percent + "%");
        mCenterView.setProPercent(percent);
    }

    @Override
    public void onStartSlide() {
        if (mIsLive && mChangePosition) {
            mChangePosition = false;
            return;
        }
        hide();
        mCenterView.setVisibility(VISIBLE);
    }

    @Override
    public void onStopSlide() {
        if (mCenterView.getVisibility() == VISIBLE) {
            mCenterView.setVisibility(GONE);
        }
    }
}
