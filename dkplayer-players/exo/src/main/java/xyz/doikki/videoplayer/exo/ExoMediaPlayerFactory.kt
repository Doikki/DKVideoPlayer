package xyz.doikki.videoplayer.exo;

import android.content.Context;

import xyz.doikki.videoplayer.player.PlayerFactory;

public class ExoMediaPlayerFactory implements PlayerFactory {

    public static ExoMediaPlayerFactory create() {
        return new ExoMediaPlayerFactory();
    }

    @Override
    public ExoMediaPlayer create(Context context) {
        return new ExoMediaPlayer(context);
    }
}
