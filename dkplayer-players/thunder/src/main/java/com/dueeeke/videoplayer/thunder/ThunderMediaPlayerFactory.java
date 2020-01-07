package com.dueeeke.videoplayer.thunder;

import android.content.Context;

import com.dueeeke.videoplayer.player.PlayerFactory;

public class ThunderMediaPlayerFactory extends PlayerFactory<ThunderMediaPlayer> {

    public static ThunderMediaPlayerFactory create() {
        return new ThunderMediaPlayerFactory();
    }

    @Override
    public ThunderMediaPlayer createPlayer(Context context) {
        return new ThunderMediaPlayer();
    }
}
