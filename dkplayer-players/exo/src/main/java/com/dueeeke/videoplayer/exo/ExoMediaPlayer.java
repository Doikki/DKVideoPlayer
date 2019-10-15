package com.dueeeke.videoplayer.exo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.dueeeke.videoplayer.util.PlayerUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.video.VideoListener;

import java.util.Map;


public class ExoMediaPlayer extends AbstractPlayer implements VideoListener, Player.EventListener {

    protected Context mAppContext;
    protected SimpleExoPlayer mInternalPlayer;
    protected MediaSource mMediaSource;
    protected Surface mSurface;
    protected PlaybackParameters mSpeedPlaybackParameters;

    protected ExoMediaSourceHelper mMediaSourceHelper;

    protected int mLastReportedPlaybackState;
    protected boolean mLastReportedPlayWhenReady;
    protected boolean mIsPreparing;
    protected boolean mIsBuffering;
    protected boolean mIsRenderStart;

    protected LoadControl mLoadControl;

    protected RenderersFactory mRenderersFactory;

    protected TrackSelector mTrackSelector;

    public ExoMediaPlayer() {
        mAppContext = PlayerUtils.getApplication();
        if (mAppContext == null) {
            throw new NullPointerException();
        }
        mLastReportedPlaybackState = Player.STATE_IDLE;
        mMediaSourceHelper = new ExoMediaSourceHelper(mAppContext);
    }

    @Override
    public void initPlayer() {
        mLoadControl = mLoadControl == null ? new DefaultLoadControl() : mLoadControl;
        mRenderersFactory = mRenderersFactory == null ? new DefaultRenderersFactory(mAppContext) : mRenderersFactory;
        mTrackSelector = mTrackSelector == null ? new DefaultTrackSelector() : mTrackSelector;
        mInternalPlayer = ExoPlayerFactory.newSimpleInstance(mAppContext, mRenderersFactory, mTrackSelector, mLoadControl);
        setOptions();
        mInternalPlayer.addListener(this);
        mInternalPlayer.addVideoListener(this);
    }

    public void setTrackSelector(TrackSelector trackSelector) {
        mTrackSelector = trackSelector;
    }

    public void setRenderersFactory(RenderersFactory renderersFactory) {
        mRenderersFactory = renderersFactory;
    }

    public void setLoadControl(LoadControl loadControl) {
        mLoadControl = loadControl;
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) {
        mMediaSource = mMediaSourceHelper.getMediaSource(path);
        mMediaSourceHelper.setHeaders(headers);
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        //no support
    }

    @Override
    public void start() {
        if (mInternalPlayer == null)
            return;
        mInternalPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        if (mInternalPlayer == null)
            return;
        mInternalPlayer.setPlayWhenReady(false);
    }

    @Override
    public void stop() {
        if (mInternalPlayer == null)
            return;
        mInternalPlayer.stop();
    }

    @Override
    public void prepareAsync() {
        if (mMediaSource == null) return;
        if (mSpeedPlaybackParameters != null) {
            mInternalPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
        }
        if (mSurface != null) {
            mInternalPlayer.setVideoSurface(mSurface);
        }
        mIsPreparing = true;
        mInternalPlayer.prepare(mMediaSource);
        mInternalPlayer.setPlayWhenReady(false);
    }

    @Override
    public void reset() {
        if (mInternalPlayer != null) {
            mInternalPlayer.stop(true);
        }
    }

    @Override
    public boolean isPlaying() {
        if (mInternalPlayer == null)
            return false;
        int state = mInternalPlayer.getPlaybackState();
        switch (state) {
            case Player.STATE_BUFFERING:
            case Player.STATE_READY:
                return mInternalPlayer.getPlayWhenReady();
            case Player.STATE_IDLE:
            case Player.STATE_ENDED:
            default:
                return false;
        }
    }

    @Override
    public void seekTo(long time) {
        if (mInternalPlayer == null)
            return;
        mInternalPlayer.seekTo(time);
    }

