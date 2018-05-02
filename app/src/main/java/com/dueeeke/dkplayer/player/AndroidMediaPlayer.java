package com.dueeeke.dkplayer.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.dueeeke.videoplayer.player.AbstractPlayer;

public class AndroidMediaPlayer extends AbstractPlayer {

    protected MediaPlayer mMediaPlayer;
    private boolean isLooping;

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void initPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnErrorListener(onErrorListener);
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        mMediaPlayer.setOnInfoListener(onInfoListener);
        mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mMediaPlayer.setOnPreparedListener(onPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
    }

    @Override
    public void setDataSource(String path) {
        try {
            mMediaPlayer.setDataSource(path);
        } catch (Exception e) {
            if (mPlayerEventListener != null) mPlayerEventListener.onError();
        }
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
        mMediaPlayer.setLooping(isLooping);
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

    @Override
    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
        mMediaPlayer.setLooping(isLooping);
    }

    @Override
    public void setEnableMediaCodec(boolean isEnable) {
        // no support
    }

    @Override
    public void setOptions() {
        // no support
    }

    @Override
    public void setSpeed(float speed) {
        // no support
    }

    @Override
    public long getTcpSpeed() {
        // no support
        return 0;
    }

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (mPlayerEventListener != null) mPlayerEventListener.onError();
            return true;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mPlayerEventListener != null) mPlayerEventListener.onCompletion();
        }
    };

    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (mPlayerEventListener != null) mPlayerEventListener.onInfo(what, extra);
            return true;
        }
    };

    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mPlayerEventListener != null) mPlayerEventListener.onBufferingUpdate(percent);
        }
    };


    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onPrepared();
                mMediaPlayer.start();
            }
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                if (mPlayerEventListener != null)
                    mPlayerEventListener.onVideoSizeChanged(videoWidth, videoHeight);
            }
        }
    };
}
