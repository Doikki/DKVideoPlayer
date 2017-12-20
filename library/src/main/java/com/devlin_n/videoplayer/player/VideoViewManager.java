package com.devlin_n.videoplayer.player;

import java.lang.ref.WeakReference;

/**
 * 视频播放器管理器.
 */
public class VideoViewManager {

    private WeakReference<BaseIjkVideoView> mPlayer; //写成弱引用防止内存泄露

    private VideoViewManager() {
    }

    private static VideoViewManager sInstance;

    public static synchronized VideoViewManager instance() {
        if (sInstance == null) {
            sInstance = new VideoViewManager();
        }
        return sInstance;
    }

    public void setCurrentVideoPlayer(BaseIjkVideoView player) {
        mPlayer = new WeakReference<>(player);
    }

    public BaseIjkVideoView getCurrentVideoPlayer() {
        if (mPlayer == null) return null;
        return mPlayer.get();
    }

    public void releaseVideoPlayer() {
        if (mPlayer != null && mPlayer.get() != null) {
            mPlayer.get().release();
            mPlayer = null;
        }
    }

    public boolean onBackPressed() {
        return mPlayer != null && mPlayer.get() != null && mPlayer.get().onBackPressed();
    }
}
