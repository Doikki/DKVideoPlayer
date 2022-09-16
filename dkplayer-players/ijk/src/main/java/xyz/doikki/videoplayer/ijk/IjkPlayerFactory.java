package xyz.doikki.videoplayer.ijk;

import android.content.Context;

import xyz.doikki.videoplayer.MediaPlayerFactory;

public class IjkPlayerFactory extends MediaPlayerFactory<IjkMPlayer> {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkMPlayer create(Context context) {
        return new IjkMPlayer(context);
    }
}
