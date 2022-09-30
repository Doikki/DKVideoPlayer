package xyz.doikki.videoplayer.exo;

import android.content.Context;

import xyz.doikki.videoplayer.DKPlayerFactory;

public class ExoMediaPlayerFactory implements DKPlayerFactory<ExoMediaPlayer> {

    public static ExoMediaPlayerFactory create() {
        return new ExoMediaPlayerFactory();
    }

    @Override
    public ExoMediaPlayer create(Context context) {
        return new ExoMediaPlayer(context);
    }
}
