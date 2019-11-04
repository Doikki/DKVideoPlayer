package com.dueeeke.videoplayer.controller;

public interface VideoControllerCallback {

    void startFadeOut();

    void stopFadeOut();

    boolean isShowing();

    void setLocked(boolean locked);

    boolean isLocked();

    void startProgress();

    void stopProgress();

    void hideInner();

    void showInner();
}
