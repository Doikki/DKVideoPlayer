package com.dueeeke.videoplayer.controller;

import android.view.View;

public interface IControlComponent<T extends MediaPlayerControl> {

    void show();

    void hide();

    void setPlayState(int playState);

    void setPlayerState(int playerState);

    void setMediaPlayer(T mediaPlayer);

    View getView();

}
