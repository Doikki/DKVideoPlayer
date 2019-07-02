package com.dueeeke.dkplayer.player;

import android.content.Context;

import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;

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
