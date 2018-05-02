package com.dueeeke.dkplayer.player;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class ExoMediaPlayer extends AbstractPlayer implements Player.EventListener,
        VideoRendererEventListener {

    private static final String TAG = "ExoMediaPlayer";

    private Context mAppContext;
    private SimpleExoPlayer2 mInternalPlayer;
    private DefaultRenderersFactory renderersFactory;
    private MediaSource mMediaSource;
    private DefaultTrackSelector mTrackSelector;
    private String mDataSource;
    private Surface mSurface;
    private Handler mainHandler;
    private Map<String, String> mHeaders = new HashMap<>();
    private PlaybackParameters mSpeedPlaybackParameters;
    private int lastReportedPlaybackState;
    private boolean lastReportedPlayWhenReady;
    private boolean mIsPrepareing = true;
    private boolean mIsBuffering = false;

//    private int audioSessionId = C.AUDIO_SESSION_ID_UNSET;
    public static final int TYPE_RTMP = 4;

    public ExoMediaPlayer(Context context) {
        mAppContext = context.getApplicationContext();
        Looper eventLooper = Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper();
        mainHandler = new Handler(eventLooper);
        lastReportedPlaybackState = Player.STATE_IDLE;
    }

    @Override
    public void start() {
        if (mInternalPlayer == null)
            return;
        mInternalPlayer.setPlayWhenReady(true);
    }

    @Override
    public void initPlayer() {

    }

    public void setDataSource(Context context, Uri uri) {
        mDataSource = uri.toString();
        mMediaSource = getMediaSource(false);
    }

    @Override
    public void setDataSource(String path) {
        setDataSource(mAppContext, Uri.parse(path));
    }

    private MediaSource getMediaSource(boolean preview) {
        Uri contentUri = Uri.parse(mDataSource);
        int contentType = inferContentType(mDataSource);
        switch (contentType) {
            case C.TYPE_SS:
                return new SsMediaSource(contentUri, new DefaultDataSourceFactory(mAppContext, null,
                        getHttpDataSourceFactory(preview)),
                        new DefaultSsChunkSource.Factory(getDataSourceFactory(preview)),
                        mainHandler, null);
            case C.TYPE_DASH:
                return new DashMediaSource(contentUri,
                        new DefaultDataSourceFactory(mAppContext, null,
                                getHttpDataSourceFactory(preview)),
                        new DefaultDashChunkSource.Factory(getDataSourceFactory(preview)),
                        mainHandler, null);
            case C.TYPE_HLS:
                return new HlsMediaSource(contentUri, getDataSourceFactory(preview), mainHandler, null);
//            case TYPE_RTMP:
//                RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory(null);
//                return new ExtractorMediaSource(contentUri, rtmpDataSourceFactory,
//                        new DefaultExtractorsFactory(), mainHandler, null);
            case C.TYPE_OTHER:
            default:
                return new ExtractorMediaSource(contentUri, getDataSourceFactory(preview),
                        new DefaultExtractorsFactory(), mainHandler, null);
        }
    }

    /**
     * Makes a best guess to infer the type from a file name.
     *
     * @param fileName Name of the file. It can include the path of the file.
     * @return The content type.
     */
    @C.ContentType
    private int inferContentType(String fileName) {
        fileName = Util.toLowerInvariant(fileName);
        if (fileName.endsWith(".mpd")) {
            return C.TYPE_DASH;
        } else if (fileName.endsWith(".m3u8")) {
            return C.TYPE_HLS;
        } else if (fileName.endsWith(".ism") || fileName.endsWith(".isml")
                || fileName.endsWith(".ism/manifest") || fileName.endsWith(".isml/manifest")) {
            return C.TYPE_SS;
        } else if (fileName.startsWith("rtmp:")) {
            return TYPE_RTMP;
        } else {
            return C.TYPE_OTHER;
        }
    }

    private DataSource.Factory getHttpDataSourceFactory(boolean preview) {
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(mAppContext,
                TAG), preview ? null : new DefaultBandwidthMeter());
        if (mHeaders != null && mHeaders.size() > 0) {
            for (Map.Entry<String, String> header : mHeaders.entrySet()) {
                dataSourceFactory.getDefaultRequestProperties().set(header.getKey(), header.getValue());
            }
        }
        return dataSourceFactory;
    }

    private DataSource.Factory getDataSourceFactory(boolean preview) {
        return new DefaultDataSourceFactory(mAppContext, preview ? null : new DefaultBandwidthMeter(),
                getHttpDataSourceFactory(preview));
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
        mInternalPlayer.release();
    }

    @Override
    public void prepareAsync() {
        if (mInternalPlayer != null)
            throw new IllegalStateException("can't prepare a prepared player");
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        mTrackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

//        mEventLogger = new EventLogger(mTrackSelector);

//        boolean preferExtensionDecoders = true;
//        boolean useExtensionRenderers = true;//是否开启扩展
        @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;

        renderersFactory = new DefaultRenderersFactory(mAppContext, null, extensionRendererMode);
        DefaultLoadControl loadControl = new DefaultLoadControl();
        mInternalPlayer = new SimpleExoPlayer2(renderersFactory, mTrackSelector, loadControl);
        mInternalPlayer.addListener(this);
        mInternalPlayer.setVideoDebugListener(this);
//        mInternalPlayer.setAudioDebugListener(this);
//        mInternalPlayer.addListener(mEventLogger);
        if (mSpeedPlaybackParameters != null) {
            mInternalPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
        }
        if (mSurface != null)
            mInternalPlayer.setVideoSurface(mSurface);

        mInternalPlayer.prepare(mMediaSource);
        mInternalPlayer.setPlayWhenReady(true);
    }

    @Override
    public void reset() {
        if (mInternalPlayer != null) {
            mInternalPlayer.release();
//            mInternalPlayer.removeListener(this);
            mInternalPlayer = null;
        }

        mSurface = null;
        mDataSource = null;
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
            reset();
//            mEventLogger = null;
        }
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
    public void setSurface(Surface surface) {
        mSurface = surface;
        if (mInternalPlayer != null) {
            mInternalPlayer.setVideoSurface(surface);
            /*if (mSurface == null) {
                mTrackSelector.setRendererDisabled(getVideoRendererIndex(), true);
            } else {
                mTrackSelector.setRendererDisabled(getVideoRendererIndex(), false);
            }*/
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
    public void setVolume(int leftVolume, int rightVolume) {
        if (mInternalPlayer != null)
            mInternalPlayer.setVolume((leftVolume + rightVolume) / 2);
    }

    @Override
    public void setLooping(boolean isLooping) {
//        this.isLooping = isLooping;
//        mMediaPlayer.setLooping(isLooping);
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
        PlaybackParameters playbackParameters = new PlaybackParameters(speed, speed);
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
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        //重新播放状态顺序为：STATE_IDLE -》STATE_BUFFERING -》STATE_READY
        //缓冲时顺序为：STATE_BUFFERING -》STATE_READY
        //Log.e(TAG, "onPlayerStateChanged: playWhenReady = " + playWhenReady + ", playbackState = " + playbackState);
        if (lastReportedPlayWhenReady != playWhenReady || lastReportedPlaybackState != playbackState) {
            if (mIsBuffering) {
                switch (playbackState) {
                    case Player.STATE_ENDED:
                    case Player.STATE_READY:
                        if (mPlayerEventListener != null) {
                            mPlayerEventListener.onInfo(IMediaPlayer.MEDIA_INFO_BUFFERING_END, mInternalPlayer.getBufferedPercentage());
                        }
                        mIsBuffering = false;
                        break;
                }
            }

            if (mIsPrepareing) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        if (mPlayerEventListener != null) {
                            mPlayerEventListener.onPrepared();
                            mPlayerEventListener.onInfo(IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START, 0);
                        }
                        mIsPrepareing = false;
                        break;
                }
            }

            switch (playbackState) {
                case Player.STATE_BUFFERING:
                    if (mPlayerEventListener != null) {
                        mPlayerEventListener.onInfo(IMediaPlayer.MEDIA_INFO_BUFFERING_START, mInternalPlayer.getBufferedPercentage());
                    }
                    mIsBuffering = true;
                    break;
                case Player.STATE_READY:
                    break;
                case Player.STATE_ENDED:
                    if (mPlayerEventListener != null) {
                        mPlayerEventListener.onCompletion();
                    }
                    break;
                default:
                    break;
            }
        }
        lastReportedPlayWhenReady = playWhenReady;
        lastReportedPlaybackState = playbackState;
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onError();
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onVideoSizeChanged(width, height);
            if (unappliedRotationDegrees > 0) {
                mPlayerEventListener.onInfo(IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED, unappliedRotationDegrees);
            }
        }
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }
}
