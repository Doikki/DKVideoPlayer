package com.dueeeke.videoplayer.controller;

import android.view.View;

public interface IControlComponent {

    void attach(MediaPlayerControlWrapper mediaPlayer);

    View getView();

    void show();

    void hide();

    void onPlayStateChanged(int playState);

    void onPlayerStateChanged(int playerState);

    void adjustPortrait(int space);

    void adjustLandscape(int space);

    void adjustReserveLandscape(int space);

}
