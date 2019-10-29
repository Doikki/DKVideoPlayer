package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dueeeke.dkplayer.widget.player.CustomIjkMediaPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;

public class IjkVideoView extends VideoView<CustomIjkMediaPlayer> {

    private boolean mIsEnableMediaCodec;

    public IjkVideoView(@NonNull Context context) {
        super(context);
    }

    {
        setPlayerFactory(new PlayerFactory<CustomIjkMediaPlayer>() {
            @Override
            public CustomIjkMediaPlayer createPlayer() {
                return new CustomIjkMediaPlayer();
            }
        });
    }

    @Override
    protected void setOptions() {
        super.setOptions();
        mMediaPlayer.setEnableMediaCodec(mIsEnableMediaCodec);
    }

    public void setEnableMediaCodec(boolean isEnable) {
        this.mIsEnableMediaCodec = isEnable;
    }

}
