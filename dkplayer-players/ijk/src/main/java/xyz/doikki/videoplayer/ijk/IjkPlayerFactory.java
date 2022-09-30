package xyz.doikki.videoplayer.ijk;

import android.content.Context;

import xyz.doikki.videoplayer.DKPlayerFactory;

public class IjkPlayerFactory implements DKPlayerFactory<IjkDKPlayer> {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkDKPlayer create(Context context) {
        return new IjkDKPlayer(context);
    }
}
