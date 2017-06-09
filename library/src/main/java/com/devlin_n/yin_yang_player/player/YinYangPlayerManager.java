package com.devlin_n.yin_yang_player.player;

import java.lang.ref.WeakReference;

/**
 * 视频播放器管理器.
 */
public class YinYangPlayerManager {

    private WeakReference<YinYangPlayer> mPlayer; //写成弱引用防止内存泄露

    private YinYangPlayerManager() {
    }

    private static YinYangPlayerManager sInstance;

    public static synchronized YinYangPlayerManager instance() {
        if (sInstance == null) {
            sInstance = new YinYangPlayerManager();
        }
        return sInstance;
    }

    public void setCurrentVideoView(YinYangPlayer player) {
        mPlayer = new WeakReference<>(player);
    }

    public YinYangPlayer getCurrentVideoView() {
        if (mPlayer == null) return null;
        return mPlayer.get();
    }

    public void releaseVideoView() {
        if (mPlayer != null && mPlayer.get() != null) {
            mPlayer.get().release();
            mPlayer = null;
        }
    }

    public boolean onBackPressed() {
        return mPlayer != null && mPlayer.get() != null && mPlayer.get().onBackPressed();
    }
}
