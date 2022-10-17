package xyz.doikki.videoplayer.sys

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.Surface
import android.view.SurfaceHolder
import xyz.doikki.videoplayer.AbstractDKPlayer
import xyz.doikki.videoplayer.DKPlayer
import xyz.doikki.videoplayer.util.orDefault
import xyz.doikki.videoplayer.internal.DKPlayerException
import xyz.doikki.videoplayer.util.tryIgnore
import xyz.doikki.videoplayer.util.L
import kotlin.concurrent.thread

/**
 * 基于系统[android.media.MediaPlayer]封装
 * 注意：不推荐，兼容性差，建议使用IJK或者Exo播放器
 */
class SysDKPlayer(context: Context) : AbstractDKPlayer(),
    MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnVideoSizeChangedListener {

    private val appContext: Context

    //系统播放器核心
    private var kernel: MediaPlayer? = null
    private var bufferedPercent = 0

    /**
     * 是否正在准备阶段：用于解决[android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START]多次回调问题
     */
    private var isPreparing = false

    init {
        appContext = context.applicationContext
    }

    override fun init() {
        kernel = MediaPlayer().also {
            it.setAudioStreamType(AudioManager.STREAM_MUSIC)
            it.setOnErrorListener(this)
            it.setOnCompletionListener(this)
            it.setOnInfoListener(this)
            it.setOnBufferingUpdateListener(this)
            it.setOnPreparedListener(this)
            it.setOnVideoSizeChangedListener(this)
        }
    }

    private fun logOnKernelInvalidate() {
        tryIgnore {
            if (kernel == null)
                L.w("player is null,can not invoke ${Thread.currentThread().stackTrace[2].methodName} method，please call init first.")
        }
    }

    private fun handlePlayerOperationException(e: Throwable) {
        e.printStackTrace()
        eventListener?.onError(e)
    }

    override fun setDataSource(path: String, headers: Map<String, String>?) {
        try {
            logOnKernelInvalidate()
            kernel!!.setDataSource(appContext, Uri.parse(path), headers)
        } catch (e: Throwable) {
            handlePlayerOperationException(e)
        }
    }

    override fun setDataSource(fd: AssetFileDescriptor) {
        try {
            logOnKernelInvalidate()
            kernel!!.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        } catch (e: Throwable) {
            handlePlayerOperationException(e)
        }
    }

    override fun start() {
        try {
            logOnKernelInvalidate()
            kernel!!.start()
        } catch (e: Throwable) {
            handlePlayerOperationException(e)
        }
    }

    override fun pause() {
        try {
            logOnKernelInvalidate()
            kernel!!.pause()
        } catch (e: Throwable) {
            handlePlayerOperationException(e)
        }
    }

    override fun stop() {
        try {
            logOnKernelInvalidate()
            kernel!!.stop()
        } catch (e: Throwable) {
            handlePlayerOperationException(e)
        }
    }

    override fun prepareAsync() {
        try {
            logOnKernelInvalidate()
            isPreparing = true
            kernel!!.prepareAsync()
        } catch (e: Throwable) {
            isPreparing = false
            handlePlayerOperationException(e)
        }
    }

    override fun reset() {
        kernel?.let {
            it.stop()
            it.reset()
            it.setSurface(null)
            it.setDisplay(null)
            it.setVolume(1f, 1f)
        }
    }

    override fun isPlaying(): Boolean {
        logOnKernelInvalidate()
        return kernel?.isPlaying.orDefault()
    }

    override fun seekTo(msec: Long) {
        try {
            logOnKernelInvalidate()
            if (Build.VERSION.SDK_INT >= 26) {
                //使用这个api seekTo定位更加准确 支持android 8.0以上的设备 https://developer.android.com/reference/android/media/MediaPlayer#SEEK_CLOSEST
                kernel!!.seekTo(msec, MediaPlayer.SEEK_CLOSEST)
            } else {
                kernel!!.seekTo(msec.toInt())
            }
        } catch (e: Throwable) {
            handlePlayerOperationException(e)
        }
    }

    override fun release() {
        kernel?.let {
            it.setOnErrorListener(null)
            it.setOnCompletionListener(null)
            it.setOnInfoListener(null)
            it.setOnBufferingUpdateListener(null)
            it.setOnPreparedListener(null)
            it.setOnVideoSizeChangedListener(null)
            it.stop()
            val mediaPlayer = it
            kernel = null
            thread {
                try {
                    mediaPlayer.release()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getCurrentPosition(): Long {
        logOnKernelInvalidate()
        return kernel?.currentPosition.orDefault().toLong()
    }

    override fun getDuration(): Long {
        logOnKernelInvalidate()
        return kernel?.duration.orDefault().toLong()
    }

    override fun getBufferedPercentage(): Int {
        return bufferedPercent
    }

    override fun setSurface(surface: Surface?) {
        try {
            logOnKernelInvalidate()
            kernel!!.setSurface(surface)
        } catch (e: Throwable) {
            handlePlayerOperationException(e)
        }
    }

    override fun setDisplay(holder: SurfaceHolder?) {
        try {
            logOnKernelInvalidate()
            kernel!!.setDisplay(holder)
        } catch (e: Throwable) {
            handlePlayerOperationException(e)
        }
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        logOnKernelInvalidate()
        kernel?.setVolume(leftVolume, rightVolume)
    }

    override fun setLooping(isLooping: Boolean) {
        logOnKernelInvalidate()
        kernel?.isLooping = isLooping
    }

    override fun setSpeed(speed: Float) {
        // only support above Android M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            L.w("Android MediaPlayer do not support set speed")
            return
        }
        logOnKernelInvalidate()
        kernel?.let {
            it.playbackParams = it.playbackParams.setSpeed(speed)
        }
    }

    override fun getSpeed(): Float {
        tryIgnore {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                logOnKernelInvalidate()
                return kernel?.playbackParams?.speed.orDefault(1f)
            } else {
                L.w("Android MediaPlayer do not support tcp speed")
            }
        }
        return 1f
    }
//
//    override fun getTcpSpeed(): Long {
//        L.w("Android MediaPlayer do not support tcp speed")
//        return super.getTcpSpeed()
//    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        eventListener?.onError(
            DKPlayerException(
                what,
                extra
            )
        )
        return true
    }

    override fun onCompletion(mp: MediaPlayer) {
        eventListener?.onCompletion()
    }

    override fun onInfo(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        //解决MEDIA_INFO_VIDEO_RENDERING_START多次回调问题
        if (what == DKPlayer.MEDIA_INFO_RENDERING_START) {
            if (isPreparing) {
                eventListener?.onInfo(what, extra)
                isPreparing = false
            }
        } else {
            eventListener?.onInfo(what, extra)
        }
        return true
    }

    override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
        bufferedPercent = percent
    }

    override fun onPrepared(mp: MediaPlayer) {
        eventListener?.onPrepared()
        start()
        // 修复播放纯音频时状态出错问题
        eventListener?.let {
            if (!isVideo()) {
                it.onInfo(DKPlayer.MEDIA_INFO_RENDERING_START, 0)
            }
        }
    }

    private fun isVideo(): Boolean {
        try {
            return kernel?.trackInfo?.any {
                it.trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO
            }.orDefault()
        } catch (e: Exception) {
        }
        return false
    }

    override fun onVideoSizeChanged(mp: MediaPlayer, width: Int, height: Int) {
        eventListener?.let {
            val videoWidth = mp.videoWidth
            val videoHeight = mp.videoHeight
            if (videoWidth != 0 && videoHeight != 0) {
                it.onVideoSizeChanged(videoWidth, videoHeight)
            }
        }
    }
}