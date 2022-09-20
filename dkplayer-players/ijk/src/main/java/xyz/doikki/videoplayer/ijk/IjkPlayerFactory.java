package xyz.doikki.videoplayer.ijk;

import android.content.Context;

import xyz.doikki.videoplayer.AVPlayerFactory;

public class IjkPlayerFactory implements AVPlayerFactory<IjkAVPlayer> {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkAVPlayer create(Context context) {
        return new IjkAVPlayer(context);
    }
}
