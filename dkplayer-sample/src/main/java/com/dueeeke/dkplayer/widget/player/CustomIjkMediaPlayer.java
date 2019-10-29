package com.dueeeke.dkplayer.widget.player;

import com.dueeeke.videoplayer.ijk.IjkPlayer;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class CustomIjkMediaPlayer extends IjkPlayer {

    private boolean mIsEnableMediaCodec;

    public void setEnableMediaCodec(boolean isEnable) {
        mIsEnableMediaCodec = isEnable;
        int value = isEnable ? 1 : 0;
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);//开启硬解码
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", value);//开启hevc硬解
    }

    @Override
    public void reset() {
        super.reset();
        setEnableMediaCodec(mIsEnableMediaCodec);
    }

    /**
     * 设置IjkMediaPlayer.OPT_CATEGORY_PLAYER相关配置
     */
    public void setPlayerOption(String name, int value) {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, name, value);
    }
}
