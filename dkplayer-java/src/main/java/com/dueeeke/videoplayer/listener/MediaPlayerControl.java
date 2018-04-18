package com.dueeeke.videoplayer.listener;

public interface MediaPlayerControl {
    void start();

    void pause();

    long getDuration();

    long getCurrentPosition();

    void seekTo(long pos);

    boolean isPlaying();

    int getBufferPercentage();

    void startFullScreen();

    void stopFullScreen();

    boolean isFullScreen();

    String getTitle();

    void setMute();

    boolean isMute();

    void setLock(boolean isLocked);

    void setScreenScale(int screenScale);
}