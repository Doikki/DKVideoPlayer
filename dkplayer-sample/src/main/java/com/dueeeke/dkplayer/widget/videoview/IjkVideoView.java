package com.dueeeke.dkplayer.widget.videoview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.dkplayer.widget.player.CustomIjkMediaPlayer;
import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;

import java.util.HashMap;
import java.util.Map;

public class IjkVideoView extends VideoView<CustomIjkMediaPlayer> {

    private HashMap<String, String> mPlayerOptions = new HashMap<>();
    private HashMap<String, String> mFormatOptions = new HashMap<>();
    private HashMap<String, String> mCodecOptions = new HashMap<>();
    private HashMap<String, String> mSwsOptions = new HashMap<>();

    public IjkVideoView(@NonNull Context context) {
        super(context);
    }

    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        for (Map.Entry<String, String> next : mPlayerOptions.entrySet()) {
            mMediaPlayer.setPlayerOption(next.getKey(), next.getValue());
        }
        for (Map.Entry<String, String> next : mFormatOptions.entrySet()) {
            mMediaPlayer.setFormatOption(next.getKey(), next.getValue());
        }
        for (Map.Entry<String, String> next : mCodecOptions.entrySet()) {
            mMediaPlayer.setCodecOption(next.getKey(), next.getValue());
        }
        for (Map.Entry<String, String> next : mSwsOptions.entrySet()) {
            mMediaPlayer.setSwsOption(next.getKey(), next.getValue());
        }
    }

    public void setEnableMediaCodec(boolean isEnable) {
        String value = isEnable ? "1" : "0";
        addPlayerOption("mediacodec", value);
        addPlayerOption("mediacodec-auto-rotate", value);
        addPlayerOption("mediacodec-handle-resolution-change", value);
        addPlayerOption("mediacodec-hevc", value);//开启hevc硬解
    }

    public void addPlayerOption(String name, String value) {
        mPlayerOptions.put(name, value);
    }

    public void addFormatOption(String name, String value) {
        mFormatOptions.put(name, value);
    }
    
    public void addCodecOption(String name, String value) {
        mCodecOptions.put(name, value);
    }
    
    public void addSwsOption(String name, String value) {
        mSwsOptions.put(name, value);
    }

}
