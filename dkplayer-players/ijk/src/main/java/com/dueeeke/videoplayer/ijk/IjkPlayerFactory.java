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
        IjkPlayer ijkPlayer = new IjkPlayer(context);
        ijkPlayer.getMediaPlayer().setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"max-buffer-size",50000L);

        return ijkPlayer;
    }
}
