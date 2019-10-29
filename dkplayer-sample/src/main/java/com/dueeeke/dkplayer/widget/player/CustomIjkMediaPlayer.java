package com.dueeeke.dkplayer.widget.player;

import com.dueeeke.videoplayer.ijk.IjkPlayer;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class CustomIjkMediaPlayer extends IjkPlayer {

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_PLAYER相关配置
     */
    public void setPlayerOption(String name, String value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, name, value);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_FORMAT相关配置
     */
    public void setFormatOption(String name, String value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, name, value);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_CODEC相关配置
     */
    public void setCodecOption(String name, String value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, name, value);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_SWS相关配置
     */
    public void setSwsOption(String name, String value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_SWS, name, value);
    }

}
