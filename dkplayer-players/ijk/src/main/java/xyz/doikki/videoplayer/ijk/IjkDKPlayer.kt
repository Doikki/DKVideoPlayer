package xyz.doikki.videoplayer.ijk

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer.OnNativeInvokeListener
import tv.danmaku.ijk.media.player.misc.ITrackInfo
import xyz.doikki.videoplayer.AbstractDKPlayer
import xyz.doikki.videoplayer.DKPlayer
import xyz.doikki.videoplayer.internal.DKPlayerException
import xyz.doikki.videoplayer.util.orDefault

open class IjkDKPlayer(private val appContext: Context) : AbstractDKPlayer(),
    IMediaPlayer.OnErrorListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnInfoListener,
    IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnPreparedListener,
    IMediaPlayer.OnVideoSizeChangedListener, OnNativeInvokeListener {

    @JvmField
    protected var kernel: IjkMediaPlayer? = null

    private var bufferedPercent = 0

    private fun createKernel(): IjkMediaPlayer {
        return IjkMediaPlayer().also {
            it.setOnErrorListener(this)
            it.setOnCompletionListener(this)
            it.setOnInfoListener(this)
            it.setOnBufferingUpdateListener(this)
            it.setOnPreparedListener(this)
            it.setOnVideoSizeChangedListener(this)
            it.setOnNativeInvokeListener(this)
        }
    }

    override fun init() {
        //native日志 todo  java.lang.UnsatisfiedLinkError: No implementation found for void tv.danmaku.ijk.media.player.IjkMediaPlayer.native_setLogLevel(int)
//        IjkMediaPlayer.native_setLogLevel(if (isDebuggable) IjkMediaPlayer.IJK_LOG_INFO else IjkMediaPlayer.IJK_LOG_SILENT)
        kernel = createKernel()
    }

    override fun setDataSource(path: String, headers: Map<String, String>?) {
        try {
            val uri = Uri.parse(path)
            if (ContentResolver.SCHEME_ANDROID_RESOURCE == uri.scheme) {
                val rawDataSourceProvider = RawDataSourceProvider.create(appContext, uri)
                kernel!!.setDataSource(rawDataSourceProvider)
            } else {
                if (headers != null && headers.containsKey("User-Agent")) {
                    //处理UA问题
                    //update by luochao: 直接在Map参数中移除字段，可能影响调用者的逻辑
                    val clonedHeaders: MutableMap<String, String> = HashMap(headers)
                    // 移除header中的User-Agent，防止重复
                    val userAgent = clonedHeaders.remove("User-Agent")
                    //                    if (TextUtils.isEmpty(userAgent)) {
//                        userAgent = "";
//                    }
                    kernel!!.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", userAgent)
                    kernel!!.setDataSource(appContext, uri, clonedHeaders)
                } else {
                    //不包含UA，直接设置
                    kernel!!.setDataSource(appContext, uri, headers)
                }
            }
        } catch (e: Throwable) {
            eventListener!!.onError(e)
        }
    }

    override fun setDataSource(fd: AssetFileDescriptor) {
        try {
            kernel!!.setDataSource(RawDataSourceProvider(fd))
        } catch (e: Exception) {
            eventListener!!.onError(e)
        }
    }

    override fun pause() {
        try {
            kernel!!.pause()
        } catch (e: IllegalStateException) {
            eventListener!!.onError(e)
        }
    }

    override fun start() {
        try {
            kernel!!.start()
        } catch (e: IllegalStateException) {
            eventListener!!.onError(e)
        }
    }

    override fun stop() {
        try {
            kernel!!.stop()
        } catch (e: IllegalStateException) {
            eventListener!!.onError(e)
        }
    }

    override fun prepareAsync() {
        try {
            kernel!!.prepareAsync()
        } catch (e: IllegalStateException) {
            eventListener!!.onError(e)
        }
    }

    override fun reset() {
        kernel!!.reset()
        kernel!!.setOnVideoSizeChangedListener(this)
    }

    override fun isPlaying(): Boolean {
        return kernel!!.isPlaying
    }

    override fun seekTo(msec: Long) {
        try {
            kernel!!.seekTo(msec.toInt().toLong())
        } catch (e: IllegalStateException) {
            eventListener!!.onError(e)
        }
    }

    override fun release() {
        kernel!!.setOnErrorListener(null)
        kernel!!.setOnCompletionListener(null)
        kernel!!.setOnInfoListener(null)
        kernel!!.setOnBufferingUpdateListener(null)
        kernel!!.setOnPreparedListener(null)
        kernel!!.setOnVideoSizeChangedListener(null)

        val temp = kernel!!
        object : Thread() {
            override fun run() {
                try {
                    temp.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
        kernel = null
    }

    override fun getCurrentPosition(): Long {
        return kernel!!.currentPosition
    }

    override fun getDuration(): Long {
        return kernel!!.duration
    }

    override fun getBufferedPercentage(): Int {
        return bufferedPercent
    }

    override fun setSurface(surface: Surface?) {
        kernel!!.setSurface(surface)
    }

    override fun setDisplay(holder: SurfaceHolder?) {
        kernel!!.setDisplay(holder)
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        kernel!!.setVolume(leftVolume, rightVolume)
    }

    override fun setLooping(isLooping: Boolean) {
        kernel!!.isLooping = isLooping
    }

    override fun setSpeed(speed: Float) {
        kernel!!.setSpeed(speed)
    }

    override fun getSpeed(): Float {
        return kernel!!.getSpeed(0f)
    }

    override fun getTcpSpeed(): Long {
        return kernel?.tcpSpeed.orDefault()
    }

    override fun onError(mp: IMediaPlayer, what: Int, extra: Int): Boolean {
        eventListener!!.onError(
            DKPlayerException(
                what,
                extra
            )
        )
        return true
    }

    override fun onCompletion(mp: IMediaPlayer) {
        eventListener!!.onCompletion()
    }

    override fun onInfo(mp: IMediaPlayer, what: Int, extra: Int): Boolean {
        eventListener!!.onInfo(what, extra)
        return true
    }

    override fun onBufferingUpdate(mp: IMediaPlayer, percent: Int) {
        bufferedPercent = percent
    }

    override fun onPrepared(mp: IMediaPlayer) {
        eventListener!!.onPrepared()
        // 修复播放纯音频时状态出错问题
        if (!isVideo) {
            eventListener!!.onInfo(DKPlayer.MEDIA_INFO_RENDERING_START, 0)
        }
    }

    private val isVideo: Boolean
        get() {
            val trackInfo = kernel!!.trackInfo ?: return false
            for (info in trackInfo) {
                if (info.trackType == ITrackInfo.MEDIA_TRACK_TYPE_VIDEO) {
                    return true
                }
            }
            return false
        }

    override fun onVideoSizeChanged(
        mp: IMediaPlayer,
        width: Int,
        height: Int,
        sar_num: Int,
        sar_den: Int
    ) {
        val videoWidth = mp.videoWidth
        val videoHeight = mp.videoHeight
        if (videoWidth != 0 && videoHeight != 0) {
            eventListener!!.onVideoSizeChanged(videoWidth, videoHeight)
        }
    }

    override fun onNativeInvoke(what: Int, args: Bundle): Boolean {
        return true
    }
}