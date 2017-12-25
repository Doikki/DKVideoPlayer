package com.dueeeke.videoplayer.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.dueeeke.videoplayer.listener.MediaEngineInterface;

import java.io.IOException;

public class AndroidMediaEngine extends BaseMediaEngine {

    public MediaPlayer mMediaPlayer;
    private MediaEngineInterface mMediaEngineInterface;

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
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
        mMediaPlayer.setVolume(1, 1);
        mMediaPlayer.reset();
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

    private MediaPlayer.OnErrorListener onErrorListener = (mp, framework_err, impl_err) -> {
        if (mMediaEngineInterface != null) mMediaEngineInterface.onError();
        return true;
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = mp -> {
        if (mMediaEngineInterface != null) mMediaEngineInterface.onCompletion();
    };

    private MediaPlayer.OnInfoListener onInfoListener = (mp, what, extra) -> {
        if (mMediaEngineInterface != null) mMediaEngineInterface.onInfo(what, extra);
        return true;
    };

    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = (mp, percent) -> {
        if (mMediaEngineInterface != null) mMediaEngineInterface.onBufferingUpdate(percent);
    };


    private MediaPlayer.OnPreparedListener onPreparedListener = mp -> {
        if (mMediaEngineInterface != null) mMediaEngineInterface.onPrepared();
    };

    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = (mp, width, height) -> {
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            if (mMediaEngineInterface != null)
                mMediaEngineInterface.onVideoSizeChanged(videoWidth, videoHeight);
        }
    };

    public void setMediaEngineInterface(MediaEngineInterface mediaEngineInterface) {
        this.mMediaEngineInterface = mediaEngineInterface;
    }
}
