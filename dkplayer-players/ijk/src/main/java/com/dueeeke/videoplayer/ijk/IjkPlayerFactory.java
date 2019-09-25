package com.dueeeke.videoplayer.ijk;

import com.dueeeke.videoplayer.player.PlayerFactory;

public class IjkPlayerFactory extends PlayerFactory<IjkPlayer> {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkPlayer createPlayer() {
        return new IjkPlayer();
    }
}
