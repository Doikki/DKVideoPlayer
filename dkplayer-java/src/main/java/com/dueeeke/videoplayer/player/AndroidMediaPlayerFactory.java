package com.dueeeke.videoplayer.player;

import android.content.Context;

public class AndroidMediaPlayerFactory extends PlayerFactory<AndroidMediaPlayer> {

    private Context mContext;

    public AndroidMediaPlayerFactory(Context context) {
        mContext = context.getApplicationContext();
    }

    public static AndroidMediaPlayerFactory create(Context context) {
        return new AndroidMediaPlayerFactory(context);
    }

    @Override
    public AndroidMediaPlayer createPlayer() {
        return new AndroidMediaPlayer(mContext);
    }
}
