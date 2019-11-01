package com.dueeeke.videoplayer.controller;

import android.view.View;

public interface IControlComponent {

    void show();

    void hide();

    void onPlayStateChange(int playState);

    void onPlayerStateChange(int playerState);

    void attach(MediaPlayerControl mediaPlayer);

    View getView();

    void setProgress(int position);

    void adjustPortrait(int space);

    void adjustLandscape(int space);

    void adjustReserveLandscape(int space);

}
