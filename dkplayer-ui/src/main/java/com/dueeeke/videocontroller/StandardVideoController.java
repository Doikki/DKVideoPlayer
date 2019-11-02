package com.dueeeke.videocontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.LiveControlView;
import com.dueeeke.videocontroller.component.PrepareView;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videocontroller.component.VodControlView;
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
        implements View.OnClickListener, GestureVideoController.GestureListener {

    protected ImageView mLockButton;

    private ProgressBar mLoadingProgress;
    private Animation mShowAnim;
    private Animation mHideAnim;

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
        mLockButton = findViewById(R.id.lock);
        mLockButton.setOnClickListener(this);
        mLoadingProgress = findViewById(R.id.loading);

        setGestureListener(this);

        mCenterView = new CenterView(getContext());
        mCenterView.setVisibility(GONE);
        addView(mCenterView);

        mHideAnim = new AlphaAnimation(1f, 0f);
        mHideAnim.setDuration(300);
        mShowAnim = new AlphaAnimation(0f, 1f);
        mShowAnim.setDuration(300);
    }

    /**
     * 快速添加各个组件
     * @param title 标题
     * @param isLive 是否为直播
     */
    public void addDefaultControlComponent(String title, boolean isLive) {
        CompleteView completeView = new CompleteView(getContext());
        ErrorView errorView = new ErrorView(getContext());
        PrepareView prepareView = new PrepareView(getContext());
        prepareView.setClickStart();
        TitleView titleView = new TitleView(getContext());
        titleView.setTitle(title);
        addControlComponent(completeView, errorView, prepareView, titleView);
        if (isLive) {
            addControlComponent(new LiveControlView(getContext()));
        } else {
            addControlComponent(new VodControlView(getContext()));
        }
        setLiveMode(isLive);
    }

    @Override
    public void adjustPortrait(int space) {
        super.adjustPortrait(space);
        FrameLayout.LayoutParams lblp = (LayoutParams) mLockButton.getLayoutParams();
        int dp24 = PlayerUtils.dp2px(getContext(), 24);
        lblp.setMargins(dp24, 0, dp24, 0);
    }

    @Override
    public void adjustLandscape(int space) {
        super.adjustLandscape(space);
        FrameLayout.LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
        int dp24 = PlayerUtils.dp2px(getContext(), 24);
        layoutParams.setMargins(dp24 + space, 0, dp24 + space, 0);
    }

    @Override
    public void adjustReserveLandscape(int space) {
        super.adjustReserveLandscape(space);
        FrameLayout.LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
        int dp24 = PlayerUtils.dp2px(getContext(), 24);
        layoutParams.setMargins(dp24, 0, dp24, 0);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lock) {
            toggleLockState();
        }
    }

    /**
     * 切换锁定状态
     */
    public void toggleLockState() {
        if (isLocked()) {
            //这两行顺序不能换
            setLocked(false);
            showInner(true);

            setGestureEnabled(true);
            mLockButton.setSelected(false);
            Toast.makeText(getContext(), R.string.dkplayer_unlocked, Toast.LENGTH_SHORT).show();
        } else {
            //这两行顺序不能换
            hideInner();
            setLocked(true);

            setGestureEnabled(false);
            mLockButton.setSelected(true);
            Toast.makeText(getContext(), R.string.dkplayer_locked, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void show() {
        super.show();
        if (mMediaPlayer.isFullScreen()) {
            if (mLockButton.getVisibility() != VISIBLE) {
                mLockButton.setVisibility(VISIBLE);
                mLockButton.setAnimation(mShowAnim);
            }
        }
    }

    @Override
    protected void hide() {
        super.hide();
        if (mMediaPlayer.isFullScreen()) {
            mLockButton.setVisibility(GONE);
            mLockButton.setAnimation(mHideAnim);
        }
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                L.e("PLAYER_NORMAL");
                setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mLockButton.setVisibility(GONE);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                L.e("PLAYER_FULL_SCREEN");
                if (isShowing()) {
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
                mLockButton.setSelected(false);
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                L.e("STATE_PLAYING");
                break;
            case VideoView.STATE_PAUSED:
                L.e("STATE_PAUSED");
                break;
            case VideoView.STATE_PREPARING:
                L.e("STATE_PREPARING");
                mLoadingProgress.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PREPARED:
                L.e("STATE_PREPARED");
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_ERROR:
                L.e("STATE_ERROR");
                hide();
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_BUFFERING:
                L.e("STATE_BUFFERING");
                mLoadingProgress.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_BUFFERED:
                L.e("STATE_BUFFERED");
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                L.e("STATE_PLAYBACK_COMPLETED");
                hide();
                mLoadingProgress.setVisibility(GONE);
                mLockButton.setSelected(false);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isLocked()) {
            showInner(false);
            Toast.makeText(getContext(), R.string.dkplayer_lock_tip, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (mMediaPlayer.isFullScreen()) {
            return stopFullScreen();
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
