package com.devlin_n.magic_player.controller;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;

import com.devlin_n.magic_player.util.WindowUtil;

/**
 * 控制器基类
 * Created by Devlin_n on 2017/4/12.
 */

public abstract class BaseMediaController extends FrameLayout {

    protected View controllerView;//控制器视图
    protected MediaPlayerControlInterface mediaPlayer;//播放器
    protected boolean mShowing;//控制器是否处于显示状态
    protected boolean mAutoRotate;//是否旋转屏幕

    /**
     * 加速度传感器监听
     */
    protected OrientationEventListener orientationEventListener;


    public BaseMediaController(@NonNull Context context) {
        this(context, null);
    }

    public BaseMediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    protected void initView() {
        controllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        orientationEventListener = new OrientationEventListener(getContext()) { // 加速度传感器监听，用于自动旋转屏幕

            private int CurrentOrientation = 0;
            private static final int PORTRAIT = 1;
            private static final int LANDSCAPE = 2;
            private static final int REVERSE_LANDSCAPE = 3;


            @Override
            public void onOrientationChanged(int orientation) {
                //根据系统设置进行自动旋转
//            boolean autoRotateOn = (android.provider.Settings.System.getInt(WindowUtil.getAppCompActivity(getContext()).getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
//            if (!autoRotateOn) return;

                if (orientation >= 340) { //屏幕顶部朝上
                    if (CurrentOrientation == PORTRAIT) return;
                    if ((CurrentOrientation == LANDSCAPE || CurrentOrientation == REVERSE_LANDSCAPE) && !mediaPlayer.isFullScreen()) {
                        CurrentOrientation = PORTRAIT;
                        return;
                    }
                    CurrentOrientation = PORTRAIT;
                    WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mediaPlayer.stopFullScreen();
                } else if (orientation >= 260 && orientation <= 280) { //屏幕左边朝上
                    if (CurrentOrientation == LANDSCAPE) return;
                    if (CurrentOrientation == PORTRAIT && mediaPlayer.isFullScreen()) {
                        CurrentOrientation = LANDSCAPE;
                        return;
                    }
                    CurrentOrientation = LANDSCAPE;
                    if (!mediaPlayer.isFullScreen()) {
                        mediaPlayer.startFullScreen();
                    }
                    WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (orientation >= 70 && orientation <= 90) { //屏幕右边朝上
                    if (CurrentOrientation == REVERSE_LANDSCAPE) return;
                    if (CurrentOrientation == PORTRAIT && mediaPlayer.isFullScreen()) {
                        CurrentOrientation = REVERSE_LANDSCAPE;
                        return;
                    }
                    CurrentOrientation = REVERSE_LANDSCAPE;
                    if (!mediaPlayer.isFullScreen()) {
                        mediaPlayer.startFullScreen();
                    }
                    WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                updateFullScreen();
            }
        };
    }

    /**
     * 设置控制器布局文件，子类必须实现
     */
    protected abstract int getLayoutId();

    /**
     * 显示
     */
    protected void show() {
    }

    /**
     * 隐藏
     */
    public void hide() {
    }

    /**
     * 重置
     */
    public void reset() {
    }

    /**
     * 销毁
     */
    public void destroy() {
        orientationEventListener.disable();
        orientationEventListener = null;
    }

    /**
     * 是否需要锁定返回键
     */
    public boolean lockBack() {
        return false;
    }

    /**
     * 返回控制器的显示状态
     */
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * 设置是否自动旋转
     */
    public void setAutoRotate(boolean autoRotate) {
        this.mAutoRotate = autoRotate;
        if (mAutoRotate) orientationEventListener.enable();
    }

    public void updateFullScreen() {
    }

    public void updatePlayButton() {
    }

    /**
     * 横竖屏切换
     */
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

    /**
     * 启动悬浮窗口
     */
    protected void startFloatScreen() {
        mediaPlayer.startFloatWindow();
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


    public interface MediaPlayerControlInterface {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(int pos);

        boolean isPlaying();

        int getBufferPercentage();

        void startFloatWindow();

        void startFullScreen();

        void stopFullScreen();

        boolean isFullScreen();

        String getTitle();
    }

    public void setMediaPlayer(MediaPlayerControlInterface mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
