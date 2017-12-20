package com.devlin_n.videoplayer.player;

import android.media.AudioManager;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.devlin_n.videoplayer.listener.IjkPlayerInterface;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkMediaEngine {

    public IjkMediaPlayer mMediaPlayer;
    private IjkPlayerInterface ijkPlayerInterface;

    public void start() {
        mMediaPlayer.start();
    }

    public void initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new IjkMediaPlayer();
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);//开启硬解码
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnErrorListener(onErrorListener);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setOnInfoListener(onInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mMediaPlayer.setOnPreparedListener(onPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        }
    }

    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaPlayer.setDataSource(path);
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void stop() {
        mMediaPlayer.stop();
    }

    public void prepareAsync() {
        mMediaPlayer.prepareAsync();
    }

    public void reset() {
        mMediaPlayer.reset();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void seekTo(long time) {
        mMediaPlayer.seekTo((int) time);
    }

    public void release() {
        if (mMediaPlayer != null)
            mMediaPlayer.release();
    }

    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    public void setDisplay(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
    }

    public void setVolume(int v1, int v2) {
        mMediaPlayer.setVolume(v1, v2);
    }

    private IMediaPlayer.OnErrorListener onErrorListener = (iMediaPlayer, framework_err, impl_err) -> {
        if (ijkPlayerInterface != null) ijkPlayerInterface.onError();
        return true;
    };

    private IMediaPlayer.OnCompletionListener onCompletionListener = iMediaPlayer -> {
        if (ijkPlayerInterface != null) ijkPlayerInterface.onCompletion();
    };

    private IMediaPlayer.OnInfoListener onInfoListener = (iMediaPlayer, what, extra) -> {
        if (ijkPlayerInterface != null) ijkPlayerInterface.onInfo(what, extra);
        return true;
    };

    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = (iMediaPlayer, i) -> {
        if (ijkPlayerInterface != null) ijkPlayerInterface.onBufferingUpdate(i);
    };


    private IMediaPlayer.OnPreparedListener onPreparedListener = iMediaPlayer -> {
        if (ijkPlayerInterface != null) ijkPlayerInterface.onPrepared();
    };

    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = (iMediaPlayer, i, i1, i2, i3) -> {
        int videoWidth = iMediaPlayer.getVideoWidth();
        int videoHeight = iMediaPlayer.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            if (ijkPlayerInterface != null)
                ijkPlayerInterface.onVideoSizeChanged(videoWidth, videoHeight);
        }
    };

    public void setIjkPlayerInterface(IjkPlayerInterface ijkPlayerInterface) {
        this.ijkPlayerInterface = ijkPlayerInterface;
    }
}
