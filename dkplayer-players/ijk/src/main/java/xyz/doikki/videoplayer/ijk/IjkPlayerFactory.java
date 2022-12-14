package xyz.doikki.videoplayer.ijk;

import android.content.Context;

import xyz.doikki.videoplayer.player.PlayerFactory;

public class IjkPlayerFactory implements PlayerFactory {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkDKPlayer create(Context context) {
        return new IjkDKPlayer(context);
    }
}
