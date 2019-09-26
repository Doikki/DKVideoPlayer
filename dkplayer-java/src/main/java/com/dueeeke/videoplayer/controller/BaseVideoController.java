package com.dueeeke.videoplayer.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * 控制器基类
 * Created by Devlin_n on 2017/4/12.
 */

public abstract class BaseVideoController<T extends MediaPlayerControl> extends FrameLayout {

    protected View mControllerView;//控制器视图
    protected T mMediaPlayer;//播放器
    protected boolean mShowing;//控制器是否处于显示状态
    protected boolean mIsLocked;
    protected int mDefaultTimeout = 4000;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    protected int mCurrentPlayState;
    protected int mCurrentPlayerState;

    public BaseVideoController(@NonNull Context context) {
        this(context, null);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        mControllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        setClickable(true);
        setFocusable(true);
    }

    /**
     * 设置控制器布局文件，子类必须实现
     */
    protected abstract int getLayoutId();

    /**
     * 重要：此方法用于将{@link VideoView} 和控制器绑定
     */
    public void setMediaPlayer(T mediaPlayer) {
        this.mMediaPlayer = mediaPlayer;
    }

    /**
     * 显示
     */
    public void show() {
    }

    /**
     * 隐藏
     */
    public void hide() {
    }

    /**
     * {@link VideoView}调用此方法向控制器设置播放状态，
     * 开发者可重写此方法并在其中更新控制器在不同播放状态下的ui
     */
    @CallSuper
    public void setPlayState(int playState) {
        mCurrentPlayState = playState;
    }

    /**
     * {@link VideoView}调用此方法向控制器设置播放器状态，
     * 开发者可重写此方法并在其中更新控制器在不同播放器状态下的ui
     */
    @CallSuper
    public void setPlayerState(int playerState) {
        mCurrentPlayerState = playerState;
    }

    /**
     * 显示移动网络播放提示
     */
    public void showNetWarning() {

    }

    /**
     * 隐藏移动网络播放提示
     */
    public void hideNetWarning() {

    }

    /**
     * 播放和暂停
     */
    protected void doPauseResume() {
        if (mCurrentPlayState == VideoView.STATE_BUFFERING) return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }

    /**
     * 横竖屏切换
     */
    protected void doStartStopFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return;
        if (mMediaPlayer.isFullScreen()) {
            mMediaPlayer.stopFullScreen();
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mMediaPlayer.startFullScreen();
        }
    }

    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mMediaPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    protected final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    protected int setProgress() {
        return 0;
    }

    /**
     * 获取当前系统时间
     */
    protected String getCurrentSystemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    protected String stringForTime(int timeMs) {
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

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            post(mShowProgress);
        }
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        return false;
    }
}
