package xyz.doikki.dkplayer.widget.player;

import android.content.Context;

import xyz.doikki.videoplayer.ijk.IjkPlayer;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class CustomIjkMediaPlayer extends IjkPlayer {

    public CustomIjkMediaPlayer(Context context) {
        super(context);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_PLAYER相关配置
     */
    public void setPlayerOption(String name, String value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, name, value);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_PLAYER相关配置
     */
    public void setPlayerOption(String name, long value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, name, value);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_FORMAT相关配置
     */
    public void setFormatOption(String name, String value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, name, value);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_FORMAT相关配置
     */
    public void setFormatOption(String name, long value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, name, value);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_CODEC相关配置
     */
    public void setCodecOption(String name, String value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, name, value);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_CODEC相关配置
     */
    public void setCodecOption(String name, long value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, name, value);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_SWS相关配置
     */
    public void setSwsOption(String name, String value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_SWS, name, value);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_SWS相关配置
     */
    public void setSwsOption(String name, long value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_SWS, name, value);
    }

}