    @Override
    public void release() {
        if (mInternalPlayer != null) {
            mInternalPlayer.removeListener(this);
            mInternalPlayer.removeVideoListener(this);
            new Thread() {
                @Override
                public void run() {
                    mInternalPlayer.release();
                    mInternalPlayer = null;
                }
            }.start();
        }

        mSurface = null;
        mIsPreparing = false;
        mIsBuffering = false;
        mLastReportedPlaybackState = Player.STATE_IDLE;
        mLastReportedPlayWhenReady = false;
        mSpeedPlaybackParameters = null;
    }

    @Override
    public long getCurrentPosition() {
        if (mInternalPlayer == null)
            return 0;
        return mInternalPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        if (mInternalPlayer == null)
            return 0;
        return mInternalPlayer.getDuration();
    }

    @Override
    public int getBufferedPercentage() {
        return mInternalPlayer == null ? 0 : mInternalPlayer.getBufferedPercentage();
    }

    @Override
    public void setSurface(Surface surface) {
        mSurface = surface;
        if (mInternalPlayer != null) {
            mInternalPlayer.setVideoSurface(surface);
        }
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        if (holder == null)
            setSurface(null);
        else
            setSurface(holder.getSurface());
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (mInternalPlayer != null)
            mInternalPlayer.setVolume((leftVolume + rightVolume) / 2);
    }

    @Override
    public void setLooping(boolean isLooping) {
        if (mInternalPlayer != null)
            mInternalPlayer.setRepeatMode(isLooping ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
    }

    @Override
    public void setEnableMediaCodec(boolean isEnable) {
        // exo player is based on MediaCodec, no need to enable
    }

    @Override
    public void setOptions() {
    }

    @Override
    public void setSpeed(float speed) {
        PlaybackParameters playbackParameters = new PlaybackParameters(speed);
        mSpeedPlaybackParameters = playbackParameters;
        if (mInternalPlayer != null) {
            mInternalPlayer.setPlaybackParameters(playbackParameters);
        }
    }

    @Override
    public long getTcpSpeed() {
        // no support
        return 0;
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (mLastReportedPlayWhenReady != playWhenReady || mLastReportedPlaybackState != playbackState) {
            switch (playbackState) {
                case Player.STATE_READY:
                    if (playWhenReady && !mIsBuffering && !mIsRenderStart) {
                        if (mPlayerEventListener != null) {
                            mPlayerEventListener.onInfo(MEDIA_INFO_VIDEO_RENDERING_START, 0);
                        }
                        mIsRenderStart = true;
                    }
                    if (mIsPreparing) {
                        if (mPlayerEventListener != null) {
                            mPlayerEventListener.onPrepared();
                        }
                        mIsPreparing = false;
                    } else if (mIsBuffering) {
                        if (mPlayerEventListener != null) {
                            mPlayerEventListener.onInfo(MEDIA_INFO_BUFFERING_END, getBufferedPercentage());
                        }
                        mIsBuffering = false;
                    }
                    break;
                case Player.STATE_BUFFERING:
                    if (!mIsPreparing) {
                        if (mPlayerEventListener != null) {
                            mPlayerEventListener.onInfo(MEDIA_INFO_BUFFERING_START, getBufferedPercentage());
                        }
                        mIsBuffering = true;
                    }
                    break;
                case Player.STATE_ENDED:
                    if (mPlayerEventListener != null) {
                        mPlayerEventListener.onCompletion();
                    }
                    break;
            }
        }
        mLastReportedPlayWhenReady = playWhenReady;
        mLastReportedPlaybackState = playbackState;
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onError();
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onVideoSizeChanged(width, height);
            if (unappliedRotationDegrees > 0) {
                mPlayerEventListener.onInfo(MEDIA_INFO_VIDEO_ROTATION_CHANGED, unappliedRotationDegrees);
            }
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        if (reason == Player.DISCONTINUITY_REASON_PERIOD_TRANSITION) {

        }
    }
}
