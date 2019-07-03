package com.dueeeke.videoplayer.player;

import android.content.Context;

public class AndroidMediaPlayerFactory extends PlayerFactory {

    private Context mContext;

    public AndroidMediaPlayerFactory(Context context) {
        mContext = context.getApplicationContext();
    }

    public static AndroidMediaPlayerFactory create(Context context) {
        return new AndroidMediaPlayerFactory(context);
    }

    @Override
    public AbstractPlayer createPlayer() {
        return new AndroidMediaPlayer(mContext);
    }
}
