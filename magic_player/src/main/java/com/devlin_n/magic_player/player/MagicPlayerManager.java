package com.devlin_n.magic_player.player;

/**
 * 视频播放器管理器.
 */
public class MagicPlayerManager {

    private MagicVideoView mVideoView;

    private MagicPlayerManager() {
    }

    private static MagicPlayerManager sInstance;

    public static synchronized MagicPlayerManager instance() {
        if (sInstance == null) {
            sInstance = new MagicPlayerManager();
        }
        return sInstance;
    }

    public void setCurrentNiceVideoPlayer(MagicVideoView videoView) {
        mVideoView = videoView;
    }

    public void releaseNiceVideoPlayer() {
        if (mVideoView != null) {
            mVideoView.release();
            mVideoView = null;
        }
    }

    public boolean onBackPressd() {
        return mVideoView != null && mVideoView.onBackPressed();
    }
}
