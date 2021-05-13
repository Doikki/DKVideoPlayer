package xyz.doikki.videoplayer.controller;

import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;

public interface IControlComponent {

    void attach(@NonNull ControlWrapper controlWrapper);

    View getView();

    void onVisibilityChanged(boolean isVisible, Animation anim);

    void onPlayStateChanged(int playState);

    void onPlayerStateChanged(int playerState);

    void setProgress(int duration, int position);

    void onLockStateChanged(boolean isLocked);

}
