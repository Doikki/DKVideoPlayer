package com.dueeeke.videoplayer.controller;

import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;

public interface IControlComponent {

    void attach(@NonNull MediaPlayerControlWrapper mediaPlayerWrapper);

    View getView();

    void show(Animation showAnim);

    void hide(Animation hideAnim);

    void onPlayStateChanged(int playState);

    void onPlayerStateChanged(int playerState);

    void adjustView(int orientation, int space);

    void setProgress(int duration, int position);

    void onLockStateChanged(boolean isLocked);

}
