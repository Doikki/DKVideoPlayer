package com.dueeeke.videoplayer.ijk;

import android.content.Context;

import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;

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
