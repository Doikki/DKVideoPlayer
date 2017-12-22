package com.dueeeke.videoplayer.player;

import android.media.AudioManager;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.dueeeke.videoplayer.listener.MediaEngineInterface;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkMediaEngine extends BaseMediaEngine {

    public IjkMediaPlayer mMediaPlayer;
    private MediaEngineInterface mMediaEngineInterface;

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
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

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaPlayer.setDataSource(path);
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public void prepareAsync() {
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long time) {
        mMediaPlayer.seekTo((int) time);
    }

    @Override
    public void release() {
        if (mMediaPlayer != null)
            mMediaPlayer.release();
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void setVolume(int v1, int v2) {
        mMediaPlayer.setVolume(v1, v2);
    }

    private IMediaPlayer.OnErrorListener onErrorListener = (iMediaPlayer, framework_err, impl_err) -> {
        if (mMediaEngineInterface != null) mMediaEngineInterface.onError();
        return true;
    };

    private IMediaPlayer.OnCompletionListener onCompletionListener = iMediaPlayer -> {
        if (mMediaEngineInterface != null) mMediaEngineInterface.onCompletion();
    };

    private IMediaPlayer.OnInfoListener onInfoListener = (iMediaPlayer, what, extra) -> {
        if (mMediaEngineInterface != null) mMediaEngineInterface.onInfo(what, extra);
        return true;
    };

    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = (iMediaPlayer, percent) -> {
        if (mMediaEngineInterface != null) mMediaEngineInterface.onBufferingUpdate(percent);
    };


    private IMediaPlayer.OnPreparedListener onPreparedListener = iMediaPlayer -> {
        if (mMediaEngineInterface != null) mMediaEngineInterface.onPrepared();
    };

    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = (iMediaPlayer, i, i1, i2, i3) -> {
        int videoWidth = iMediaPlayer.getVideoWidth();
        int videoHeight = iMediaPlayer.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            if (mMediaEngineInterface != null)
                mMediaEngineInterface.onVideoSizeChanged(videoWidth, videoHeight);
        }
    };

    public void setMediaEngineInterface(MediaEngineInterface mediaEngineInterface) {
        this.mMediaEngineInterface = mediaEngineInterface;
    }
}
