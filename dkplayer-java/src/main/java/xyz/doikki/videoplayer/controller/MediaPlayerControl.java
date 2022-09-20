package xyz.doikki.videoplayer.controller;

import xyz.doikki.videoplayer.AVPlayerFunction;

public interface MediaPlayerControl extends AVPlayerFunction, VideoController {

    void pause();

    void startFullScreen();

    void stopFullScreen();

    boolean isFullScreen();



    void setScreenScaleType(int screenScaleType);

    void setSpeed(float speed);

    float getSpeed();

    long getTcpSpeed();

    void replay(boolean resetPosition);

    void setMirrorRotation(boolean enable);


    int[] getVideoSize();

    void setRotation(float rotation);

    void startTinyScreen();

    void stopTinyScreen();

    boolean isTinyScreen();
}