package com.devlin_n.magic_player.player;

import java.lang.ref.WeakReference;

/**
 * 视频播放器管理器.
 */
public class MagicPlayerManager {

    private WeakReference<MagicVideoView> mVideoView; //写成弱引用防止内存泄露

    private MagicPlayerManager() {
    }

    private static MagicPlayerManager sInstance;

    public static synchronized MagicPlayerManager instance() {
        if (sInstance == null) {
            sInstance = new MagicPlayerManager();
        }
        return sInstance;
    }

    public void setCurrentVideoView(MagicVideoView videoView) {
        mVideoView = new WeakReference<>(videoView);
    }

    public MagicVideoView getCurrentVideoView() {
        if (mVideoView == null) return null;
        return mVideoView.get();
    }

    public void releaseVideoView() {
        if (mVideoView != null && mVideoView.get() != null) {
            mVideoView.get().release();
            mVideoView = null;
        }
    }

    public boolean onBackPressed() {
        return mVideoView != null && mVideoView.get() != null && mVideoView.get().onBackPressed();
    }
}
