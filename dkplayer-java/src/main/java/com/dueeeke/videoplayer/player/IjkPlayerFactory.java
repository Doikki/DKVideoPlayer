package com.dueeeke.videoplayer.player;

import android.content.Context;

public class IjkPlayerFactory extends PlayerFactory {

    private Context mContext;

    public IjkPlayerFactory(Context context) {
        mContext = context.getApplicationContext();
    }

    public static IjkPlayerFactory create(Context context) {
        return new IjkPlayerFactory(context);
    }

    @Override
    public AbstractPlayer createPlayer() {
        return new IjkPlayer(mContext);
    }
}
