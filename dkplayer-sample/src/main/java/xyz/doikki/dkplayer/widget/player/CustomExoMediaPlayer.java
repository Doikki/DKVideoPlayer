package xyz.doikki.dkplayer.widget.player;


import android.content.Context;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;

import xyz.doikki.videoplayer.exo.ExoMediaPlayer;
import xyz.doikki.videoplayer.util.L;

/**
 * 自定义ExoMediaPlayer，目前扩展了诸如边播边存，以及可以直接设置Exo自己的MediaSource。
 */
public class CustomExoMediaPlayer extends ExoMediaPlayer {

    public CustomExoMediaPlayer(Context context) {
        super(context);
    }

    public void setDataSource(MediaSource dataSource) {
        mMediaSource = dataSource;
    }

    @Override
    public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason);
        L.d("onPositionDiscontinuity " + reason);
    }
}
