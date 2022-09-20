package xyz.doikki.videoplayer.exo;

import android.content.Context;

import xyz.doikki.videoplayer.AVPlayerFactory;

public class ExoMediaPlayerFactory implements AVPlayerFactory<ExoMediaPlayer> {

    public static ExoMediaPlayerFactory create() {
        return new ExoMediaPlayerFactory();
    }

    @Override
    public ExoMediaPlayer create(Context context) {
        return new ExoMediaPlayer(context);
    }
}
