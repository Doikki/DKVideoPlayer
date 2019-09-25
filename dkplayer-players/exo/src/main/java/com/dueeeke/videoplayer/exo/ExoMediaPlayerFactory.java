package com.dueeeke.videoplayer.exo;

import com.dueeeke.videoplayer.player.PlayerFactory;

public class ExoMediaPlayerFactory extends PlayerFactory<ExoMediaPlayer> {

    public static ExoMediaPlayerFactory create() {
        return new ExoMediaPlayerFactory();
    }

    @Override
    public ExoMediaPlayer createPlayer() {
        return new ExoMediaPlayer();
    }
}
