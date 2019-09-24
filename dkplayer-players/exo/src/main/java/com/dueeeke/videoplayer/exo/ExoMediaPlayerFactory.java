package com.dueeeke.videoplayer.exo;

import android.content.Context;

import com.dueeeke.videoplayer.player.PlayerFactory;

public class ExoMediaPlayerFactory extends PlayerFactory<ExoMediaPlayer> {

    private Context mContext;

    public ExoMediaPlayerFactory(Context context) {
        mContext = context.getApplicationContext();
    }

    public static ExoMediaPlayerFactory create(Context context) {
        return new ExoMediaPlayerFactory(context);
    }

    @Override
    public ExoMediaPlayer createPlayer() {
        return new ExoMediaPlayer(mContext);
    }
}
