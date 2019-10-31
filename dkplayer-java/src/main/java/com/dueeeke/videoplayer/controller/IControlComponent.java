package com.dueeeke.videoplayer.controller;

import android.view.View;

public interface IControlComponent {

    void show();

    void hide();

    void setPlayState(int playState);

    void setPlayerState(int playerState);

    void attach(MediaPlayerControl mediaPlayer);

    View getView();

}
