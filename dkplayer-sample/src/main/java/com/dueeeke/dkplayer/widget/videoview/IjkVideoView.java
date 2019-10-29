package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dueeeke.dkplayer.widget.player.CustomIjkMediaPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.HashMap;
import java.util.Map;

public class IjkVideoView extends VideoView<CustomIjkMediaPlayer> {

    private boolean mIsEnableMediaCodec;

    private HashMap<String, Integer> mPlayerOptions = new HashMap<>();

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
        for (Map.Entry<String, Integer> next : mPlayerOptions.entrySet()) {
            mMediaPlayer.setPlayerOption(next.getKey(), next.getValue());
        }
        mMediaPlayer.setEnableMediaCodec(mIsEnableMediaCodec);
    }

    public void setEnableMediaCodec(boolean isEnable) {
        this.mIsEnableMediaCodec = isEnable;
    }

    public void setPlayerOption(String name, int value) {
        mPlayerOptions.put(name, value);
    }

}
