package xyz.doikki.videoplayer.controller;

import xyz.doikki.videoplayer.AVPlayerFunction;

public interface MediaPlayerControl extends AVPlayerFunction,  VideoController {


    void startFullScreen();

    void stopFullScreen();

    boolean isFullScreen();


    /**
     * 获取缓冲网速：只有IJK播放器支持
     * @return
     */
    long getTcpSpeed();

    void replay(boolean resetPosition);

    void setMirrorRotation(boolean enable);


    int[] getVideoSize();

    void setRotation(float rotation);

    void startTinyScreen();

    void stopTinyScreen();

    boolean isTinyScreen();
}