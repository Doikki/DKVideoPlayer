package com.dueeeke.videoplayer.controller;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;

/**
 * MediaPlayerControl包装类，扩张了MediaPlayerControl部分功能
 */
public class MediaPlayerControlWrapper implements MediaPlayerControl, VideoControllerCallback {
    
    private MediaPlayerControl mBase;
    private VideoControllerCallback mCallback;
    
    public MediaPlayerControlWrapper(MediaPlayerControl base, VideoControllerCallback callback) {
        mBase = base;
        mCallback = callback;
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
        if (mBase.isPlaying()) {
            mBase.pause();
        } else {
            mBase.start();
        }
    }

    /**
     * 横竖屏切换
     */
    public void toggleFullScreen(Activity activity) {
        if (mBase.isFullScreen()) {
            stopFullScreen(activity);
        } else {
            startFullScreen(activity);
        }
    }

    /**
     * 子类中请使用此方法来进入全屏
     */
    private void startFullScreen(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mBase.startFullScreen();
    }

    /**
     * 子类中请使用此方法来退出全屏
     */
    private void stopFullScreen(Activity activity) {
        mBase.stopFullScreen();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public void startFadeOut() {
        mCallback.startFadeOut();
    }

    @Override
    public void stopFadeOut() {
        mCallback.stopFadeOut();
    }
}
