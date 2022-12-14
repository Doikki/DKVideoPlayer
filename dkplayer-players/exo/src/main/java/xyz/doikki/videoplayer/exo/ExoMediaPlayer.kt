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
import xyz.doikki.videoplayer.GlobalConfig
import xyz.doikki.videoplayer.internal.PlayerException
import xyz.doikki.videoplayer.player.AbstractPlayer
import xyz.doikki.videoplayer.player.IPlayer
import xyz.doikki.videoplayer.util.orDefault

open class ExoMediaPlayer(context: Context) : AbstractPlayer(), Player.Listener {

    @JvmField
    protected var appContext: Context

    @JvmField
    protected var internalPlayer: ExoPlayer? = null

    @JvmField
    protected var mediaSource: MediaSource? = null

    @JvmField
    protected var mediaSourceHelper: ExoMediaSourceHelper
    private var speedPlaybackParameters: PlaybackParameters? = null
    private var isPreparing = false
    var loadControl: LoadControl? = null
    var renderersFactory: RenderersFactory? = null
    var trackSelector: TrackSelector? = null

    override fun init() {
        internalPlayer = ExoPlayer.Builder(
            appContext,
            renderersFactory ?: DefaultRenderersFactory(appContext).also { renderersFactory = it },
            DefaultMediaSourceFactory(appContext),
            trackSelector ?: DefaultTrackSelector(appContext).also { trackSelector = it },
            loadControl ?: DefaultLoadControl().also { loadControl = it },
            DefaultBandwidthMeter.getSingletonInstance(appContext),
            DefaultAnalyticsCollector(Clock.DEFAULT)
        )
            .build()
        //准备好就开始播放
        internalPlayer!!.playWhenReady = true

        //播放器日志
        if (GlobalConfig.isDebuggable && trackSelector is MappingTrackSelector) {
            internalPlayer!!.addAnalyticsListener(
                EventLogger(trackSelector as MappingTrackSelector?, "ExoPlayer")
            )
        }
        internalPlayer!!.addListener(this)
    }

    override fun setDataSource(path: String, headers: MutableMap<String, String>?) {
        mediaSource = mediaSourceHelper.getMediaSource(path, headers)
    }

    override fun setDataSource(fd: AssetFileDescriptor) {
        //no support
    }

    override fun start() {
        internalPlayer?.playWhenReady = true
    }

    override fun pause() {
        internalPlayer?.playWhenReady = false
    }

    override fun stop() {
        internalPlayer?.stop()
    }

    override fun prepareAsync() {
        val internalPlayer = internalPlayer ?: return
        val mediaSource = mediaSource ?: return
        speedPlaybackParameters?.let {
            internalPlayer.playbackParameters = it
        }
        isPreparing = true
        internalPlayer.setMediaSource(mediaSource)
        internalPlayer.prepare()
    }

    override fun reset() {
        internalPlayer?.let {
            it.stop()
            it.clearMediaItems()
            it.setVideoSurface(null)
            isPreparing = false
        }
    }

    override fun isPlaying(): Boolean {
        return when (internalPlayer?.playbackState) {
            Player.STATE_BUFFERING, Player.STATE_READY -> internalPlayer?.playWhenReady.orDefault()
            Player.STATE_IDLE, Player.STATE_ENDED -> false
            else -> false
        }
    }

    override fun seekTo(msec: Long) {
        internalPlayer?.seekTo(msec)
    }

    override fun release() {
        internalPlayer?.let {
            it.removeListener(this)
            it.release()
        }
        internalPlayer = null
        isPreparing = false
        speedPlaybackParameters = null
    }

    override fun getCurrentPosition(): Long {
        return internalPlayer?.currentPosition.orDefault()
    }

    override fun getDuration(): Long {
        return internalPlayer?.duration.orDefault()
    }

    override fun getBufferedPercentage(): Int {
        return internalPlayer?.bufferedPercentage.orDefault()
    }

    override fun setSurface(surface: Surface?) {
        internalPlayer?.setVideoSurface(surface)
    }

    override fun setDisplay(holder: SurfaceHolder?) {
        internalPlayer?.setVideoSurfaceHolder(holder)
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        internalPlayer?.volume = (leftVolume + rightVolume) / 2
    }

    override fun setLooping(isLooping: Boolean) {
        internalPlayer?.repeatMode =
            if (isLooping) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
    }

    override fun setSpeed(speed: Float) {
        val playbackParameters = PlaybackParameters(speed)
        speedPlaybackParameters = playbackParameters
        internalPlayer?.playbackParameters = playbackParameters
    }

    override fun getSpeed(): Float {
        return speedPlaybackParameters?.speed.orDefault(1f)
    }

    override fun getTcpSpeed(): Long {
        // no support
        return 0
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        eventListener?.let {
            if (isPreparing) {
                if (playbackState == Player.STATE_READY) {
                    it.onPrepared()
                    it.onInfo(IPlayer.MEDIA_INFO_RENDERING_START, 0)
                    isPreparing = false
                }
                return
            }
            when (playbackState) {
                Player.STATE_BUFFERING -> it.onInfo(
                    IPlayer.MEDIA_INFO_BUFFERING_START,
                    getBufferedPercentage()
                )
                Player.STATE_READY -> it.onInfo(
                    IPlayer.MEDIA_INFO_BUFFERING_END,
                    getBufferedPercentage()
                )
                Player.STATE_ENDED -> it.onCompletion()
                Player.STATE_IDLE -> {}
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        eventListener?.onError(PlayerException(error))
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        eventListener?.onVideoSizeChanged(videoSize.width, videoSize.height)
        if (videoSize.unappliedRotationDegrees > 0) {
            eventListener?.onInfo(
                IPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED,
                videoSize.unappliedRotationDegrees
            )
        }
    }

    init {
        appContext = context.applicationContext
        mediaSourceHelper = ExoMediaSourceHelper.getInstance(context)
    }
}