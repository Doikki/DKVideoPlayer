package xyz.doikki.videoplayer.exo;

import android.content.Context;

import xyz.doikki.videoplayer.MediaPlayerFactory;

public class ExoMediaPlayerFactory extends MediaPlayerFactory<ExoMediaPlayer> {

    public static ExoMediaPlayerFactory create() {
        return new ExoMediaPlayerFactory();
    }

    @Override
    public ExoMediaPlayer create(Context context) {
        return new ExoMediaPlayer(context);
    }
}
