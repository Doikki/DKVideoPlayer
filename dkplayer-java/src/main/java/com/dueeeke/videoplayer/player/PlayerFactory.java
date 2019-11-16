package com.dueeeke.videoplayer.player;

import android.content.Context;

public abstract class PlayerFactory<P extends AbstractPlayer> {

    public abstract P createPlayer(Context context);
}
