package com.devlin_n.magic_player;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 控制器基类
 * Created by Devlin_n on 2017/4/12.
 */

public abstract class BaseMediaController extends FrameLayout {

    protected View controllerView;//控制器视图
    protected MediaPlayerControlInterface mediaPlayer;//播放器

    protected OrientationEventListener orientationEventListener = new OrientationEventListener(getContext()) { // 加速度传感器监听，用于自动旋转屏幕

        private int tag = 0;

        @Override
        public void onOrientationChanged(int orientation) {
            boolean autoRotateOn = (android.provider.Settings.System.getInt(WindowUtil.getAppCompActivity(getContext()).getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
            if (!autoRotateOn) return;

            if (orientation >= 340) {
                if (tag == 1) return;
                if ((tag == 2 || tag == 3) && !mediaPlayer.isFullScreen()) {
                    tag = 1;
                    return;
                }
                tag = 1;
                WindowUtil.getAppCompActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mediaPlayer.stopFullScreen();
            } else if (orientation >= 260 && orientation <= 280) {
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


    public BaseMediaController(@NonNull Context context) {
        super(context);
        initView();
    }

    public BaseMediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    protected void initView() {
        controllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
    }

    protected abstract int getLayoutId();

    protected void show(){}

    protected void hide(){}

    protected void reset(){}

    protected boolean lockBack(){
        return false;
    }

    protected void updateFullScreen(){}

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


    protected void startFloatScreen() {
        mediaPlayer.startFloatScreen();
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

        String getTitle();
    }

    public void setMediaPlayer(MediaPlayerControlInterface mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
