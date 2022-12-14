package xyz.doikki.videoplayer.exo

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.view.Surface
import android.view.SurfaceHolder
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.Clock
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.video.VideoSize
import xyz.doikki.videoplayer.player.AbstractPlayer
import xyz.doikki.videoplayer.player.IPlayer
import xyz.doikki.videoplayer.GlobalConfig
import xyz.doikki.videoplayer.internal.PlayerException

open class ExoMediaPlayer(context: Context) : AbstractPlayer(), Player.Listener {
    protected var mAppContext: Context
    protected var mInternalPlayer: ExoPlayer? = null
    @JvmField
    protected var mMediaSource: MediaSource? = null
    protected var mMediaSourceHelper: ExoMediaSourceHelper
    private var mSpeedPlaybackParameters: PlaybackParameters? = null
    private var mIsPreparing = false
    private var mLoadControl: LoadControl? = null
    private var mRenderersFactory: RenderersFactory? = null
    private var mTrackSelector: TrackSelector? = null
    override fun init() {
        mInternalPlayer = ExoPlayer.Builder(
            mAppContext,
            (if (mRenderersFactory == null) DefaultRenderersFactory(mAppContext).also {
                mRenderersFactory = it
            } else mRenderersFactory)!!,
            DefaultMediaSourceFactory(mAppContext),
            (if (mTrackSelector == null) DefaultTrackSelector(mAppContext).also {
                mTrackSelector = it
            } else mTrackSelector)!!,
            (if (mLoadControl == null) DefaultLoadControl().also {
                mLoadControl = it
            } else mLoadControl)!!,
            DefaultBandwidthMeter.getSingletonInstance(mAppContext),
            DefaultAnalyticsCollector(Clock.DEFAULT))
            .build()
        //准备好就开始播放
        mInternalPlayer!!.playWhenReady = true

        //播放器日志
        if (GlobalConfig.isDebuggable && mTrackSelector is MappingTrackSelector) {
            mInternalPlayer!!.addAnalyticsListener(
                EventLogger(
                    mTrackSelector as MappingTrackSelector?,
                    "ExoPlayer"
                )
            )
        }
        mInternalPlayer!!.addListener(this)
    }

    fun setTrackSelector(trackSelector: TrackSelector?) {
        mTrackSelector = trackSelector
    }

    fun setRenderersFactory(renderersFactory: RenderersFactory?) {
        mRenderersFactory = renderersFactory
    }

    fun setLoadControl(loadControl: LoadControl?) {
        mLoadControl = loadControl
    }

    override fun setDataSource(path: String, headers: Map<String, String>?) {
        mMediaSource = mMediaSourceHelper.getMediaSource(path, headers)
    }

    override fun setDataSource(fd: AssetFileDescriptor) {
        //no support
    }

    override fun start() {
        if (mInternalPlayer == null) return
        mInternalPlayer!!.playWhenReady = true
    }

    override fun pause() {
        if (mInternalPlayer == null) return
        mInternalPlayer!!.playWhenReady = false
    }

    override fun stop() {
        if (mInternalPlayer == null) return
        mInternalPlayer!!.stop()
    }

    override fun prepareAsync() {
        if (mInternalPlayer == null) return
        if (mMediaSource == null) return
        if (mSpeedPlaybackParameters != null) {
            mInternalPlayer!!.playbackParameters = mSpeedPlaybackParameters!!
        }
        mIsPreparing = true
        mInternalPlayer!!.setMediaSource(mMediaSource!!)
        mInternalPlayer!!.prepare()
    }

    override fun reset() {
        if (mInternalPlayer != null) {
            mInternalPlayer!!.stop()
            mInternalPlayer!!.clearMediaItems()
            mInternalPlayer!!.setVideoSurface(null)
            mIsPreparing = false
        }
    }

    override fun isPlaying(): Boolean {
        if (mInternalPlayer == null) return false
        val state = mInternalPlayer!!.playbackState
        return when (state) {
            Player.STATE_BUFFERING, Player.STATE_READY -> mInternalPlayer!!.playWhenReady
            Player.STATE_IDLE, Player.STATE_ENDED -> false
            else -> false
        }
    }

    override fun seekTo(msec: Long) {
        if (mInternalPlayer == null) return
        mInternalPlayer!!.seekTo(msec)
    }

    override fun release() {
        if (mInternalPlayer != null) {
            mInternalPlayer!!.removeListener(this)
            mInternalPlayer!!.release()
            mInternalPlayer = null
        }
        mIsPreparing = false
        mSpeedPlaybackParameters = null
    }

    override fun getCurrentPosition(): Long {
        return if (mInternalPlayer == null) 0 else mInternalPlayer!!.currentPosition
    }

    override fun getDuration(): Long {
        return if (mInternalPlayer == null) 0 else mInternalPlayer!!.duration
    }

    override fun getBufferedPercentage(): Int {
        return if (mInternalPlayer == null) 0 else mInternalPlayer!!.bufferedPercentage
    }

    override fun setSurface(surface: Surface?) {
        if (mInternalPlayer != null) {
            mInternalPlayer!!.setVideoSurface(surface)
        }
    }

    override fun setDisplay(holder: SurfaceHolder?) {
        if (holder == null) setSurface(null) else setSurface(holder.surface)
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        if (mInternalPlayer != null) mInternalPlayer!!.volume = (leftVolume + rightVolume) / 2
    }

    override fun setLooping(isLooping: Boolean) {
        if (mInternalPlayer != null) mInternalPlayer!!.repeatMode =
            if (isLooping) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
    }

    override fun setSpeed(speed: Float) {
        val playbackParameters = PlaybackParameters(speed)
        mSpeedPlaybackParameters = playbackParameters
        if (mInternalPlayer != null) {
            mInternalPlayer!!.playbackParameters = playbackParameters
        }
    }

    override fun getSpeed(): Float {
        return if (mSpeedPlaybackParameters != null) {
            mSpeedPlaybackParameters!!.speed
        } else 1f
    }

    override fun getTcpSpeed(): Long {
        // no support
        return 0
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (eventListener == null) return
        if (mIsPreparing) {
            if (playbackState == Player.STATE_READY) {
                eventListener!!.onPrepared()
                eventListener!!.onInfo(IPlayer.MEDIA_INFO_RENDERING_START, 0)
                mIsPreparing = false
            }
            return
        }
        when (playbackState) {
            Player.STATE_BUFFERING -> eventListener!!.onInfo(
                IPlayer.MEDIA_INFO_BUFFERING_START,
                getBufferedPercentage()
            )
            Player.STATE_READY -> eventListener!!.onInfo(
                IPlayer.MEDIA_INFO_BUFFERING_END,
                getBufferedPercentage()
            )
            Player.STATE_ENDED -> eventListener!!.onCompletion()
            Player.STATE_IDLE -> {}
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        if (eventListener != null) {
            eventListener!!.onError(
                PlayerException(
                    error
                )
            )
        }
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        if (eventListener != null) {
            eventListener!!.onVideoSizeChanged(videoSize.width, videoSize.height)
            if (videoSize.unappliedRotationDegrees > 0) {
                eventListener!!.onInfo(
                    IPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED,
                    videoSize.unappliedRotationDegrees
                )
            }
        }
    }

    init {
        mAppContext = context.applicationContext
        mMediaSourceHelper = ExoMediaSourceHelper.getInstance(context)
    }
}