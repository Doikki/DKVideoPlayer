package com.dueeeke.videoplayer.controller;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

/**
 * 此类的目的是为了在ControlComponent中既能调用VideoView的api又能调用BaseVideoController的api，
 * 并对部分api做了封装，方便使用
 */
public class MediaPlayerControlWrapper implements MediaPlayerControl, IVideoController {
    
    private MediaPlayerControl mBase;
    private IVideoController mController;
    
    public MediaPlayerControlWrapper(@NonNull MediaPlayerControl base, @NonNull IVideoController controller) {
        mBase = base;
        mController = controller;
    }
    
    @Override
    public void start() {
        mBase.start();
    }

    @Override
    public void pause() {
        mBase.pause();
    }

    @Override
    public long getDuration() {
        return mBase.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return mBase.getCurrentPosition();
    }

    @Override
    public void seekTo(long pos) {
        mBase.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mBase.isPlaying();
    }

    @Override
    public int getBufferedPercentage() {
        return mBase.getBufferedPercentage();
    }

    @Override
    public void startFullScreen() {
        mBase.startFullScreen();
    }

    @Override
    public void stopFullScreen() {
        mBase.stopFullScreen();
    }

    @Override
    public boolean isFullScreen() {
        return mBase.isFullScreen();
    }

    @Override
    public void setMute(boolean isMute) {
        mBase.setMute(isMute);
    }

    @Override
    public boolean isMute() {
        return mBase.isMute();
    }

    @Override
    public void setScreenScaleType(int screenScaleType) {
        mBase.setScreenScaleType(screenScaleType);
    }

    @Override
    public void setSpeed(float speed) {
        mBase.setSpeed(speed);
    }

    @Override
    public long getTcpSpeed() {
        return mBase.getTcpSpeed();
    }

    @Override
    public void replay(boolean resetPosition) {
        mBase.replay(resetPosition);
    }

    @Override
    public void setMirrorRotation(boolean enable) {
        mBase.setMirrorRotation(enable);
    }

    @Override
    public Bitmap doScreenShot() {
        return mBase.doScreenShot();
    }

    @Override
    public int[] getVideoSize() {
        return mBase.getVideoSize();
    }

    @Override
    public void setRotation(float rotation) {
        mBase.setRotation(rotation);
    }

    @Override
    public void startTinyScreen() {
        mBase.startTinyScreen();
    }

    @Override
    public void stopTinyScreen() {
        mBase.stopTinyScreen();
    }

    @Override
    public boolean isTinyScreen() {
        return mBase.isTinyScreen();
    }

    /**
     * 播放和暂停
     */
    public void togglePlay() {
        if (isPlaying()) {
            pause();
        } else {
            start();
        }
    }

    /**
     * 横竖屏切换，会旋转屏幕
     */
    public void toggleFullScreen(Activity activity) {
        if (activity == null || activity.isFinishing())
            return;
        if (isFullScreen()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            stopFullScreen();
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            startFullScreen();
        }
    }

    /**
     * 横竖屏切换，不会旋转屏幕
     */
    public void toggleFullScreen() {
        if (isFullScreen()) {
            stopFullScreen();
        } else {
            startFullScreen();
        }
    }

    /**
     * 横竖屏切换，根据适配宽高决定是否旋转屏幕
     */
    public void toggleFullScreenByVideoSize(Activity activity) {
        if (activity == null || activity.isFinishing())
            return;
        int[] size = getVideoSize();
        int width = size[0];
        int height = size[1];
        if (isFullScreen()) {
            stopFullScreen();
            if (width > height) {
               activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            startFullScreen();
            if (width > height) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    @Override
    public void startFadeOut() {
        mController.startFadeOut();
    }

    @Override
    public void stopFadeOut() {
        mController.stopFadeOut();
    }

    @Override
    public boolean isShowing() {
        return mController.isShowing();
    }

    @Override
    public void setLocked(boolean locked) {
        mController.setLocked(locked);
    }

    @Override
    public boolean isLocked() {
        return mController.isLocked();
    }

    @Override
    public void startProgress() {
        mController.startProgress();
    }

    @Override
    public void stopProgress() {
        mController.stopProgress();
    }

    @Override
    public void hideInner() {
        mController.hideInner();
    }

    @Override
    public void showInner() {
        mController.showInner();
    }

    /**
     * 切换锁定状态
     */
    public void toggleLockState() {
        setLocked(!isLocked());
    }


    /**
     * 切换显示/隐藏状态
     */
    public void toggleShowState() {
        if (isShowing()) {
            hideInner();
        } else {
            showInner();
        }
    }
}
