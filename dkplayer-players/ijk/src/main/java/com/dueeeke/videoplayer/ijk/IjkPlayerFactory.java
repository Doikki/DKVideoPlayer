package com.dueeeke.videoplayer.ijk;

import android.content.Context;

import com.dueeeke.videoplayer.player.PlayerFactory;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkPlayerFactory extends PlayerFactory<IjkPlayer> {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkPlayer createPlayer(Context context) {
        return new IjkPlayer(context);
    }
}
